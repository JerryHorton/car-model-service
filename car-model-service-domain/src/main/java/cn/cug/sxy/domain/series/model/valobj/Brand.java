package cn.cug.sxy.domain.series.model.valobj;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @version 1.0
 * @Date 2025/7/21 15:06
 * @Description 品牌值对象
 * @Author jerryhotton
 */

@Getter
@ToString
@EqualsAndHashCode
public class Brand {

    private final String name;

    public Brand(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Brand name cannot be null or empty");
        }
        this.name = name;
    }

}
