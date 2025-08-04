package cn.cug.sxy.domain.series.model.valobj;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @version 1.0
 * @Date 2025/7/21 15:08
 * @Description 车型系列ID值对象
 * @Author jerryhotton
 */

@Getter
@ToString
@EqualsAndHashCode
public class SeriesId {

    private final Long id;

    public SeriesId(Long id) {
        if (id == null || id < 0) {
            throw new IllegalArgumentException("Series ID cannot be null or less than 0");
        }
        this.id = id;
    }

}
