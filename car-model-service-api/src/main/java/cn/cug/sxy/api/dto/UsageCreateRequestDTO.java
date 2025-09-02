package cn.cug.sxy.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

/**
 * @version 1.0
 * @Date 2025/8/6 18:38
 * @Description 建用法请求DTO
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsageCreateRequestDTO implements Serializable {

    /**
     * 实例ID
     */
    @NotNull(message = "实例ID不能为空")
    private Long instanceId;
    /**
     * 父GROUP节点ID
     */
    @NotNull(message = "父节点ID不能为空")
    private Long parentGroupNodeId;
    /**
     * 归属系统分组ID
     */
    @NotNull(message = "系统分组ID不能为空")
    private Long groupId;
    /**
     * 用法名称
     */
    @NotBlank(message = "用法名称不能为空")
    private String usageName;
    /**
     * 爆炸图文件
     */
    private MultipartFile explodedViewFile;
    /**
     * 排序序号
     */
    private Integer sortOrder;
    /**
     * 创建人
     */
    @NotBlank(message = "创建人不能为空")
    private String creator;

    /**
     * 配置组合列表（可选）
     */
    @Valid
    private List<CombinationCreateDTO> combinations;

    /**
     * 配置组合创建DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CombinationCreateDTO {

        /**
         * 组合名称
         */
        @NotBlank(message = "配置组合名称不能为空")
        private String combinationName;
        /**
         * 排序序号
         */
        private Integer sortOrder;
        /**
         * 配置项ID列表
         */
        @Size(min = 1, message = "配置组合必须包含至少一个配置项")
        private List<Long> configItemIds;
    }

}
