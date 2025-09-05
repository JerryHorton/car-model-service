package cn.cug.sxy.api.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version 1.0
 * @Date 2025/9/3 15:10
 * @Description 备件绑定工时代码结果VO
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartBindHourResultVO {

    /**
     * Excel行号
     */
    private Integer rowNumber;
    /**
     * 备件编码
     */
    private String partCode;
    /**
     * 工时代码
     */
    private String workHourCode;
    /**
     * 是否成功
     */
    private Boolean success;
    /**
     * 错误信息
     */
    private String errorMessage;

}
