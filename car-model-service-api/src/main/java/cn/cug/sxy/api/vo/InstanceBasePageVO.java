package cn.cug.sxy.api.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @version 1.0
 * @Date 2025/8/1 09:50
 * @Description 实例基础分页VO
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstanceBasePageVO implements Serializable {

    /**
     * 实例基础VO列表
     */
    private List<InstanceBaseVO> instances;
    /**
     * 总记录数
     */
    private Long total;
    /**
     * 当前页码
     */
    private Integer pageNo;
    /**
     * 每页条数
     */
    private Integer pageSize;
    /**
     * 总页数
     */
    private Integer totalPages;

}
