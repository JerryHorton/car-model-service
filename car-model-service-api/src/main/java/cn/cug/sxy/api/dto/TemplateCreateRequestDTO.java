package cn.cug.sxy.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @version 1.0
 * @Date 2025/7/29 10:52
 * @Description 车型结构树模板创建请求DTO
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TemplateCreateRequestDTO implements Serializable {

    /**
     * 模板编码
     */
    @NotBlank(message = "模板编码不能为空")
    @Size(max = 50, message = "模板编码长度不能超过50个字符")
    private String templateCode;
    /**
     * 模板名称
     */
    @NotBlank(message = "模板名称不能为空")
    private String templateName;
    /**
     * 模板描述
     */
    private String templateDesc;
    /**
     * 模板版本
     */
    @NotBlank(message = "模板版本不能为空")
    @Size(max = 20, message = "模板版本长度不能超过20个字符")
    private String version;
    /**
     * 创建者
     */
    @NotBlank(message = "创建者不能为空")
    @Size(max = 50, message = "创建者长度不能超过50个字符")
    private String creator;
    /**
     * 节点列表
     */
    private List<NodeCreate> nodes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NodeCreate {

        /**
         * 节点名称
         */
        @NotBlank(message = "节点名称不能为空")
        @Size(max = 100, message = "节点名称长度不能超过100个字符")
        private String nodeName;
        /**
         * 节点英文名称
         */
        @Size(max = 100, message = "节点英文名称长度不能超过100个字符")
        private String nodeNameEn;
        /**
         * 节点类型
         */
        @NotBlank(message = "节点类型不能为空")
        private String nodeType;
        /**
         * 排序序号
         */
        private Integer sortOrder;
        /**
         * 系统大类ID
         */
        private Long categoryId;
        /**
         * 系统组ID
         */
        private Long groupId;
        /**
         * 子节点列表
         */
        private List<NodeCreate> children;

    }

}
