package cn.cug.sxy.api.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/9/4 11:09
 * @Description 工时树VO
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkHourTreeVO {

    /**
     * 工时VO
     */
    private WorkHourVO workHourVO;
    /**
     * 子工时VO列表
     */
    private List<WorkHourVO> children;

}
