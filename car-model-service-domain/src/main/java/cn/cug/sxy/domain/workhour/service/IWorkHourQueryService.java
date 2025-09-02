package cn.cug.sxy.domain.workhour.service;

import cn.cug.sxy.domain.workhour.model.entity.WorkHourEntity;
import cn.cug.sxy.domain.workhour.model.valobj.WorkHourCode;
import cn.cug.sxy.domain.workhour.model.valobj.WorkHourId;
import cn.cug.sxy.domain.workhour.model.valobj.WorkHourStatus;
import cn.cug.sxy.domain.workhour.model.valobj.WorkHourType;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/1/27 10:00
 * @Description 工时查询服务接口
 * @Author jerryhotton
 */

public interface IWorkHourQueryService {

    /**
     * 根据ID查询工时
     *
     * @param workHourId 工时ID
     * @return 工时实体
     */
    WorkHourEntity getById(WorkHourId workHourId);

    /**
     * 根据编码查询工时
     *
     * @param workHourCode 工时代码
     * @return 工时实体
     */
    WorkHourEntity getByCode(WorkHourCode workHourCode);

    /**
     * 根据父ID查询子工时列表
     *
     * @param parentId 父工时ID
     * @return 子工时列表
     */
    List<WorkHourEntity> getByParentId(WorkHourId parentId);

    /**
     * 查询所有主工时
     *
     * @return 主工时列表
     */
    List<WorkHourEntity> getAllMainWorkHours();

    /**
     * 根据状态查询工时列表
     *
     * @param status 状态
     * @return 工时列表
     */
    List<WorkHourEntity> getByStatus(WorkHourStatus status);

    /**
     * 根据类型查询工时列表
     *
     * @param type 类型
     * @return 工时列表
     */
    List<WorkHourEntity> getByType(WorkHourType type);

    /**
     * 查询工时树结构
     *
     * @param workHourId 工时ID
     * @return 工时树结构
     */
    List<WorkHourEntity> getWorkHourTree(WorkHourId workHourId);

    /**
     * 检查编码是否存在
     *
     * @param workHourCode 工时代码
     * @return 是否存在
     */
    boolean isCodeExists(WorkHourCode workHourCode);

    /**
     * 获取工时批量上传模板
     *
     * @return 模板数据
     */
    byte[] getWorkHourTemplate();

}