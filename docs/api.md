# AgentSec API 摘要

版权所有 © 2026 上海如静知华信息科技有限公司。

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/auth/login` | 登录并获取 JWT |
| GET | `/api/admin/dashboard` | Agent 安全测试指标 |
| GET | `/api/admin/work-orders` | 红队测试任务 |
| GET | `/api/shopfloor/dashboard` | 安全测试员工作台 |
| POST | `/api/shopfloor/work-orders/{id}/reports` | 提交攻击复现和发现 |
| POST | `/api/shopfloor/attack-simulation` | 根据 Agent 能力配置评估攻击面和发布建议 |

接口只用于已授权隔离环境的防御性验证。请勿在请求、日志或测试数据中存放真实凭证和生产敏感信息。
