/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agentsec.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.List;
import java.util.Set;

/** 在 Agent 执行多步工具链之前识别权限升级、秘密外传和不可逆操作。 */
@Service
public class AgentActionChainPolicyService {
    public ChainResult evaluate(ChainRequest request) {
        List<String> blockers = new ArrayList<>();
        List<String> controls = new ArrayList<>();
        List<Integer> highRiskSteps = new ArrayList<>();
        Set<Integer> sequences = new HashSet<>();
        int riskScore = 0;

        for (ActionStep step : request.steps()) {
            int stepRisk = 0;
            if (!sequences.add(step.sequence())) blockers.add("工具步骤序号重复: " + step.sequence());
            if (!request.approvedScopes().contains(step.requiredScope())) {
                blockers.add("步骤 " + step.sequence() + " 请求未批准权限: " + step.requiredScope());
                stepRisk += 40;
            }
            if (!step.argumentsSchemaValid()) {
                blockers.add("步骤 " + step.sequence() + " 的工具参数未通过结构校验");
                stepRisk += 30;
            }
            if (step.untrustedInput() && (step.externalWrite() || step.destructive())) {
                blockers.add("步骤 " + step.sequence() + " 由不可信内容驱动高影响操作");
                stepRisk += 35;
            }
            if (step.secretAccess() && step.externalNetwork()) {
                blockers.add("步骤 " + step.sequence() + " 同时访问秘密并连接外部网络");
                stepRisk += 50;
            }
            if (step.externalNetwork() && !step.destinationAllowed()) {
                blockers.add("步骤 " + step.sequence() + " 的网络目标不在允许清单");
                stepRisk += 40;
            }
            if (step.destructive() && !step.humanApproved()) {
                blockers.add("步骤 " + step.sequence() + " 的不可逆操作未获逐次人工批准");
                stepRisk += 50;
            }
            if (step.untrustedInput()) stepRisk += 10;
            if (step.secretAccess()) stepRisk += 10;
            if (step.externalNetwork()) stepRisk += 10;
            if (step.externalWrite()) stepRisk += 15;
            if (step.destructive()) stepRisk += 25;
            if (stepRisk >= request.highRiskStepThreshold()) highRiskSteps.add(step.sequence());
            riskScore += stepRisk;
        }

        riskScore = Math.min(100, riskScore);
        controls.add("为每个工具步骤绑定主体、权限范围、目标和幂等键");
        if (!highRiskSteps.isEmpty()) controls.add("高风险步骤进入隔离执行并保存输入输出摘要");
        if (request.steps().stream().anyMatch(ActionStep::secretAccess)) controls.add("使用短期凭据代理且禁止明文进入模型上下文");
        if (request.steps().stream().anyMatch(ActionStep::destructive)) controls.add("执行前再次确认目标并准备可恢复快照");

        Decision decision = !blockers.isEmpty() ? Decision.DENY
                : riskScore >= request.reviewRiskThreshold()
                || request.steps().stream().anyMatch(step -> step.externalWrite() || step.destructive())
                ? Decision.REVIEW : Decision.ALLOW;
        return new ChainResult(decision, riskScore, List.copyOf(highRiskSteps), List.copyOf(blockers),
                List.copyOf(controls), traceDigest(request));
    }

    private String traceDigest(ChainRequest request) {
        String value = request.executionId() + "|" + request.agentCode() + "|" + request.steps().stream()
                .map(step -> step.sequence() + ":" + step.toolName() + ":" + step.requiredScope())
                .reduce((left, right) -> left + "|" + right).orElse("");
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 unavailable", exception);
        }
    }

    public record ChainRequest(@NotBlank String executionId, @NotBlank String agentCode,
            @NotEmpty Set<@NotBlank String> approvedScopes,
            @Min(1) @Max(100) int highRiskStepThreshold,
            @Min(1) @Max(100) int reviewRiskThreshold,
            @NotEmpty List<@Valid ActionStep> steps) {}
    public record ActionStep(@Min(1) int sequence, @NotBlank String toolName,
            @NotBlank String requiredScope, boolean argumentsSchemaValid,
            boolean untrustedInput, boolean secretAccess, boolean externalNetwork,
            boolean destinationAllowed, boolean externalWrite, boolean destructive,
            boolean humanApproved) {}
    public record ChainResult(Decision decision, int riskScore, List<Integer> highRiskSteps,
            List<String> blockers, List<String> controls, String traceDigest) {}
    public enum Decision { ALLOW, REVIEW, DENY }
}
