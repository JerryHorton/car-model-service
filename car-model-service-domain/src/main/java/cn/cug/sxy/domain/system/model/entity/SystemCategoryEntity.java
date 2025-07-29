package cn.cug.sxy.domain.system.model.entity;

import cn.cug.sxy.domain.system.model.valobj.CategoryCode;
import cn.cug.sxy.domain.system.model.valobj.CategoryId;
import cn.cug.sxy.domain.system.model.valobj.CategoryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Date 2025/7/25 17:45
 * @Description
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SystemCategoryEntity {

    /**
     * 系统大类ID
     */
    private CategoryId categoryId;
    /**
     * 系统大类编码（值对象）
     */
    private CategoryCode categoryCode;
    /**
     * 系统大类名称
     */
    private String categoryName;
    /**
     * 系统大类英文名称
     */
    private String categoryNameEn;
    /**
     * 排序顺序，用于界面展示排序
     */
    private Integer sortOrder;
    /**
     * 状态：启用、禁用、删除
     */
    private CategoryStatus status;
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

    public static SystemCategoryEntity create(CategoryCode categoryCode, String categoryName,
                                              String categoryNameEn, Integer sortOrder, String creator) {
        return SystemCategoryEntity.builder()
                .categoryCode(categoryCode)
                .categoryName(categoryName)
                .categoryNameEn(categoryNameEn)
                .sortOrder(sortOrder != null ? sortOrder : 0)
                .status(CategoryStatus.ENABLED)
                .creator(creator)
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();
    }

    public void update(CategoryCode categoryCode, String categoryName, String categoryNameEn, Integer sortOrder) {
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
        this.categoryNameEn = categoryNameEn;
        if (sortOrder != null) {
            this.sortOrder = sortOrder;
        }
        this.updatedTime = LocalDateTime.now();
    }

    public void updateCategoryCode(CategoryCode categoryCode) {
        this.categoryCode = categoryCode;
        this.updatedTime = LocalDateTime.now();
    }

    public void updateStatus(CategoryStatus status) {
        this.status = status;
        this.updatedTime = LocalDateTime.now();
    }

    public void disable() {
        this.status = CategoryStatus.DISABLED;
        this.updatedTime = LocalDateTime.now();
    }

    public void enable() {
        this.status = CategoryStatus.ENABLED;
        this.updatedTime = LocalDateTime.now();
    }

}
