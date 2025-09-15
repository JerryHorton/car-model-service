package cn.cug.sxy.infrastructure.adapter.repository;

import cn.cug.sxy.domain.part.adapter.repository.IUsagePartRepository;
import cn.cug.sxy.domain.part.model.entity.UsageBindPartExcelData;
import cn.cug.sxy.domain.part.model.valobj.PartId;
import cn.cug.sxy.domain.usage.model.entity.UsagePartEntity;
import cn.cug.sxy.domain.usage.model.valobj.UsageId;
import cn.cug.sxy.infrastructure.converter.UsagePartConverter;
import cn.cug.sxy.infrastructure.dao.IPartDao;
import cn.cug.sxy.infrastructure.dao.IUsagePartDao;
import cn.cug.sxy.infrastructure.dao.po.PartPO;
import cn.cug.sxy.infrastructure.dao.po.UsagePartPO;
import cn.cug.sxy.infrastructure.local.excel.ExcelUtils;
import cn.cug.sxy.infrastructure.local.excel.UsageBindPartExcelRowParser;
import cn.cug.sxy.infrastructure.local.excel.UsagePartDataValidator;
import cn.cug.sxy.infrastructure.minio.IFileStorageService;
import cn.cug.sxy.types.enums.ResponseCode;
import cn.cug.sxy.types.enums.TemplateFileType;
import cn.cug.sxy.types.exception.AppException;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/9/5 09:29
 * @Description 用法备件关联仓储实现
 * @Author jerryhotton
 */

@Repository
public class UsagePartRepository implements IUsagePartRepository {

    private final IUsagePartDao usagePartDao;
    private final IPartDao partDao;
    private final IFileStorageService fileStorageService;

    public UsagePartRepository(
            IUsagePartDao usagePartDao,
            IPartDao partDao,
            IFileStorageService fileStorageService) {
        this.usagePartDao = usagePartDao;
        this.partDao = partDao;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public UsagePartEntity save(UsagePartEntity usagePartEntity) {
        UsagePartPO usagePartPO = UsagePartConverter.toPO(usagePartEntity);
        if (usagePartPO.getId() == null) {
            usagePartDao.insert(usagePartPO);
            if (usagePartPO.getId() != null) {
                usagePartEntity.setId(usagePartPO.getId());
            }
        } else {
            usagePartDao.updateCount(usagePartPO);
        }

        return usagePartEntity;
    }

    @Override
    public boolean batchSave(List<UsagePartEntity> usagePartEntities) {
        if (usagePartEntities == null || usagePartEntities.isEmpty()) {
            return false;
        }
        List<UsagePartPO> usagePartPOList = usagePartEntities.stream()
                .map(UsagePartConverter::toPO)
                .collect(Collectors.toList());
        int result = usagePartDao.batchInsert(usagePartPOList);
        return result > 0;
    }

    @Override
    public boolean deleteByUsageIdAndPartId(UsageId usageId, PartId partId) {
        if (usageId == null || partId == null) {
            return false;
        }
        int result = usagePartDao.deleteByUsageIdAndPartId(usageId.getId(), partId.getId());
        return result > 0;
    }

    @Override
    public boolean deleteByUsageId(UsageId usageId) {
        if (usageId == null) {
            return false;
        }
        int result = usagePartDao.deleteByUsageId(usageId.getId());
        return result > 0;
    }

    @Override
    public boolean deleteByPartId(PartId partId) {
        if (partId == null) {
            return false;
        }
        int result = usagePartDao.deleteByPartId(partId.getId());
        return result > 0;
    }

    @Override
    public List<UsagePartEntity> findByUsageId(UsageId usageId) {
        if (usageId == null) {
            return List.of();
        }
        List<UsagePartPO> usagePartPOList = usagePartDao.selectByUsageId(usageId.getId());
        List<UsagePartEntity> result = new ArrayList<>();

        for (UsagePartPO po : usagePartPOList) {
            UsagePartEntity entity = UsagePartConverter.toEntity(po);
            // 查询备件信息
            PartPO partPO = partDao.selectById(po.getPartId());
            if (partPO != null) {
                entity.setPartCode(partPO.getPartCode());
                entity.setPartName(partPO.getPartName());
            }
            result.add(entity);
        }

        return result;
    }

    @Override
    public List<UsagePartEntity> findByPartId(PartId partId) {
        if (partId == null) {
            return List.of();
        }
        List<UsagePartPO> usagePartPOList = usagePartDao.selectByPartId(partId.getId());
        return UsagePartConverter.toEntityList(usagePartPOList);
    }

    @Override
    public Optional<UsagePartEntity> findByUsageIdAndPartId(UsageId usageId, PartId partId) {
        if (usageId == null || partId == null) {
            return Optional.empty();
        }
        UsagePartPO usagePartPO = usagePartDao.selectByUsageIdAndPartId(usageId.getId(), partId.getId());
        if (usagePartPO == null) {
            return Optional.empty();
        }
        return Optional.of(UsagePartConverter.toEntity(usagePartPO));
    }

    @Override
    public boolean exists(UsageId usageId, PartId partId) {
        if (usageId == null || partId == null) {
            return false;
        }

        return usagePartDao.exists(usageId.getId(), partId.getId());
    }

    @Override
    public List<UsageBindPartExcelData> readUsagePartExcel(MultipartFile file) throws IOException {
        return ExcelUtils.readExcel(file, new UsageBindPartExcelRowParser());
    }

    @Override
    public String validateUsagePartData(UsageBindPartExcelData data) {
        return UsagePartDataValidator.validateUsagePartData(data);
    }

    @Override
    public String uploadTemplate(MultipartFile file) {
        return fileStorageService.uploadTemplate(file, TemplateFileType.USAGE_PART.getType());
    }

    @Override
    public String getTemplateInfo() {
        return fileStorageService.getTemplateFileInfo(TemplateFileType.USAGE_PART.getType());
    }

    @Override
    public boolean isTemplateExists() {
        return fileStorageService.isTemplateExists(TemplateFileType.USAGE_PART.getType());
    }

    @Override
    public byte[] getUsagePartTemplate() throws IOException {
        boolean exists = fileStorageService.isTemplateExists(TemplateFileType.USAGE_PART.getType());
        if (!exists) {
            throw new AppException(ResponseCode.TEMPLATE_NOT_FOUND);
        }
        try {
            return fileStorageService.getTemplateFile(TemplateFileType.USAGE_PART.getType());
        } catch (IOException e) {
            throw new AppException(ResponseCode.TEMPLATE_READ_ERROR, e);
        }
    }

}
