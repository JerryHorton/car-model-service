package cn.cug.sxy.domain.part.service;

import cn.cug.sxy.domain.part.model.entity.UsageBindPartExcelData;
import cn.cug.sxy.domain.part.model.entity.UsageBindPartResultEntity;
import cn.cug.sxy.domain.part.model.valobj.PartId;
import cn.cug.sxy.domain.usage.model.entity.UsagePartEntity;
import cn.cug.sxy.domain.usage.model.valobj.UsageId;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @version 1.0
 * @Date 2025/9/5 10:18
 * @Description 用法备件管理服务接口
 * @Author jerryhotton
 */

public interface IPartUsageManageService {

    /**
     * 绑定备件到用法
     *
     * @param usageId 用法ID
     * @param partId  备件ID
     * @param count   数量
     * @return 绑定结果
     */
    UsagePartEntity bindPart(UsageId usageId, PartId partId, Integer count);

    /**
     * 解绑备件
     *
     * @param usageId 用法ID
     * @param partId  备件ID
     * @return 是否解绑成功
     */
    boolean unbindPart(UsageId usageId, PartId partId);

    /**
     * 清空用法关联的所有备件
     *
     * @param usageId 用法ID
     * @return 是否清空成功
     */
    boolean clearParts(UsageId usageId);

    /**
     * 查询用法关联的所有备件
     *
     * @param usageId 用法ID
     * @return 备件列表
     */
    List<UsagePartEntity> getPartsByUsageId(UsageId usageId);

    /**
     * 批量上传备件
     *
     * @param usageId 用法ID
     * @param file    Excel文件
     * @return 上传结果
     * @throws IOException IO异常
     */
    List<UsageBindPartResultEntity> batchUpload(UsageId usageId, MultipartFile file) throws IOException;

    /**
     * 上传模板
     *
     * @param file 模板文件
     * @return 上传结果信息
     */
    String uploadTemplate(MultipartFile file);

    /**
     * 获取模板信息
     *
     * @return 模板信息
     */
    String getTemplateInfo();

    /**
     * 检查模板是否存在
     *
     * @return 是否存在
     */
    boolean isTemplateExists();

    /**
     * 获取模板文件
     *
     * @return 模板文件字节数组
     * @throws IOException 读取异常
     */
    byte[] getTemplate() throws IOException;

}
