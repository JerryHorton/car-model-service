package cn.cug.sxy.api.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Date 2025/8/6 19:29
 * @Description 配置项VO
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigItemVO implements Serializable {

    /**
     * 配置项ID
     */
    private Long id;
    /**
     * 配置类别ID
     */
    private Long categoryId;
    /**
     * 配置项编码
     */
    private String itemCode;
    /**
     * 配置项名称
     */
    private String itemName;
    /**
     * 配置值
     */
    private String itemValue;
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
