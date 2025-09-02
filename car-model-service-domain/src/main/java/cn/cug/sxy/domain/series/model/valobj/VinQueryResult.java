package cn.cug.sxy.domain.series.model.valobj;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * @version 1.0
 * @Date 2025/7/22 17:18
 * @Description VIN码查询结果值对象
 * @Author jerryhotton
 */

@Getter
@ToString
@Builder
public class VinQueryResult {

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
     * 是否查询成功
     */
    private final boolean isSuccess;
    /**
     * 错误消息
     */
    private final String errorMessage;

    /**
     * 创建成功的查询结果
     */
    public static VinQueryResult success(ModelCode modelCode, Brand brand, String modelName, PowerType powerType) {
        return VinQueryResult.builder()
                .brand(brand)
                .modelName(modelName)
                .powerType(powerType)
                .isSuccess(true)
                .build();
    }

    /**
     * 创建失败的查询结果
     */
    public static VinQueryResult failure(String errorMessage) {
        return VinQueryResult.builder()
                .isSuccess(false)
                .errorMessage(errorMessage)
                .build();
    }

}
