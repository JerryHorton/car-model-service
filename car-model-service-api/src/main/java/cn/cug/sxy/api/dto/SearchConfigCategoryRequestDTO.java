package cn.cug.sxy.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @version 1.0
 * @Date 2025/8/7 10:26
 * @Description 搜索配置类别请求DTO
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchConfigCategoryRequestDTO implements Serializable {

    /**
     * 类别编码
     */
    private String categoryCode;
    /**
     * 名称关键词
     */
    private String nameKeyword;

}
