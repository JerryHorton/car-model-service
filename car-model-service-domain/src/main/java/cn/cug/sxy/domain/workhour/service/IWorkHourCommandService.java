package cn.cug.sxy.domain.workhour.service;

import cn.cug.sxy.domain.workhour.model.entity.WorkHourBatchUploadResultEntity;
import cn.cug.sxy.domain.workhour.model.entity.WorkHourEntity;
import cn.cug.sxy.domain.workhour.model.valobj.WorkHourCode;
import cn.cug.sxy.domain.workhour.model.valobj.WorkHourId;
import cn.cug.sxy.domain.workhour.model.valobj.WorkHourType;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

/**
 * @version 1.0
 * @Date 2025/1/27 10:00
 * @Description 工时命令服务接口
 * @Author jerryhotton
 */

public interface IWorkHourCommandService {

    /**
     * 创建主工时
     *
     * @param code          工时代码
     * @param description   工时描述
     * @param standardHours 标准工时
     * @param type          工时类型
     * @param creator       创建人
     * @return 创建的工时实体
     */
    WorkHourEntity createMainWorkHour(
            WorkHourCode code,
            String description,
            BigDecimal standardHours,
            WorkHourType type,
            String creator);

    /**
     * 创建子工时
     *
     * @param code          工时代码
     * @param description   工时描述
     * @param standardHours 标准工时
     * @param type          工时类型
     * @param parentId      父工时ID
     * @param stepOrder     步骤顺序号
     * @param creator       创建人
     * @return 创建的工时实体
     */
    WorkHourEntity createSubWorkHour(
            WorkHourCode code,
            String description,
            BigDecimal standardHours,
            WorkHourType type,
            WorkHourId parentId,
            Integer stepOrder,
            String creator);

    /**
     * 更新工时
     *
     * @param workHourId    工时ID
     * @param description   工时描述
     * @param standardHours 标准工时
     * @param stepOrder     步骤顺序号
     * @return 更新后的工时实体
     */
    WorkHourEntity updateWorkHour(
            WorkHourId workHourId,
            String description,
            BigDecimal standardHours,
            Integer stepOrder);

    /**
     * 启用工时
     *
     * @param workHourId 工时ID
     * @return 是否成功
     */
    boolean enableWorkHour(WorkHourId workHourId);

    /**
     * 禁用工时
     *
     * @param workHourId 工时ID
     * @return 是否成功
     */
    boolean disableWorkHour(WorkHourId workHourId);

    /**
     * 删除工时
     *
     * @param workHourId 工时ID
     * @return 是否成功
     */
    boolean deleteWorkHour(WorkHourId workHourId);

    /**
     * 批量上传子工时
     *
     * @param file     Excel文件
     * @param parentId 父工时ID
     * @param creator  创建人
     * @return 批量上传结果列表
     */
    List<WorkHourBatchUploadResultEntity> batchUploadSubWorkHours(MultipartFile file, WorkHourId parentId, String creator);

    /**
     * 上传工时批量上传模板
     *
     * @param file Excel模板文件
     * @return 上传结果信息
     */
    String uploadTemplate(MultipartFile file);

    /**
     * 获取工时批量上传模板信息
     *
     * @return 模板文件信息
     */
    String getTemplateInfo();

}