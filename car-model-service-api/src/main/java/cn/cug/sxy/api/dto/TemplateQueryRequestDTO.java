package cn.cug.sxy.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version 1.0
 * @Date 2025/7/29 16:41
 * @Description 模板查询DTO
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TemplateQueryRequestDTO {

    /**
     * 模板编码
     */
    private String templateCode;
    /**
     * 名称关键字
     */
    private String nameKeyword;
    /**
     * 状态
     */
    private String status;
    /**
     * 当前页码
     */
    private Integer pageNo;
    /**
     * 每页条数
     */
    private Integer pageSize;

}
