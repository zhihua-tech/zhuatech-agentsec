/* Copyright 2026 上海如静知华信息科技有限公司 */
package cn.zhuatech.agentsec.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ToolScopeAuthorizationService {
    public Result authorize(Request request) {
        Set<String> approved = new HashSet<>(request.approvedScopes());
        List<String> excessScopes = request.requestedScopes().stream()
            .filter(scope -> !approved.contains(scope)).distinct().toList();
        boolean hardDeny = !excessScopes.isEmpty()
            || request.destructiveAction() && !request.humanConfirmation();
        String decision = hardDeny ? "DENY"
            : request.externalWrite() || request.secretAccess() || request.destructiveAction() ? "REVIEW" : "ALLOW";
        List<String> actions = new ArrayList<>();
        if (!excessScopes.isEmpty()) actions.add("移除未批准工具范围：" + String.join(",", excessScopes));
        if (request.destructiveAction()) actions.add("要求逐次人工确认并记录准确目标");
        if (request.externalWrite()) actions.add("展示外部写入内容、接收方和幂等键");
        if (request.secretAccess()) actions.add("使用短期凭据代理，禁止向模型上下文返回明文");
        if (actions.isEmpty()) actions.add("允许执行只读且最小权限的工具调用");
        return new Result(request.agentCode(), decision, excessScopes,
            request.humanConfirmation(), actions);
    }

    public record Request(@NotBlank String agentCode,
                          @NotEmpty List<@NotBlank String> requestedScopes,
                          @NotEmpty List<@NotBlank String> approvedScopes,
                          boolean externalWrite, boolean destructiveAction,
                          boolean secretAccess, boolean humanConfirmation) {}
    public record Result(String agentCode, String decision, List<String> excessScopes,
                         boolean humanConfirmed, List<String> actions) {}
}
