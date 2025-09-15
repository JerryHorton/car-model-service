package cn.cug.sxy.api.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version 1.0
 * @Date 2025/9/10 09:58
 * @Description 系统分组VO
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SystemGroupVO {

    /**
     * 系统分组ID
     */
    private Long id;
    /**
     * 系统大类ID
     */
    private Long categoryId;
    /**
     * 系统大类编码
     */
    private String categoryCode;
    /**
     * 系统大类名称
     */
    private String categoryName;
    /**
     * 系统分组编码
     */
    private String groupCode;
    /**
     * 系统分组名称
     */
    private String groupName;
    /**
     * 系统分组英文名称
     */
    private String groupNameEn;
    /**
     * 排序顺序
     */
    private Integer sortOrder;
    /**
     * 状态
     */
    private String status;
    /**
     * 创建人
     */
    private String creator;

}
