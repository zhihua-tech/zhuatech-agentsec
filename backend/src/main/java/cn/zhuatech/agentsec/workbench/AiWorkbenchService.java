/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agentsec.workbench;

import static cn.zhuatech.agentsec.workbench.WorkbenchInput.*;

import java.math.*;
import java.util.*;
import org.springframework.stereotype.Service;

/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
@Service
public class AiWorkbenchService {
  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  public Map<String, Object> execute(Map<String, Object> r) {
    validate(r);
    keys(r, "executionId", "expectedRoot", "steps");
    String execution = id(r, "executionId");
    Object expectedValue = r.get("expectedRoot");
    if (!(expectedValue instanceof String expected) || !expected.matches("([0-9a-f]{64})?"))
      throw new IllegalArgumentException("预期根摘要须为空或64位小写十六进制");
    String previous = digest(execution);
    List<Map<String, Object>> out = new ArrayList<>();
    int next = 1;
    for (var step : rows(r, "steps", 1, 500)) {
      keys(step, "sequence", "tool", "inputDigest", "outputDigest");
      int sequence = integer(step, "sequence", 1, 500);
      if (sequence != next++) throw new IllegalArgumentException("步骤必须从1开始连续且按顺序提交");
      String tool = id(step, "tool"),
          input = text(step, "inputDigest", 64),
          output = text(step, "outputDigest", 64);
      if (!input.matches("[0-9a-f]{64}") || !output.matches("[0-9a-f]{64}"))
        throw new IllegalArgumentException("输入输出摘要须为64位小写十六进制");
      String hash = digest(previous + "|" + sequence + "|" + tool + "|" + input + "|" + output);
      out.add(Map.of("sequence", sequence, "previousDigest", previous, "digest", hash));
      previous = hash;
    }
    return Map.of(
        "executionId",
        execution,
        "rootDigest",
        previous,
        "expectedRootProvided",
        !expected.isEmpty(),
        "matchesExpected",
        !expected.isEmpty() && previous.equals(expected),
        "steps",
        out,
        "algorithm",
        "SHA-256");
  }
}
