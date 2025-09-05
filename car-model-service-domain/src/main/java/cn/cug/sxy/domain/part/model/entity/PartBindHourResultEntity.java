package cn.cug.sxy.domain.part.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version 1.0
 * @Date 2025/9/3
 * @Description 备件绑定工时结果实体
 * @Author jerryhotton
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartBindHourResultEntity {

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
