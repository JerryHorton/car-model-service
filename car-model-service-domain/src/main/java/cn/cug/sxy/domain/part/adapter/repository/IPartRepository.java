package cn.cug.sxy.domain.part.adapter.repository;

import cn.cug.sxy.domain.part.model.entity.PartBindHourExcelData;
import cn.cug.sxy.domain.part.model.entity.PartEntity;
import cn.cug.sxy.domain.part.model.valobj.PartCode;
import cn.cug.sxy.domain.part.model.valobj.PartId;
import cn.cug.sxy.domain.workhour.model.valobj.WorkHourId;
import cn.cug.sxy.types.enums.Status;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * @version 1.0
 * @Date 2025/9/3
 * @Description 备件仓库接口
 * @Author jerryhotton
 */
public interface IPartRepository {

    /**
     * 保存备件
     *
     * @param partEntity 备件实体
     * @return 保存结果
     */
    PartEntity save(PartEntity partEntity);

    /**
     * 查询备件
     *
     * @param partId 备件ID
     * @return 备件实体
     */
    Optional<PartEntity> findById(PartId partId);

    /**
     * 查询备件
     *
     * @param partCode 备件编码
     * @return 备件实体
     */
    Optional<PartEntity> findByCode(PartCode partCode);

    /**
     * 检查备件编码是否存在
     *
     * @param partCode 备件编码
     * @return 是否存在
     */
    boolean existsByCode(PartCode partCode);

    /**
     * 启用备件
     *
     * @param partId 备件ID
     * @return 是否启用成功
     */
    boolean enable(PartId partId);

    /**
     * 禁用备件
     *
     * @param partId 备件ID
     * @return 是否禁用成功
     */
    boolean disable(PartId partId);

    /**
     * 删除备件
     *
     * @param partId 备件ID
     * @return 是否删除成功
     */
    boolean delete(PartId partId);

    /**
     * 查询所有备件
     *
     * @return 备件列表
     */
    List<PartEntity> findAll();

    /**
     * 根据状态查询备件
     *
     * @param status 状态
     * @return 备件列表
     */
    List<PartEntity> findByStatus(Status status);

    /**
     * 读取备件工时Excel文件
     *
     * @param file Excel文件
     * @return 备件工时数据列表
     */
    List<PartBindHourExcelData> readPartHourExcel(MultipartFile file) throws IOException;

    /**
     * 验证备件工时数据
     *
     * @param data 备件工时数据
     * @return 错误信息，null表示验证通过
     */
    String validatePartHourData(PartBindHourExcelData data);

    /**
     * 绑定备件工时
     *
     * @param partId 备件ID
     * @param hourCode 工时代码
     * @return 是否绑定成功
     */
    boolean bindPartHour(PartId partId, String hourCode);

    /**
     * 根据备件ID查询绑定的工时ID列表
     *
     * @param partId 备件ID
     * @return 工时ID列表
     */
    List<WorkHourId> findHourIdsByPartId(PartId partId);

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

    /**
     * 检查备件工时关联模板是否存在
     *
     * @return 是否存在
     */
    boolean isTemplateExists();

    /**
     * 获取备件工时关联模板文件
     *
     * @return 模板文件字节数组
     */
    byte[] getPartHourTemplate();

}