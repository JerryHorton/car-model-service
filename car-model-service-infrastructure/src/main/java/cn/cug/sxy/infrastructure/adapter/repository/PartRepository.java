package cn.cug.sxy.infrastructure.adapter.repository;

import cn.cug.sxy.domain.part.adapter.repository.IPartRepository;
import cn.cug.sxy.domain.part.model.entity.PartBindHourExcelData;
import cn.cug.sxy.domain.part.model.entity.PartEntity;
import cn.cug.sxy.domain.part.model.valobj.PartCode;
import cn.cug.sxy.domain.part.model.valobj.PartId;
import cn.cug.sxy.domain.workhour.model.valobj.WorkHourId;
import cn.cug.sxy.infrastructure.converter.PartConverter;
import cn.cug.sxy.infrastructure.dao.IPartDao;
import cn.cug.sxy.infrastructure.dao.IPartHourDao;
import cn.cug.sxy.infrastructure.dao.IWorkHourDao;
import cn.cug.sxy.infrastructure.dao.po.PartHourPO;
import cn.cug.sxy.infrastructure.dao.po.PartPO;
import cn.cug.sxy.infrastructure.dao.po.WorkHourPO;
import cn.cug.sxy.infrastructure.local.excel.ExcelUtils;
import cn.cug.sxy.infrastructure.local.excel.PartBindHourExcelRowParser;
import cn.cug.sxy.infrastructure.local.excel.PartHourDataValidator;
import cn.cug.sxy.infrastructure.minio.IFileStorageService;
import cn.cug.sxy.types.enums.ResponseCode;
import cn.cug.sxy.types.enums.Status;
import cn.cug.sxy.types.enums.TemplateFileType;
import cn.cug.sxy.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/9/3
 * @Description 备件仓库实现
 * @Author jerryhotton
 */
@Slf4j
@Repository
public class PartRepository implements IPartRepository {

    private final IPartDao partDao;
    private final IPartHourDao partHourDao;
    private final IWorkHourDao workHourDao;
    private final IFileStorageService fileStorageService;

