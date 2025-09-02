package cn.cug.sxy.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

/**
 * @version 1.0
 * @Date 2025/8/7 17:29
 * @Description 更新用法请求DTO
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsageUpdateRequestDTO implements Serializable {

    /**
     * 用法ID
     */
    @NotNull(message = "用法ID不能为空")
    private Long usageId;
    /**
     * 用法名称
     */
    @NotBlank(message = "用法名称不能为空")
    private String usageName;
    /**
     * 归属系统分组ID（用于生成文件路径）
     */
    @NotNull(message = "系统分组ID不能为空")
    private Long groupId;
    /**
     * 爆炸图文件（可选，null表示不更新爆炸图）
     */
    private MultipartFile explodedViewFile;
    /**
     * 配置组合列表（可选，null表示不更新配置组合）
     */
    @Valid
    private List<CombinationUpdateDTO> combinations;

    /**
     * 配置组合更新DTO
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CombinationUpdateDTO {

        /**
         * 组合名称
         */
        @NotBlank(message = "组合名称不能为空")
        private String combinationName;

        /**
         * 排序序号
         */
        @NotNull(message = "排序序号不能为空")
        private Integer sortOrder;

        /**
         * 配置项ID列表
         */
        @NotNull(message = "配置项ID列表不能为空")
        private List<Long> configItemIds;

    }

}
