package cn.cug.sxy.api.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/9/4 11:24
 * @Description 备件详情VO
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartDetailVO {

    /**
     * 备件VO
     */
    private PartVO partVO;
    /**
     * 工时树VO
     */
    private List<WorkHourTreeVO> workHourTreeVOList;

}
