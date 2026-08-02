/* Copyright 2026 上海如静知华信息科技有限公司 */
package cn.zhuatech.agentsec.repository; import cn.zhuatech.agentsec.model.WorkRecord; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface WorkRecordRepository extends JpaRepository<WorkRecord,Long>{List<WorkRecord> findAllByOrderByDueDateAsc();List<WorkRecord> findByOperatingUnitCodeOrderByDueDateAsc(String code);long countByStatus(WorkRecord.Status status);}
