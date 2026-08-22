/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agentsec.model;
import jakarta.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="agentsec_review_record") public class ReviewRecord extends BaseEntity {
    public enum Result { PENDING, PASSED, FAILED }
    @Column(nullable=false,unique=true,length=32) private String reviewRecordNo; @ManyToOne(optional=false,fetch=FetchType.LAZY) private WorkRecord workRecord;
    @Column(nullable=false,length=30) private String reviewRecordType; @Column(nullable=false) private int reviewRecordQty; @Column(nullable=false) private int defectQty; @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private Result result;
    @Column(length=50) private String inspector; @Column(nullable=false) private LocalDateTime createdAt;
    protected ReviewRecord(){} public ReviewRecord(String reviewRecordNo,WorkRecord workRecord,String reviewRecordType,int reviewRecordQty,int defectQty,Result result,String inspector){this.reviewRecordNo=reviewRecordNo;this.workRecord=workRecord;this.reviewRecordType=reviewRecordType;this.reviewRecordQty=reviewRecordQty;this.defectQty=defectQty;this.result=result;this.inspector=inspector;this.createdAt=LocalDateTime.now();}
    public String getReviewRecordNo(){return reviewRecordNo;} public WorkRecord getWorkRecord(){return workRecord;} public String getReviewRecordType(){return reviewRecordType;} public int getReviewRecordQty(){return reviewRecordQty;} public int getDefectQty(){return defectQty;} public Result getResult(){return result;} public String getInspector(){return inspector;}
}
