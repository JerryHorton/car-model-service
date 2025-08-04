package cn.cug.sxy.api.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Date 2025/7/30 10:01
 * @Description 节点VO
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TemplateNodeVO {

    /**
     * 节点ID
     */
    private Long id;
    /**
     * 模板ID
     */
    private Long templateId;
    /**
     * 父节点ID
     */
    private Long parentId;
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
     * 状态
     */
    private String status;
    /**
     * 创建者
     */
    private String creator;
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

}
