# Agent轨迹链摘要校验

知华科技（上海如静知华信息科技有限公司） · https://www.zhuatech.cn/

## 本次实现

按连续步骤和前序摘要构建SHA-256链，覆盖工具名、输入摘要及输出摘要，并比对外部保存的预期根摘要。链摘要不是签名，不能证明数据来源。

管理端与业务工作台登录后右下角「业务计算工具」进入。输入是可编辑的 JSON；提供合成示例、校验、加载反馈、错误提示、前100行预览和完整 JSON 导出。未登录时不显示入口；演示令牌不能调用。

## 接口 / API

`POST /api/workbench/execution-trace-chain`

- `Content-Type: application/json`；使用现有登录返回的 Bearer JWT，不新增默认账号。
- 成功200：`{success:true,message,data}`；无有效身份401；参数错误400。现有安全过滤器继续生效。
- 所有已认证账号均可对自行提交的数据运算；不查询业务库，因此不授予新的业务记录读写权限。调用方必须自行取得输入数据的处理权限。
- 严格拒绝未知字段、重复业务编号、非法日期及越界数值。最大深度12、节点10000、单字段64000字符、累计128000字符、通用数组最多500项；各业务还有限制，见 `WorkbenchInput` 和 `AiWorkbenchService`。
- 应在生产网关配置128 KiB请求体与速率限制；当前业务校验发生在JSON解析之后，不把它当作传输层防护。

### 请求字段

| 字段 | 类型 |
| --- | --- |
| `executionId` | string |
| `expectedRoot` | string |
| `steps` | array |

所有字段必填，空数组仅在示例/服务允许的场景使用。日期采用 ISO-8601；字段单位和输出算法见服务代码及以下示例。

```json
{
  "executionId": "EXEC1",
  "expectedRoot": "",
  "steps": [
    {
      "sequence": 1,
      "tool": "search",
      "inputDigest": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
      "outputDigest": "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb"
    },
    {
      "sequence": 2,
      "tool": "summarize",
      "inputDigest": "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
      "outputDigest": "cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc"
    }
  ]
}
```

### 调用示例

```sh
# 将上方请求保存为 request.json；TOKEN 为自己登录获得的短期令牌。
curl --fail-with-body -X POST "$API_BASE/api/workbench/execution-trace-chain" \\
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \\
  --data-binary @request.json
```

## 计算口径与边界

最多500步，必须从1连续且按顺序提交；输入输出摘要均为64位小写十六进制。初始链为SHA-256(executionId)，每步覆盖前序摘要、序号、工具和输入输出摘要。预期根摘要可为空；为空时matchesExpected=false。仅在预期根被独立可信保存时比较才有意义，不提供签名、远程证明或不可抵赖性。

## 复核与限制

这是已经实现并经过单元及控制器测试的本地算法，不是待对接占位接口。结果需要业务人员复核。不会调用外部模型、不修改或持久化业务记录，不自动审批、执行工具、通知或改变生产状态。输入与结果只在当前页面内存中，刷新/退出登录后不保留；需要时手动导出。不要输入未经授权的生产敏感数据。

新增 `AiWorkbenchServiceTest` 的14项测试覆盖业务结果、两项领域边界、参数边界、确定性/不修改输入、空请求、未知字段、长度/嵌套限制及HTTP认证、成功与错误；另有 `AiWorkbenchSecurityIntegrationTest` 的4项测试经过实际安全过滤器验证有效JWT、匿名、无效令牌和参数错误。集成环境使用H2，测试令牌由应用测试上下文签发；不添加运行时账号或密钥。运行 `cd backend && mvn test` 和 `cd frontend && npm run build`。真实MySQL全流程、容量与真实模型验收不在这些测试结论之内。

## English summary

**Agent execution trace hash chain.** Builds a SHA-256 execution trace chain covering sequence, tool name and input/output digests, then compares an independently retained expected root. Hashes are not signatures. Use the sample above with the documented POST endpoint. Validation errors are explicit; no model invocation, database update, automated approval or tool execution is performed. JSON results can be exported. Existing licensing terms remain unchanged.

Commercial licensing or customization: [han@zhuatech.cn](mailto:han@zhuatech.cn), [jack@zhuatech.cn](mailto:jack@zhuatech.cn), [WhatsApp +86 17521234993](https://wa.me/8617521234993).

商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。中文README原有两个微信二维码继续保留。
