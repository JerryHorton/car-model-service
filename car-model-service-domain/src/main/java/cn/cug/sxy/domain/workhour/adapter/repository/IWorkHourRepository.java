package cn.cug.sxy.domain.workhour.adapter.repository;

import cn.cug.sxy.domain.part.model.valobj.PartId;
import cn.cug.sxy.domain.workhour.model.entity.WorkHourEntity;
import cn.cug.sxy.domain.workhour.model.valobj.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * @version 1.0
 * @Date 2025/1/27 10:00
 * @Description 工时仓储接口
 * @Author jerryhotton
 */

public interface IWorkHourRepository {

    /**
     * 保存工时
     *
     * @param workHourEntity 工时实体
     * @return 保存后的工时实体
     */
    WorkHourEntity save(WorkHourEntity workHourEntity);

    /**
     * 根据ID查找工时
     *
     * @param workHourId 工时ID
     * @return 工时实体
     */
    Optional<WorkHourEntity> findById(WorkHourId workHourId);

    /**
     * 根据编码查找工时
     *
     * @param workHourCode 工时代码
     * @return 工时实体
     */
    Optional<WorkHourEntity> findByCode(WorkHourCode workHourCode);

    /**
     * 根据父ID查找子工时列表
     *
     * @param parentId 父工时ID
     * @return 子工时列表
     */
    List<WorkHourEntity> findByParentId(WorkHourId parentId);

    /**
     * 查找所有主工时
     *
     * @return 主工时列表
     */
    List<WorkHourEntity> findAllMainWorkHours();

    /**
     * 根据状态查找工时列表
     *
     * @param status 状态
     * @return 工时列表
     */
    List<WorkHourEntity> findByStatus(WorkHourStatus status);

    /**
     * 根据类型查找工时列表
     *
     * @param type 类型
     * @return 工时列表
     */
    List<WorkHourEntity> findByType(WorkHourType type);

    /**
     * 检查编码是否存在
     *
     * @param workHourCode 工时代码
     * @return 是否存在
     */
    boolean existsByCode(WorkHourCode workHourCode);

    /**
     * 删除工时
     *
     * @param workHourId 工时ID
     * @return 是否删除成功
     */
    boolean remove(WorkHourId workHourId);

    /**
     * 更新工时
     *
     * @param workHourEntity 工时实体
     * @return 更新影响行数
     */
    int update(WorkHourEntity workHourEntity);

    /**
     * 查找工时树结构
     *
     * @param workHourId 工时ID
     * @return 工时树结构
     */
    WorkHourEntity findWorkHourTree(WorkHourId workHourId);

    /**
     * 查找工时树结构
     *
     * @param partId 部件ID
     * @return 工时树结构
     */
    List<WorkHourEntity> findWorkHourTreeByPartId(PartId partId);

    /**
     * 上传工时模板
     *
     * @param file 模板文件
     * @return 模板文件URL
     */
    String uploadTemplate(MultipartFile file);

    /**
     * 获取工时模板信息
     *
     * @return 模板信息
     */
    String getTemplateInfo();

    /**
     * 读取工时Excel文件
     *
     * @param file Excel文件
     * @return 工时数据列表
     */
    List<WorkHourExcelData> readWorkHourExcel(MultipartFile file) throws IOException;

    /**
     * 获取工时模板
     *
     * @return 模板数据
     */
    byte[] getWorkHourTemplate();

    /**
     * 校验工时Excel数据
     *
     * @param excelData 工时Excel数据
     */
    String validateWorkHourExcelData(WorkHourExcelData excelData);

}