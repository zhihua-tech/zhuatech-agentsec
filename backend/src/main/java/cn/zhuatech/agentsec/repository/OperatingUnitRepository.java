/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agentsec.repository; import cn.zhuatech.agentsec.model.OperatingUnit; import org.springframework.data.jpa.repository.JpaRepository; import java.util.Optional;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
public interface OperatingUnitRepository extends JpaRepository<OperatingUnit,Long>{/**
                                                                                    * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                    */
Optional<OperatingUnit> findByCode(String code);}
