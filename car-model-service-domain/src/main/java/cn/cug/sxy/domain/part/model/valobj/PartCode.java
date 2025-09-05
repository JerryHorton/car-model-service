package cn.cug.sxy.domain.part.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version 1.0
 * @Date 2025/9/3
 * @Description 备件编码值对象
 * @Author jerryhotton
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartCode {

    /**
     * 备件编码值
     */
    private String code;
    
}