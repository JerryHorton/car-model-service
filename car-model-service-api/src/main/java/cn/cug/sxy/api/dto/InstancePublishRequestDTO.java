package cn.cug.sxy.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Date 2025/8/1 17:03
 * @Description
 * @Author jerryhotton
 */

@Data
public class InstancePublishRequestDTO implements Serializable {

    /**
     * 实例ID
     */
    @NotNull(message = "实例ID不能为空")
    private Long instanceId;
    /**
     * 生效时间
     */
    @NotNull(message = "生效时间不能为空")
    private LocalDateTime effectiveTime;

}
