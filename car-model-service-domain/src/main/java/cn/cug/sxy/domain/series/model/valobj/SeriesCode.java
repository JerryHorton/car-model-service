package cn.cug.sxy.domain.series.model.valobj;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @version 1.0
 * @Date 2025/7/21 15:07
 * @Description 车型系列编码值对象
 * @Author jerryhotton
 */

@Getter
@ToString
@EqualsAndHashCode
public class SeriesCode {

    private final String code;

    public SeriesCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Series code cannot be null or empty");
        }
        this.code = code;
    }

}
