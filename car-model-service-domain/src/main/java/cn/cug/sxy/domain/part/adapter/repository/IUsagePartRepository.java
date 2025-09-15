package cn.cug.sxy.domain.part.adapter.repository;

import cn.cug.sxy.domain.part.model.entity.UsageBindPartExcelData;
import cn.cug.sxy.domain.part.model.valobj.PartId;
import cn.cug.sxy.domain.usage.model.entity.UsagePartEntity;
import cn.cug.sxy.domain.usage.model.valobj.UsageId;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * @version 1.0
 * @Date 2025/9/5 09:26
 * @Description 用法备件关联仓储接口
 * @Author jerryhotton
 */

public interface IUsagePartRepository {

    /**
     * 保存用法备件关联
     *
     * @param usagePartEntity 用法备件关联实体
     * @return 保存后的实体
     */
    UsagePartEntity save(UsagePartEntity usagePartEntity);

    /**
     * 批量保存用法备件关联
     *
     * @param usagePartEntities 用法备件关联实体列表
     * @return 保存结果
     */
    boolean batchSave(List<UsagePartEntity> usagePartEntities);

    /**
     * 根据用法ID和备件ID删除关联
     *
     * @param usageId 用法ID
     * @param partId  备件ID
     * @return 是否删除成功
     */
    boolean deleteByUsageIdAndPartId(UsageId usageId, PartId partId);

    /**
     * 根据用法ID删除所有关联
     *
     * @param usageId 用法ID
     * @return 是否删除成功
     */
    boolean deleteByUsageId(UsageId usageId);

    /**
     * 根据备件ID删除所有关联
     *
     * @param partId 备件ID
     * @return 是否删除成功
     */
    boolean deleteByPartId(PartId partId);

    /**
     * 根据用法ID查询所有关联的备件
     *
     * @param usageId 用法ID
     * @return 用法备件关联列表
     */
    List<UsagePartEntity> findByUsageId(UsageId usageId);

    /**
     * 根据备件ID查询所有关联的用法
     *
     * @param partId 备件ID
     * @return 用法备件关联列表
     */
    List<UsagePartEntity> findByPartId(PartId partId);

    /**
     * 根据用法ID和备件ID查询关联
     *
     * @param usageId 用法ID
     * @param partId  备件ID
     * @return 用法备件关联
     */
    Optional<UsagePartEntity> findByUsageIdAndPartId(UsageId usageId, PartId partId);

    /**
     * 检查用法和备件是否已关联
     *
     * @param usageId 用法ID
     * @param partId  备件ID
     * @return 是否已关联
     */
    boolean exists(UsageId usageId, PartId partId);

    /**
     * 读取用法备件Excel文件
     *
     * @param file Excel文件
     * @return 用法备件数据列表
     * @throws IOException IO异常
     */
    List<UsageBindPartExcelData> readUsagePartExcel(MultipartFile file) throws IOException;

    /**
     * 验证用法备件数据
     *
     * @param data 用法备件数据
     * @return 错误信息，null表示验证通过
     */
    String validateUsagePartData(UsageBindPartExcelData data);

    /**
     * 上传用法备件关联模板
     *
     * @param file 模板文件
     * @return 上传结果信息
     */
    String uploadTemplate(MultipartFile file);

    /**
     * 获取用法备件关联模板信息
     *
     * @return 模板信息
     */
    String getTemplateInfo();

    /**
     * 检查用法备件关联模板是否存在
     *
     * @return 是否存在
     */
    boolean isTemplateExists();

    /**
     * 获取用法备件关联模板文件
     *
     * @return 模板文件字节数组
     * @throws IOException 读取异常
     */
    byte[] getUsagePartTemplate() throws IOException;

}
