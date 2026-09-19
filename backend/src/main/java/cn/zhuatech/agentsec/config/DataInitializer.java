/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agentsec.config;

import cn.zhuatech.agentsec.model.*;
import cn.zhuatech.agentsec.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Configuration
public class DataInitializer {
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Bean
    CommandLineRunner seed(OperatingUnitRepository operatingUnits, WorkRecordRepository orders,
                           ResourceRegisterRepository resources, ReviewRecordRepository reviewRecords,
                           UserRepository users, PasswordEncoder encoder) {
        return args -> {
            if (operatingUnits.count() > 0) return;
            OperatingUnit primaryUnit = operatingUnits.save(new OperatingUnit("AGENTSEC-RED", "Agent 红队组", "AI 安全实验室", 180));
            OperatingUnit secondaryUnit = operatingUnits.save(new OperatingUnit("AGENTSEC-TOOL", "工具安全组", "应用安全中心", 120));
            OperatingUnit tertiaryUnit = operatingUnits.save(new OperatingUnit("AGENTSEC-EVAL", "评测与复现组", "质量工程部", 96));

            WorkRecord t1 = orders.save(new WorkRecord("RED-260801-018", "FINANCE-AGENT", "财务 Agent 越权工具攻击演练", primaryUnit, 24, 16, 1, LocalDate.now().plusDays(1), WorkRecord.Status.RUNNING, "CRITICAL"));
            WorkRecord t2 = orders.save(new WorkRecord("RED-260801-021", "SERVICE-AGENT", "客服 Agent 提示注入回归", primaryUnit, 18, 8, 0, LocalDate.now().plusDays(1), WorkRecord.Status.RUNNING, "HIGH"));
            WorkRecord t3 = orders.save(new WorkRecord("RED-260802-006", "DEV-AGENT", "研发 Agent 沙箱逃逸验证", secondaryUnit, 12, 0, 0, LocalDate.now().plusDays(3), WorkRecord.Status.RELEASED, "CRITICAL"));
            WorkRecord t4 = orders.save(new WorkRecord("RED-260728-015", "HR-AGENT", "招聘助手隐私泄漏复测", tertiaryUnit, 20, 20, 1, LocalDate.now(), WorkRecord.Status.COMPLETED, "MEDIUM"));

            resources.saveAll(List.of(
                new ResourceRegister("LAB-INJECT-03", "提示注入用例库", primaryUnit, ResourceRegister.Status.RUNNING, 92),
                new ResourceRegister("LAB-SANDBOX-02", "隔离工具执行池", secondaryUnit, ResourceRegister.Status.IDLE, 81),
                new ResourceRegister("LAB-TRACE-05", "Agent 轨迹采集器", tertiaryUnit, ResourceRegister.Status.RUNNING, 95),
                new ResourceRegister("LAB-SECRET-08", "凭证泄漏探针", primaryUnit, ResourceRegister.Status.ALARM, 61)
            ));
            reviewRecords.saveAll(List.of(
                new ReviewRecord("FIND-260801-032", t1, "转账工具越权复现", 6, 0, ReviewRecord.Result.PASSED, "何谨"),
                new ReviewRecord("FIND-260801-011", t2, "间接提示注入验证", 3, 0, ReviewRecord.Result.PASSED, "陆遥"),
                new ReviewRecord("FIND-260801-018", t4, "敏感字段脱敏复核", 5, 1, ReviewRecord.Result.FAILED, "何谨"),
                new ReviewRecord("FIND-260802-003", t3, "沙箱边界复测", 4, 0, ReviewRecord.Result.PENDING, "陆遥")
            ));
            String demo = encoder.encode("Demo@2026");
            users.saveAll(List.of(
                new UserAccount("operator", demo, "陆遥", UserAccount.Role.DOMAIN_USER, "AGENTSEC-RED"),
                new UserAccount("planner", demo, "何谨", UserAccount.Role.DOMAIN_OPERATOR, null),
                new UserAccount("quality", demo, "顾清", UserAccount.Role.QUALITY, null),
                new UserAccount("admin", encoder.encode("ZhuaTech@2026"), "系统管理员", UserAccount.Role.ADMIN, null)
            ));
        };
    }
}
