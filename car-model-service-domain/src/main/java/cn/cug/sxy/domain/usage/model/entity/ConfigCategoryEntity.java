package cn.cug.sxy.domain.usage.model.entity;

import cn.cug.sxy.domain.usage.model.valobj.ConfigCategoryId;
import cn.cug.sxy.types.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Date 2025/8/5 19:27
 * @Description 配置类别实体
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigCategoryEntity {

    /**
     * 配置类别ID
     */
    private ConfigCategoryId id;
    /**
     * 类别编码
     * 全局唯一，如：BATTERY_CAPACITY、MOTOR_TYPE
     */
    private String categoryCode;
    /**
     * 类别名称
     * 用于显示的友好名称，如：电池包容量、电机型号
     */
    private String categoryName;
    /**
     * 排序序号
     * 用于控制配置类别在前端的显示顺序
     */
    private Integer sortOrder;
    /**
     * 状态
     */
    private Status status;
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

    /**
     * 创建配置类别
     *
     * @param categoryCode 类别编码
     * @param categoryName 类别名称
     * @param sortOrder    排序序号
     * @param creator      创建人
     * @return 配置类别实体
     */
    public static ConfigCategoryEntity create(String categoryCode, String categoryName, Integer sortOrder, String creator) {
        return ConfigCategoryEntity.builder()
                .categoryCode(categoryCode)
                .categoryName(categoryName)
                .sortOrder(sortOrder)
                .status(Status.ENABLED)
                .creator(creator)
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();
    }

    /**
     * 更新配置类别信息
     *
     * @param categoryCode 类别编码
     * @param categoryName 类别名称
     * @param sortOrder    排序序号
     */
    public void update(String categoryCode, String categoryName, Integer sortOrder) {
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
        this.sortOrder = sortOrder;
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 更新排序序号
     *
     * @param sortOrder 排序序号
     */
    public void updateSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 启用配置类别
     */
    public void enable() {
        this.status = Status.ENABLED;
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 禁用配置类别
     */
    public void disable() {
        this.status = Status.DISABLED;
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 检查是否可用
     *
     * @return 是否可用
     */
    public boolean isEnabled() {
        return Status.ENABLED.equals(this.status);
    }

    /**
     * 验证类别编码格式
     * 编码应为大写字母、数字和下划线组成
     *
     * @param categoryCode 类别编码
     * @return 是否有效
     */
    public static boolean isValidCategoryCode(String categoryCode) {
        return categoryCode != null && categoryCode.matches("^[A-Z_]+$");
    }

}
