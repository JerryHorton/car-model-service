package cn.cug.sxy.domain.system.service;

import cn.cug.sxy.domain.system.adapter.repository.ISystemGroupRepository;
import cn.cug.sxy.domain.system.model.entity.SystemGroupEntity;
import cn.cug.sxy.domain.system.model.valobj.CategoryId;
import cn.cug.sxy.domain.system.model.valobj.GroupCode;
import cn.cug.sxy.domain.system.model.valobj.GroupId;
import cn.cug.sxy.domain.system.model.valobj.GroupStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @version 1.0
 * @Date 2025/7/28 13:44
 * @Description 系统分组服务实现
 * @Author jerryhotton
 */

@Service
public class SystemGroupService implements ISystemGroupService {

    private final ISystemGroupRepository systemGroupRepository;

    public SystemGroupService(ISystemGroupRepository systemGroupRepository) {
        this.systemGroupRepository = systemGroupRepository;
    }

    @Override
    public SystemGroupEntity createGroup(CategoryId categoryId, GroupCode groupCode, String groupName, String groupNameEn, Integer sortOrder, String creator) {
        // 检查编码是否已存在
        if (systemGroupRepository.existsByGroupCode(groupCode)) {
            throw new IllegalArgumentException("系统分组编码[" + groupCode.getCode() + "]已存在");
        }
        // 创建系统分组实体
        SystemGroupEntity entity = SystemGroupEntity.create(categoryId, groupCode, groupName, groupNameEn, sortOrder, creator);
        // 保存实体
        systemGroupRepository.save(entity);

        return entity;
    }

    @Override
    public boolean updateGroup(GroupId groupId, CategoryId categoryId, GroupCode groupCode, String groupName, String groupNameEn, Integer sortOrder) {
        // 查找系统分组
        Optional<SystemGroupEntity> optionalEntity = systemGroupRepository.findByGroupCode(groupCode);
        if (!optionalEntity.isPresent()) {
            return false;
        }
        // 更新系统分组
        SystemGroupEntity entity = optionalEntity.get();
        entity.update(categoryId, groupCode, groupName, groupNameEn, sortOrder);

        // 保存更新
        return systemGroupRepository.update(entity) > 0;
    }

    @Override
    public boolean updateStatus(GroupCode groupCode, GroupStatus status) {
        // 查找系统分组
        Optional<SystemGroupEntity> optionalEntity = systemGroupRepository.findByGroupCode(groupCode);
        if (!optionalEntity.isPresent()) {
            return false;
        }
        // 更新状态
        SystemGroupEntity entity = optionalEntity.get();
        entity.updateStatus(status);

        // 保存更新
        return systemGroupRepository.update(entity) > 0;
    }

    @Override
    public SystemGroupEntity findByCode(GroupCode groupCode) {
        if (groupCode == null) {
            return null;
        }
        Optional<SystemGroupEntity> optionalEntityOpt = systemGroupRepository.findByGroupCode(groupCode);
        if (!optionalEntityOpt.isPresent()) {
            return null;
        }
        return optionalEntityOpt.get();
    }

    @Override
    public List<SystemGroupEntity> findByCategoryId(CategoryId categoryId) {
        if (categoryId == null) {
            return Collections.emptyList();
        }

        return systemGroupRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<SystemGroupEntity> findAll() {
        return systemGroupRepository.findAll();
    }

    @Override
    public List<SystemGroupEntity> findByStatus(GroupStatus status) {
        if (status == null) {
            return Collections.emptyList();
        }

        return systemGroupRepository.findByStatus(status.getCode());
    }

    @Override
    public List<SystemGroupEntity> findByNameLike(String groupName) {
        if (groupName == null || groupName.isEmpty()) {
            return Collections.emptyList();
        }

        return systemGroupRepository.findByGroupNameLike(groupName);
    }

    @Override
    public boolean isCodeExists(GroupCode groupCode) {
        return systemGroupRepository.existsByGroupCode(groupCode);
    }

    @Override
    public boolean deleteGroup(GroupId groupId) {
        if (groupId == null) {
            return false;
        }

        return systemGroupRepository.deleteById(groupId) > 0;
    }

    @Override
    public boolean logicalDeleteGroup(GroupId groupId) {
        if (groupId == null) {
            return false;
        }

        return systemGroupRepository.logicalDeleteById(groupId) > 0;
    }

    @Override
    public boolean deleteGroupsByCategoryId(CategoryId categoryId) {
        if (categoryId == null) {
            return false;
        }

        return systemGroupRepository.deleteByCategoryId(categoryId) > 0;
    }

    @Override
    public boolean logicalDeleteGroupsByCategoryId(CategoryId categoryId) {
        if (categoryId == null) {
            return false;
        }

        return systemGroupRepository.logicalDeleteByCategoryId(categoryId) > 0;
    }

}
