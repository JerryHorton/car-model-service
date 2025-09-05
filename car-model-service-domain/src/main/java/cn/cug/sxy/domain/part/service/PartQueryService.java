package cn.cug.sxy.domain.part.service;

import cn.cug.sxy.domain.part.adapter.repository.IPartRepository;
import cn.cug.sxy.domain.part.model.entity.PartEntity;
import cn.cug.sxy.domain.part.model.valobj.PartCode;
import cn.cug.sxy.domain.part.model.valobj.PartId;
import cn.cug.sxy.domain.workhour.model.valobj.WorkHourId;
import cn.cug.sxy.types.enums.Status;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @version 1.0
 * @Date 2025/9/3
 * @Description 备件查询服务实现
 * @Author jerryhotton
 */
@Service
public class PartQueryService implements IPartQueryService {

    private final IPartRepository partRepository;

    public PartQueryService(IPartRepository partRepository) {
        this.partRepository = partRepository;
    }

    @Override
    public PartEntity getPartById(PartId partId) {
        Optional<PartEntity> partEntityOpt = partRepository.findById(partId);
        return partEntityOpt.orElse(null);
    }

    @Override
    public List<PartEntity> getAllParts() {
        return partRepository.findAll();
    }

    @Override
    public boolean isCodeExists(PartCode partCode) {
        return partRepository.existsByCode(partCode);
    }

    @Override
    public byte[] getPartHourTemplate() {
        return partRepository.getPartHourTemplate();
    }

}