package cn.cug.sxy.api.dto;

import lombok.Data;

/**
 * @version 1.0
 * @Date 2025/9/5 11:24
 * @Description 清空用法关联的所有备件请求DTO
 * @Author jerryhotton
 */

@Data
public class ClearPartsRequestDTO {

    /**
     * 用法ID
     */
    private Long usageId;

}
