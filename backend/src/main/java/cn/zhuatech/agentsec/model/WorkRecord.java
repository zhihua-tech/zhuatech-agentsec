/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agentsec.model;
import jakarta.persistence.*; import java.time.LocalDate;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Entity @Table(name="agentsec_work_record") public class WorkRecord extends BaseEntity {
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public enum Status { RELEASED, RUNNING, PAUSED, COMPLETED }
    @Column(nullable=false,unique=true,length=32) private String recordNo; @Column(nullable=false,length=40) private String subjectCode; @Column(nullable=false,length=120) private String subjectName;
    @ManyToOne(optional=false,fetch=FetchType.LAZY) private OperatingUnit operatingUnit; @Column(nullable=false) private int plannedQty; @Column(nullable=false) private int completedQty; @Column(nullable=false) private int defectQty;
    @Column(nullable=false) private LocalDate dueDate; @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private Status status; @Column(length=40) private String versionNo;
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    protected WorkRecord(){} /**
                              * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                              */
public WorkRecord(String recordNo,String subjectCode,String subjectName,OperatingUnit operatingUnit,int plannedQty,int completedQty,int defectQty,LocalDate dueDate,Status status,String versionNo){this.recordNo=recordNo;this.subjectCode=subjectCode;this.subjectName=subjectName;this.operatingUnit=operatingUnit;this.plannedQty=plannedQty;this.completedQty=completedQty;this.defectQty=defectQty;this.dueDate=dueDate;this.status=status;this.versionNo=versionNo;}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getRecordNo(){return recordNo;} /**
                                                   * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                   */
public String getSubjectCode(){return subjectCode;} /**
                                                                                                       * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                       */
public String getSubjectName(){return subjectName;} /**
                                                                                                                                                           * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                           */
public OperatingUnit getOperatingUnit(){return operatingUnit;} /**
                                                                                                                                                                                                                          * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                                                                                          */
public int getPlannedQty(){return plannedQty;} /**
                                                                                                                                                                                                                                                                         * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                                                                                                                                         */
public int getCompletedQty(){return completedQty;} /**
                                                                                                                                                                                                                                                                                                                            * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                                                                                                                                                                                            */
public int getDefectQty(){return defectQty;} /**
                                                                                                                                                                                                                                                                                                                                                                         * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                                                                                                                                                                                                                                         */
public LocalDate getDueDate(){return dueDate;} /**
                                                                                                                                                                                                                                                                                                                                                                                                                        * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                                                                                                                                                                                                                                                                                        */
public Status getStatus(){return status;} /**
                                                                                                                                                                                                                                                                                                                                                                                                                                                                  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                                                                                                                                                                                                                                                                                                                                  */
public String getVersionNo(){return versionNo;} /**
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  */
public void report(int good,int defect){completedQty+=good;defectQty+=defect;if(completedQty>=plannedQty)status=Status.COMPLETED;else status=Status.RUNNING;}
}
