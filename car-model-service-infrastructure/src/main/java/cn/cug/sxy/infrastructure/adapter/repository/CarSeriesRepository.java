package cn.cug.sxy.infrastructure.adapter.repository;

import cn.cug.sxy.domain.series.adapter.repository.ICarSeriesRepository;
import cn.cug.sxy.domain.series.model.entity.CarSeriesEntity;
import cn.cug.sxy.domain.series.model.valobj.Brand;
import cn.cug.sxy.domain.series.model.valobj.SeriesCode;
import cn.cug.sxy.domain.series.model.valobj.SeriesId;
import cn.cug.sxy.infrastructure.converter.CarSeriesConverter;
import cn.cug.sxy.infrastructure.dao.ICarSeriesDao;
import cn.cug.sxy.infrastructure.dao.po.CarSeries;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/7/21 15:33
 * @Description 车型系列仓储实现
 * @Author jerryhotton
 */

@Repository
public class CarSeriesRepository implements ICarSeriesRepository {

    private final ICarSeriesDao carSeriesDao;

    public CarSeriesRepository(ICarSeriesDao carSeriesDao) {
        this.carSeriesDao = carSeriesDao;
    }

    @Override
    public void save(CarSeriesEntity carSeriesEntity) {
        CarSeries carSeries = CarSeriesConverter.toPO(carSeriesEntity);
        carSeriesDao.insert(carSeries);
    }

    @Override
    public boolean remove(SeriesId seriesId) {
        int count = carSeriesDao.deleteBySeriesId(seriesId.getId());

        return count == 1;
    }

    @Override
    public Optional<CarSeriesEntity> findById(SeriesId seriesId) {
        CarSeries carSeries = carSeriesDao.selectBySeriesId(seriesId.getId());
        if (null == carSeries) {
            return Optional.empty();
        }

        return Optional.of(CarSeriesConverter.toEntity(carSeries));
    }

    @Override
    public Optional<CarSeriesEntity> findByCode(SeriesCode seriesCode) {
        CarSeries carSeries = carSeriesDao.selectBySeriesCode(seriesCode.getCode());
        if (null == carSeries) {
            return Optional.empty();
        }

        return Optional.of(CarSeriesConverter.toEntity(carSeries));
    }

    @Override
    public List<CarSeriesEntity> findByBrand(Brand brand) {
        List<CarSeries> carSeriesList = carSeriesDao.selectBySeriesBrand(brand.getName());
        if (null == carSeriesList || carSeriesList.isEmpty()) {
            return Collections.emptyList();
        }

        return carSeriesList.stream()
                .map(CarSeriesConverter::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<CarSeriesEntity> findBySeriesNameLike(String seriesName) {
        // 转义特殊字符，防止意外的通配符行为
        seriesName = seriesName.replace("%", "\\%").replace("_", "\\_");
        String seriesNameLike = "%" + seriesName + "%";
        List<CarSeries> carSeriesList = carSeriesDao.selectBySeriesNameLike(seriesNameLike);
        if (null == carSeriesList || carSeriesList.isEmpty()) {
            return Collections.emptyList();
        }

        return carSeriesList.stream()
                .map(CarSeriesConverter::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<CarSeriesEntity> findAll() {
        List<CarSeries> carSeriesList = carSeriesDao.selectAll();
        if (null == carSeriesList || carSeriesList.isEmpty()) {
            return Collections.emptyList();
        }

        return carSeriesList.stream()
                .map(CarSeriesConverter::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByCode(SeriesCode seriesCode) {
        CarSeries carSeries = carSeriesDao.selectBySeriesCode(seriesCode.getCode());

        return carSeries != null;
    }

}
