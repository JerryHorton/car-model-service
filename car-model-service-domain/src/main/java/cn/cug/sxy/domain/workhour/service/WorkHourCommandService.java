package cn.cug.sxy.domain.workhour.service;

import cn.cug.sxy.domain.workhour.adapter.repository.IWorkHourRepository;
import cn.cug.sxy.domain.workhour.model.entity.WorkHourBatchUploadResultEntity;
import cn.cug.sxy.domain.workhour.model.entity.WorkHourEntity;
import cn.cug.sxy.domain.workhour.model.valobj.WorkHourCode;
import cn.cug.sxy.domain.workhour.model.valobj.WorkHourExcelData;
import cn.cug.sxy.domain.workhour.model.valobj.WorkHourId;
import cn.cug.sxy.domain.workhour.model.valobj.WorkHourType;
import cn.cug.sxy.types.enums.ResponseCode;
import cn.cug.sxy.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @version 1.0
 * @Date 2025/1/27 10:00
 * @Description 工时命令服务实现
 * @Author jerryhotton
 */

@Slf4j
@Service
public class WorkHourCommandService implements IWorkHourCommandService {

    private final IWorkHourRepository workHourRepository;

    public WorkHourCommandService(IWorkHourRepository workHourRepository) {
        this.workHourRepository = workHourRepository;
    }

    @Override
    public WorkHourEntity createMainWorkHour(
            WorkHourCode code,
            String description,
            BigDecimal standardHours,
            WorkHourType type,
            String creator) {
        // 检查编码是否已存在
        if (workHourRepository.existsByCode(code)) {
            throw new AppException(ResponseCode.WORK_HOUR_CODE_EXISTS_ERROR);
        }
        WorkHourEntity workHourEntity = WorkHourEntity.create(
                code,
                description,
                standardHours,
                type,
                null, // 主工时没有父ID
                null, // 主工时没有步骤顺序号
                creator
        );

        return workHourRepository.save(workHourEntity);
    }

    @Override
    public WorkHourEntity createSubWorkHour(
            WorkHourCode code,
            String description,
            BigDecimal standardHours,
            WorkHourType type,
            WorkHourId parentId,
            Integer stepOrder,
            String creator) {
        // 检查编码是否已存在
        if (workHourRepository.existsByCode(code)) {
            throw new AppException(ResponseCode.WORK_HOUR_CODE_EXISTS_ERROR);
        }
        // 检查父工时是否存在
        Optional<WorkHourEntity> parentWorkHour = workHourRepository.findById(parentId);
        if (!parentWorkHour.isPresent()) {
            throw new AppException(ResponseCode.PARENT_WORK_HOUR_NOT_FOUND_ERROR);
        }
        // 检查父工时是否为主工时
        if (!parentWorkHour.get().isMainWorkHour()) {
            throw new AppException(ResponseCode.PARENT_WORK_HOUR_MUST_BE_MAIN_ERROR);
        }
        WorkHourEntity workHourEntity = WorkHourEntity.create(
                code,
                description,
                standardHours,
                type,
                parentId,
                stepOrder,
                creator
        );

        return workHourRepository.save(workHourEntity);
    }

    @Override
    public WorkHourEntity updateWorkHour(
            WorkHourId workHourId,
            String description,
            BigDecimal standardHours,
            Integer stepOrder) {
        Optional<WorkHourEntity> workHourOpt = workHourRepository.findById(workHourId);
        if (!workHourOpt.isPresent()) {
            throw new AppException(ResponseCode.WORK_HOUR_NOT_FOUND_ERROR);
        }
        WorkHourEntity workHourEntity = workHourOpt.get();
        workHourEntity.update(description, standardHours, stepOrder);
        workHourRepository.update(workHourEntity);

        return workHourEntity;
    }

    @Override
    public boolean enableWorkHour(WorkHourId workHourId) {
        Optional<WorkHourEntity> workHourOpt = workHourRepository.findById(workHourId);
        if (!workHourOpt.isPresent()) {
            throw new AppException(ResponseCode.WORK_HOUR_NOT_FOUND_ERROR);
        }
        WorkHourEntity workHourEntity = workHourOpt.get();
        workHourEntity.enable();
        workHourRepository.update(workHourEntity);

        return true;
    }

    @Override
    public boolean disableWorkHour(WorkHourId workHourId) {
        Optional<WorkHourEntity> workHourOpt = workHourRepository.findById(workHourId);
        if (!workHourOpt.isPresent()) {
            throw new AppException(ResponseCode.WORK_HOUR_NOT_FOUND_ERROR);
        }
        WorkHourEntity workHourEntity = workHourOpt.get();
        workHourEntity.disable();
        workHourRepository.update(workHourEntity);

        return true;
    }

    @Override
    public boolean deleteWorkHour(WorkHourId workHourId) {
        Optional<WorkHourEntity> workHourOpt = workHourRepository.findById(workHourId);
        if (!workHourOpt.isPresent()) {
            throw new AppException(ResponseCode.WORK_HOUR_NOT_FOUND_ERROR);
        }

        WorkHourEntity workHourEntity = workHourOpt.get();
        // 如果是主工时，检查是否有子工时
        if (workHourEntity.isMainWorkHour()) {
            var subWorkHours = workHourRepository.findByParentId(workHourId);
            if (!subWorkHours.isEmpty()) {
                throw new AppException(ResponseCode.CANNOT_DELETE_MAIN_WORK_HOUR_WITH_SUBS_ERROR);
            }
        }
        workHourEntity.delete();
        workHourRepository.update(workHourEntity);

        return true;
    }

