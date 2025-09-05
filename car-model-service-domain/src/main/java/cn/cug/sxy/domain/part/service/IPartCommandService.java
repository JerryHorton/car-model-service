package cn.cug.sxy.domain.part.service;

import cn.cug.sxy.domain.part.model.entity.PartEntity;
import cn.cug.sxy.domain.part.model.entity.PartBindHourResultEntity;
import cn.cug.sxy.domain.part.model.valobj.PartCode;
import cn.cug.sxy.domain.part.model.valobj.PartId;
import cn.cug.sxy.domain.workhour.model.valobj.WorkHourId;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @version 1.0
 * @Date 2025/9/3
 * @Description 备件命令服务接口
 * @Author jerryhotton
 */
public interface IPartCommandService {

    /**
     * 创建备件
     *
     * @param partCode 备件编码
     * @param name     备件名称
     * @param creator  创建人
     * @param remark   备注
     * @return 创建后的备件实体
     */
    PartEntity createPart(PartCode partCode, String name, String creator, String remark);

    /**
     * 更新备件
     *
     * @param partId 备件ID
     * @param name   备件名称
     * @param remark 备注
     * @return 更新后的备件实体
     */
    PartEntity updatePart(PartId partId, String name, String remark);

    /**
     * 启用备件
     *
     * @param partId 备件ID
     * @return 是否启用成功
     */
    boolean enablePart(PartId partId);

    /**
     * 禁用备件
     *
     * @param partId 备件ID
     * @return 是否禁用成功
     */
    boolean disablePart(PartId partId);

    /**
     * 删除备件
     *
     * @param partId 备件ID
     * @return 是否删除成功
     */
    boolean deletePart(PartId partId);

    /**
     * 批量绑定备件工时关系
     *
     * @param file Excel文件，包含备件编码和工时代码
     * @param creator 创建人
     * @return 绑定结果列表
     */
    List<PartBindHourResultEntity> batchBindHours(MultipartFile file, String creator) throws IOException;

    /**
     * 解绑工时
     *
     * @param partId 备件ID
     * @param hourId 工时ID
     * @return 是否解绑成功
     */
    boolean unbindHour(PartId partId, WorkHourId hourId);

    /**
     * 上传备件工时关联模板
     *
     * @param file 模板文件
     * @return 上传结果信息
     */
    String uploadTemplate(MultipartFile file);

    /**
     * 获取备件工时关联模板信息
     *
     * @return 模板信息
     */
    String getTemplateInfo();

}