package cn.cug.sxy.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version 1.0
 * @Date 2025/8/12 10:42
 * @Description 车系创建请求DTO
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarSeriesCreateRequestDTO {

    /**
     * 车系编码
     */
    @NotBlank(message = "车系编码不能为空")
    private String seriesCode;
    /**
     * 品牌名称
     */
    @NotBlank(message = "品牌名称不能为空")
    private String brand;
    /**
     * 车系名称
     */
    @NotBlank(message = "车系名称不能为空")
    private String seriesName;
    /**
     * 车系描述
     */
    private String description;
    /**
     * 创建者
     */
    @NotBlank(message = "创建者不能为空")
    private String creator;

}
