package cn.cug.sxy.infrastructure.adapter.repository;

import cn.cug.sxy.domain.usage.adapter.repository.IUsageRepository;
import cn.cug.sxy.domain.usage.model.entity.UsageEntity;
import cn.cug.sxy.domain.usage.model.valobj.UsageId;
import cn.cug.sxy.infrastructure.converter.UsageConverter;
import cn.cug.sxy.infrastructure.dao.IUsageDao;
import cn.cug.sxy.infrastructure.dao.po.UsagePO;
import cn.cug.sxy.infrastructure.minio.IFileStorageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/8/6 10:53
 * @Description 用法仓储实现
 * @Author jerryhotton
 */

@Repository
public class UsageRepository implements IUsageRepository {

    private final IUsageDao usageDao;
    private final IFileStorageService fileStorageService;

    public UsageRepository(
            IUsageDao usageDao,
            IFileStorageService fileStorageService) {
        this.usageDao = usageDao;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public UsageEntity save(UsageEntity usage) {
        if (usage == null) {
            throw new IllegalArgumentException("用法实体不能为空");
        }
        UsagePO usagePO = UsageConverter.toPO(usage);
        if (usage.getId() == null) {
            // 插入操作
            usageDao.insert(usagePO);
            if (usagePO.getId() != null) {
                usage.setId(new UsageId(usagePO.getId()));
            }
        } else {
            // 更新操作
            usageDao.update(usagePO);
        }

        return usage;
    }

    @Override
    public UsageEntity save(UsageEntity usage, MultipartFile explodedViewFile, Long groupId) {
        // 处理文件上传
        if (explodedViewFile != null && !explodedViewFile.isEmpty()) {
            // 使用用法ID作为文件路径的一部分，如果还没有ID则使用时间戳
            Long usageIdForPath = usage.getId() != null ? usage.getId().getId() : System.currentTimeMillis();
            String fileUrl = fileStorageService.uploadExplodedViewImage(explodedViewFile, usageIdForPath, groupId);
            String path = fileStorageService.generateExplodedViewPath(usageIdForPath, groupId, explodedViewFile.getOriginalFilename());
            String presignedUrl = fileStorageService.generatePresignedUrl(path, 7, TimeUnit.DAYS);
            usage.setExplodedViewImg(fileUrl);
            usage.setDownloadUrl(presignedUrl);
        }
        // 保存用法
        return save(usage);
    }

    @Override
    public Optional<UsageEntity> findById(UsageId usageId) {
        if (usageId == null || usageId.getId() == null) {
            return Optional.empty();
        }
        UsagePO usagePO = usageDao.selectById(usageId.getId());
        if (usagePO == null) {
            return Optional.empty();
        }
        UsageEntity entity = UsageConverter.toEntity(usagePO);

        return Optional.of(entity);
    }

    @Override
    public List<UsageEntity> findByIds(List<UsageId> usageIds) {
        if (usageIds == null || usageIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<UsagePO> usagePOS = usageDao.selectByIds(usageIds.stream().map(UsageId::getId).collect(Collectors.toList()));
        if (usagePOS == null || usagePOS.isEmpty()) {
            return Collections.emptyList();
        }

        return UsageConverter.toEntityList(usagePOS);
    }

    @Override
    public List<UsageEntity> findByNameLike(String nameKeyword) {
        if (StringUtils.isNotBlank(nameKeyword)) {
            nameKeyword = nameKeyword.trim().replace("%", "\\%").replace("_", "\\_");
        }
        String likePattern = "%" + nameKeyword + "%";
        List<UsagePO> usagePOs = usageDao.selectByNameLike(likePattern);
        if (usagePOs == null || usagePOs.isEmpty()) {
            return Collections.emptyList();
        }

        return UsageConverter.toEntityList(usagePOs);
    }

    @Override
    public List<UsageEntity> findAllEnabled() {
        List<UsagePO> usagePOs = usageDao.selectAllEnabled();
        if (usagePOs == null || usagePOs.isEmpty()) {
            return Collections.emptyList();
        }

        return UsageConverter.toEntityList(usagePOs);
    }

    @Override
    public boolean deleteById(UsageId usageId) {
        if (usageId == null) {
            return false;
        }

        return usageDao.deleteById(usageId.getId()) > 0;
    }

}
