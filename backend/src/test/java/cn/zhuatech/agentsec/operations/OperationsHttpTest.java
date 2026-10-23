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
 * 真实HTTP认证、核算与错误边界。上海如静知华信息科技有限公司 · https://www.zhuatech.cn/
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OperationsHttpTest {
  @LocalServerPort int port;
  @Autowired OperationsReviewService service;
  @Autowired cn.zhuatech.agentsec.security.JwtService jwt;
  private final HttpClient client = HttpClient.newHttpClient();
  private final JsonMapper json = new JsonMapper();

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  private String auth() {
    return "Bearer " + jwt.generate("admin");
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  private String payload() {
    return json.writeValueAsString(service.schema().get("sample"));
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
  @Test
  void schemaRequiresIdentity() throws Exception {
    assertEquals(401, send("schema", null, null).statusCode());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void evaluationRequiresIdentity() throws Exception {
    assertEquals(401, send("evaluate", payload(), null).statusCode());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void invalidCredentialRejected() throws Exception {
    assertEquals(401, send("evaluate", payload(), "Bearer invalid").statusCode());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void authenticatedSchemaIsCorrect() throws Exception {
    var r = send("schema", null, auth());
    assertEquals(200, r.statusCode(), r.body());
    assertEquals(service.schema().get("title"), json.readTree(r.body()).at("/data/title").asText());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void realEvaluationReturnsComputedDataAndNoStore() throws Exception {
    var r = send("evaluate", payload(), auth());
    assertEquals(200, r.statusCode(), r.body());
    assertEquals(1, json.readTree(r.body()).at("/data/recordCount").asInt());
    assertFalse(json.readTree(r.body()).at("/data/automaticWrite").asBoolean());
    assertTrue(r.headers().firstValue("Cache-Control").orElse("").contains("no-store"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void malformedJsonIs400() throws Exception {
    assertEquals(400, send("evaluate", "{bad", auth()).statusCode());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void repeatedKeysAre400() throws Exception {
    var r =
        send(
            "evaluate", "{\"asOf\":\"2026-10-23\",\"asOf\":\"2026-10-23\",\"records\":[]}", auth());
    assertEquals(400, r.statusCode());
    assertTrue(r.body().contains("JSON"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void trailingDataIs400() throws Exception {
    assertEquals(400, send("evaluate", payload() + " {}", auth()).statusCode());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void arrayRootIs400() throws Exception {
    assertEquals(400, send("evaluate", "[]", auth()).statusCode());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void missingFieldsAre400() throws Exception {
    assertEquals(
        400, send("evaluate", "{\"asOf\":\"2026-10-23\",\"records\":[{}]}", auth()).statusCode());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void duplicateIdsRejectWholeBatch() throws Exception {
    var map = OpsInput.parse(payload());
    Object first = ((List<?>) map.get("records")).getFirst();
    map.put("records", List.of(first, first));
    var r = send("evaluate", json.writeValueAsString(map), auth());
    assertEquals(400, r.statusCode());
    assertTrue(r.body().contains("重复"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void bodyIsLimitedBeforeJsonParsing() throws Exception {
    var r = send("evaluate", " ".repeat(262145), auth());
    assertEquals(400, r.statusCode());
    assertTrue(r.body().contains("256KiB"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void emptyBodyIs400() throws Exception {
    assertEquals(400, send("evaluate", "", auth()).statusCode());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void invalidUtf8Is400() throws Exception {
    var req =
        HttpRequest.newBuilder(
                URI.create("http://127.0.0.1:" + port + "/api/operations-review/evaluate"))
            .header("Authorization", auth())
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofByteArray(new byte[] {(byte) 0xc3, (byte) 0x28}))
            .build();
    var r = client.send(req, HttpResponse.BodyHandlers.ofString());
    assertEquals(400, r.statusCode());
    assertTrue(r.body().contains("UTF-8"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void anonymousOversizedBodyStillRequiresIdentity() throws Exception {
    assertEquals(401, send("evaluate", " ".repeat(262145), null).statusCode());
  }
}
