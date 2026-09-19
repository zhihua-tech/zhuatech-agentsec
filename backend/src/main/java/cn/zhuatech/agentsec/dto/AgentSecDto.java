/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agentsec.dto;
import jakarta.validation.constraints.*; import java.time.*; import java.util.List;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
public final class AgentSecDto { /**
                                  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                  */
private AgentSecDto(){}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record Metric(String label,String value,String hint,String tone){}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record WorkRecordView(Long id,String recordNo,String subjectCode,String subjectName,String operatingUnit,String workshop,int plannedQty,int completedQty,int defectQty,LocalDate dueDate,String status,String versionNo,int progress){}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record ControlView(String code,String name,String operatingUnit,String status,int oee,LocalDateTime lastHeartbeat){}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record ReviewRecordView(String reviewRecordNo,String recordNo,String subjectName,String reviewRecordType,int reviewRecordQty,int defectQty,String result,String inspector){}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record Dashboard(List<Metric> metrics,List<WorkRecordView> workRecords,List<ControlView> resources,List<ReviewRecordView> reviewRecords){}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record ReportRequest(@NotBlank String operationName,@Positive int goodQty,@PositiveOrZero int defectQty,@Size(max=200) String remark){}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record ReportResult(String recordNo,int completedQty,int defectQty,int progress,String status){}
}
