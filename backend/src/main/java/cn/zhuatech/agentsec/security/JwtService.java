/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agentsec.security;
import io.jsonwebtoken.Jwts; import io.jsonwebtoken.security.Keys; import org.springframework.beans.factory.annotation.Value; import org.springframework.stereotype.Service;
import javax.crypto.SecretKey; import java.nio.charset.StandardCharsets; import java.time.Duration; import java.util.Date;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Service public class JwtService {
    private final SecretKey key; private final Duration expiration;
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public JwtService(@Value("${app.jwt.secret}") String secret,@Value("${app.jwt.expiration:PT12H}") Duration expiration){key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));this.expiration=expiration;}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String generate(String username){Date now=new Date();return Jwts.builder().subject(username).issuedAt(now).expiration(new Date(now.getTime()+expiration.toMillis())).signWith(key).compact();}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String username(String token){return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();}
}
