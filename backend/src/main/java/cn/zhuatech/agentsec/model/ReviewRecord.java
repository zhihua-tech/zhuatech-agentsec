/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agentsec.model;
import jakarta.persistence.*; import java.time.LocalDateTime;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Entity @Table(name="agentsec_review_record") public class ReviewRecord extends BaseEntity {
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public enum Result { PENDING, PASSED, FAILED }
    @Column(nullable=false,unique=true,length=32) private String reviewRecordNo; @ManyToOne(optional=false,fetch=FetchType.LAZY) private WorkRecord workRecord;
    @Column(nullable=false,length=30) private String reviewRecordType; @Column(nullable=false) private int reviewRecordQty; @Column(nullable=false) private int defectQty; @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private Result result;
    @Column(length=50) private String inspector; @Column(nullable=false) private LocalDateTime createdAt;
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    protected ReviewRecord(){} /**
                                * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                */
public ReviewRecord(String reviewRecordNo,WorkRecord workRecord,String reviewRecordType,int reviewRecordQty,int defectQty,Result result,String inspector){this.reviewRecordNo=reviewRecordNo;this.workRecord=workRecord;this.reviewRecordType=reviewRecordType;this.reviewRecordQty=reviewRecordQty;this.defectQty=defectQty;this.result=result;this.inspector=inspector;this.createdAt=LocalDateTime.now();}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getReviewRecordNo(){return reviewRecordNo;} /**
                                                               * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                               */
public WorkRecord getWorkRecord(){return workRecord;} /**
                                                                                                                     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                     */
public String getReviewRecordType(){return reviewRecordType;} /**
                                                                                                                                                                                   * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                                                   */
public int getReviewRecordQty(){return reviewRecordQty;} /**
                                                                                                                                                                                                                                            * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                                                                                                            */
public int getDefectQty(){return defectQty;} /**
                                                                                                                                                                                                                                                                                         * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                                                                                                                                                         */
public Result getResult(){return result;} /**
                                                                                                                                                                                                                                                                                                                                   * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                                                                                                                                                                                                   */
public String getInspector(){return inspector;}
}
