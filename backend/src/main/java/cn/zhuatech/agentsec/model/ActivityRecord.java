/* Copyright 2026 上海如静知华信息科技有限公司 */
package cn.zhuatech.agentsec.model;
import jakarta.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="agentsec_activity_record") public class ActivityRecord extends BaseEntity {
    @ManyToOne(optional=false,fetch=FetchType.LAZY) private WorkRecord workRecord; @Column(nullable=false,length=50) private String operationName; @Column(nullable=false) private int goodQty; @Column(nullable=false) private int defectQty;
    @Column(nullable=false,length=50) private String operatorName; @Column(nullable=false) private LocalDateTime reportedAt; @Column(length=200) private String remark;
    protected ActivityRecord(){} public ActivityRecord(WorkRecord workRecord,String operationName,int goodQty,int defectQty,String operatorName,String remark){this.workRecord=workRecord;this.operationName=operationName;this.goodQty=goodQty;this.defectQty=defectQty;this.operatorName=operatorName;this.reportedAt=LocalDateTime.now();this.remark=remark;}
    public WorkRecord getWorkRecord(){return workRecord;} public String getOperationName(){return operationName;} public int getGoodQty(){return goodQty;} public int getDefectQty(){return defectQty;} public String getOperatorName(){return operatorName;} public LocalDateTime getReportedAt(){return reportedAt;}
}
