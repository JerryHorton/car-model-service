package cn.cug.sxy.domain.system.model.valobj;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @version 1.0
 * @Date 2025/7/25 17:42
 * @Description 系统大类编码值对象
 * @Author jerryhotton
 */

@Getter
@EqualsAndHashCode
@ToString
public class CategoryCode {

    private final String code;

    public CategoryCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Category code cannot be null or empty");
        }
        this.code = code;
    }

}
