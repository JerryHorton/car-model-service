package cn.cug.sxy.domain.usage.model.entity;

import cn.cug.sxy.domain.usage.model.valobj.ConfigCategoryId;
import cn.cug.sxy.domain.usage.model.valobj.ConfigItemId;
import cn.cug.sxy.types.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Date 2025/8/5 19:41
 * @Description 配置项实体
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigItemEntity {

    /**
     * 配置项ID
     */
    private ConfigItemId id;
    /**
     * 配置类别ID
     */
    private ConfigCategoryId categoryId;
    /**
     * 配置项编码
     * 全局唯一，如：BATTERY_39KWH、MOTOR_TZ220XSP01
     */
    private String itemCode;
    /**
     * 配置项名称
     * 用于显示的友好名称，如：39kWh电池包、Tz220XSP01电机
     */
    private String itemName;
    /**
     * 配置值
     * 具体的配置值，如：39kWh、Tz220XSP01
     */
    private String itemValue;
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
     * 创建配置项
     *
     * @param categoryId 配置类别ID
     * @param itemCode   配置项编码
     * @param itemName   配置项名称
     * @param itemValue  配置值
     * @param creator    创建人
     * @return 配置项实体
     */
    public static ConfigItemEntity create(ConfigCategoryId categoryId, String itemCode,
                                          String itemName, String itemValue, String creator) {
        return ConfigItemEntity.builder()
                .categoryId(categoryId)
                .itemCode(itemCode)
                .itemName(itemName)
                .itemValue(itemValue)
                .status(Status.ENABLED)
                .creator(creator)
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();
    }

    /**
     * 更新配置项信息
     *
     * @param itemName  配置项名称
     * @param itemValue 配置值
     */
    public void update(String itemName, String itemValue) {
        this.itemName = itemName;
        this.itemValue = itemValue;
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 启用配置项
     */
    public void enable() {
        this.status = Status.ENABLED;
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 禁用配置项
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
     * 验证配置项编码格式
     * 编码应为大写字母、数字和下划线组成
     *
     * @param itemCode 配置项编码
     * @return 是否有效
     */
    public static boolean isValidItemCode(String itemCode) {
        return itemCode != null && itemCode.matches("^[A-Z0-9_]+$");
    }

    /**
     * 检查配置值是否匹配关键字
     * 用于模糊搜索功能
     *
     * @param keyword 搜索关键字
     * @return 是否匹配
     */
    public boolean matchesValueKeyword(String keyword) {
        return this.itemValue != null &&
                this.itemValue.toLowerCase().contains(keyword.toLowerCase());
    }

}
