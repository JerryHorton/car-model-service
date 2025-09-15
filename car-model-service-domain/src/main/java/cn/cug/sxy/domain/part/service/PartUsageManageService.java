package cn.cug.sxy.domain.part.service;

import cn.cug.sxy.domain.part.adapter.repository.IPartRepository;
import cn.cug.sxy.domain.part.adapter.repository.IUsagePartRepository;
import cn.cug.sxy.domain.part.model.entity.PartEntity;
import cn.cug.sxy.domain.part.model.entity.UsageBindPartExcelData;
import cn.cug.sxy.domain.part.model.entity.UsageBindPartResultEntity;
import cn.cug.sxy.domain.part.model.valobj.PartCode;
import cn.cug.sxy.domain.part.model.valobj.PartId;
import cn.cug.sxy.domain.usage.adapter.repository.IUsageRepository;
import cn.cug.sxy.domain.usage.model.entity.UsageEntity;
import cn.cug.sxy.domain.usage.model.entity.UsagePartEntity;
import cn.cug.sxy.domain.usage.model.valobj.UsageId;
import cn.cug.sxy.types.enums.ResponseCode;
import cn.cug.sxy.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @version 1.0
 * @Date 2025/9/5 10:20
 * @Description 用法备件管理服务实现
 * @Author jerryhotton
 */

@Slf4j
@Service
public class PartUsageManageService implements IPartUsageManageService {

    private final IUsagePartRepository usagePartRepository;
    private final IUsageRepository usageRepository;
    private final IPartRepository partRepository;

    public PartUsageManageService(
            IUsagePartRepository usagePartRepository,
            IUsageRepository usageRepository,
            IPartRepository partRepository) {
        this.usagePartRepository = usagePartRepository;
        this.usageRepository = usageRepository;
        this.partRepository = partRepository;
    }

    @Override
    public UsagePartEntity bindPart(UsageId usageId, PartId partId, Integer count) {
        // 验证用法是否存在
        usageRepository.findById(usageId)
                .orElseThrow(() -> new AppException(ResponseCode.USAGE_NOT_FOUND));
        // 验证备件是否存在
        PartEntity partEntity = partRepository.findById(partId)
                .orElseThrow(() -> new AppException(ResponseCode.PART_NOT_FOUND));
        // 检查是否已经绑定
        Optional<UsagePartEntity> existingBinding = usagePartRepository.findByUsageIdAndPartId(usageId, partId);
        if (existingBinding.isPresent()) {
            // 已经绑定，更新数量
            UsagePartEntity entity = existingBinding.get();
            entity.updateCount(count);
            return usagePartRepository.save(entity);
        } else {
            // 新建绑定关系
            UsagePartEntity entity = UsagePartEntity.create(usageId, partId, count);
            entity.setPartCode(partEntity.getCode().getCode());
            entity.setPartName(partEntity.getName());
            return usagePartRepository.save(entity);
        }
    }

    @Override
    public boolean unbindPart(UsageId usageId, PartId partId) {
        // 验证用法是否存在
        usageRepository.findById(usageId)
                .orElseThrow(() -> new AppException(ResponseCode.USAGE_NOT_FOUND));
        // 验证备件是否存在
        partRepository.findById(partId)
                .orElseThrow(() -> new AppException(ResponseCode.PART_NOT_FOUND));

        // 删除绑定关系
        return usagePartRepository.deleteByUsageIdAndPartId(usageId, partId);
    }

    @Override
    public boolean clearParts(UsageId usageId) {
        // 验证用法是否存在
        usageRepository.findById(usageId)
                .orElseThrow(() -> new AppException(ResponseCode.USAGE_NOT_FOUND));

        // 删除所有绑定关系
        return usagePartRepository.deleteByUsageId(usageId);
    }

    @Override
    public List<UsagePartEntity> getPartsByUsageId(UsageId usageId) {
        // 验证用法是否存在
        usageRepository.findById(usageId)
                .orElseThrow(() -> new AppException(ResponseCode.USAGE_NOT_FOUND));

        // 查询绑定关系
        return usagePartRepository.findByUsageId(usageId);
    }

    @Override
    public List<UsageBindPartResultEntity> batchUpload(UsageId usageId, MultipartFile file) throws IOException {
        // 验证用法是否存在
        UsageEntity usageEntity = usageRepository.findById(usageId)
                .orElseThrow(() -> new AppException(ResponseCode.USAGE_NOT_FOUND));
        // 读取Excel文件
        List<UsageBindPartExcelData> dataList = usagePartRepository.readUsagePartExcel(file);
        List<UsageBindPartResultEntity> results = new ArrayList<>();
        List<UsagePartEntity> entitiesToSave = new ArrayList<>();
        // 处理每一行数据
        for (int i = 0; i < dataList.size(); i++) {
            UsageBindPartExcelData data = dataList.get(i);
            UsageBindPartResultEntity result = new UsageBindPartResultEntity();
            result.setRowNumber(i + 2); // Excel行号从2开始（第1行是表头）
            // 验证数据
            String errorMessage = usagePartRepository.validateUsagePartData(data);
            if (errorMessage != null) {
                result.setSuccess(false);
                result.setErrorMessage(errorMessage);
                results.add(result);
                continue;
            }
            // 验证成功，设置结果
            result.setPartCode(data.getPartCode());
            result.setCount(Integer.valueOf(data.getCount()));
            result.setUsageId(usageId.getId());
            try {
                // 根据备件编码查找备件
                PartEntity partEntity = partRepository.findByCode(new PartCode(data.getPartCode()))
                        .orElse(null);
                if (partEntity == null) {
                    result.setSuccess(false);
                    result.setErrorMessage("备件编码不存在");
                    results.add(result);
                    continue;
                }
                result.setPartId(partEntity.getId().getId());
                // 检查是否已经绑定
                Optional<UsagePartEntity> usagePartEntityOpt = usagePartRepository.findByUsageIdAndPartId(usageId, partEntity.getId());
                if (usagePartEntityOpt.isPresent()) {
                    // 已经绑定，更新数量
                    UsagePartEntity entity = usagePartEntityOpt.get();
                    entity.updateCount(Integer.valueOf(data.getCount()));
                    usagePartRepository.save(entity);
                } else {
                    // 新建绑定关系
                    UsagePartEntity entity = UsagePartEntity.create(usageId, partEntity.getId(), Integer.valueOf(data.getCount()));
                    entitiesToSave.add(entity);
                }
                result.setSuccess(true);
            } catch (Exception e) {
                log.error("处理用法备件关联数据异常", e);
                result.setSuccess(false);
                result.setErrorMessage("处理异常: " + e.getMessage());
            }

            results.add(result);
        }
        // 批量保存新的绑定关系
        if (!entitiesToSave.isEmpty()) {
            usagePartRepository.batchSave(entitiesToSave);
        }

        return results;
    }

    @Override
    public String uploadTemplate(MultipartFile file) {
        return usagePartRepository.uploadTemplate(file);
    }

    @Override
    public String getTemplateInfo() {
        return usagePartRepository.getTemplateInfo();
    }

    @Override
    public boolean isTemplateExists() {
        return usagePartRepository.isTemplateExists();
    }

    @Override
    public byte[] getTemplate() throws IOException {
        return usagePartRepository.getUsagePartTemplate();
    }

}
