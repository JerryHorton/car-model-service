package cn.cug.sxy.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @version 1.0
 * @Date 2025/9/3
 * @Description 备件更新请求
 * @Author jerryhotton
 */
@Data
public class PartUpdateRequestDTO {

    /**
     * 备件ID
     */
    @NotNull
    private Long id;
    /**
     * 备件名称
     */
    private String partName;
    /**
     * 状态
     */
    private String status;
    /**
     * 备注
     */
    private String remark;

}