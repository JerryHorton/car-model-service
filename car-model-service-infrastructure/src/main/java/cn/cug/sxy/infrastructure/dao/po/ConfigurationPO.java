package cn.cug.sxy.infrastructure.dao.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Date 2025/8/4 16:16
 * @Description 车型配置持久化对象
 * @Author jerryhotton
 */

@Data
public class ConfigurationPO {

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 配置编码
     */
    private String configCode;
    /**
     * 配置名称
     */
    private String configName;
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
