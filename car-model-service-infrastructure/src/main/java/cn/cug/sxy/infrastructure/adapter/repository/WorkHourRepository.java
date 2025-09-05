package cn.cug.sxy.infrastructure.adapter.repository;

import cn.cug.sxy.domain.part.model.valobj.PartId;
import cn.cug.sxy.domain.workhour.adapter.repository.IWorkHourRepository;
import cn.cug.sxy.domain.workhour.model.entity.WorkHourEntity;
import cn.cug.sxy.domain.workhour.model.valobj.*;
import cn.cug.sxy.infrastructure.converter.WorkHourConverter;
import cn.cug.sxy.infrastructure.dao.IPartHourDao;
import cn.cug.sxy.infrastructure.dao.IWorkHourDao;
import cn.cug.sxy.infrastructure.dao.po.PartHourPO;
import cn.cug.sxy.infrastructure.dao.po.WorkHourPO;
import cn.cug.sxy.infrastructure.local.excel.WorkHourDataValidator;
import cn.cug.sxy.infrastructure.local.excel.WorkHourExcelRowParser;
import cn.cug.sxy.infrastructure.minio.IFileStorageService;
import cn.cug.sxy.infrastructure.local.excel.ExcelUtils;
import cn.cug.sxy.types.enums.ResponseCode;
import cn.cug.sxy.types.enums.TemplateFileType;
import cn.cug.sxy.types.exception.AppException;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/1/27 10:00
 * @Description 工时仓储实现
 * @Author jerryhotton
 */

@Repository
public class WorkHourRepository implements IWorkHourRepository {

    private final IWorkHourDao workHourDao;
    private final IPartHourDao partHourDao;
    private final IFileStorageService fileStorageService;

    public WorkHourRepository(
            IWorkHourDao workHourDao,
            IPartHourDao partHourDao,
            IFileStorageService fileStorageService) {
        this.workHourDao = workHourDao;
        this.partHourDao = partHourDao;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public WorkHourEntity save(WorkHourEntity workHourEntity) {
        WorkHourPO workHourPO = WorkHourConverter.toPO(workHourEntity);
        workHourDao.insert(workHourPO);
        // 设置生成的ID
        if (workHourPO.getId() != null && workHourEntity.getId() == null) {
            workHourEntity.setId(new WorkHourId(workHourPO.getId()));
        }

        return workHourEntity;
    }

    @Override
    public Optional<WorkHourEntity> findById(WorkHourId workHourId) {
        if (workHourId == null) {
            return Optional.empty();
        }
        WorkHourPO workHourPO = workHourDao.selectById(workHourId.getId());
        if (workHourPO == null) {
            return Optional.empty();
        }

        return Optional.of(WorkHourConverter.toEntity(workHourPO));
    }

    @Override
    public Optional<WorkHourEntity> findByCode(WorkHourCode workHourCode) {
        if (workHourCode == null) {
            return Optional.empty();
        }
        WorkHourPO workHourPO = workHourDao.selectByCode(workHourCode.getCode());
        if (workHourPO == null) {
            return Optional.empty();
        }

        return Optional.of(WorkHourConverter.toEntity(workHourPO));
    }

    @Override
    public List<WorkHourEntity> findByParentId(WorkHourId parentId) {
        if (parentId == null) {
            return List.of();
        }
        List<WorkHourPO> workHourPOList = workHourDao.selectByParentId(parentId.getId());

        return workHourPOList.stream()
                .map(WorkHourConverter::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkHourEntity> findAllMainWorkHours() {
        List<WorkHourPO> workHourPOList = workHourDao.selectAllMainWorkHours();

        return workHourPOList.stream()
                .map(WorkHourConverter::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkHourEntity> findByStatus(WorkHourStatus status) {
        if (status == null) {
            return List.of();
        }
        List<WorkHourPO> workHourPOList = workHourDao.selectByStatus(status.getCode());

        return workHourPOList.stream()
                .map(WorkHourConverter::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkHourEntity> findByType(WorkHourType type) {
        if (type == null) {
            return List.of();
        }
        List<WorkHourPO> workHourPOList = workHourDao.selectByType(type.getCode());

        return WorkHourConverter.toEntityList(workHourPOList);
    }

    @Override
    public boolean existsByCode(WorkHourCode workHourCode) {
        if (workHourCode == null) {
            return false;
        }

        return workHourDao.existsByCode(workHourCode.getCode());
    }

    @Override
    public boolean remove(WorkHourId workHourId) {
        if (workHourId == null) {
            return false;
        }
        int count = workHourDao.deleteById(workHourId.getId());

        return count == 1;
    }

    @Override
    public int update(WorkHourEntity workHourEntity) {
        if (workHourEntity == null) {
            return 0;
        }
        WorkHourPO workHourPO = WorkHourConverter.toPO(workHourEntity);

        return workHourDao.update(workHourPO);
    }

    @Override
    public WorkHourEntity findWorkHourTree(WorkHourId workHourId) {
        if (workHourId == null) {
            return null;
        }
        // 先查找根节点
        Optional<WorkHourEntity> rootOpt = findById(workHourId);
        if (!rootOpt.isPresent()) {
            return null;
        }
        WorkHourEntity root = rootOpt.get();
        // 递归构建树结构
        buildWorkHourTree(root);

        return root;
    }

    @Override
    public List<WorkHourEntity> findWorkHourTreeByPartId(PartId partId) {
        if (partId == null) {
            return List.of();
        }
        // 查找关联的工时
        List<PartHourPO> partHourPOList = partHourDao.selectByPartId(partId.getId());
        if (partHourPOList.isEmpty()) {
            return List.of();
        }

        return partHourPOList.stream()
                .map(partHourPO -> findWorkHourTree(new WorkHourId(partHourPO.getHourId())))
                .collect(Collectors.toList());
    }

    @Override
    public String uploadTemplate(MultipartFile file) {
        return fileStorageService.uploadTemplate(file, TemplateFileType.WORK_HOUR.getType());
    }

    @Override
    public String getTemplateInfo() {
        return fileStorageService.getTemplateFileInfo(TemplateFileType.WORK_HOUR.getType());
    }

    @Override
    public List<WorkHourExcelData> readWorkHourExcel(MultipartFile file) throws IOException {
        return ExcelUtils.readExcel(file, new WorkHourExcelRowParser());
    }

    @Override
    public byte[] getWorkHourTemplate() {
        boolean exists = fileStorageService.isTemplateExists(TemplateFileType.WORK_HOUR.getType());
        if (!exists) {
            throw new AppException(ResponseCode.TEMPLATE_NOT_FOUND);
        }
        try {
            return fileStorageService.getTemplateFile(TemplateFileType.WORK_HOUR.getType());
        } catch (IOException e) {
            throw new AppException(ResponseCode.TEMPLATE_READ_ERROR, e);
        }
    }

    @Override
    public String validateWorkHourExcelData(WorkHourExcelData excelData) {
        // 校验工时Excel数据
        return WorkHourDataValidator.validateWorkHourData(excelData);
    }

    /**
     * 递归构建工时树
     */
    private void buildWorkHourTree(WorkHourEntity parent) {
        List<WorkHourEntity> children = findByParentId(parent.getId());
        children.sort(Comparator.comparing(WorkHourEntity::getStepOrder));
        if (!children.isEmpty()) {
            // 为每个子节点递归构建子树
            for (WorkHourEntity child : children) {
                buildWorkHourTree(child);
            }
            // 将子节点设置到父节点的children属性中
            parent.setChildren(children);
        }
    }

} 