/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agentsec.controller;

import cn.zhuatech.agentsec.common.ApiResponse;
import cn.zhuatech.agentsec.service.ToolScopeAuthorizationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@RestController
@RequestMapping("/api/agentsec/insights")
public class ToolScopeAuthorizationController {
    private final ToolScopeAuthorizationService service;
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public ToolScopeAuthorizationController(ToolScopeAuthorizationService service) { this.service = service; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/tool-scope-authorization")
    public ApiResponse<ToolScopeAuthorizationService.Result> authorize(
        @Valid @RequestBody ToolScopeAuthorizationService.Request request) {
        return ApiResponse.ok(service.authorize(request));
    }
}
