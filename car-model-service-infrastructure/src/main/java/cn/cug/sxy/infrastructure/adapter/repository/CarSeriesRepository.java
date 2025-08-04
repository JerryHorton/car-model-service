package cn.cug.sxy.infrastructure.adapter.repository;

import cn.cug.sxy.domain.series.adapter.repository.ICarSeriesRepository;
import cn.cug.sxy.domain.series.model.entity.CarSeriesEntity;
import cn.cug.sxy.domain.series.model.valobj.Brand;
import cn.cug.sxy.domain.series.model.valobj.SeriesCode;
import cn.cug.sxy.domain.series.model.valobj.SeriesId;
import cn.cug.sxy.infrastructure.converter.CarSeriesConverter;
import cn.cug.sxy.infrastructure.dao.ICarSeriesDao;
import cn.cug.sxy.infrastructure.dao.po.CarSeriesPO;
import cn.cug.sxy.types.common.Constants;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @version 1.0
 * @Date 2025/7/21 15:33
 * @Description 车型系列仓储实现
 * @Author jerryhotton
 */

@Repository
public class CarSeriesRepository extends AbstractRepository implements ICarSeriesRepository {

    private final ICarSeriesDao carSeriesDao;

    public CarSeriesRepository(ICarSeriesDao carSeriesDao) {
        this.carSeriesDao = carSeriesDao;
    }

    @Override
    public void save(CarSeriesEntity carSeriesEntity) {
        CarSeriesPO carSeriesPO = CarSeriesConverter.toPO(carSeriesEntity);
        carSeriesDao.insert(carSeriesPO);
        if (carSeriesPO.getId() != null && carSeriesEntity.getId() == null) {
            carSeriesEntity.setId(new SeriesId(carSeriesPO.getId()));
        }
    }

    @Override
    public boolean remove(SeriesId seriesId) {
        if (seriesId == null) {
            return false;
        }
        int count = carSeriesDao.deleteBySeriesId(seriesId.getId());

        return count == 1;
    }

    @Override
    public Optional<CarSeriesEntity> findById(SeriesId seriesId) {
        if (seriesId == null) {
            return Optional.empty();
        }
        String cacheKey = Constants.RedisKey.CAR_SERIES_BY_ID_KEY + seriesId.getId();
        CarSeriesPO carSeriesPO = getDataFromCacheOrDB(cacheKey, () -> carSeriesDao.selectBySeriesId(seriesId.getId()));
        if (carSeriesPO == null) {
            return Optional.empty();
        }

        return Optional.of(CarSeriesConverter.toEntity(carSeriesPO));
    }

    @Override
    public Optional<CarSeriesEntity> findByCode(SeriesCode seriesCode) {
        if (seriesCode == null) {
            return Optional.empty();
        }
        String cacheKey = Constants.RedisKey.CAR_SERIES_BY_CODE_KEY + seriesCode.getCode();
        CarSeriesPO carSeriesPO = getDataFromCacheOrDB(cacheKey, () -> carSeriesDao.selectBySeriesCode(seriesCode.getCode()));
        if (null == carSeriesPO) {
            return Optional.empty();
        }

        return Optional.of(CarSeriesConverter.toEntity(carSeriesPO));
    }

    @Override
    public List<CarSeriesEntity> findByBrand(Brand brand) {
        if (brand == null) {
            return Collections.emptyList();
        }
        String cacheKey = Constants.RedisKey.CAR_SERIES_BY_BRAND_KEY + brand.getName();
        List<CarSeriesPO> carSeriesPOList = getDataFromCacheOrDB(cacheKey, () -> carSeriesDao.selectBySeriesBrand(brand.getName()));
        if (carSeriesPOList == null || carSeriesPOList.isEmpty()) {
            return Collections.emptyList();
        }

        return CarSeriesConverter.toEntityList(carSeriesPOList);
    }

    @Override
    public List<CarSeriesEntity> findBySeriesNameLike(String seriesName) {
        if (null == seriesName || seriesName.isEmpty()) {
            return Collections.emptyList();
        }
        // 转义特殊字符，防止意外的通配符行为
        seriesName = seriesName.replace("%", "\\%").replace("_", "\\_");
        String seriesNameLike = "%" + seriesName + "%";
        List<CarSeriesPO> carSeriesPOList = carSeriesDao.selectBySeriesNameLike(seriesNameLike);
        if (null == carSeriesPOList || carSeriesPOList.isEmpty()) {
            return Collections.emptyList();
        }

        return CarSeriesConverter.toEntityList(carSeriesPOList);
    }

    @Override
    public List<CarSeriesEntity> findAll() {
        List<CarSeriesPO> carSeriesPOList = carSeriesDao.selectAll();
        if (null == carSeriesPOList || carSeriesPOList.isEmpty()) {
            return Collections.emptyList();
        }

        return CarSeriesConverter.toEntityList(carSeriesPOList);
    }

    @Override
    public boolean existsByCode(SeriesCode seriesCode) {
        CarSeriesPO carSeriesPO = carSeriesDao.selectBySeriesCode(seriesCode.getCode());

        return carSeriesPO != null;
    }

    @Override
    public int update(CarSeriesEntity carSeriesEntity) {
        CarSeriesPO carSeriesPO = CarSeriesConverter.toPO(carSeriesEntity);
        return carSeriesDao.update(carSeriesPO);
    }

}
