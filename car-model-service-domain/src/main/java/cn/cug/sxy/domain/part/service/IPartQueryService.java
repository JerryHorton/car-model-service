package cn.cug.sxy.domain.part.service;

import cn.cug.sxy.domain.part.model.entity.PartEntity;
import cn.cug.sxy.domain.part.model.valobj.PartCode;
import cn.cug.sxy.domain.part.model.valobj.PartId;
import cn.cug.sxy.domain.workhour.model.valobj.WorkHourId;
import cn.cug.sxy.types.enums.Status;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/9/3
 * @Description 备件查询服务接口
 * @Author jerryhotton
 */
public interface IPartQueryService {

    /**
     * 根据ID查询备件
     *
     * @param partId 备件ID
     * @return 备件实体
     */
    PartEntity getPartById(PartId partId);

    /**
     * 查询所有备件
     *
     * @return 备件列表
     */
    List<PartEntity> getAllParts();

    /**
     * 检查编码是否存在
     *
     * @param partCode 备件编码
     * @return 是否存在
     */
    boolean isCodeExists(PartCode partCode);

    /**
     * 获取备件工时关联模板文件
     *
     * @return 模板文件字节数组
     */
    byte[] getPartHourTemplate();

}