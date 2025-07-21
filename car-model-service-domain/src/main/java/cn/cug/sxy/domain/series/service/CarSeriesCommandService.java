package cn.cug.sxy.domain.series.service;

import cn.cug.sxy.domain.series.adapter.repository.ICarSeriesRepository;
import cn.cug.sxy.domain.series.model.entity.CarSeriesEntity;
import cn.cug.sxy.domain.series.model.valobj.Brand;
import cn.cug.sxy.domain.series.model.valobj.SeriesCode;
import cn.cug.sxy.domain.series.model.valobj.SeriesId;
import org.springframework.stereotype.Service;

/**
 * @version 1.0
 * @Date 2025/7/21 15:35
 * @Description 车型系列命令服务实现
 * @Author jerryhotton
 */

@Service
public class CarSeriesCommandService implements ICarSeriesCommandService {

    private final ICarSeriesRepository carSeriesRepository;

    public CarSeriesCommandService(ICarSeriesRepository carSeriesRepository) {
        this.carSeriesRepository = carSeriesRepository;
    }

    @Override
    public CarSeriesEntity createCarSeries(SeriesId seriesId, SeriesCode seriesCode, Brand brand, String seriesName, String description) {
        CarSeriesEntity carSeriesEntity = CarSeriesEntity.builder()
                .seriesId(seriesId)
                .seriesCode(seriesCode)
                .brand(brand)
                .seriesName(seriesName)
                .description(description)
                .build();
        carSeriesRepository.save(carSeriesEntity);

        return carSeriesEntity;
    }

    @Override
    public boolean removeCarSeries(SeriesId seriesId) {
        return carSeriesRepository.remove(seriesId);
    }

}
