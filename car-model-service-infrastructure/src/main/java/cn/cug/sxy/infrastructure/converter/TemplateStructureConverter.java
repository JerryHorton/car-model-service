package cn.cug.sxy.infrastructure.converter;

import cn.cug.sxy.domain.structure.model.entity.StructureTemplateEntity;
import cn.cug.sxy.types.enums.Status;
import cn.cug.sxy.domain.structure.model.valobj.TemplateCode;
import cn.cug.sxy.domain.structure.model.valobj.TemplateId;
import cn.cug.sxy.infrastructure.dao.po.TemplateStructurePO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/7/29 09:01
 * @Description 车型结构树模板转换器
 * @Author jerryhotton
 */

public class TemplateStructureConverter {

    /**
     * 将实体转换为PO
     *
     * @param entity 模板实体
     * @return 模板PO
     */
    public static TemplateStructurePO toPO(StructureTemplateEntity entity) {
        if (entity == null) {
            return null;
        }
        TemplateStructurePO templateStructurePO = new TemplateStructurePO();
        templateStructurePO.setId(entity.getId() != null ? entity.getId().getId() : null);
        templateStructurePO.setTemplateCode(entity.getTemplateCode() != null ? entity.getTemplateCode().getCode() : null);
        templateStructurePO.setTemplateName(entity.getTemplateName());
        templateStructurePO.setTemplateDesc(entity.getTemplateDesc());
        templateStructurePO.setVersion(entity.getVersion());
        templateStructurePO.setStatus(entity.getStatus() != null ? entity.getStatus().getCode() : null);
        templateStructurePO.setCreator(entity.getCreator());
        templateStructurePO.setCreatedTime(entity.getCreatedTime());
        templateStructurePO.setUpdatedTime(entity.getUpdatedTime());

        return templateStructurePO;
    }

    /**
     * 将PO转换为实体
     *
     * @param po 模板PO
     * @return 模板实体
     */
    public static StructureTemplateEntity toEntity(TemplateStructurePO po) {
        if (po == null) {
            return null;
        }

        return StructureTemplateEntity.builder()
                .id(po.getId() != null ? new TemplateId(po.getId()) : null)
                .templateCode(po.getTemplateCode() != null ? new TemplateCode(po.getTemplateCode()) : null)
                .templateName(po.getTemplateName())
                .templateDesc(po.getTemplateDesc())
                .version(po.getVersion())
                .status(po.getStatus() != null ? Status.fromCode(po.getStatus()) : null)
                .creator(po.getCreator())
                .createdTime(po.getCreatedTime())
                .updatedTime(po.getUpdatedTime())
                .build();
    }

    /**
     * 将PO列表转换为实体列表
     *
     * @param poList 模板PO列表
     * @return 模板实体列表
     */
    public static List<StructureTemplateEntity> toEntityList(List<TemplateStructurePO> poList) {
        if (poList == null || poList.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        return poList.stream()
                .map(TemplateStructureConverter::toEntity)
                .collect(Collectors.toList());
    }

}
