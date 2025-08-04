package cn.cug.sxy.domain.structure.model.entity;

import cn.cug.sxy.domain.structure.model.valobj.ConfigurationCode;
import cn.cug.sxy.domain.structure.model.valobj.ConfigurationId;
import cn.cug.sxy.domain.structure.model.valobj.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Date 2025/8/4 16:11
 * @Description 车型配置实体
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigurationEntity {

    /**
     * 配置ID
     */
    private ConfigurationId id;
    /**
     * 配置编码
     */
    private ConfigurationCode configCode;
    /**
     * 配置名称
     */
    private String configName;
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
     * 创建配置实体
     *
     * @param configCode 配置编码
     * @param configName 配置名称
     * @param creator    创建人
     * @return 配置实体
     */
    public static ConfigurationEntity create(ConfigurationCode configCode, String configName, String creator) {
        // 参数校验
        if (configCode == null) {
            throw new IllegalArgumentException("配置编码不能为空");
        }
        if (StringUtils.isBlank(configName)) {
            throw new IllegalArgumentException("配置名称不能为空");
        }
        if (configName.length() > 128) {
            throw new IllegalArgumentException("配置名称长度不能超过128个字符");
        }
        if (StringUtils.isBlank(creator)) {
            throw new IllegalArgumentException("创建人不能为空");
        }

        LocalDateTime now = LocalDateTime.now();
        return ConfigurationEntity.builder()
                .configCode(configCode)
                .configName(configName)
                .status(Status.ENABLED)
                .creator(creator)
                .createdTime(now)
                .updatedTime(now)
                .build();
    }

    /**
     * 更新配置信息
     *
     * @param configName 配置名称
     */
    public void update(String configName) {
        if (StringUtils.isNotBlank(configName)) {
            if (configName.length() > 128) {
                throw new IllegalArgumentException("配置名称长度不能超过128个字符");
            }
            this.configName = configName;
            this.updatedTime = LocalDateTime.now();
        }
    }

    /**
     * 启用配置
     */
    public void enable() {
        this.status = Status.ENABLED;
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 禁用配置
     */
    public void disable() {
        this.status = Status.DISABLED;
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 更新状态
     *
     * @param status 状态
     */
    public void updateStatus(Status status) {
        if (status != null) {
            this.status = status;
            this.updatedTime = LocalDateTime.now();
        }
    }

}
