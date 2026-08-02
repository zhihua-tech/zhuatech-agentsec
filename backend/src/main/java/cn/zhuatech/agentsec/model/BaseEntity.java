/* Copyright 2026 上海如静知华信息科技有限公司 */
package cn.zhuatech.agentsec.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@MappedSuperclass
public abstract class BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, updatable=false) private LocalDateTime createdAt;
    @Column(nullable=false) private LocalDateTime updatedAt;
    @PrePersist void create(){ createdAt=LocalDateTime.now(); updatedAt=createdAt; }
    @PreUpdate void update(){ updatedAt=LocalDateTime.now(); }
    public Long getId(){ return id; } public LocalDateTime getCreatedAt(){ return createdAt; } public LocalDateTime getUpdatedAt(){ return updatedAt; }
}
