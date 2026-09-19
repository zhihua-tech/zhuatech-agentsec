/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agentsec.controller;

import cn.zhuatech.agentsec.common.ApiResponse;
import cn.zhuatech.agentsec.service.AgentActionChainPolicyService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agentsec/insights")
public class AgentActionChainPolicyController {
    private final AgentActionChainPolicyService service;
    public AgentActionChainPolicyController(AgentActionChainPolicyService service) { this.service = service; }
    @PostMapping("/action-chain-policy")
    public ApiResponse<AgentActionChainPolicyService.ChainResult> evaluate(
            @Valid @RequestBody AgentActionChainPolicyService.ChainRequest request) {
        return ApiResponse.ok("Agent 工具链安全评估完成", service.evaluate(request));
    }
}
