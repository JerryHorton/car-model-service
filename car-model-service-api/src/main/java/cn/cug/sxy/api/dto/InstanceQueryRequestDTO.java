package cn.cug.sxy.api.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @version 1.0
 * @Date 2025/7/31 15:56
 * @Description 实例查询请求DTO
 * @Author jerryhotton
 */

@Data
public class InstanceQueryRequestDTO implements Serializable {

    /**
     * 实例编码
     */
    private String instanceCode;
    /**
     * 名称关键字
     */
    private String nameKeyword;
    /**
     * 状态
     */
    private String status;
    /**
     * 车型系列ID
     */
    private Long seriesId;
    /**
     * 车型ID
     */
    private Long modelId;
    /**
     * 当前页码
     */
    private Integer pageNo;
    /**
     * 每页条数
     */
    private Integer pageSize;

}
