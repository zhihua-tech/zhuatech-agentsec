/* Copyright 2026 上海如静知华信息科技有限公司 */
package cn.zhuatech.agentsec.dto;
import jakarta.validation.constraints.*; import java.time.*; import java.util.List;
public final class AgentSecDto { private AgentSecDto(){}
    public record Metric(String label,String value,String hint,String tone){}
    public record WorkRecordView(Long id,String recordNo,String subjectCode,String subjectName,String operatingUnit,String workshop,int plannedQty,int completedQty,int defectQty,LocalDate dueDate,String status,String versionNo,int progress){}
    public record ControlView(String code,String name,String operatingUnit,String status,int oee,LocalDateTime lastHeartbeat){}
    public record ReviewRecordView(String reviewRecordNo,String recordNo,String subjectName,String reviewRecordType,int reviewRecordQty,int defectQty,String result,String inspector){}
    public record Dashboard(List<Metric> metrics,List<WorkRecordView> workRecords,List<ControlView> resources,List<ReviewRecordView> reviewRecords){}
    public record ReportRequest(@NotBlank String operationName,@Positive int goodQty,@PositiveOrZero int defectQty,@Size(max=200) String remark){}
    public record ReportResult(String recordNo,int completedQty,int defectQty,int progress,String status){}
}
