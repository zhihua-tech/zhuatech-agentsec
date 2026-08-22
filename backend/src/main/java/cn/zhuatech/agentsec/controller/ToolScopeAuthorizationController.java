/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agentsec.controller;

import cn.zhuatech.agentsec.common.ApiResponse;
import cn.zhuatech.agentsec.service.ToolScopeAuthorizationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agentsec/insights")
public class ToolScopeAuthorizationController {
    private final ToolScopeAuthorizationService service;
    public ToolScopeAuthorizationController(ToolScopeAuthorizationService service) { this.service = service; }
    @PostMapping("/tool-scope-authorization")
    public ApiResponse<ToolScopeAuthorizationService.Result> authorize(
        @Valid @RequestBody ToolScopeAuthorizationService.Request request) {
        return ApiResponse.ok(service.authorize(request));
    }
}
