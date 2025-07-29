package cn.cug.sxy.domain.system.model.valobj;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @version 1.0
 * @Date 2025/7/21 15:08
 * @Description 系统分组ID值对象
 * @Author jerryhotton
 */

@Getter
@ToString
@EqualsAndHashCode
public class GroupId {

    private final Long id;

    public GroupId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("GroupId ID cannot be null or less than or equal to 0");
        }
        this.id = id;
    }

}
