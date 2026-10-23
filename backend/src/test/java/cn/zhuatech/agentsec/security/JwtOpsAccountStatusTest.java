/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agentsec.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;

/**
 * 现有令牌不能绕过停用、锁定和过期状态。上海如静知华信息科技有限公司 · https://www.zhuatech.cn/
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
class JwtOpsAccountStatusTest {
  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @AfterEach
  void clear() {
    SecurityContextHolder.clearContext();
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @ParameterizedTest
  @ValueSource(strings = {"disabled", "locked", "accountExpired", "credentialsExpired"})
  void inactiveAccountCannotReusePreviouslyIssuedToken(String reason) throws Exception {
    var jwt = mock(JwtService.class);
    var users = mock(UserDetailsService.class);
    when(jwt.username("test-token")).thenReturn("user");
    var builder = User.withUsername("user").password("not-a-login-password").roles("VIEWER");
    switch (reason) {
      case "disabled" -> builder.disabled(true);
      case "locked" -> builder.accountLocked(true);
      case "accountExpired" -> builder.accountExpired(true);
      case "credentialsExpired" -> builder.credentialsExpired(true);
      default -> throw new IllegalArgumentException();
    }
    when(users.loadUserByUsername("user")).thenReturn(builder.build());
    var request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer test-token");
    int[] called = {0};
    new JwtAuthenticationFilter(jwt, users)
        .doFilter(
            request,
            new MockHttpServletResponse(),
            (r, s) -> {
              called[0]++;
              assertNull(SecurityContextHolder.getContext().getAuthentication());
            });
    assertEquals(1, called[0]);
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void enabledAccountStillAuthenticates() throws Exception {
    var jwt = mock(JwtService.class);
    var users = mock(UserDetailsService.class);
    when(jwt.username("test-token")).thenReturn("user");
    when(users.loadUserByUsername("user"))
        .thenReturn(
            User.withUsername("user").password("not-a-login-password").roles("VIEWER").build());
    var r = new MockHttpServletRequest();
    r.addHeader("Authorization", "Bearer test-token");
    new JwtAuthenticationFilter(jwt, users)
        .doFilter(
            r,
            new MockHttpServletResponse(),
            (request, response) ->
                assertEquals(
                    "user", SecurityContextHolder.getContext().getAuthentication().getName()));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void emptyJwtDoesNotTriggerServerError() throws Exception {
    var jwt = mock(JwtService.class);
    var users = mock(UserDetailsService.class);
    when(jwt.username("")).thenThrow(new IllegalArgumentException("empty jwt"));
    var request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer ");
    new JwtAuthenticationFilter(jwt, users)
        .doFilter(
            request,
            new MockHttpServletResponse(),
            (r, s) -> assertNull(SecurityContextHolder.getContext().getAuthentication()));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void deletedUserCannotReuseToken() throws Exception {
    var jwt = mock(JwtService.class);
    var users = mock(UserDetailsService.class);
    when(jwt.username("test-token")).thenReturn("deleted");
    when(users.loadUserByUsername("deleted")).thenThrow(new UsernameNotFoundException("deleted"));
    var request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer test-token");
    new JwtAuthenticationFilter(jwt, users)
        .doFilter(
            request,
            new MockHttpServletResponse(),
            (r, s) -> assertNull(SecurityContextHolder.getContext().getAuthentication()));
  }
}
