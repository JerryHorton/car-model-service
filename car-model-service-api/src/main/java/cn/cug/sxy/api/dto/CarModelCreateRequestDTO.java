package cn.cug.sxy.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * @version 1.0
 * @Date 2025/8/12 11:23
 * @Description 车型创建请求DTO
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarModelCreateRequestDTO {

    /**
     * 车型编码
     */
    @NotBlank(message = "车型编码不能为空")
    private String modelCode;
    /**
     * 车型名称
     */
    @NotBlank(message = "车型名称不能为空")
    private String modelName;
    /**
     * 品牌名称
     */
    @NotBlank(message = "品牌名称不能为空")
    private String brand;
    /**
     * 动力类型
     */
    @NotBlank(message = "动力类型不能为空")
    private String powerType;
    /**
     * 车系ID
     */
    @NotNull(message = "车系ID不能为空")
    private Long seriesId;
    /**
     * 车型图标文件
     */
    private MultipartFile iconFile;
    /**
     * 车型描述
     */
    private String description;
    /**
     * 创建者
     */
    @NotBlank(message = "创建者不能为空")
    private String creator;

}
