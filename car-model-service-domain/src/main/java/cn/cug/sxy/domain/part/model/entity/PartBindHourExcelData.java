package cn.cug.sxy.domain.part.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version 1.0
 * @Date 2025/9/3 14:36
 * @Description 备件工时Excel数据类
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartBindHourExcelData {

    /**
     * 备件编码
     */
    private String partCode;
    /**
     * 工时代码
     */
    private String hourCode;

}
