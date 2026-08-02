/* Copyright 2026 上海如静知华信息科技有限公司 */
package cn.zhuatech.agentsec.model;
import jakarta.persistence.*; import java.time.LocalDate;
@Entity @Table(name="agentsec_work_record") public class WorkRecord extends BaseEntity {
    public enum Status { RELEASED, RUNNING, PAUSED, COMPLETED }
    @Column(nullable=false,unique=true,length=32) private String recordNo; @Column(nullable=false,length=40) private String subjectCode; @Column(nullable=false,length=120) private String subjectName;
    @ManyToOne(optional=false,fetch=FetchType.LAZY) private OperatingUnit operatingUnit; @Column(nullable=false) private int plannedQty; @Column(nullable=false) private int completedQty; @Column(nullable=false) private int defectQty;
    @Column(nullable=false) private LocalDate dueDate; @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private Status status; @Column(length=40) private String versionNo;
    protected WorkRecord(){} public WorkRecord(String recordNo,String subjectCode,String subjectName,OperatingUnit operatingUnit,int plannedQty,int completedQty,int defectQty,LocalDate dueDate,Status status,String versionNo){this.recordNo=recordNo;this.subjectCode=subjectCode;this.subjectName=subjectName;this.operatingUnit=operatingUnit;this.plannedQty=plannedQty;this.completedQty=completedQty;this.defectQty=defectQty;this.dueDate=dueDate;this.status=status;this.versionNo=versionNo;}
    public String getRecordNo(){return recordNo;} public String getSubjectCode(){return subjectCode;} public String getSubjectName(){return subjectName;} public OperatingUnit getOperatingUnit(){return operatingUnit;} public int getPlannedQty(){return plannedQty;} public int getCompletedQty(){return completedQty;} public int getDefectQty(){return defectQty;} public LocalDate getDueDate(){return dueDate;} public Status getStatus(){return status;} public String getVersionNo(){return versionNo;} public void report(int good,int defect){completedQty+=good;defectQty+=defect;if(completedQty>=plannedQty)status=Status.COMPLETED;else status=Status.RUNNING;}
}
