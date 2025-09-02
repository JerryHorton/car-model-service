package cn.cug.sxy.domain.structure.model.entity;

import cn.cug.sxy.types.enums.Status;
import cn.cug.sxy.domain.structure.model.valobj.TemplateCode;
import cn.cug.sxy.domain.structure.model.valobj.TemplateId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Date 2025/7/28 15:43
 * @Description 车型结构树模板实体
 * @Author jerryhotton
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StructureTemplateEntity {

    /**
     * 模板ID
     */
    private TemplateId id;
    /**
     * 模板编码
     */
    private TemplateCode templateCode;
    /**
     * 模板名称
     */
    private String templateName;
    /**
     * 模板描述
     */
    private String templateDesc;
    /**
     * 模板版本
     */
    private String version;
    /**
     * 状态：启用、禁用、删除
     */
    private Status status;
    /**
     * 创建人
     */
    private String creator;
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

    /**
     * 创建车型结构树模板
     *
     * @param templateCode 模板编码
     * @param templateName 模板名称
     * @param version      模板版本
     * @param creator      创建人
     * @return 车型结构树模板实体
     */
    public static StructureTemplateEntity create(TemplateCode templateCode, String templateName, String templateDesc, String version, String creator) {
        return StructureTemplateEntity.builder()
                .templateCode(templateCode)
                .templateName(templateName)
                .templateDesc(templateDesc)
                .version(version)
                .status(Status.ENABLED)
                .creator(creator)
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();
    }

    /**
     * 更新模板基本信息
     *
     * @param templateName 模板名称
     * @param version      模板版本
     */
    public void update(String templateName, String version) {
        this.templateName = templateName;
        this.version = version;
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 更新模板状态
     *
     * @param status 状态
     */
    public void updateStatus(Status status) {
        this.status = status;
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 启用模板
     */
    public void enable() {
        this.status = Status.ENABLED;
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 禁用模板
     */
    public void disable() {
        this.status = Status.DISABLED;
        this.updatedTime = LocalDateTime.now();
    }

}
