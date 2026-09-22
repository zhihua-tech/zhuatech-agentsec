/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agentsec.config;
import cn.zhuatech.agentsec.repository.UserRepository; import cn.zhuatech.agentsec.security.JwtAuthenticationFilter; import org.springframework.beans.factory.annotation.Value; import org.springframework.context.annotation.*; import org.springframework.http.HttpMethod; import org.springframework.security.authentication.AuthenticationManager; import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration; import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; import org.springframework.security.config.annotation.web.builders.HttpSecurity; import org.springframework.security.config.http.SessionCreationPolicy; import org.springframework.security.core.userdetails.*; import org.springframework.security.crypto.bcrypt.*; import org.springframework.security.crypto.password.PasswordEncoder; import org.springframework.security.web.SecurityFilterChain; import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; import org.springframework.web.cors.*; import java.util.List;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Configuration @EnableMethodSecurity public class SecurityConfig {
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Bean UserDetailsService userDetailsService(UserRepository repo){return username->repo.findByUsername(username).map(u->User.withUsername(u.getUsername()).password(u.getPassword()).roles(u.getRole().name()).disabled(!u.isEnabled()).build()).orElseThrow(()->new UsernameNotFoundException("用户不存在"));}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Bean AuthenticationManager authenticationManager(AuthenticationConfiguration cfg)throws Exception{return cfg.getAuthenticationManager();}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Bean CorsConfigurationSource corsConfigurationSource(@Value("${app.cors.allowed-origins}") List<String> origins){CorsConfiguration c=new CorsConfiguration();c.setAllowedOrigins(origins);c.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));c.setAllowedHeaders(List.of("*"));c.setAllowCredentials(true);UrlBasedCorsConfigurationSource s=new UrlBasedCorsConfigurationSource();s.registerCorsConfiguration("/**",c);return s;}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Bean SecurityFilterChain securityFilterChain(HttpSecurity http,JwtAuthenticationFilter filter)throws Exception{return http.csrf(c->c.disable()).cors(c->{ }).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).authorizeHttpRequests(a->a.requestMatchers("/api/auth/login","/error").permitAll().requestMatchers(HttpMethod.OPTIONS,"/**").permitAll().anyRequest().authenticated()).addFilterBefore(filter,UsernamePasswordAuthenticationFilter.class).exceptionHandling(errors -> errors
                .authenticationEntryPoint((request, response, exception) -> writeError(response, 401, "请先登录或重新登录"))
                .accessDeniedHandler((request, response, exception) -> writeError(response, 403, "没有操作权限"))).build();}

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private static void writeError(jakarta.servlet.http.HttpServletResponse response, int status, String message) throws java.io.IOException {
        response.setStatus(status);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write("{\"success\":false,\"message\":\"" + message + "\"}");
    }

}
