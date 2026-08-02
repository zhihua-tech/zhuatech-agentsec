/* Copyright 2026 上海如静知华信息科技有限公司 */
package cn.zhuatech.agentsec.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** 对 Agent 的提示注入、越权工具、敏感信息和持久化风险进行规则化演练评分。 */
@Service
public class AgentAttackSimulationService {
    public record Request(@NotBlank String agentName, @NotEmpty List<String> attackTypes,
                          boolean toolWriteEnabled, boolean secretAccess,
                          boolean sandboxEnabled, boolean humanApproval, boolean memoryEnabled) {}
    public record Result(String agentName, int riskScore, String severity,
                         String releaseDecision, List<String> findings, List<String> mitigations) {}

    public Result simulate(Request request) {
        int score = Math.min(35, request.attackTypes().size() * 8);
        List<String> findings = new ArrayList<>();
        List<String> mitigations = new ArrayList<>();
        if (request.toolWriteEnabled()) { score += 20; findings.add("Agent 具备有副作用的工具写权限"); mitigations.add("对写操作增加参数校验、审批与幂等保护"); }
        if (request.secretAccess()) { score += 25; findings.add("执行上下文可访问敏感凭证"); mitigations.add("使用短期范围凭证并隔离密钥读取能力"); }
        if (!request.sandboxEnabled()) { score += 20; findings.add("工具执行未启用隔离环境"); mitigations.add("启用文件、网络和进程级沙箱"); }
        if (!request.humanApproval()) { score += 15; findings.add("高风险动作缺少人工确认"); mitigations.add("为转账、删除、发布和外发动作增加人工门禁"); }
        if (request.memoryEnabled() && request.attackTypes().stream().anyMatch(x -> x.toLowerCase(Locale.ROOT).contains("memory"))) { score += 12; findings.add("长期记忆可能被污染并跨会话传播"); mitigations.add("对记忆写入做来源标注、清洗与过期控制"); }
        score = Math.min(score, 100);
        String severity = score >= 75 ? "CRITICAL" : score >= 50 ? "HIGH" : score >= 25 ? "MEDIUM" : "LOW";
        String decision = score >= 75 ? "BLOCK" : score >= 40 ? "RETEST" : "PASS";
        if (findings.isEmpty()) findings.add("当前配置未发现高影响暴露面");
        if (mitigations.isEmpty()) mitigations.add("保留回归测试并监控工具调用异常");
        return new Result(request.agentName(), score, severity, decision, findings, mitigations);
    }
}
