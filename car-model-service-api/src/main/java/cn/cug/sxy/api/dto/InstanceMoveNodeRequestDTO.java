package cn.cug.sxy.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * @version 1.0
 * @Date 2025/7/31 16:03
 * @Description 移动节点请求DTO
 * @Author jerryhotton
 */

@Data
public class InstanceMoveNodeRequestDTO implements Serializable {

    /**
     * 节点ID
     */
    @NotNull(message = "节点ID不能为空")
    private Long nodeId;
    /**
     * 新父节点ID，如为null则表示移动为根节点
     */
    private Long newParentId;
    /**
     * 排序顺序
     */
    private Integer sortOrder;

}
