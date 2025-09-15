package cn.cug.sxy.api.vo;

import lombok.Data;

/**
 * @version 1.0
 * @Date 2025/9/10 09:40
 * @Description 系统大类VO
 * @Author jerryhotton
 */

@Data
public class SystemCategoryVO {

    /**
     * 系统大类ID
     */
    private Long id;
    /**
     * 系统大类编码
     */
    private String categoryCode;
    /**
     * 系统大类名称
     */
    private String categoryName;
    /**
     * 系统大类英文名称
     */
    private String categoryNameEn;
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
