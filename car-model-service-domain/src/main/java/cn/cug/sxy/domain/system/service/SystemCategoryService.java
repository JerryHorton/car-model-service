package cn.cug.sxy.domain.system.service;

import cn.cug.sxy.domain.system.adapter.repository.ISystemCategoryRepository;
import cn.cug.sxy.domain.system.model.entity.SystemCategoryEntity;
import cn.cug.sxy.domain.system.model.valobj.CategoryCode;
import cn.cug.sxy.domain.system.model.valobj.CategoryId;
import cn.cug.sxy.domain.system.model.valobj.CategoryStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @version 1.0
 * @Date 2025/7/28 09:15
 * @Description 系统大类服务实现
 * @Author jerryhotton
 */

@Service
public class SystemCategoryService implements ISystemCategoryService {

    private final ISystemCategoryRepository systemCategoryRepository;

    public SystemCategoryService(ISystemCategoryRepository systemCategoryRepository) {
        this.systemCategoryRepository = systemCategoryRepository;
    }

    @Override
    public SystemCategoryEntity createCategory(CategoryCode categoryCode, String categoryName, String categoryNameEn, Integer sortOrder, String creator) {
        // 检查编码是否已存在
        if (systemCategoryRepository.existsByCategoryCode(categoryCode)) {
            throw new IllegalArgumentException("系统大类编码[" + categoryCode.getCode() + "]已存在");
        }
        // 创建系统大类实体
        SystemCategoryEntity entity = SystemCategoryEntity.create(categoryCode, categoryName, categoryNameEn, sortOrder, creator);
        // 保存实体
        systemCategoryRepository.save(entity);

        return entity;
    }

    @Override
    public boolean updateCategory(CategoryId categoryId, CategoryCode categoryCode, String categoryName, String categoryNameEn, Integer sortOrder) {
        // 检查编码是否已存在
        if (systemCategoryRepository.existsByCategoryCode(categoryCode)) {
            throw new IllegalArgumentException("系统大类编码[" + categoryCode.getCode() + "]已存在");
        }
        Optional<SystemCategoryEntity> systemCategoryEntityOpt = systemCategoryRepository.findById(categoryId);
        if (!systemCategoryEntityOpt.isPresent()) {
            return false;
        }
        SystemCategoryEntity systemCategoryEntity = systemCategoryEntityOpt.get();
        systemCategoryEntity.update(categoryCode, categoryName, categoryNameEn, sortOrder);

        return systemCategoryRepository.update(systemCategoryEntity) > 0;
    }

    @Override
    public boolean updateStatus(CategoryId categoryId, CategoryStatus status) {
        Optional<SystemCategoryEntity> systemCategoryEntityOpt = systemCategoryRepository.findById(categoryId);
        if (!systemCategoryEntityOpt.isPresent()) {
            return false;
        }
        SystemCategoryEntity systemCategoryEntity = systemCategoryEntityOpt.get();
        systemCategoryEntity.updateStatus(status);
        return systemCategoryRepository.update(systemCategoryEntity) > 0;
    }

    @Override
    public SystemCategoryEntity findById(CategoryId categoryId) {
        Optional<SystemCategoryEntity> systemCategoryEntityOpt = systemCategoryRepository.findById(categoryId);
        if (!systemCategoryEntityOpt.isPresent()) {
            return null;
        }

        return systemCategoryEntityOpt.get();
    }

    @Override
    public SystemCategoryEntity findByCode(CategoryCode categoryCode) {
        Optional<SystemCategoryEntity> systemCategoryEntityOpt = systemCategoryRepository.findByCategoryCode(categoryCode);
        if (!systemCategoryEntityOpt.isPresent()) {
            return null;
        }

        return systemCategoryEntityOpt.get();
    }

    @Override
    public List<SystemCategoryEntity> findAll() {
        return systemCategoryRepository.findAll();
    }

    @Override
    public List<SystemCategoryEntity> findByStatus(CategoryStatus status) {
        return systemCategoryRepository.findByStatus(status.getCode());
    }

    @Override
    public List<SystemCategoryEntity> findByNameLike(String categoryName) {
        return systemCategoryRepository.findByCategoryNameLike(categoryName);
    }

    @Override
    public boolean isCodeExists(CategoryCode categoryCode) {
        return systemCategoryRepository.existsByCategoryCode(categoryCode);
    }

}
