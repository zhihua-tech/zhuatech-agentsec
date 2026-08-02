/* Copyright 2026 上海如静知华信息科技有限公司 */
package cn.zhuatech.agentsec.dto;
import cn.zhuatech.agentsec.model.UserAccount; import jakarta.validation.constraints.NotBlank;
public final class AuthDto {
    private AuthDto(){}
    public record LoginRequest(@NotBlank(message="请输入用户名") String username,@NotBlank(message="请输入密码") String password){}
    public record UserView(Long id,String username,String fullName,String role,String operatingUnitCode){
        public static UserView from(UserAccount u){return new UserView(u.getId(),u.getUsername(),u.getFullName(),u.getRole().name(),u.getOperatingUnitCode());}
    }
    public record LoginResponse(String token,UserView user){}
}