    public PartRepository(
            IPartDao partDao,
            IPartHourDao partHourDao,
            IWorkHourDao workHourDao,
            IFileStorageService fileStorageService) {
        this.partDao = partDao;
        this.partHourDao = partHourDao;
        this.workHourDao = workHourDao;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public PartEntity save(PartEntity partEntity) {
        PartPO partPO = PartConverter.toPO(partEntity);
        if (partPO.getId() == null) {
            partDao.insert(partPO);
            if (partPO.getId() != null && partEntity.getId() == null) {
                partEntity.setId(new PartId(partPO.getId()));
            }
        } else {
            partDao.update(partPO);
        }

        return partEntity;
    }

    @Override
    public Optional<PartEntity> findById(PartId partId) {
        if (partId == null) {
            return Optional.empty();
        }
        PartPO partPO = partDao.selectById(partId.getId());
        if (partPO == null) {
            return Optional.empty();
        }

        return Optional.of(PartConverter.toEntity(partPO));
    }

    @Override
    public Optional<PartEntity> findByCode(PartCode partCode) {
        if (partCode == null) {
            return Optional.empty();
        }
        PartPO partPO = partDao.selectByCode(partCode.getCode());
        if (partPO == null) {
            return Optional.empty();
        }

        return Optional.of(PartConverter.toEntity(partPO));
    }

    @Override
    public boolean existsByCode(PartCode partCode) {
        if (partCode == null) {
            return false;
        }

        return partDao.existsByCode(partCode.getCode());
    }

    @Override
    public boolean enable(PartId partId) {
        if (partId == null) {
            return false;
        }

        PartPO partPO = partDao.selectById(partId.getId());
        if (partPO == null) {
            return false;
        }

        partPO.setStatus(Status.ENABLED.getCode());
        int result = partDao.update(partPO);

        return result > 0;
    }

    @Override
    public boolean disable(PartId partId) {
        if (partId == null) {
            return false;
        }

        PartPO partPO = partDao.selectById(partId.getId());
        if (partPO == null) {
            return false;
        }

        partPO.setStatus(Status.DISABLED.getCode());
        int result = partDao.update(partPO);

        return result > 0;
    }

    @Override
    public boolean delete(PartId partId) {
        if (partId == null) {
            return false;
        }
        int result = partDao.delete(partId.getId());

        return result > 0;
    }

    @Override
    public List<PartEntity> findAll() {
        List<PartPO> partPOList = partDao.selectAll();

        return PartConverter.toEntityList(partPOList);
    }

    @Override
    public List<PartEntity> findByStatus(Status status) {
        if (status == null) {
            return List.of();
        }

        List<PartPO> partPOList = partDao.selectByStatus(status.getCode());

        return PartConverter.toEntityList(partPOList);
    }

    @Override
    public List<PartBindHourExcelData> readPartHourExcel(MultipartFile file) throws IOException {
        return ExcelUtils.readExcel(file, new PartBindHourExcelRowParser());
    }

    @Override
    public String validatePartHourData(PartBindHourExcelData data) {
        return PartHourDataValidator.validatePartHourData(data);
    }

    @Override
    public boolean bindPartHour(PartId partId, String hourCode) {
        if (partId == null || hourCode == null || hourCode.isEmpty()) {
            return false;
        }
        // 查询工时
        WorkHourPO workHourPO = workHourDao.selectByCode(hourCode);
        if (workHourPO == null) {
            throw new AppException(ResponseCode.PART_BIND_HOUR_NOT_FOUND_ERROR);
        }
        // 验证是否为主工时
        if (workHourPO.getParentId() != null) {
            throw new AppException(ResponseCode.PART_BIND_HOUR_NOT_MAIN_ERROR);
        }
        // 检查是否已绑定
        if (partHourDao.exists(partId.getId(), workHourPO.getId())) {
            throw new AppException(ResponseCode.PART_BIND_DUPLICATE_ERROR);
        }
        // 绑定工时
        PartHourPO partHourPO = new PartHourPO();
        partHourPO.setPartId(partId.getId());
        partHourPO.setHourId(workHourPO.getId());
        partHourDao.insert(partHourPO);

        return true;
    }

    @Override
    public List<WorkHourId> findHourIdsByPartId(PartId partId) {
        if (partId == null) {
            return List.of();
        }
        List<PartHourPO> partHourPOList = partHourDao.selectByPartId(partId.getId());

        return partHourPOList.stream()
                .map(po -> new WorkHourId(po.getHourId()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean unbindHour(PartId partId, WorkHourId hourId) {
        if (partId == null || hourId == null) {
            return false;
        }
        int result = partHourDao.deleteByPartIdAndHourId(partId.getId(), hourId.getId());

        return result > 0;
    }

    @Override
    public String uploadTemplate(MultipartFile file) {
        return fileStorageService.uploadTemplate(file, TemplateFileType.PART_HOUR.getType());
    }

    @Override
    public String getTemplateInfo() {
        return fileStorageService.getTemplateFileInfo(TemplateFileType.PART_HOUR.getType());
    }

    @Override
    public boolean isTemplateExists() {
        return fileStorageService.isTemplateExists(TemplateFileType.PART_HOUR.getType());
    }

    @Override
    public byte[] getPartHourTemplate() {
        boolean exists = fileStorageService.isTemplateExists(TemplateFileType.PART_HOUR.getType());
        if (!exists) {
            throw new AppException(ResponseCode.TEMPLATE_NOT_FOUND);
        }
        try {
            return fileStorageService.getTemplateFile(TemplateFileType.WORK_HOUR.getType());
        } catch (IOException e) {
            throw new AppException(ResponseCode.TEMPLATE_READ_ERROR, e);
        }
    }

}