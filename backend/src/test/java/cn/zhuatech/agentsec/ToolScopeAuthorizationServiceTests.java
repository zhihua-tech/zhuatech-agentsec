/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agentsec;

import cn.zhuatech.agentsec.service.ToolScopeAuthorizationService;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
class ToolScopeAuthorizationServiceTests {
    private final ToolScopeAuthorizationService service = new ToolScopeAuthorizationService();

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void deniesUnapprovedAndUnconfirmedToolScope() {
        var result = service.authorize(new ToolScopeAuthorizationService.Request(
            "DEPLOY-AGENT", List.of("repo:read", "prod:delete"), List.of("repo:read"),
            true, true, false, false));
        assertEquals("DENY", result.decision());
        assertFalse(result.excessScopes().isEmpty());
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void allowsApprovedReadOnlyScope() {
        var result = service.authorize(new ToolScopeAuthorizationService.Request(
            "SEARCH-AGENT", List.of("docs:read"), List.of("docs:read"),
            false, false, false, false));
        assertEquals("ALLOW", result.decision());
    }
}
