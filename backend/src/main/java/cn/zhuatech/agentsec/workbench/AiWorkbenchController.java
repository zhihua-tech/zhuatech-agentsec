/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agentsec.workbench;

import java.util.*;
import org.springframework.http.*;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Authenticated, non-persistent computation over caller-supplied data. 上海如静知华信息科技有限公司 ·
 * https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@RestController
@RequestMapping("/api/workbench")
public class AiWorkbenchController {
  private final AiWorkbenchService service;

  /** 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  public AiWorkbenchController(AiWorkbenchService service) {
    this.service = service;
  }

  /**
   * No repository lookup, tool execution or third-party transmission.
   * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
   */
  @PostMapping("/execution-trace-chain")
  public ResponseEntity<Map<String, Object>> run(
      @RequestBody Map<String, Object> request, Authentication actor) {
    if (actor == null || !actor.isAuthenticated() || actor instanceof AnonymousAuthenticationToken)
      return ResponseEntity.status(401).body(Map.of("success", false, "message", "请先登录"));
    try {
      return ResponseEntity.ok(
          Map.of("success", true, "message", "计算完成；未修改业务数据", "data", service.execute(request)));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
    }
  }
}
