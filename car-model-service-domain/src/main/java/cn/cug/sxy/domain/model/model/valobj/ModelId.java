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

    private final String id;

    public ModelId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Model ID cannot be null or empty");
        }
        this.id = id;
    }

}
