package cn.cug.sxy.domain.workhour.service;

import cn.cug.sxy.domain.workhour.adapter.repository.IWorkHourRepository;
import cn.cug.sxy.domain.workhour.model.entity.WorkHourEntity;
import cn.cug.sxy.domain.workhour.model.valobj.WorkHourCode;
import cn.cug.sxy.domain.workhour.model.valobj.WorkHourId;
import cn.cug.sxy.domain.workhour.model.valobj.WorkHourStatus;
import cn.cug.sxy.domain.workhour.model.valobj.WorkHourType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @version 1.0
 * @Date 2025/1/27 10:00
 * @Description 工时查询服务实现
 * @Author jerryhotton
 */

@Service
public class WorkHourQueryService implements IWorkHourQueryService {

    private final IWorkHourRepository workHourRepository;

    public WorkHourQueryService(IWorkHourRepository workHourRepository) {
        this.workHourRepository = workHourRepository;
    }

    @Override
    public WorkHourEntity getById(WorkHourId workHourId) {
        Optional<WorkHourEntity> workHourEntityOpt = workHourRepository.findById(workHourId);
        return workHourEntityOpt.orElse(null);
    }

    @Override
    public WorkHourEntity getByCode(WorkHourCode workHourCode) {
        Optional<WorkHourEntity> workHourEntityOpt = workHourRepository.findByCode(workHourCode);
        return workHourEntityOpt.orElse(null);
    }

    @Override
    public List<WorkHourEntity> getByParentId(WorkHourId parentId) {
        return workHourRepository.findByParentId(parentId);
    }

    @Override
    public List<WorkHourEntity> getAllMainWorkHours() {
        return workHourRepository.findAllMainWorkHours();
    }

    @Override
    public List<WorkHourEntity> getByStatus(WorkHourStatus status) {
        return workHourRepository.findByStatus(status);
    }

    @Override
    public List<WorkHourEntity> getByType(WorkHourType type) {
        return workHourRepository.findByType(type);
    }

    @Override
    public List<WorkHourEntity> getWorkHourTree(WorkHourId workHourId) {
        return workHourRepository.findWorkHourTree(workHourId);
    }

    @Override
    public boolean isCodeExists(WorkHourCode workHourCode) {
        return workHourRepository.existsByCode(workHourCode);
    }

    @Override
    public byte[] getWorkHourTemplate() {
       return workHourRepository.getWorkHourTemplate();
    }

}