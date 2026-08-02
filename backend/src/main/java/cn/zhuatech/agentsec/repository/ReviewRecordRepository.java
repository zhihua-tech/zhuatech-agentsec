/* Copyright 2026 上海如静知华信息科技有限公司 */
package cn.zhuatech.agentsec.repository; import cn.zhuatech.agentsec.model.ReviewRecord; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface ReviewRecordRepository extends JpaRepository<ReviewRecord,Long>{List<ReviewRecord> findTop10ByOrderByIdDesc();long countByResult(ReviewRecord.Result result);}
