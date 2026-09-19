/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agentsec.controller;

import cn.zhuatech.agentsec.common.ApiResponse;
import cn.zhuatech.agentsec.service.AgentActionChainPolicyService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@RestController
@RequestMapping("/api/agentsec/insights")
public class AgentActionChainPolicyController {
    private final AgentActionChainPolicyService service;
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public AgentActionChainPolicyController(AgentActionChainPolicyService service) { this.service = service; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/action-chain-policy")
    public ApiResponse<AgentActionChainPolicyService.ChainResult> evaluate(
            @Valid @RequestBody AgentActionChainPolicyService.ChainRequest request) {
        return ApiResponse.ok("Agent 工具链安全评估完成", service.evaluate(request));
    }
}
