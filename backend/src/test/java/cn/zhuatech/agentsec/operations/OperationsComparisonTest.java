/* 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agentsec.operations;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import tools.jackson.databind.json.JsonMapper;

/**
 * 对照、快照和本项目业务回归。上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
class OperationsComparisonTest {
  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  private static final JsonMapper JSON = new JsonMapper();

  private final OperationsReviewService review = new OperationsReviewService();
  private final OperationsComparisonService service = new OperationsComparisonService(review);

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @SuppressWarnings("unchecked")
  private Map<String, Object> sample() {
    return JSON.readValue(JSON.writeValueAsString(review.schema().get("sample")), Map.class);
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @SuppressWarnings("unchecked")
  private Map<String, Object> row(Map<String, Object> scenario) {
    return (Map<String, Object>) ((List<?>) scenario.get("records")).getFirst();
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  private Map<String, Object> request(Map<String, Object> left, Map<String, Object> right) {
    return new LinkedHashMap<>(
        Map.of(
            "expectedRuleFingerprint",
            service.contract().get("ruleFingerprint"),
            "baseline",
            left,
            "candidate",
            right));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @SuppressWarnings("unchecked")
  private Map<String, Object> first(Map<String, Object> report) {
    return (Map<String, Object>) ((List<?>) report.get("rows")).getFirst();
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @SuppressWarnings("unchecked")
  static Stream<Map<String, Object>> cases() throws Exception {
    try (var in =
        OperationsComparisonTest.class.getResourceAsStream("/operations/comparison-cases.json")) {
      return ((List<Map<String, Object>>) JSON.readValue(in, List.class)).stream();
    }
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @SuppressWarnings("unchecked")
  private void expected(Object actual, Map<String, Object> expected) {
    var values = (Map<String, Object>) actual;
    expected.forEach(
        (k, v) -> {
          String s = v.toString();
          if (s.startsWith("#"))
            assertEquals(
                0,
                new BigDecimal(s.substring(1)).compareTo(new BigDecimal(values.get(k).toString())),
                k);
          else assertEquals(s, values.get(k).toString(), k);
        });
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @ParameterizedTest
  @MethodSource("cases")
  @SuppressWarnings("unchecked")
  void projectSpecificBeforeAndAfter(Map<String, Object> fixture) {
    var a = sample();
    var b = sample();
    row(b).putAll((Map<String, Object>) fixture.get("patch"));
    var r = first(service.compare(request(a, b)));
    expected(r.get("before"), (Map<String, Object>) fixture.get("baseline"));
    expected(r.get("after"), (Map<String, Object>) fixture.get("expected"));
    assertEquals(false, service.compare(request(a, b)).get("automaticWrite"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void identicalInputsAreUnchanged() {
    var r = service.compare(request(sample(), sample()));
    assertEquals("UNCHANGED", first(r).get("kind"));
    assertEquals("SAME", first(r).get("transition"));
    assertEquals(r.get("baselineInputDigest"), r.get("candidateInputDigest"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void exactSignedDeltasNeverUseDouble() {
    var c =
        OperationsComparisonService.changes(
            Map.of("n", new BigDecimal("9007199254740993.123456")),
            Map.of("n", new BigDecimal("9007199254740992.123455")));
    assertEquals(new BigDecimal("-1.000001"), c.getFirst().get("numericDelta"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void numericScaleOnlyIsNotChange() {
    assertTrue(
        OperationsComparisonService.changes(
                Map.of("n", new BigDecimal("1.00")), Map.of("n", new BigDecimal("1")))
            .isEmpty());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void textAndNaAreNotNumeric() {
    var c =
        OperationsComparisonService.changes(Map.of("n", "N/A"), Map.of("n", new BigDecimal("1")));
    assertNull(c.getFirst().get("numericDelta"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void addedRowsHaveNoBaseline() {
    var b = sample();
    row(b).put("id", "NEW-001");
    var r = service.compare(request(sample(), b));
    assertEquals(2, ((List<?>) r.get("rows")).size());
    assertTrue(((List<?>) r.get("rows")).toString().contains("ADDED"));
    assertTrue(((List<?>) r.get("rows")).toString().contains("REMOVED"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void caseInsensitivePairingRecordsSpellingChange() {
    var b = sample();
    row(b).put("id", row(b).get("id").toString().toLowerCase(Locale.ROOT));
    var r = service.compare(request(sample(), b));
    assertEquals(1, ((List<?>) r.get("rows")).size());
    assertEquals("CHANGED", first(r).get("kind"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void datesAreExplicitlyCompared() {
    var b = sample();
    b.put("asOf", "2026-10-24");
    assertEquals(true, service.compare(request(sample(), b)).get("dateChanged"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void contractFingerprintsAreStable() {
    var other = new OperationsComparisonService(review);
    assertEquals(
        service.contract().get("ruleFingerprint"), other.contract().get("ruleFingerprint"));
    assertTrue(service.contract().get("contractFingerprint").toString().matches("[0-9a-f]{64}"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void staleRuleIsConflict() {
    var r = request(sample(), sample());
    r.put("expectedRuleFingerprint", "0".repeat(64));
    assertThrows(OperationsComparisonService.VersionConflict.class, () -> service.compare(r));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void malformedFingerprintRejected() {
    var r = request(sample(), sample());
    r.put("expectedRuleFingerprint", "bad");
    assertThrows(IllegalArgumentException.class, () -> service.compare(r));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void nullInputRejected() {
    assertThrows(IllegalArgumentException.class, () -> service.compare((Map<String, Object>) null));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void unknownRootRejected() {
    var r = request(sample(), sample());
    r.put("execute", true);
    assertThrows(IllegalArgumentException.class, () -> service.compare(r));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void missingCandidateRejected() {
    var r = request(sample(), sample());
    r.remove("candidate");
    assertThrows(IllegalArgumentException.class, () -> service.compare(r));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void wrongScenarioShapeRejected() {
    var r = request(sample(), sample());
    r.put("candidate", List.of());
    assertThrows(IllegalArgumentException.class, () -> service.compare(r));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void invalidCandidateRejectsWholeRequest() {
    var b = sample();
    row(b).put("unexpected", 1);
    assertThrows(IllegalArgumentException.class, () -> service.compare(request(sample(), b)));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void duplicateIdsRejected() {
    var b = sample();
    b.put("records", List.of(row(b), row(b)));
    assertThrows(IllegalArgumentException.class, () -> service.compare(request(sample(), b)));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void emptyBaselineRejected() {
    var a = sample();
    a.put("records", List.of());
    assertThrows(IllegalArgumentException.class, () -> service.compare(request(a, sample())));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void combinedRecordLimitRejectedBeforeDomainExecution() {
    var a = sample();
    var b = sample();
    a.put("records", Collections.nCopies(251, Map.of()));
    b.put("records", Collections.nCopies(250, Map.of()));
    assertThrows(IllegalArgumentException.class, () -> service.compare(request(a, b)));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void repeatedJsonKeysRejected() {
    assertThrows(
        IllegalArgumentException.class, () -> service.compare("{\"baseline\":{},\"baseline\":{}}"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void trailingJsonRejected() {
    assertThrows(
        IllegalArgumentException.class,
        () -> service.compare(JSON.writeValueAsString(request(sample(), sample())) + " {}"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  @SuppressWarnings("unchecked")
  void snapshotRoundtripRecalculates() {
    var snap = service.snapshot(sample());
    var imported = service.importSnapshot(JSON.writeValueAsString(snap));
    assertEquals(false, imported.get("ruleChanged"));
    var scenario = (Map<String, Object>) imported.get("scenario");
    assertEquals("UNCHANGED", first(service.compare(request(sample(), scenario))).get("kind"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void wrongProjectRejected() {
    var s = new LinkedHashMap<>(service.snapshot(sample()));
    s.put("project", "other");
    assertThrows(
        IllegalArgumentException.class, () -> service.importSnapshot(JSON.writeValueAsString(s)));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void schemaMismatchIsConflict() {
    var s = new LinkedHashMap<>(service.snapshot(sample()));
    s.put("contractFingerprint", "0".repeat(64));
    assertThrows(
        OperationsComparisonService.VersionConflict.class,
        () -> service.importSnapshot(JSON.writeValueAsString(s)));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void oldAlgorithmInputsAcceptedWithWarning() {
    var s = new LinkedHashMap<>(service.snapshot(sample()));
    s.put("ruleFingerprint", "0".repeat(64));
    assertEquals(true, service.importSnapshot(JSON.writeValueAsString(s)).get("ruleChanged"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void unknownSnapshotFieldRejected() {
    var s = new LinkedHashMap<>(service.snapshot(sample()));
    s.put("results", List.of());
    assertThrows(
        IllegalArgumentException.class, () -> service.importSnapshot(JSON.writeValueAsString(s)));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void reportCannotBeImportedAsInputs() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            service.importSnapshot(
                JSON.writeValueAsString(service.compare(request(sample(), sample())))));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void invalidImportedRowsAreAtomicFailure() {
    var bad = sample();
    row(bad).remove("id");
    var s = new LinkedHashMap<>(service.snapshot(sample()));
    s.put("scenario", bad);
    assertThrows(
        IllegalArgumentException.class, () -> service.importSnapshot(JSON.writeValueAsString(s)));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void numericStringsAreNotConvertedToBinaryFloat() {
    var s = service.snapshot(sample());
    assertEquals(sample(), service.importSnapshot(JSON.writeValueAsString(s)).get("scenario"));
  }
}
