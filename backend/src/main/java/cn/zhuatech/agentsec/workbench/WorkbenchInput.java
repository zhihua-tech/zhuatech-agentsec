/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agentsec.workbench;

import java.math.*;
import java.util.*;

/**
 * Bounded request parsing; no files, model calls or database access. 上海如静知华信息科技有限公司 ·
 * https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
final class WorkbenchInput {
  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  private WorkbenchInput() {}

  /**
   * Enforce aggregate limits before business computation. 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
   */
  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  static void validate(Map<String, Object> value) {
    if (value == null) throw new IllegalArgumentException("请求不能为空");
    inspect(value, 0, new int[] {0, 0});
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  private static void inspect(Object v, int depth, int[] budget) {
    if (depth > 12 || ++budget[0] > 10000) throw new IllegalArgumentException("请求嵌套或节点过多");
    if (v instanceof String s) {
      budget[1] += s.length();
      if (s.length() > 64000 || budget[1] > 128000) throw new IllegalArgumentException("文本超过处理上限");
    } else if (v instanceof Map<?, ?> m) {
      if (m.size() > 100) throw new IllegalArgumentException("字段过多");
      for (var e : m.entrySet()) {
        if (!(e.getKey() instanceof String)) throw new IllegalArgumentException("字段名须为字符串");
        inspect(e.getKey(), depth + 1, budget);
        inspect(e.getValue(), depth + 1, budget);
      }
    } else if (v instanceof List<?> l) {
      if (l.size() > 500) throw new IllegalArgumentException("数组最多500项");
      for (Object x : l) inspect(x, depth + 1, budget);
    } else if (v != null && !(v instanceof Number) && !(v instanceof Boolean))
      throw new IllegalArgumentException("不支持的数据类型");
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  static void keys(Map<String, Object> m, String... names) {
    Set<String> allowed = Set.of(names);
    if (!allowed.containsAll(m.keySet())) throw new IllegalArgumentException("存在未声明字段");
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  static String text(Map<String, Object> m, String k, int max) {
    Object o = m.get(k);
    if (!(o instanceof String s) || s.isBlank() || s.length() > max)
      throw new IllegalArgumentException(k + "须为非空文本且长度不超过" + max);
    return s;
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  static String id(Map<String, Object> m, String k) {
    String s = text(m, k, 80);
    if (!s.matches("[A-Za-z0-9][A-Za-z0-9_.:-]*"))
      throw new IllegalArgumentException(k + "标识格式不正确");
    return s;
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  static BigDecimal number(Map<String, Object> m, String k, BigDecimal min, BigDecimal max) {
    Object o = m.get(k);
    if (!(o instanceof Number)) throw new IllegalArgumentException(k + "须为数值");
    try {
      BigDecimal n = new BigDecimal(o.toString());
      if (n.compareTo(min) < 0 || n.compareTo(max) > 0)
        throw new IllegalArgumentException(k + "超出范围");
      return n;
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(k + "须为有限数值");
    }
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  static BigDecimal decimal(Map<String, Object> m, String k, double min, double max) {
    return number(m, k, BigDecimal.valueOf(min), BigDecimal.valueOf(max));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  static int integer(Map<String, Object> m, String k, int min, int max) {
    try {
      return number(m, k, BigDecimal.valueOf(min), BigDecimal.valueOf(max)).intValueExact();
    } catch (ArithmeticException e) {
      throw new IllegalArgumentException(k + "须为整数");
    }
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  static boolean flag(Map<String, Object> m, String k) {
    if (!(m.get(k) instanceof Boolean b)) throw new IllegalArgumentException(k + "须为布尔值");
    return b;
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  static List<?> list(Map<String, Object> m, String k, int min, int max) {
    if (!(m.get(k) instanceof List<?> l) || l.size() < min || l.size() > max)
      throw new IllegalArgumentException(k + "项数须为" + min + "至" + max);
    return l;
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @SuppressWarnings("unchecked")
  static Map<String, Object> object(Object o) {
    if (!(o instanceof Map<?, ?>)) throw new IllegalArgumentException("列表元素须为对象");
    return (Map<String, Object>) o;
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  static List<Map<String, Object>> rows(Map<String, Object> m, String k, int min, int max) {
    return list(m, k, min, max).stream().map(WorkbenchInput::object).toList();
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  static List<String> strings(Map<String, Object> m, String k, int min, int max) {
    List<String> r = new ArrayList<>();
    for (Object o : list(m, k, min, max)) {
      if (!(o instanceof String s) || s.isBlank() || s.length() > 2000)
        throw new IllegalArgumentException(k + "须为非空字符串数组");
      r.add(s);
    }
    return List.copyOf(r);
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  static BigDecimal round(double n) {
    if (!Double.isFinite(n)) throw new IllegalArgumentException("结果超出数值范围");
    return BigDecimal.valueOf(n).setScale(6, RoundingMode.HALF_UP).stripTrailingZeros();
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  static void unique(Set<String> ids, String id) {
    if (!ids.add(id)) throw new IllegalArgumentException("编号重复: " + id);
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  static BigDecimal money(Map<String, Object> row, String key) {
    BigDecimal v = decimal(row, key, 0, 1e12);
    if (v.stripTrailingZeros().scale() > 2) throw new IllegalArgumentException(key + "金额最多两位小数");
    return v.setScale(2, RoundingMode.UNNECESSARY);
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  static java.time.LocalDate date(String s) {
    try {
      if (!s.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}"))
        throw new IllegalArgumentException("日期须为YYYY-MM-DD");
      var d = java.time.LocalDate.parse(s);
      if (d.getYear() < 1900 || d.getYear() > 2200)
        throw new IllegalArgumentException("日期年份须在1900至2200");
      return d;
    } catch (java.time.DateTimeException e) {
      throw new IllegalArgumentException("日期无效");
    }
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  static java.time.LocalDateTime local(String s) {
    try {
      var d = java.time.LocalDateTime.parse(s);
      date(d.toLocalDate().toString());
      if (d.getSecond() != 0 || d.getNano() != 0)
        throw new IllegalArgumentException("本地时间必须精确到整分钟");
      return d;
    } catch (java.time.DateTimeException e) {
      throw new IllegalArgumentException("本地时间无效");
    }
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  static java.time.Instant instant(String s) {
    try {
      var d = java.time.Instant.parse(s);
      if (d.isBefore(java.time.Instant.parse("1900-01-01T00:00:00Z"))
          || d.isAfter(java.time.Instant.parse("2200-12-31T23:59:59Z")))
        throw new IllegalArgumentException("时间超出范围");
      return d;
    } catch (java.time.DateTimeException e) {
      throw new IllegalArgumentException("时间须为ISO-8601 UTC时间");
    }
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  static String digest(String text) {
    try {
      return HexFormat.of()
          .formatHex(
              java.security.MessageDigest.getInstance("SHA-256")
                  .digest(text.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
    } catch (java.security.NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 unavailable", e);
    }
  }
}
