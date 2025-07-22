package cn.cug.sxy.domain.model.service;

import cn.cug.sxy.domain.model.adapter.repository.ICarModelRepository;
import cn.cug.sxy.domain.model.model.entity.CarModelEntity;
import cn.cug.sxy.domain.model.model.valobj.ModelCode;
import cn.cug.sxy.domain.model.model.valobj.ModelId;
import cn.cug.sxy.domain.model.model.valobj.ModelStatus;
import cn.cug.sxy.domain.model.model.valobj.PowerType;
import cn.cug.sxy.domain.series.model.valobj.Brand;
import cn.cug.sxy.domain.series.model.valobj.SeriesId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @version 1.0
 * @Date 2025/7/22 13:51
 * @Description 车型查询服务实现类
 * @Author jerryhotton
 */

@Service
public class CarModelQueryService implements ICarModelQueryService {

    private final ICarModelRepository carModelRepository;

    public CarModelQueryService(ICarModelRepository carModelRepository) {
        this.carModelRepository = carModelRepository;
    }

    @Override
    public CarModelEntity getById(ModelId modelId) {
        Optional<CarModelEntity> carModelEntityOpt = carModelRepository.findById(modelId);
        if (!carModelEntityOpt.isPresent()) {
            return null;
        }

        return carModelEntityOpt.get();
    }

    @Override
    public CarModelEntity getByCode(ModelCode modelCode) {
        Optional<CarModelEntity> carModelEntityOpt = carModelRepository.findByCode(modelCode);
        if (!carModelEntityOpt.isPresent()) {
            return null;
        }

        return carModelEntityOpt.get();
    }

    @Override
    public List<CarModelEntity> getByBrand(Brand brand) {
        return carModelRepository.findByBrand(brand);
    }

    @Override
    public List<CarModelEntity> getByPowerType(PowerType powerType) {
        return carModelRepository.findByPowerType(powerType);
    }

    @Override
    public List<CarModelEntity> getByStatus(ModelStatus status) {
        return carModelRepository.findByStatus(status);
    }

    @Override
    public List<CarModelEntity> getBySeriesId(SeriesId seriesId) {
        return carModelRepository.findBySeriesId(seriesId);
    }

    @Override
    public List<CarModelEntity> getByModelNameLike(String modelName) {
        return carModelRepository.findByModelNameLike(modelName);
    }

    @Override
    public List<CarModelEntity> getAll() {
        return carModelRepository.findAll();
    }

    @Override
    public boolean isCodeExists(ModelCode modelCode) {
        return carModelRepository.existsByCode(modelCode);
    }

}
