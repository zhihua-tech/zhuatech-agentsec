/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agentsec.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtService jwt;
  private final UserDetailsService users;

  /** 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  public JwtAuthenticationFilter(JwtService jwt, UserDetailsService users) {
    this.jwt = jwt;
    this.users = users;
  }

  /** 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Override
  protected void doFilterInternal(
      HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {
    String header = req.getHeader("Authorization");
    if (header != null
        && header.startsWith("Bearer ")
        && SecurityContextHolder.getContext().getAuthentication() == null) {
      try {
        UserDetails user = users.loadUserByUsername(jwt.username(header.substring(7)));
        if (!user.isEnabled()
            || !user.isAccountNonLocked()
            || !user.isAccountNonExpired()
            || !user.isCredentialsNonExpired()) {
          SecurityContextHolder.clearContext();
          chain.doFilter(req, res);
          return;
        }
        var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
        SecurityContextHolder.getContext().setAuthentication(auth);
      } catch (JwtException | UsernameNotFoundException | IllegalArgumentException ignored) {
        SecurityContextHolder.clearContext();
      }
    }
    chain.doFilter(req, res);
  }
}
