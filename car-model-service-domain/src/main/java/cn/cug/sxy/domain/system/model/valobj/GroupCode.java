package cn.cug.sxy.domain.system.model.valobj;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @version 1.0
 * @Date 2025/7/25 17:41
 * @Description 系统分组编码值对象
 * @Author jerryhotton
 */

@Getter
@ToString
@EqualsAndHashCode
public class GroupCode {

    private final String code;

    public GroupCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Group code cannot be null or empty");
        }
        this.code = code;
    }

}
