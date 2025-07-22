package cn.cug.sxy.domain.model.model.entity;

import cn.cug.sxy.domain.model.model.valobj.ModelCode;
import cn.cug.sxy.domain.model.model.valobj.ModelId;
import cn.cug.sxy.domain.model.model.valobj.ModelStatus;
import cn.cug.sxy.domain.model.model.valobj.PowerType;
import cn.cug.sxy.domain.series.model.valobj.Brand;
import cn.cug.sxy.domain.series.model.valobj.SeriesId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.UUID;

/**
 * @version 1.0
 * @Date 2025/7/21 19:12
 * @Description 车型实体
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarModelEntity {

    /**
     * 车型ID
     */
    private ModelId modelId;
    /**
     * 车型编码
     */
    private ModelCode modelCode;
    /**
     * 车型名称
     */
    private String modelName;
    /**
     * 品牌名称
     */
    private Brand brand;
    /**
     * 动力类型，例如"汽油"、"柴油"、"混合动力"、"纯电动"等
     */
    private PowerType powerType;
    /**
     * 车型状态，包括"待发布"、"已发布"、"废止"三种状态
     */
    private ModelStatus status;
    /**
     * 车型系列ID
     */
    private SeriesId seriesId;
    /**
     * 车型图标文件路径
     */
    private String iconPath;
    /**
     * 车型描述
     */
    private String description;

    public static CarModelEntity create(
            ModelCode modelCode,
            Brand brand,
            PowerType powerType,
            SeriesId seriesId,
            String modelName,
            String iconPath,
            String description) {

        // 验证必填字段
        if (modelCode == null) {
            throw new IllegalArgumentException("Model code is required");
        }
        if (brand == null) {
            throw new IllegalArgumentException("Brand is required");
        }
        if (powerType == null) {
            throw new IllegalArgumentException("Power type is required");
        }
        if (seriesId == null) {
            throw new IllegalArgumentException("Series ID is required");
        }
        if (modelName == null || modelName.trim().isEmpty()) {
            throw new IllegalArgumentException("Model name is required");
        }

        return CarModelEntity.builder()
                .modelId(new ModelId(UUID.randomUUID().toString()))
                .modelCode(modelCode)
                .brand(brand)
                .powerType(powerType)
                .status(ModelStatus.PENDING) // 新创建的车型默认为待发布状态
                .seriesId(seriesId)
                .modelName(modelName)
                .iconPath(iconPath)
                .description(description)
                .build();
    }

    /**
     * 发布车型
     * 只有待发布状态的车型才能发布
     */
    public void publish() {
        this.status = this.status.publish();
    }

    /**
     * 废止车型
     * 只有已发布状态的车型才能废止
     */
    public void deprecate() {
        this.status = this.status.deprecate();
    }

    /**
     * 判断车型是否可以删除
     * 只有待发布状态的车型才能删除
     *
     * @return 是否可以删除
     */
    public boolean canDelete() {
        return Objects.equals(this.status, ModelStatus.PENDING);
    }

}
