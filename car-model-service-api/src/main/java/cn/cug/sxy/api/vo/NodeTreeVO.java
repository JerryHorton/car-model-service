package cn.cug.sxy.api.vo;

import cn.cug.sxy.api.dto.TemplateCreateResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/7/29 11:46
 * @Description
 * @Author jerryhotton
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NodeTreeVO {

    /**
     * 节点ID
     */
    private Long id;
    /**
     * 节点编码
     */
    private String nodeCode;
    /**
     * 节点名称
     */
    private String nodeName;
    /**
     * 节点英文名称
     */
    private String nodeNameEn;
    /**
     * 节点类型
     */
    private String nodeType;
    /**
     * 排序序号
     */
    private Integer sortOrder;
    /**
     * 父节点ID
     */
    private Long parentId;
    /**
     * 子节点列表
     */
    private List<NodeTreeVO> children;

}
