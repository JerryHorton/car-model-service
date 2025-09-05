package cn.cug.sxy.domain.part.service;

import cn.cug.sxy.domain.part.adapter.repository.IPartRepository;
import cn.cug.sxy.domain.part.model.entity.PartBindHourExcelData;
import cn.cug.sxy.domain.part.model.entity.PartEntity;
import cn.cug.sxy.domain.part.model.entity.PartBindHourResultEntity;
import cn.cug.sxy.domain.part.model.valobj.PartCode;
import cn.cug.sxy.domain.part.model.valobj.PartId;
import cn.cug.sxy.domain.workhour.model.valobj.WorkHourId;
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
 * @Date 2025/9/3
 * @Description 备件命令服务实现
 * @Author jerryhotton
 */
@Slf4j
@Service
public class PartCommandService implements IPartCommandService {

    private final IPartRepository partRepository;

    public PartCommandService(IPartRepository partRepository) {
        this.partRepository = partRepository;
    }

    @Override
    public PartEntity createPart(PartCode partCode, String name, String creator, String remark) {
        // 检查编码是否已存在
        if (partRepository.existsByCode(partCode)) {
            throw new AppException(ResponseCode.PART_CODE_EXISTS_ERROR);
        }
        // 创建备件实体
        PartEntity partEntity = PartEntity.create(partCode, name, creator, remark);

        // 保存备件
        return partRepository.save(partEntity);
    }

    @Override
    public PartEntity updatePart(PartId partId, String name, String remark) {
        // 查找备件
        Optional<PartEntity> partEntityOpt = partRepository.findById(partId);
        if (!partEntityOpt.isPresent()) {
            throw new AppException(ResponseCode.PART_NOT_FOUND_ERROR);
        }
        // 更新备件信息
        partEntityOpt.get().update(name, remark);

        // 保存更新
        return partRepository.save(partEntityOpt.get());
    }

    @Override
    public boolean enablePart(PartId partId) {
        // 查找备件
        Optional<PartEntity> partEntity = partRepository.findById(partId);
        if (!partEntity.isPresent()) {
            throw new AppException(ResponseCode.PART_NOT_FOUND_ERROR);
        }

        // 启用备件
        return partRepository.enable(partId);
    }

    @Override
    public boolean disablePart(PartId partId) {
        // 查找备件
        Optional<PartEntity> partEntity = partRepository.findById(partId);
        if (!partEntity.isPresent()) {
            throw new AppException(ResponseCode.PART_NOT_FOUND_ERROR);
        }

        // 禁用备件
        return partRepository.disable(partId);
    }

    @Override
    public boolean deletePart(PartId partId) {
        // 查找备件
        Optional<PartEntity> partEntity = partRepository.findById(partId);
        if (!partEntity.isPresent()) {
            throw new AppException(ResponseCode.PART_NOT_FOUND_ERROR);
        }

        // 删除备件
        return partRepository.delete(partId);
    }

    @Override
    public List<PartBindHourResultEntity> batchBindHours(MultipartFile file, String creator) throws IOException {
        try {
            // 初始化结果列表
            List<PartBindHourResultEntity> results = new ArrayList<>();
            // 读取Excel文件
            List<PartBindHourExcelData> partHourRelations = partRepository.readPartHourExcel(file);
            // 批量处理数据
            for (int i = 0; i < partHourRelations.size(); i++) {
                PartBindHourExcelData relation = partHourRelations.get(i);
                int rowNumber = i + 2; // Excel行号从2开始（第1行是标题）
                // 验证数据
                String errorMessage = partRepository.validatePartHourData(relation);
                if (errorMessage != null) {
                    results.add(buildErrorResult(rowNumber, relation, errorMessage));
                    continue;
                }
                // 查询备件
                Optional<PartEntity> partOpt = partRepository.findByCode(new PartCode(relation.getPartCode()));
                if (!partOpt.isPresent()) {
                    results.add(buildErrorResult(rowNumber, relation, "备件不存在"));
                    continue;
                }
                // 绑定工时
                try {
                    boolean success = partRepository.bindPartHour(partOpt.get().getId(), relation.getHourCode());
                    if (success) {
                        // 添加成功结果
                        results.add(PartBindHourResultEntity.builder()
                                .rowNumber(rowNumber)
                                .partCode(relation.getPartCode())
                                .workHourCode(relation.getHourCode())
                                .success(true)
                                .build());
                    } else {
                        results.add(buildErrorResult(rowNumber, relation, "绑定失败"));
                    }
                } catch (AppException e) {
                    results.add(buildErrorResult(rowNumber, relation, e.getInfo()));
                } catch (Exception e) {
                    results.add(buildErrorResult(rowNumber, relation, "绑定失败: " + e.getMessage()));
                }
            }

            return results;
        } catch (Exception e) {
            log.error("批量绑定备件工时关系失败", e);
            throw new AppException(ResponseCode.UN_ERROR);
        }
    }

    /**
     * 构建错误结果
     */
    private PartBindHourResultEntity buildErrorResult(int rowNumber, PartBindHourExcelData relation, String errorMessage) {
        return PartBindHourResultEntity.builder()
                .rowNumber(rowNumber)
                .partCode(relation != null ? relation.getPartCode() : null)
                .workHourCode(relation != null ? relation.getHourCode() : null)
                .success(false)
                .errorMessage(errorMessage)
                .build();
    }

    @Override
    public boolean unbindHour(PartId partId, WorkHourId hourId) {
        // 查找备件
        Optional<PartEntity> partEntityOpt = partRepository.findById(partId);
        if (!partEntityOpt.isPresent()) {
            throw new AppException(ResponseCode.PART_NOT_FOUND_ERROR);
        }

        // 解绑工时
        return partRepository.unbindHour(partId, hourId);
    }

    @Override
    public String uploadTemplate(MultipartFile file) {
        return partRepository.uploadTemplate(file);
    }

    @Override
    public String getTemplateInfo() {
        return partRepository.getTemplateInfo();
    }

} 