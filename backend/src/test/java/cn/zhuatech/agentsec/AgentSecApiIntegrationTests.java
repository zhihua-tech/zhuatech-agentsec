/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agentsec;
import org.junit.jupiter.api.*; import org.springframework.beans.factory.annotation.Autowired; import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc; import org.springframework.boot.test.context.SpringBootTest; import org.springframework.http.MediaType; import org.springframework.test.web.servlet.MockMvc; import java.util.regex.*; import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*; import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@SpringBootTest @AutoConfigureMockMvc class AgentSecApiIntegrationTests {
    @Autowired MockMvc mvc; private String operatorToken; private String plannerToken;
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @BeforeEach void login()throws Exception{operatorToken=token("operator","Demo@2026");plannerToken=token("planner","Demo@2026");}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private String token(String u,String p)throws Exception{String json=mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("{\"username\":\""+u+"\",\"password\":\""+p+"\"}")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();Matcher matcher=Pattern.compile("\\\"token\\\":\\\"([^\\\"]+)\\\"").matcher(json);if(!matcher.find())throw new AssertionError("登录响应中缺少 token");return matcher.group(1);}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void operatorCanReadShopfloorDashboard()throws Exception{mvc.perform(get("/api/shopfloor/dashboard").header("Authorization","Bearer "+operatorToken)).andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true)).andExpect(jsonPath("$.data.metrics[0].label").value("Agent 测试用例"));}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void plannerCanReadWorkRecords()throws Exception{mvc.perform(get("/api/admin/work-orders").header("Authorization","Bearer "+plannerToken)).andExpect(status().isOk()).andExpect(jsonPath("$.data.length()").value(4));}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void operatorCanSubmitProductionReport()throws Exception{mvc.perform(post("/api/shopfloor/work-orders/1/reports").header("Authorization","Bearer "+operatorToken).contentType(MediaType.APPLICATION_JSON).content("{\"operationName\":\"攻击复现\",\"goodQty\":2,\"defectQty\":1,\"remark\":\"轨迹已归档\"}")).andExpect(status().isOk()).andExpect(jsonPath("$.message").value("测试发现已提交")).andExpect(jsonPath("$.data.completedQty").value(18));}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void operatorCanRunAttackSimulation()throws Exception{mvc.perform(post("/api/shopfloor/attack-simulation").header("Authorization","Bearer "+operatorToken).contentType(MediaType.APPLICATION_JSON).content("{\"agentName\":\"财务Agent\",\"attackTypes\":[\"prompt-injection\",\"memory-poisoning\"],\"toolWriteEnabled\":true,\"secretAccess\":true,\"sandboxEnabled\":false,\"humanApproval\":false,\"memoryEnabled\":true}")).andExpect(status().isOk()).andExpect(jsonPath("$.data.severity").value("CRITICAL")).andExpect(jsonPath("$.data.releaseDecision").value("BLOCK"));}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void anonymousRequestIsDenied()throws Exception{mvc.perform(get("/api/admin/dashboard")).andExpect(status().isUnauthorized());}
}
