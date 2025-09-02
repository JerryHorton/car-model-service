package cn.cug.sxy.api.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @version 1.0
 * @Date 2025/7/29 13:45
 * @Description 模板详情VO
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TemplateDetailVO implements Serializable {

    /**
     * 模板ID
     */
    private Long id;
    /**
     * 模板编码
     */
    private String templateCode;
    /**
     * 模板名称
     */
    private String templateName;
    /**
     * 模板版本
     */
    private String version;
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
    /**
     * 节点树
     */
    private List<TemplateNodeTreeVO> nodeTree;

}
