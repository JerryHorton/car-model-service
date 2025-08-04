package cn.cug.sxy.domain.structure.service;

import cn.cug.sxy.domain.model.model.valobj.ModelId;
import cn.cug.sxy.domain.series.model.valobj.SeriesId;
import cn.cug.sxy.domain.structure.model.entity.StructureInstanceEntity;
import cn.cug.sxy.domain.structure.model.entity.StructureInstanceNodeEntity;
import cn.cug.sxy.domain.structure.model.valobj.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @version 1.0
 * @Date 2025/7/30 16:50
 * @Description 车型结构树实例领域服务接口
 * @Author jerryhotton
 */

public interface IInstanceService {

    /**
     * 基于模板创建实例
     *
     * @param instanceCode    实例编码
     * @param instanceName    实例名称
     * @param instanceDesc    实例描述
     * @param seriesId        车系ID
     * @param modelId         车型ID
     * @param templateId      模板ID
     * @param instanceVersion 实例版本
     * @param creator         创建人
     * @return 创建的实例实体
     */
    StructureInstanceEntity createInstanceFromTemplate(
            InstanceCode instanceCode,
            String instanceName,
            String instanceDesc,
            SeriesId seriesId,
            ModelId modelId,
            TemplateId templateId,
            String instanceVersion,
            String creator);

    /**
     * 创建实例新版本
     *
     * @param sourceInstanceId 源实例ID
     * @param newVersion       新版本号
     * @param instanceDesc     实例描述
     * @param creator          创建人
     * @return 创建的新版本实例实体
     */
    StructureInstanceEntity createNewVersionWithNodes(
            InstanceId sourceInstanceId,
            String newVersion,
            String instanceDesc,
            String creator);

    /**
     * 添加实例节点
     *
     * @param instanceId      实例ID
     * @param parentNodeId    父节点ID，根节点为null
     * @param nodeName        节点名称
     * @param nodeNameEn      节点英文名称
     * @param nodeTypeStr     节点类型字符串
     * @param categoryIdValue 系统大类ID值
     * @param groupIdValue    系统组ID值
     * @param usageIdValue    用法ID值
     * @param sortOrder       排序顺序
     * @param creator         创建人
     * @return 创建的实例节点实体
     */
    StructureInstanceNodeEntity addNode(
            InstanceId instanceId,
            InstanceNodeId parentNodeId,
            String nodeName,
            String nodeNameEn,
            String nodeTypeStr,
            Long categoryIdValue,
            Long groupIdValue,
            Long usageIdValue,
            Integer sortOrder,
            String creator);

    /**
     * 更新节点信息
     *
     * @param nodeId     节点ID
     * @param nodeName   节点名称
     * @param nodeNameEn 节点英文名称
     * @param sortOrder  排序顺序
     * @return 是否更新成功
     */
    boolean updateNodeInfo(
            InstanceNodeId nodeId,
            String nodeName,
            String nodeNameEn,
            Integer sortOrder);

    /**
     * 移动节点
     *
     * @param nodeId      节点ID
     * @param newParentId 新父节点ID，如为null则表示移动为根节点
     * @param sortOrder   排序顺序
     * @return 是否移动成功
     */
    boolean moveNode(
            InstanceNodeId nodeId,
            InstanceNodeId newParentId,
            Integer sortOrder);

    /**
     * 获取实例及其完整树结构
     *
     * @param instanceId 实例ID
     * @return 包含实例和节点树的Map
     */
    Map<String, Object> getInstanceWithFullTree(InstanceId instanceId);

    /**
     * 根据实例编码和版本获取实例及其完整树结构
     *
     * @param instanceCode 实例编码
     * @param version      版本号
     * @return 包含实例和节点树的Map
     */
    Map<String, Object> getInstanceWithFullTreeByCodeAndVersion(InstanceCode instanceCode, String version);

    /**
     * 删除实例及其所有节点
     *
     * @param instanceId 实例ID
     * @return 是否删除成功
     */
    boolean deleteInstanceWithAllNodes(InstanceId instanceId);

    /**
     * 导入节点树
     *
     * @param instanceId   实例ID
     * @param parentNodeId 父节点ID，如为null则表示导入为根节点
     * @param nodes        要导入的节点列表
     * @param creator      创建人
     * @return 导入的节点数量
     */
    int importNodeTree(
            InstanceId instanceId,
            InstanceNodeId parentNodeId,
            List<StructureInstanceNodeEntity> nodes,
            String creator);

    /**
     * 校验实例结构
     *
     * @param instanceId 实例ID
     * @return 校验结果，包含是否有效及问题列表
     */
    Map<String, Object> validateInstanceStructure(InstanceId instanceId);


    /**
     * 根据车型ID查询实例列表
     *
     * @param instanceCode 实例编码
     * @param nameKeyword  实例名称关键词
     * @param status       实例状态
     * @param seriesId     车系ID
     * @param modelId      车型ID
     * @param pageNo       页码
     * @param pageSize     每页数量
     * @return 实例分页VO
     */
    StructureInstancePageVO findInstances(
            InstanceCode instanceCode,
            String nameKeyword,
            Status status,
            SeriesId seriesId,
            ModelId modelId,
            int pageNo,
            int pageSize
    );

    /**
     * 删除节点及其子节点
     *
     * @param nodeId 节点ID
     * @return 删除的节点数量
     */
    int deleteNodeAndChildren(InstanceNodeId nodeId);

    /**
     * 启用实例
     *
     * @param instanceId 实例ID
     * @return 是否启用成功
     */
    boolean enableInstance(InstanceId instanceId);

    /**
     * 禁用实例
     *
     * @param instanceId 实例ID
     * @return 是否禁用成功
     */
    boolean disableInstance(InstanceId instanceId);

    /**
     * 发布实例
     *
     * @param instanceId    实例ID
     * @param effectiveTime 生效时间
     * @return 是否发布成功
     */
    boolean publishInstance(InstanceId instanceId, LocalDateTime effectiveTime);

    /**
     * 取消发布实例
     *
     * @param instanceId 实例ID
     * @return 是否取消发布成功
     */
    boolean unpublishInstance(InstanceId instanceId);

    /**
     * 根据ID查询实例
     *
     * @param instanceId 实例ID
     * @return 实例
     */
    StructureInstanceEntity findInstanceById(InstanceId instanceId);

    /**
     * 根据ID查询节点
     *
     * @param nodeId 节点ID
     * @return 节点
     */
    StructureInstanceNodeEntity findNodeById(InstanceNodeId nodeId);

    /**
     * 查询子节点列表
     *
     * @param parentNodeId 父节点ID
     * @return 子节点列表
     */
    List<StructureInstanceNodeEntity> findChildNodes(InstanceNodeId parentNodeId);

    /**
     * 查询子树
     *
     * @param rootNodeId 根节点ID
     * @return 子树节点列表
     */
    List<StructureInstanceNodeEntity> findSubTree(InstanceNodeId rootNodeId);

    /**
     * 比较两个实例版本的差异
     *
     * @param instanceId1 实例ID1
     * @param instanceId2 实例ID2
     * @return 差异结果
     */
    Map<String, Object> compareInstances(InstanceId instanceId1, InstanceId instanceId2);

}
