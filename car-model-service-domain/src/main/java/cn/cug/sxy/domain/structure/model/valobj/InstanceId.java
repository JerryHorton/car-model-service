package cn.cug.sxy.domain.structure.model.valobj;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @version 1.0
 * @Date 2025/7/28 15:19
 * @Description 车型结构树实例ID值对象
 * @Author jerryhotton
 */

@Getter
@ToString
@EqualsAndHashCode
public class InstanceId {

    private final Long id;

    public InstanceId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Instance ID cannot be null or less than or equal to 0");
        }
        this.id = id;
    }

}
