/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agentsec.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@MappedSuperclass
public abstract class BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, updatable=false) private LocalDateTime createdAt;
    @Column(nullable=false) private LocalDateTime updatedAt;
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PrePersist void create(){ createdAt=LocalDateTime.now(); updatedAt=createdAt; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PreUpdate void update(){ updatedAt=LocalDateTime.now(); }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public Long getId(){ return id; } /**
                                       * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                       */
public LocalDateTime getCreatedAt(){ return createdAt; } /**
                                                                                                * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                */
public LocalDateTime getUpdatedAt(){ return updatedAt; }
}
