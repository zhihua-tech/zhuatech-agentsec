/* 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agentsec.operations;

import static org.junit.jupiter.api.Assertions.*;

import java.net.*;
import java.net.http.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import tools.jackson.databind.json.JsonMapper;

/**
 * 真实HTTP认证和导入/对照边界。上海如静知华信息科技有限公司 · https://www.zhuatech.cn/
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OperationsComparisonHttpTest {
  @LocalServerPort int port;
  @Autowired OperationsComparisonService service;
  @Autowired OperationsReviewService review;
  @Autowired cn.zhuatech.agentsec.security.JwtService jwt;

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  private final JsonMapper json = new JsonMapper();

  private final HttpClient client = HttpClient.newHttpClient();

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  private String auth() {
    return "Bearer " + jwt.generate("admin");
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @SuppressWarnings("unchecked")
  private Map<String, Object> sample() {
    return (Map<String, Object>) review.schema().get("sample");
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  private Map<String, Object> data() {
    return new LinkedHashMap<>(
        Map.of(
            "expectedRuleFingerprint",
            service.contract().get("ruleFingerprint"),
            "baseline",
            sample(),
            "candidate",
            sample()));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  private HttpResponse<String> send(String path, String body, String authorization)
      throws Exception {
    var b =
        HttpRequest.newBuilder(
                URI.create("http://127.0.0.1:" + port + "/api/operations-review/" + path))
            .timeout(java.time.Duration.ofSeconds(15))
            .header("Content-Type", "application/json");
    if (authorization != null) b.header("Authorization", authorization);
    if (body == null) b.GET();
    else b.POST(HttpRequest.BodyPublishers.ofString(body));
    return client.send(b.build(), HttpResponse.BodyHandlers.ofString());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  private String payload() {
    return json.writeValueAsString(data());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void contractRequiresIdentity() throws Exception {
    assertEquals(401, send("comparison-contract", null, null).statusCode());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void compareRequiresIdentity() throws Exception {
    assertEquals(401, send("compare", payload(), null).statusCode());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void importRequiresIdentity() throws Exception {
    assertEquals(401, send("import-check", "{}", null).statusCode());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void invalidCredentialRejected() throws Exception {
    assertEquals(401, send("compare", payload(), "Bearer invalid").statusCode());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void authenticatedContractIsNoStore() throws Exception {
    var r = send("comparison-contract", null, auth());
    assertEquals(200, r.statusCode(), r.body());
    assertEquals(
        service.contract().get("ruleFingerprint"),
        json.readTree(r.body()).at("/data/ruleFingerprint").asText());
    assertTrue(r.headers().firstValue("Cache-Control").orElse("").contains("no-store"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void computedComparisonHasTrustedResponseMetadata() throws Exception {
    var r = send("compare", payload(), auth());
    assertEquals(200, r.statusCode(), r.body());
    var n = json.readTree(r.body()).get("data");
    assertEquals("admin", n.get("evaluatedBy").asText());
    java.time.Instant.parse(n.get("evaluatedAt").asText());
    UUID.fromString(n.get("correlationId").asText());
    assertEquals("UNCHANGED", n.at("/rows/0/kind").asText());
    assertFalse(n.get("automaticWrite").asBoolean());
    assertTrue(r.headers().firstValue("Cache-Control").orElse("").contains("no-store"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void staleRuleReturns409() throws Exception {
    var d = data();
    d.put("expectedRuleFingerprint", "0".repeat(64));
    assertEquals(409, send("compare", json.writeValueAsString(d), auth()).statusCode());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void validImportIsValidatedByServer() throws Exception {
    var r = send("import-check", json.writeValueAsString(service.snapshot(sample())), auth());
    assertEquals(200, r.statusCode(), r.body());
    assertFalse(json.readTree(r.body()).at("/data/ruleChanged").asBoolean());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void wrongProjectImportReturns400() throws Exception {
    var d = new LinkedHashMap<>(service.snapshot(sample()));
    d.put("project", "other");
    assertEquals(400, send("import-check", json.writeValueAsString(d), auth()).statusCode());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void wrongContractImportReturns409() throws Exception {
    var d = new LinkedHashMap<>(service.snapshot(sample()));
    d.put("contractFingerprint", "0".repeat(64));
    assertEquals(409, send("import-check", json.writeValueAsString(d), auth()).statusCode());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void malformedImportReturns400() throws Exception {
    assertEquals(400, send("import-check", "{bad", auth()).statusCode());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void repeatedJsonKeysReturn400() throws Exception {
    assertEquals(400, send("compare", "{\"baseline\":{},\"baseline\":{}}", auth()).statusCode());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void trailingDataReturns400() throws Exception {
    assertEquals(400, send("compare", payload() + " {}", auth()).statusCode());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void emptyBodyReturns400() throws Exception {
    assertEquals(400, send("compare", "", auth()).statusCode());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void bodyIsLimitedBeforeParsing() throws Exception {
    var r = send("compare", " ".repeat(262145), auth());
    assertEquals(400, r.statusCode());
    assertTrue(r.body().contains("256KiB"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void anonymousOversizedBodyStillRequiresIdentity() throws Exception {
    assertEquals(401, send("compare", " ".repeat(262145), null).statusCode());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void invalidUtf8Returns400() throws Exception {
    var r =
        client.send(
            HttpRequest.newBuilder(
                    URI.create("http://127.0.0.1:" + port + "/api/operations-review/import-check"))
                .header("Authorization", auth())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofByteArray(new byte[] {(byte) 0xc3, 0x28}))
                .build(),
            HttpResponse.BodyHandlers.ofString());
    assertEquals(400, r.statusCode());
    assertTrue(r.body().contains("UTF-8"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void originalEndpointStillWorks() throws Exception {
    assertEquals(200, send("evaluate", json.writeValueAsString(sample()), auth()).statusCode());
  }
}
