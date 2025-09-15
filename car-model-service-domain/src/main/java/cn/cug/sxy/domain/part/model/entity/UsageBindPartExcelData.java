package cn.cug.sxy.domain.part.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version 1.0
 * @Date 2025/9/5 09:28
 * @Description 用法备件绑定Excel数据
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsageBindPartExcelData {

    /**
     * 备件编码
     */
    private String partCode;
    /**
     * 数量
     */
    private String count;

}
