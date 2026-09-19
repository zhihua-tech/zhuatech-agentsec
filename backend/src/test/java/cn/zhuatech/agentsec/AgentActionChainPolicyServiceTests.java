/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agentsec;

import cn.zhuatech.agentsec.service.AgentActionChainPolicyService;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
class AgentActionChainPolicyServiceTests {
    private final AgentActionChainPolicyService service = new AgentActionChainPolicyService();

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void allowsTrustedReadOnlyChain() {
        var result = service.evaluate(request(step(1, "docs.search", "docs:read",
                false, false, false, true, false, false, false)));
        assertThat(result.decision()).isEqualTo(AgentActionChainPolicyService.Decision.ALLOW);
        assertThat(result.traceDigest()).hasSize(64);
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void reviewsApprovedExternalWrite() {
        var result = service.evaluate(request(step(1, "ticket.create", "ticket:write",
                false, false, true, true, true, false, true)));
        assertThat(result.decision()).isEqualTo(AgentActionChainPolicyService.Decision.REVIEW);
        assertThat(result.blockers()).isEmpty();
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void deniesSecretExfiltrationAndUnapprovedDestructiveAction() {
        var result = service.evaluate(request(step(1, "shell.run", "prod:admin",
                true, true, true, false, true, true, false)));
        assertThat(result.decision()).isEqualTo(AgentActionChainPolicyService.Decision.DENY);
        assertThat(result.blockers()).hasSize(5);
        assertThat(result.highRiskSteps()).containsExactly(1);
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private AgentActionChainPolicyService.ChainRequest request(AgentActionChainPolicyService.ActionStep step) {
        return new AgentActionChainPolicyService.ChainRequest("RUN-100", "OPS-AGENT",
                Set.of("docs:read", "ticket:write"), 30, 20, List.of(step));
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private AgentActionChainPolicyService.ActionStep step(int sequence, String tool, String scope,
            boolean untrusted, boolean secret, boolean network, boolean destinationAllowed,
            boolean write, boolean destructive, boolean approved) {
        return new AgentActionChainPolicyService.ActionStep(sequence, tool, scope, true,
                untrusted, secret, network, destinationAllowed, write, destructive, approved);
    }
}
