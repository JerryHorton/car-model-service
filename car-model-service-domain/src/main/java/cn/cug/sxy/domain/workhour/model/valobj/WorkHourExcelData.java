package cn.cug.sxy.domain.workhour.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version 1.0
 * @Date 2025/9/1 16:59
 * @Description 工时Excel数据类
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkHourExcelData {

    /**
     * 工时代码
     */
    private String code;
    /**
     * 工时描述
     */
    private String description;
    /**
     * 标准工时
     */
    private String standardHours;
    /**
     * 步骤顺序
     */
    private String stepOrder;

}
