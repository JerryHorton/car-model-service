package cn.cug.sxy.infrastructure.converter;

import cn.cug.sxy.domain.structure.model.entity.StructureInstanceNodeEntity;
import cn.cug.sxy.domain.structure.model.valobj.InstanceId;
import cn.cug.sxy.domain.structure.model.valobj.InstanceNodeId;
import cn.cug.sxy.domain.structure.model.valobj.NodeType;
import cn.cug.sxy.domain.structure.model.valobj.Status;
import cn.cug.sxy.infrastructure.dao.po.InstanceStructureNodePO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @version 1.0
 * @Date 2025/7/30 17:58
 * @Description 实例结构树节点转换
 * @Author jerryhotton
 */

public class InstanceStructureNodeConverter {

    public static InstanceStructureNodePO toPO(StructureInstanceNodeEntity entity) {
        if (entity == null) {
            return null;
        }
        InstanceStructureNodePO po = new InstanceStructureNodePO();
        po.setId(entity.getId() == null ? null : entity.getId().getId());
        po.setInstanceId(entity.getInstanceId() == null ? null : entity.getInstanceId().getId());
        po.setParentId(entity.getParentId() != null ? entity.getParentId().getId() : null);
        po.setNodeType(entity.getNodeType() == null ? null : entity.getNodeType().name());
        po.setNodeCode(entity.getNodeCode());
        po.setNodeName(entity.getNodeName());
        po.setNodeNameEn(entity.getNodeNameEn());
        po.setSortOrder(entity.getSortOrder());
        po.setCategoryId(entity.getCategoryId());
        po.setGroupId(entity.getGroupId());
        po.setUsageId(entity.getUsageId());
        po.setNodePath(entity.getNodePath());
        po.setNodeLevel(entity.getNodeLevel());
        po.setStatus(entity.getStatus().getCode());
        po.setCreator(entity.getCreator());
        po.setCreatedTime(entity.getCreatedTime());
        po.setUpdatedTime(entity.getUpdatedTime());

        return po;
    }

    public static StructureInstanceNodeEntity toEntity(InstanceStructureNodePO po) {
        if (po == null) {
            return null;
        }

        return StructureInstanceNodeEntity.builder()
                .id(new InstanceNodeId(po.getId()))
                .instanceId(new InstanceId(po.getInstanceId()))
                .parentId(po.getParentId() != null ? new InstanceNodeId(po.getParentId()) : null)
                .nodeType(NodeType.fromCode(po.getNodeType()))
                .nodeCode(po.getNodeCode())
                .nodeName(po.getNodeName())
                .nodeNameEn(po.getNodeNameEn())
                .sortOrder(po.getSortOrder())
                .categoryId(po.getCategoryId())
                .groupId(po.getGroupId())
                .usageId(po.getUsageId())
                .nodePath(po.getNodePath())
                .nodeLevel(po.getNodeLevel())
                .status(Status.fromCode(po.getStatus()))
                .creator(po.getCreator())
                .createdTime(po.getCreatedTime())
                .updatedTime(po.getUpdatedTime())
                .build();
    }

    public static List<StructureInstanceNodeEntity> toEntityList(List<InstanceStructureNodePO> poList) {
        if (poList == null || poList.isEmpty()) {
            return Collections.emptyList();
        }

        return poList.stream()
                .map(InstanceStructureNodeConverter::toEntity)
                .collect(Collectors.toList());
    }

    public static List<InstanceStructureNodePO> toPOList(List<StructureInstanceNodeEntity> poList) {
        if (poList == null || poList.isEmpty()) {
            return Collections.emptyList();
        }

        return poList.stream()
                .map(InstanceStructureNodeConverter::toPO)
                .collect(Collectors.toList());
    }

}
