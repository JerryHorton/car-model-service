package cn.cug.sxy.domain.model.model.valobj;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @version 1.0
 * @Date 2025/7/21 18:55
 * @Description 车型ID值对象
 * @Author jerryhotton
 */

@Getter
@ToString
@EqualsAndHashCode
public class ModelId {

    private final Long id;

    public ModelId(Long id) {
        if (id == null || id < 0) {
            throw new IllegalArgumentException("Model ID cannot be null or less than 0");
        }
        this.id = id;
    }

}