    @Override
    public List<WorkHourBatchUploadResultEntity> batchUploadSubWorkHours(MultipartFile file, WorkHourId parentId, String creator) {
        try {
            // 验证父工时是否存在且为主工时
            Optional<WorkHourEntity> parentWorkHourOpt = workHourRepository.findById(parentId);
            if (!parentWorkHourOpt.isPresent()) {
                throw new AppException(ResponseCode.PARENT_WORK_HOUR_NOT_FOUND_ERROR);
            }
            WorkHourEntity parentWorkHour = parentWorkHourOpt.get();
            if (!parentWorkHour.isMainWorkHour()) {
                throw new AppException(ResponseCode.PARENT_WORK_HOUR_MUST_BE_MAIN_ERROR);
            }
            // 初始化结果列表
            List<WorkHourBatchUploadResultEntity> results = new ArrayList<>();
            // 读取Excel文件
            List<WorkHourExcelData> excelDataList = workHourRepository.readWorkHourExcel(file);
            // 批量处理数据
            for (int i = 0; i < excelDataList.size(); i++) {
                WorkHourExcelData excelData = excelDataList.get(i);
                int rowNumber = i + 2; // Excel行号从2开始（第1行是标题）
                // 验证数据
                String errorMessage = workHourRepository.validateWorkHourExcelData(excelData);
                if (null != errorMessage) {
                    results.add(WorkHourBatchUploadResultEntity.builder()
                            .rowNumber(rowNumber)
                            .code(excelData.getCode())
                            .description(excelData.getDescription())
                            .standardHours(excelData.getStandardHours())
                            .stepOrder(excelData.getStepOrder())
                            .success(false)
                            .errorMessage(errorMessage)
                            .build());
                    continue;
                }
                // 检查工时代码是否已存在
                if (workHourRepository.existsByCode(new WorkHourCode(excelData.getCode()))) {
                    results.add(WorkHourBatchUploadResultEntity.builder()
                            .rowNumber(rowNumber)
                            .code(excelData.getCode())
                            .description(excelData.getDescription())
                            .standardHours(excelData.getStandardHours())
                            .stepOrder(excelData.getStepOrder())
                            .success(false)
                            .errorMessage("工时代码已存在")
                            .build());
                    continue;
                }
                // 创建子工时
                WorkHourEntity workHourEntity = WorkHourEntity.create(
                        new WorkHourCode(excelData.getCode()),
                        excelData.getDescription(),
                        new BigDecimal(excelData.getStandardHours()),
                        WorkHourType.SUB,
                        parentId,
                        Integer.parseInt(excelData.getStepOrder()),
                        creator
                );
                // 保存到数据库
                workHourRepository.save(workHourEntity);
                results.add(WorkHourBatchUploadResultEntity.builder()
                        .rowNumber(rowNumber)
                        .code(excelData.getCode())
                        .description(excelData.getDescription())
                        .standardHours(excelData.getStandardHours())
                        .stepOrder(excelData.getStepOrder())
                        .success(true)
                        .workHourId(workHourEntity.getId().getId())
                        .build());
            }

            return results;
        } catch (Exception e) {
            log.error("批量上传子工时失败", e);
            throw new AppException(ResponseCode.UN_ERROR);
        }
    }

    @Override
    public String uploadTemplate(MultipartFile file) {
        try {
            log.info("上传工时批量上传模板 fileName={}, fileSize={}",
                    file.getOriginalFilename(), file.getSize());
            // 验证文件
            if (file.isEmpty()) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), "上传文件不能为空");
            }
            // 验证文件类型
            String fileName = file.getOriginalFilename();
            if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), "只支持Excel文件格式(.xlsx/.xls)");
            }
            // 验证文件大小（限制为2MB）
            if (file.getSize() > 2 * 1024 * 1024) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), "模板文件大小不能超过2MB");
            }
            // 调用MinIO服务上传模板
            String result = workHourRepository.uploadTemplate(file);
            log.info("工时批量上传模板上传成功");

            return result;
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("上传工时批量上传模板异常", e);
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "上传模板失败: " + e.getMessage());
        }
    }

    @Override
    public String getTemplateInfo() {
        return workHourRepository.getTemplateInfo();
    }

    /**
     * 验证Excel数据
     */
    private void validateWorkHourExcelData(WorkHourExcelData data) {
        if (StringUtils.isBlank(data.getCode())) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), "工时代码不能为空");
        }
        if (StringUtils.isBlank(data.getDescription())) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), "工时描述不能为空");
        }
        if (StringUtils.isBlank(data.getStandardHours())) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), "标准工时不能为空");
        }
        if (StringUtils.isBlank(data.getStepOrder())) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), "步骤顺序不能为空");
        }
        try {
            new BigDecimal(data.getStandardHours());
        } catch (NumberFormatException e) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), "标准工时格式不正确");
        }
        try {
            Integer.parseInt(data.getStepOrder());
        } catch (NumberFormatException e) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), "步骤顺序格式不正确");
        }
    }

} 