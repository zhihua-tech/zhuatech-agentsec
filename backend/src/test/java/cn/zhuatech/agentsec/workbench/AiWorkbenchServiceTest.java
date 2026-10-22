/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agentsec.workbench;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
class AiWorkbenchServiceTest {
  private final AiWorkbenchService service = new AiWorkbenchService();

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  private Map<String, Object> sample() {
    return Map.<String, Object>ofEntries(
        Map.entry("executionId", "EXEC1"),
        Map.entry("expectedRoot", ""),
        Map.entry(
            "steps",
            List.of(
                Map.<String, Object>ofEntries(
                    Map.entry("sequence", 1),
                    Map.entry("tool", "search"),
                    Map.entry(
                        "inputDigest",
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"),
                    Map.entry(
                        "outputDigest",
                        "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb")),
                Map.<String, Object>ofEntries(
                    Map.entry("sequence", 2),
                    Map.entry("tool", "summarize"),
                    Map.entry(
                        "inputDigest",
                        "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb"),
                    Map.entry(
                        "outputDigest",
                        "cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc")))));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void computesBusinessResult() {
    var result = service.execute(sample());
    assertEquals(64, result.get("rootDigest").toString().length());
    assertEquals(false, result.get("expectedRootProvided"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void coversFeatureBoundary() {
    Map<String, Object> invalid = new HashMap<>(sample());
    invalid.put("expectedRoot", "bad");
    assertThrows(IllegalArgumentException.class, () -> service.execute(invalid));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void coversAdditionalBoundaryOne() {
    var r = new HashMap<>(sample());
    r.put("expectedRoot", service.execute(r).get("rootDigest"));
    assertEquals(true, service.execute(r).get("matchesExpected"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void coversAdditionalBoundaryTwo() {
    var r = new HashMap<>(sample());
    var steps = new ArrayList<>((List<?>) r.get("steps"));
    Collections.reverse(steps);
    r.put("steps", steps);
    assertThrows(IllegalArgumentException.class, () -> service.execute(r));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void deterministicAndDoesNotMutateInput() {
    var input = sample();
    String before = input.toString();
    assertEquals(service.execute(input), service.execute(input));
    assertEquals(before, input.toString());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void rejectsNull() {
    assertThrows(IllegalArgumentException.class, () -> service.execute(null));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void rejectsMissingFields() {
    assertThrows(IllegalArgumentException.class, () -> service.execute(Map.of()));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void rejectsUnknownFields() {
    var input = new HashMap<>(sample());
    input.put("__unknown", true);
    assertThrows(IllegalArgumentException.class, () -> service.execute(input));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void rejectsOversizedText() {
    assertThrows(
        IllegalArgumentException.class,
        () -> service.execute(Map.of("payload", "x".repeat(64001))));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void rejectsDeepInput() {
    Map<String, Object> v = Map.of("x", 1);
    for (int i = 0; i < 14; i++) v = Map.of("x", v);
    final var input = v;
    assertThrows(IllegalArgumentException.class, () -> service.execute(input));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void httpRequiresAuthentication() throws Exception {
    var mvc = MockMvcBuilders.standaloneSetup(new AiWorkbenchController(service)).build();
    mvc.perform(
            post("/api/workbench/execution-trace-chain")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"executionId\":\"EXEC1\",\"expectedRoot\":\"\",\"steps\":[{\"sequence\":1,\"tool\":\"search\",\"inputDigest\":\"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\",\"outputDigest\":\"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb\"},{\"sequence\":2,\"tool\":\"summarize\",\"inputDigest\":\"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb\",\"outputDigest\":\"cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc\"}]}"))
        .andExpect(status().isUnauthorized());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void httpAllowsAuthenticatedComputation() throws Exception {
    var mvc = MockMvcBuilders.standaloneSetup(new AiWorkbenchController(service)).build();
    mvc.perform(
            post("/api/workbench/execution-trace-chain")
                .principal(new TestingAuthenticationToken("test-user", "unused", "ROLE_OPERATOR"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"executionId\":\"EXEC1\",\"expectedRoot\":\"\",\"steps\":[{\"sequence\":1,\"tool\":\"search\",\"inputDigest\":\"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\",\"outputDigest\":\"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb\"},{\"sequence\":2,\"tool\":\"summarize\",\"inputDigest\":\"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb\",\"outputDigest\":\"cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc\"}]}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void httpRejectsInvalidInput() throws Exception {
    var mvc = MockMvcBuilders.standaloneSetup(new AiWorkbenchController(service)).build();
    mvc.perform(
            post("/api/workbench/execution-trace-chain")
                .principal(new TestingAuthenticationToken("test-user", "unused", "ROLE_OPERATOR"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void rejectsAnonymousAuthenticationObject() {
    var actor =
        new AnonymousAuthenticationToken(
            "test-key",
            "anonymous",
            List.of(
                new org.springframework.security.core.authority.SimpleGrantedAuthority(
                    "ROLE_ANONYMOUS")));
    assertEquals(
        401, new AiWorkbenchController(service).run(sample(), actor).getStatusCode().value());
  }
}
