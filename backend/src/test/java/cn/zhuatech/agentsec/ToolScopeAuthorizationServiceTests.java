/* Copyright 2026 上海如静知华信息科技有限公司 */
package cn.zhuatech.agentsec;

import cn.zhuatech.agentsec.service.ToolScopeAuthorizationService;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ToolScopeAuthorizationServiceTests {
    private final ToolScopeAuthorizationService service = new ToolScopeAuthorizationService();

    @Test void deniesUnapprovedAndUnconfirmedToolScope() {
        var result = service.authorize(new ToolScopeAuthorizationService.Request(
            "DEPLOY-AGENT", List.of("repo:read", "prod:delete"), List.of("repo:read"),
            true, true, false, false));
        assertEquals("DENY", result.decision());
        assertFalse(result.excessScopes().isEmpty());
    }

    @Test void allowsApprovedReadOnlyScope() {
        var result = service.authorize(new ToolScopeAuthorizationService.Request(
            "SEARCH-AGENT", List.of("docs:read"), List.of("docs:read"),
            false, false, false, false));
        assertEquals("ALLOW", result.decision());
    }
}
