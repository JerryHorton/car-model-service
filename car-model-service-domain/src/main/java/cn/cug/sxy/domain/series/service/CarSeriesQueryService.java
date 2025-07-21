package cn.cug.sxy.domain.series.service;

import cn.cug.sxy.domain.series.adapter.repository.ICarSeriesRepository;
import cn.cug.sxy.domain.series.model.entity.CarSeriesEntity;
import cn.cug.sxy.domain.series.model.valobj.Brand;
import cn.cug.sxy.domain.series.model.valobj.SeriesCode;
import cn.cug.sxy.domain.series.model.valobj.SeriesId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @version 1.0
 * @Date 2025/7/21 16:01
 * @Description 车系查询服务实现
 * @Author jerryhotton
 */

@Service
public class CarSeriesQueryService implements ICarSeriesQueryService {

    private final ICarSeriesRepository carSeriesRepository;

    public CarSeriesQueryService(ICarSeriesRepository carSeriesRepository) {
        this.carSeriesRepository = carSeriesRepository;
    }

    @Override
    public Optional<CarSeriesEntity> getById(SeriesId seriesId) {
        return carSeriesRepository.findById(seriesId);
    }

    @Override
    public Optional<CarSeriesEntity> getByCode(SeriesCode seriesCode) {
        return carSeriesRepository.findByCode(seriesCode);
    }

    @Override
    public List<CarSeriesEntity> getByBrand(Brand brand) {
        return carSeriesRepository.findByBrand(brand);
    }

    @Override
    public List<CarSeriesEntity> getBySeriesNameLike(String seriesName) {
        return carSeriesRepository.findBySeriesNameLike(seriesName);
    }

    @Override
    public List<CarSeriesEntity> getAll() {
        return carSeriesRepository.findAll();
    }

    @Override
    public boolean isCodeExists(SeriesCode seriesCode) {
        return carSeriesRepository.existsByCode(seriesCode);
    }

}
