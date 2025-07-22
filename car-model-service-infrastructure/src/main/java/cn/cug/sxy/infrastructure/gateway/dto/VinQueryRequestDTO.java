package cn.cug.sxy.infrastructure.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version 1.0
 * @Date 2025/7/22 18:42
 * @Description Vin查询请求
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VinQueryRequestDTO {

    /**
     * Vin码
     */
    private String vin;

}
