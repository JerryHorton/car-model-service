package cn.cug.sxy.domain.structure.adapter.repository;

import cn.cug.sxy.domain.model.model.valobj.ModelId;
import cn.cug.sxy.domain.series.model.valobj.SeriesId;
import cn.cug.sxy.domain.structure.model.entity.StructureInstanceEntity;
import cn.cug.sxy.domain.structure.model.valobj.InstanceCode;
import cn.cug.sxy.domain.structure.model.valobj.InstanceId;
import cn.cug.sxy.domain.structure.model.valobj.Status;
import cn.cug.sxy.domain.structure.model.valobj.StructureInstancePageVO;

import java.util.List;
import java.util.Optional;

/**
 * @version 1.0
 * @Date 2025/7/30 16:56
 * @Description 车型结构树实例仓储接口
 * @Author jerryhotton
 */

public interface IInstanceRepository {

    /**
     * 保存实例
     *
     * @param instance 实例实体
     * @return 保存后的实例实体
     */
    StructureInstanceEntity save(StructureInstanceEntity instance);

    /**
     * 更新实例
     *
     * @param instance 实例实体
     * @return 更新的记录数
     */
    int update(StructureInstanceEntity instance);

    /**
     * 更新实例状态
     *
     * @param instanceId 实例ID
     * @param status     状态
     * @return 更新的记录数
     */
    int updateStatus(InstanceId instanceId, Status status);

    /**
     * 更新实例发布状态
     *
     * @param instanceId  实例ID
     * @param isPublished 是否发布
     * @return 更新的记录数
     */
    int updatePublishStatus(InstanceId instanceId, boolean isPublished);

    /**
     * 根据ID查询实例
     *
     * @param instanceId 实例ID
     * @return 实例Optional
     */
    Optional<StructureInstanceEntity> findById(InstanceId instanceId);

    /**
     * 根据实例编码查询实例列表
     *
     * @param instanceCode 实例编码
     * @return 实例列表
     */
    List<StructureInstanceEntity> findByInstanceCode(InstanceCode instanceCode);

    /**
     * 根据实例编码和版本查询实例
     *
     * @param instanceCode 实例编码
     * @param version      版本号
     * @return 实例Optional
     */
    Optional<StructureInstanceEntity> findByInstanceCodeAndVersion(InstanceCode instanceCode, String version);

    /**
     * 根据车系ID查询实例列表
     *
     * @param seriesId 车系ID
     * @return 实例列表
     */
    List<StructureInstanceEntity> findBySeriesId(SeriesId seriesId);

    /**
     * 根据车型ID查询实例列表
     *
     * @param modelId 车型ID
     * @return 实例列表
     */
    List<StructureInstanceEntity> findByModelId(ModelId modelId);

    /**
     * 根据状态查询实例列表
     *
     * @param status 状态
     * @return 实例列表
     */
    List<StructureInstanceEntity> findByStatus(Status status);

    /**
     * 分页查询实例列表
     *
     * @param instanceCode 实例编码
     * @param nameKeyword 实例名称关键词
     * @param status 实例状态
     * @param seriesId 车系ID
     * @param modelId 车型ID
     * @param pageNo 页码
     * @param pageSize 每页数量
     * @return 实例分页VO
     */
    StructureInstancePageVO findInstances(InstanceCode instanceCode, String nameKeyword, Status status, SeriesId seriesId, ModelId modelId, int pageNo, int pageSize);

    /**
     * 查询所有实例
     *
     * @return 实例列表
     */
    List<StructureInstanceEntity> findAll();

    /**
     * 删除实例
     *
     * @param instanceId 实例ID
     * @return 删除的记录数
     */
    int deleteById(InstanceId instanceId);

    /**
     * 检查实例编码是否存在
     *
     * @param instanceCode 实例编码
     * @return 是否存在
     */
    boolean existsByCode(InstanceCode instanceCode);

    /**
     * 检查实例编码和版本组合是否存在
     *
     * @param instanceCode 实例编码
     * @param version      版本号
     * @return 是否存在
     */
    boolean existsByCodeAndVersion(InstanceCode instanceCode, String version);

}
