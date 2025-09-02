package cn.cug.sxy.infrastructure.converter;

import cn.cug.sxy.domain.structure.model.entity.StructureTemplateNodeEntity;
import cn.cug.sxy.domain.structure.model.valobj.NodeType;
import cn.cug.sxy.types.enums.Status;
import cn.cug.sxy.domain.structure.model.valobj.TemplateId;
import cn.cug.sxy.domain.structure.model.valobj.TemplateNodeId;
import cn.cug.sxy.infrastructure.dao.po.TemplateStructureNodePO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/7/29 09:31
 * @Description 车型结构树模板节点转换器
 * @Author jerryhotton
 */

public class TemplateStructureNodeConverter {

    /**
     * 将实体转换为PO
     *
     * @param entity 节点实体
     * @return 节点PO
     */
    public static TemplateStructureNodePO toPO(StructureTemplateNodeEntity entity) {
        if (entity == null) {
            return null;
        }
        TemplateStructureNodePO templateStructureNodePO = new TemplateStructureNodePO();
        templateStructureNodePO.setId(entity.getId() != null ? entity.getId().getId() : null);
        templateStructureNodePO.setTemplateId(entity.getTemplateId() != null ? entity.getTemplateId().getId() : null);
        templateStructureNodePO.setParentId(entity.getParentId() != null ? entity.getParentId().getId() : null);
        templateStructureNodePO.setNodeName(entity.getNodeName());
        templateStructureNodePO.setNodeNameEn(entity.getNodeNameEn());
        templateStructureNodePO.setNodeType(entity.getNodeType() != null ? entity.getNodeType().name() : null);
        templateStructureNodePO.setNodeCode(entity.getNodeCode() != null ? entity.getNodeCode() : null);
        templateStructureNodePO.setSortOrder(entity.getSortOrder());
        templateStructureNodePO.setCategoryId(entity.getCategoryId());
        templateStructureNodePO.setGroupId(entity.getGroupId());
        templateStructureNodePO.setNodeLevel(entity.getNodeLevel());
        templateStructureNodePO.setNodePath(entity.getNodePath());
        templateStructureNodePO.setStatus(entity.getStatus() != null ? entity.getStatus().getCode() : null);
        templateStructureNodePO.setCreator(entity.getCreator());
        templateStructureNodePO.setCreatedTime(entity.getCreatedTime());
        templateStructureNodePO.setUpdatedTime(entity.getUpdatedTime());

        return templateStructureNodePO;
    }

    /**
     * 将PO转换为实体
     *
     * @param po 节点PO
     * @return 节点实体
     */
    public static StructureTemplateNodeEntity toEntity(TemplateStructureNodePO po) {
        if (po == null) {
            return null;
        }

        return StructureTemplateNodeEntity.builder()
                .id(po.getId() != null ? new TemplateNodeId(po.getId()) : null)
                .templateId(po.getTemplateId() != null ? new TemplateId(po.getTemplateId()) : null)
                .parentId(po.getParentId() != null ? new TemplateNodeId(po.getParentId()) : null)
                .nodeName(po.getNodeName())
                .nodeNameEn(po.getNodeNameEn())
                .nodeType(po.getNodeType() != null ? NodeType.fromCode(po.getNodeType()) : null)
                .nodeCode(po.getNodeCode())
                .sortOrder(po.getSortOrder())
                .categoryId(po.getCategoryId())
                .groupId(po.getGroupId())
                .nodeLevel(po.getNodeLevel())
                .nodePath(po.getNodePath())
                .status(po.getStatus() != null ? Status.fromCode(po.getStatus()) : null)
                .creator(po.getCreator())
                .createdTime(po.getCreatedTime())
                .updatedTime(po.getUpdatedTime())
                .build();
    }

    /**
     * 将PO列表转换为实体列表
     *
     * @param poList 节点PO列表
     * @return 节点实体列表
     */
    public static List<StructureTemplateNodeEntity> toEntityList(List<TemplateStructureNodePO> poList) {
        if (poList == null || poList.isEmpty()) {
            return Collections.emptyList();
        }

        return poList.stream()
                .map(TemplateStructureNodeConverter::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * 将实体列表转换为PO列表
     *
     * @param entityList 节点实体列表
     * @return 节点PO列表
     */
    public static List<TemplateStructureNodePO> toPOList(List<StructureTemplateNodeEntity> entityList) {
        if (entityList == null || entityList.isEmpty()) {
            return Collections.emptyList();
        }

        return entityList.stream()
                .map(TemplateStructureNodeConverter::toPO)
                .collect(Collectors.toList());
    }

}
