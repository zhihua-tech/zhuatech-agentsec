/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agentsec.model;
import jakarta.persistence.*;
@Entity @Table(name="agentsec_user")
public class UserAccount extends BaseEntity {
    public enum Role { ADMIN, DOMAIN_OPERATOR, DOMAIN_USER, QUALITY }
    @Column(nullable=false,unique=true,length=32) private String username; @Column(nullable=false) private String password;
    @Column(nullable=false,length=50) private String fullName; @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private Role role;
    @Column(name="operating_unit_code",length=32) private String operatingUnitCode; @Column(nullable=false) private boolean enabled=true;
    protected UserAccount(){}
    public UserAccount(String username,String password,String fullName,Role role,String operatingUnitCode){this.username=username;this.password=password;this.fullName=fullName;this.role=role;this.operatingUnitCode=operatingUnitCode;}
    public String getUsername(){return username;} public String getPassword(){return password;} public String getFullName(){return fullName;} public Role getRole(){return role;} public String getOperatingUnitCode(){return operatingUnitCode;} public boolean isEnabled(){return enabled;}
}
