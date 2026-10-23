/* 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agentsec.operations;

import jakarta.servlet.http.HttpServletRequest;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import org.springframework.http.*;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 已认证只读输入对照，未读取或修改他人业务数据。上海如静知华信息科技有限公司 · https://www.zhuatech.cn/
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@RestController("specializedOperationsComparisonController")
@RequestMapping("/api/operations-review")
public class OperationsComparisonController {
  private final OperationsComparisonService service;

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  public OperationsComparisonController(OperationsComparisonService service) {
    this.service = service;
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  private void authorize(Authentication principal) {
    if (principal == null
        || !principal.isAuthenticated()
        || principal instanceof AnonymousAuthenticationToken)
      throw new org.springframework.web.server.ResponseStatusException(
          HttpStatus.UNAUTHORIZED, "请先登录");
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @GetMapping("/comparison-contract")
  public ResponseEntity<?> contract(Authentication principal) {
    authorize(principal);
    return ok(service.contract());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @PostMapping("/compare")
  public ResponseEntity<?> compare(HttpServletRequest request, Authentication principal) {
    authorize(principal);
    return execute(request, principal, false);
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @PostMapping("/import-check")
  public ResponseEntity<?> importCheck(HttpServletRequest request, Authentication principal) {
    authorize(principal);
    return execute(request, principal, true);
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  private ResponseEntity<?> execute(
      HttpServletRequest request, Authentication principal, boolean importing) {
    try {
      byte[] bytes = request.getInputStream().readNBytes(262145);
      if (bytes.length > 262144) throw new IllegalArgumentException("请求体不能超过256KiB");
      String raw = StandardCharsets.UTF_8.newDecoder().decode(ByteBuffer.wrap(bytes)).toString();
      var result =
          new LinkedHashMap<String, Object>(
              importing ? service.importSnapshot(raw) : service.compare(raw));
      result.put("evaluatedBy", principal.getName());
      result.put("evaluatedAt", Instant.now().toString());
      result.put("correlationId", UUID.randomUUID().toString());
      return ok(OpsInput.safe(result));
    } catch (OperationsComparisonService.VersionConflict ex) {
      return error(409, ex.getMessage());
    } catch (IllegalArgumentException ex) {
      return error(400, ex.getMessage());
    } catch (java.io.IOException ex) {
      return error(400, "请求体读取失败或不是有效UTF-8");
    }
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  private ResponseEntity<?> ok(Object data) {
    return ResponseEntity.ok()
        .cacheControl(CacheControl.noStore())
        .body(Map.of("success", true, "data", data));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  private ResponseEntity<?> error(int status, String message) {
    return ResponseEntity.status(status)
        .cacheControl(CacheControl.noStore())
        .body(Map.of("success", false, "message", message));
  }
}
