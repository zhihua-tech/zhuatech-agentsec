# ZhuaTech AgentSec


## 本次升级：工具批准版本与失效时间核对

核对请求绑定版本与批准版本是否一致、授权到期及不可逆动作的双人批准，版本不符不得沿用旧批准。不签发或执行工具令牌。

新增已认证的结构化录入工作台（`/operations.html`），支持批量核算、逐条结果依据、分级复核和CSV/完整JSON导出。结果由真实后端算法计算，不是预置演示数字。所有数值精确传递，不调用外部模型。

这是**只读核算**：不写库、不审批、不自动执行财务、签署、销毁或设备控制。输入条件满足不是执行授权。[算法、输入边界与接口说明](docs/OPERATIONS-REVIEW.md)。
## 本次功能更新：Agent轨迹链摘要校验

按连续步骤和前序摘要构建SHA-256链，覆盖工具名、输入摘要及输出摘要，并比对外部保存的预期根摘要。链摘要不是签名，不能证明数据来源。 登录后从「业务计算工具」进入，支持真实后端校验与结果导出。详见[接口、示例与边界说明](docs/AI_BUSINESS_UPGRADE_20261023.md)。本次不改变既有许可及联系方式。

[简体中文](README.md) | [English](README.en.md)

## 把 Agent 当成真实攻击目标来测试

提示注入只是开始。企业 Agent 一旦具备工具写权限、敏感凭证、长期记忆和自主执行能力，风险会沿着模型、工具和业务系统连续传播。ZhuaTech AgentSec 提供威胁建模、攻击用例、隔离执行、轨迹证据、修复复测和发布门禁的一体化参考实现。

这是**上海如静知华信息科技有限公司**发布的非商业社区源码项目。公司官网：[知华科技](https://www.zhuatech.cn/)。

## 安全测试闭环

```text
Agent 资产登记
   ↓  模型 / Tool / Memory / Credential / Sandbox
威胁建模 → 攻击计划 → 隔离执行 → 证据复现 → 修复复测 → 发布决策
```

覆盖的测试面：

- 直接与间接提示注入、系统提示泄漏和指令优先级绕过
- 高影响工具越权、参数篡改、重复执行和人工审批绕过
- 密钥、个人信息和内部资料泄漏
- 长期记忆污染、跨会话传播和来源丢失
- 文件、网络、进程隔离与沙箱逃逸

`POST /api/shopfloor/attack-simulation` 会依据 Agent 权限和测试配置生成可解释风险分数、发现和缓解建议，可直接用于接口联调与自动化测试。

## 界面证据

![知华科技 AgentSec 安全测试管理端](docs/images/agentsec-security-dashboard.png)

管理端围绕测试任务、攻击覆盖、有效发现、阻断发布和实验环境运行状态组织信息。

![知华科技 AgentSec 移动测试工作台](docs/images/agentsec-analyst-h5.png)

H5 端用于现场执行用例、记录复现条件、查看隔离环境和提交风险升级。

## 运行社区演示

```bash
cd frontend
npm install
npm run dev:demo
```

访问 `http://localhost:5173`，安全负责人账号 `planner / Demo@2026`，测试员账号 `operator / Demo@2026`。Java 后端包名为 `cn.zhuatech.agentsec`，采用 Spring Boot + JWT + JPA + Flyway + MySQL 8；前端采用 Vue 3 + Vite。详见 [API](docs/api.md)、[架构](docs/architecture.md)和[部署](deploy/README.md)。

> 安全提醒：演示用例只适用于获得授权的隔离测试环境。仓库不包含真实攻击载荷、客户数据或凭证，请勿对未授权系统开展测试。

## 非商业许可

本工程仅允许个人学习、研究和非商业技术交流，**不得用于商业用途**。企业内部使用、生产部署、SaaS、项目交付、商业安全测试、收费培训、咨询实施、品牌替换或再分发，必须取得上海如静知华信息科技有限公司书面授权。以 [LICENSE](LICENSE) 的完整条款为准。

需要 Agent 安全评估、AI 红队平台、企业私有化或深度开发定制，请访问[知华科技官网](https://www.zhuatech.cn/)或扫码联系：

| Agent 安全咨询 | 定制与商业授权 |
| --- | --- |
| ![微信二维码一](docs/images/zhuatech-wechat-consulting.png) | ![微信二维码二](docs/images/zhuatech-wechat-consulting-2.png) |

关键词：Agent Security、AI Agent 安全、AI 红队、提示注入测试、Agent 工具越权、Agent 沙箱、Java 安全平台、知华科技。

## Agent 工具权限门禁

新增 `POST /api/agentsec/insights/tool-scope-authorization`。运行前比对 Agent 请求权限与已批准权限，结合外部调用、破坏性操作、敏感信息访问和人工确认状态，返回 `ALLOW`、`REVIEW` 或 `DENY` 并列出越权范围，降低工具调用越权和不可逆操作风险。

## Agent 多步工具链风险控制

`POST /api/agentsec/insights/action-chain-policy` 对完整工具调用链检查权限升级、不可信输入、秘密与外部网络组合、外部写入、不可逆操作和人工确认，返回风险分、高风险步骤与 `ALLOW / REVIEW / DENY`。详见[工具链安全门禁](docs/ENTERPRISE_ACTION_CHAIN_POLICY.md)。

## 输入对照与版本化快照

在既有「工具批准版本与失效时间核对」基础上，新增 `/comparison.html`：基准与候选两组数据都由当前真实算法重算，按编号展示新增、移除、逐字段变化和精确数值差，支持筛选、分页和全量导出。原核算入口可导出输入快照，对照页经后端完整校验后整体导入；失败不覆盖当前输入。

快照区分项目、字段契约及算法版本。建议较宽松不代表风险已下降，输入摘要不是签名；操作人和时间仅为响应元数据，不是持久化审计。本次未新增模型调用或业务写入，也不代表全系统生产认证。详见[输入对照操作与接口说明](docs/INPUT-COMPARISON.md)。
