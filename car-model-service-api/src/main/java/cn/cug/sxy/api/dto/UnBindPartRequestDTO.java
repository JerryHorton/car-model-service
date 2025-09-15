package cn.cug.sxy.api.dto;

import lombok.Data;

/**
 * @version 1.0
 * @Date 2025/9/5 14:01
 * @Description 解绑备件请求参数
 * @Author jerryhotton
 */

@Data
public class UnBindPartRequestDTO {

    /**
     * 用法ID
     */
    private Long usageId;
    /**
     * 备件ID
     */
    private Long partId;

}
