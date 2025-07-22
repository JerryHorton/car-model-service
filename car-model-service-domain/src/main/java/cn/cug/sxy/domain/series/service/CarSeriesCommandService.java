package cn.cug.sxy.domain.series.service;

import cn.cug.sxy.domain.series.adapter.repository.ICarSeriesRepository;
import cn.cug.sxy.domain.series.model.entity.CarSeriesEntity;
import cn.cug.sxy.domain.series.model.valobj.Brand;
import cn.cug.sxy.domain.series.model.valobj.SeriesCode;
import cn.cug.sxy.domain.series.model.valobj.SeriesId;
import cn.cug.sxy.types.enums.ResponseCode;
import cn.cug.sxy.types.exception.AppException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @version 1.0
 * @Date 2025/7/21 15:35
 * @Description 车型系列命令服务实现
 * @Author jerryhotton
 */

@Service
public class CarSeriesCommandService implements ICarSeriesCommandService {

    private final Logger logger = LoggerFactory.getLogger(CarSeriesCommandService.class);

    private final ICarSeriesRepository carSeriesRepository;

    public CarSeriesCommandService(ICarSeriesRepository carSeriesRepository) {
        this.carSeriesRepository = carSeriesRepository;
    }

    @Override
    public CarSeriesEntity createCarSeries(SeriesCode seriesCode, Brand brand, String seriesName, String description) {
        logger.info("创建车系 seriesCode={}, brand={}, seriesName={}, description={}", seriesCode, brand, seriesName, description);
        if (carSeriesRepository.existsByCode(seriesCode)) {
            logger.warn("创建车系失败，车系编码已存在 seriesCode={}", seriesCode);
            throw new AppException(ResponseCode.CAR_SERIES_CODE_EXISTS_ERROR);
        }
        CarSeriesEntity carSeriesEntity = CarSeriesEntity.create(
                seriesCode,
                brand,
                seriesName,
                description
        );
        carSeriesRepository.save(carSeriesEntity);
        logger.info("创建车系成功 seriesCode={}, brand={}, seriesName={}, description={}", seriesCode, brand, seriesName, description);

        return carSeriesEntity;
    }

    @Override
    public boolean removeCarSeries(SeriesId seriesId) {
        logger.info("删除车系 seriesId={}", seriesId);
        boolean removed = carSeriesRepository.remove(seriesId);
        logger.info("删除车系 seriesId={}, removed={}", seriesId, removed);

        return removed;
    }

    @Override
    public int updateCarSeries(CarSeriesEntity carSeriesEntity) {
        SeriesId seriesId = carSeriesEntity.getSeriesId();
        logger.info("更新车系 seriesId={}", seriesId);
        int updateCount = carSeriesRepository.update(carSeriesEntity);
        logger.info("更新车系成功 seriesId={}, updateCount={}", seriesId, updateCount);

        return updateCount;
    }

}
