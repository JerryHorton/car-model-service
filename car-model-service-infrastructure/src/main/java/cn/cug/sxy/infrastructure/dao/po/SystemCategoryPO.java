package cn.cug.sxy.infrastructure.dao.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Date 2025/7/28 08:56
 * @Description 系统大类持久化对象
 * @Author jerryhotton
 */

@Data
public class SystemCategoryPO {

    /**
     * 自增ID
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
     * 排序
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
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

}
