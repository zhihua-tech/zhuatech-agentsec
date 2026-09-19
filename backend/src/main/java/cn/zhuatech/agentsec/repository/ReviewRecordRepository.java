/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agentsec.repository; import cn.zhuatech.agentsec.model.ReviewRecord; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
public interface ReviewRecordRepository extends JpaRepository<ReviewRecord,Long>{/**
                                                                                  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                  */
List<ReviewRecord> findTop10ByOrderByIdDesc();/**
                                                                                                                                * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                */
long countByResult(ReviewRecord.Result result);}
