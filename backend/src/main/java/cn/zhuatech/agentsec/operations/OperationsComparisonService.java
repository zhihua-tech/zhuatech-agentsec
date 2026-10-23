/* 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agentsec.operations;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

/**
 * 两组授权输入按当前算法重算；差异不代表业务执行或风险已下降。上海如静知华信息科技有限公司 · https://www.zhuatech.cn/
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Service
public class OperationsComparisonService {
  private static final JsonMapper JSON = new JsonMapper();
  private static final String FORMAT = "zhuatech-review-input/v1";
  private final OperationsReviewService review;
  private final String contractFingerprint, ruleFingerprint;
  private final Map<String, Object> example;

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @SuppressWarnings("unchecked")
  public OperationsComparisonService(OperationsReviewService review) {
    this.review = review;
    try (var contract =
            OperationsReviewService.class.getResourceAsStream("/operations/schema.json");
        var code =
            OperationsReviewService.class.getResourceAsStream("OperationsReviewService.class");
        var sample = getClass().getResourceAsStream("/operations/comparison-example.json")) {
      if (contract == null || code == null || sample == null)
        throw new IllegalStateException("缺少对照核算资源");
      contractFingerprint = digest(contract.readAllBytes());
      ruleFingerprint =
          digest(
              (contractFingerprint + ":" + digest(code.readAllBytes()))
                  .getBytes(StandardCharsets.UTF_8));
      example = JSON.readValue(sample, Map.class);
    } catch (java.io.IOException ex) {
      throw new IllegalStateException("无法读取对照核算契约", ex);
    }
  }

  /**
   * 输入契约和算法字节码摘要不是签名。上海如静知华信息科技有限公司 · https://www.zhuatech.cn/
   * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
   */
  public Map<String, Object> contract() {
    var out = new LinkedHashMap<String, Object>();
    for (String key : List.of("slug", "title", "description", "fields", "columns", "sample"))
      out.put(key, review.schema().get(key));
    out.put("project", review.schema().get("slug"));
    out.put("contractFingerprint", contractFingerprint);
    out.put("ruleFingerprint", ruleFingerprint);
    out.put("format", FORMAT);
    out.put("maxUiRecords", 100);
    out.put("maxCombinedRecords", 500);
    out.put("examplePatch", example.get("patch"));
    out.put("exampleDescription", example.get("description"));
    return out;
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  public Map<String, Object> compare(String raw) {
    return compare(OpsInput.parse(raw));
  }

  /**
   * 所有输入通过验证后才返回完整对照。上海如静知华信息科技有限公司 · https://www.zhuatech.cn/
   * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
   */
  @SuppressWarnings("unchecked")
  public Map<String, Object> compare(Map<String, Object> input) {
    if (input == null) throw new IllegalArgumentException("请求须为JSON对象");
    OpsInput.inspect(input, 0, new int[] {0, 0});
    OpsInput.keys(input, List.of("expectedRuleFingerprint", "baseline", "candidate"));
    if (!ruleFingerprint.equals(fingerprint(input.get("expectedRuleFingerprint"))))
      throw new VersionConflict("规则版本已变化，请刷新契约后重算");
    var baseline = object(input.get("baseline"));
    var candidate = object(input.get("candidate"));
    if (size(baseline) + size(candidate) > 500)
      throw new IllegalArgumentException("两组输入合计最多500条记录");
    Map<String, Object> before = review.execute(baseline), after = review.execute(candidate);
    var left = index(before);
    var right = index(after);
    Set<String> ids = new TreeSet<>(left.keySet());
    ids.addAll(right.keySet());
    List<Map<String, Object>> rows = new ArrayList<>();
    Map<String, Integer> counts = new LinkedHashMap<>();
    for (String k : List.of("added", "removed", "changed", "unchanged", "relaxed", "tightened"))
      counts.put(k, 0);
    for (String id : ids) {
      var b = left.get(id);
      var a = right.get(id);
      var changes = changes(b, a);
      String kind =
          b == null ? "ADDED" : a == null ? "REMOVED" : changes.isEmpty() ? "UNCHANGED" : "CHANGED";
      String transition =
          b == null
              ? "NEW"
              : a == null
                  ? "REMOVED"
                  : rank(a.get("decision")) > rank(b.get("decision"))
                      ? "RELAXED"
                      : rank(a.get("decision")) < rank(b.get("decision")) ? "TIGHTENED" : "SAME";
      counts.compute(kind.toLowerCase(Locale.ROOT), (k, v) -> v + 1);
      if (transition.equals("RELAXED")) counts.compute("relaxed", (k, v) -> v + 1);
      if (transition.equals("TIGHTENED")) counts.compute("tightened", (k, v) -> v + 1);
      var row = new LinkedHashMap<String, Object>();
      row.put("id", a == null ? b.get("id") : a.get("id"));
      row.put("kind", kind);
      row.put("transition", transition);
      row.put("before", b);
      row.put("after", a);
      row.put("changes", changes);
      rows.add(row);
    }
    var result = new LinkedHashMap<String, Object>();
    result.put("project", review.schema().get("slug"));
    result.put("contractFingerprint", contractFingerprint);
    result.put("ruleFingerprint", ruleFingerprint);
    result.put("baselineDate", baseline.get("asOf"));
    result.put("candidateDate", candidate.get("asOf"));
    result.put("dateChanged", !baseline.get("asOf").equals(candidate.get("asOf")));
    result.put("baselineInputDigest", digest(JSON.writeValueAsBytes(canonical(baseline))));
    result.put("candidateInputDigest", digest(JSON.writeValueAsBytes(canonical(candidate))));
    result.put("summary", counts);
    result.put("rows", rows);
    result.put("automaticWrite", false);
    return result;
  }

  /**
   * 导入只接受版本化输入，不把历史核算结果当作可信结果。上海如静知华信息科技有限公司 · https://www.zhuatech.cn/
   * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
   */
  public Map<String, Object> importSnapshot(String raw) {
    var input = OpsInput.parse(raw);
    OpsInput.keys(
        input, List.of("format", "project", "contractFingerprint", "ruleFingerprint", "scenario"));
    if (!FORMAT.equals(input.get("format")))
      throw new IllegalArgumentException("不是受支持的输入快照格式，不能导入核算报告");
    if (!review.schema().get("slug").equals(input.get("project")))
      throw new IllegalArgumentException("输入快照属于其他项目");
    if (!contractFingerprint.equals(fingerprint(input.get("contractFingerprint"))))
      throw new VersionConflict("输入字段契约已变化，需人工核对后重新录入");
    String oldRule = fingerprint(input.get("ruleFingerprint"));
    var scenario = object(input.get("scenario"));
    review.execute(scenario);
    return Map.of(
        "scenario",
        stringNumbers(scenario),
        "ruleChanged",
        !ruleFingerprint.equals(oldRule),
        "sourceRuleFingerprint",
        oldRule,
        "currentRuleFingerprint",
        ruleFingerprint,
        "automaticWrite",
        false);
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  public Map<String, Object> snapshot(Map<String, Object> scenario) {
    review.execute(scenario);
    return Map.of(
        "format",
        FORMAT,
        "project",
        review.schema().get("slug"),
        "contractFingerprint",
        contractFingerprint,
        "ruleFingerprint",
        ruleFingerprint,
        "scenario",
        stringNumbers(scenario));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @SuppressWarnings("unchecked")
  private static Map<String, Object> object(Object value) {
    if (!(value instanceof Map<?, ?> map)) throw new IllegalArgumentException("两组输入须为对象");
    return (Map<String, Object>) map;
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  private static int size(Map<String, Object> input) {
    if (!(input.get("records") instanceof List<?> rows))
      throw new IllegalArgumentException("records须为数组");
    return rows.size();
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  private static String fingerprint(Object value) {
    if (!(value instanceof String s) || !s.matches("[0-9a-f]{64}"))
      throw new IllegalArgumentException("指纹格式错误");
    return s;
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  private static int rank(Object decision) {
    return switch (decision.toString()) {
      case "HOLD" -> 0;
      case "REVIEW" -> 1;
      case "PASS" -> 2;
      default -> throw new IllegalStateException("未知建议等级");
    };
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @SuppressWarnings("unchecked")
  private static Map<String, Map<String, Object>> index(Map<String, Object> result) {
    Map<String, Map<String, Object>> out = new TreeMap<>();
    for (Object row : (List<?>) result.get("results")) {
      var r = (Map<String, Object>) row;
      out.put(r.get("id").toString().toLowerCase(Locale.ROOT), r);
    }
    return out;
  }

  /**
   * 数值差异使用精确十进制，不对N/A/日期/文本猜测数值。上海如静知华信息科技有限公司 · https://www.zhuatech.cn/
   * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
   */
  static List<Map<String, Object>> changes(Map<String, Object> before, Map<String, Object> after) {
    Set<String> keys = new TreeSet<>();
    if (before != null) keys.addAll(before.keySet());
    if (after != null) keys.addAll(after.keySet());
    List<Map<String, Object>> out = new ArrayList<>();
    for (String key : keys) {
      Object b = before == null ? null : before.get(key), a = after == null ? null : after.get(key);
      boolean numeric = b instanceof Number && a instanceof Number;
      BigDecimal delta =
          numeric ? new BigDecimal(a.toString()).subtract(new BigDecimal(b.toString())) : null;
      if (numeric ? delta.signum() == 0 : Objects.equals(b, a)) continue;
      var change = new LinkedHashMap<String, Object>();
      change.put("field", key);
      change.put("before", b);
      change.put("after", a);
      change.put("numericDelta", delta);
      out.add(change);
    }
    return out;
  }

  /**
   * 摘要按字段键排序，记录顺序及文本形式保留，非审批签名。上海如静知华信息科技有限公司 · https://www.zhuatech.cn/
   * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
   */
  private static Object canonical(Object value) {
    if (value instanceof Map<?, ?> m) {
      var out = new TreeMap<String, Object>();
      m.forEach((k, v) -> out.put(k.toString(), canonical(v)));
      return out;
    }
    if (value instanceof List<?> l)
      return l.stream().map(OperationsComparisonService::canonical).toList();
    return value;
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  private static Object stringNumbers(Object value) {
    if (value instanceof Number n) return new BigDecimal(n.toString()).toPlainString();
    if (value instanceof Map<?, ?> m) {
      var out = new LinkedHashMap<String, Object>();
      m.forEach((k, v) -> out.put(k.toString(), stringNumbers(v)));
      return out;
    }
    if (value instanceof List<?> l)
      return l.stream().map(OperationsComparisonService::stringNumbers).toList();
    return value;
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  private static String digest(byte[] bytes) {
    try {
      return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
    } catch (java.security.NoSuchAlgorithmException ex) {
      throw new IllegalStateException(ex);
    }
  }

  /**
   * 契约或算法变化时明确要求刷新，不能静默沿用旧报告。上海如静知华信息科技有限公司 · https://www.zhuatech.cn/
   * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
   */
  public static class VersionConflict extends IllegalArgumentException {
    /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
    public VersionConflict(String message) {
      super(message);
    }
  }
}
