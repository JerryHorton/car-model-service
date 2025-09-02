package cn.cug.sxy.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @version 1.0
 * @Date 2025/8/7 10:55
 * @Description 创建配置项请求DTO
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigItemCreateRequestDTO implements Serializable {

    /**
     * 配置类别ID
     */
    @NotNull(message = "配置类别ID不能为空")
    private Long categoryId;
    /**
     * 配置项编码
     * 全局唯一，如：BATTERY_39KWH、MOTOR_TZ220XSP01
     */
    @NotBlank(message = "配置项编码不能为空")
    private String itemCode;
    /**
     * 配置项名称
     * 用于显示的友好名称，如：电池包、电机
     */
    @NotBlank(message = "配置项名称不能为空")
    private String itemName;
    /**
     * 配置值
     * 具体的配置值，如：39kWh、Tz220XSP01
     */
    @NotBlank(message = "配置值不能为空")
    private String itemValue;
    /**
     * 创建人
     */
    @NotBlank(message = "创建人不能为空")
    private String creator;

}
