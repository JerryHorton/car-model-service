package cn.cug.sxy.domain.structure.model.entity;

import cn.cug.sxy.domain.series.model.valobj.ModelId;
import cn.cug.sxy.domain.series.model.valobj.SeriesId;
import cn.cug.sxy.domain.structure.model.valobj.InstanceCode;
import cn.cug.sxy.domain.structure.model.valobj.InstanceId;
import cn.cug.sxy.types.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Date 2025/7/28 15:18
 * @Description 车型结构树实例实体
 * @Author jerryhotton
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StructureInstanceEntity {

    /**
     * 实例ID
     */
    private InstanceId id;
    /**
     * 实例编码
     */
    private InstanceCode instanceCode;
    /**
     * 实例名称
     */
    private String instanceName;
    /**
     * 实例描述
     */
    private String instanceDesc;
    /**
     * 车型系列ID
     */
    private SeriesId seriesId;
    /**
     * 车型ID
     */
    private ModelId modelId;
    /**
     * 结构实例版本号
     */
    private String version;
    /**
     * 状态
     */
    private Status status;
    /**
     * 是否已发布
     */
    private Boolean isPublished;
    /**
     * 生效时间
     */
    private LocalDateTime effectiveTime;
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
     * 创建车型结构树实例
     *
     * @param instanceCode 实例编码
     * @param instanceName 实例名称
     * @param instanceDesc 实例描述
     * @param seriesId     车型系列ID
     * @param modelId      车型ID
     * @param version      实例版本
     * @param creator      创建人
     * @return 车型结构树实例实体
     */
    public static StructureInstanceEntity create(InstanceCode instanceCode, String instanceName, String instanceDesc,
                                                 SeriesId seriesId, ModelId modelId, String version, String creator) {
        return StructureInstanceEntity.builder()
                .instanceCode(instanceCode)
                .instanceName(instanceName)
                .instanceDesc(instanceDesc)
                .seriesId(seriesId)
                .modelId(modelId)
                .version(version)
                .status(Status.ENABLED)
                .isPublished(false)
                .creator(creator)
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();
    }

    /**
     * 更新实例基本信息
     *
     * @param seriesId 车型系列ID
     * @param modelId  车型ID
     * @param version  实例版本
     */
    public void update(SeriesId seriesId, ModelId modelId, String version) {
        this.seriesId = seriesId;
        this.modelId = modelId;
        this.version = version;
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 更新实例状态
     *
     * @param status 状态
     */
    public void updateStatus(Status status) {
        this.status = status;
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 发布实例
     *
     * @param effectiveTime 生效时间
     */
    public void publish(LocalDateTime effectiveTime) {
        this.isPublished = true;
        this.effectiveTime = effectiveTime;
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 取消发布实例
     */
    public void unpublish() {
        this.isPublished = false;
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 启用实例
     */
    public void enable() {
        this.status = Status.ENABLED;
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 禁用实例
     */
    public void disable() {
        this.status = Status.DISABLED;
        this.updatedTime = LocalDateTime.now();
    }

}
