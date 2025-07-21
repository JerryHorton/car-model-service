package cn.cug.sxy.domain.series.model.aggregate;

import cn.cug.sxy.domain.series.model.entity.CarSeriesEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version 1.0
 * @Date 2025/7/21 15:12
 * @Description 车型系列聚合根
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarSeriesAggregate {

    private CarSeriesEntity carSeriesEntity;

}
