package cn.cug.sxy.trigger.http;

import cn.cug.sxy.api.IWorkHourService;
import cn.cug.sxy.api.dto.WorkHourCreateRequestDTO;
import cn.cug.sxy.api.dto.WorkHourUpdateRequestDTO;
import cn.cug.sxy.api.response.Response;
import cn.cug.sxy.api.vo.WorkHourBatchUploadResultVO;
import cn.cug.sxy.api.vo.WorkHourVO;
import cn.cug.sxy.domain.workhour.model.entity.WorkHourBatchUploadResultEntity;
import cn.cug.sxy.domain.workhour.model.entity.WorkHourEntity;
import cn.cug.sxy.domain.workhour.model.valobj.WorkHourCode;
import cn.cug.sxy.domain.workhour.model.valobj.WorkHourId;
import cn.cug.sxy.domain.workhour.model.valobj.WorkHourType;
import cn.cug.sxy.domain.workhour.service.IWorkHourCommandService;
import cn.cug.sxy.domain.workhour.service.IWorkHourQueryService;
import cn.cug.sxy.types.enums.ResponseCode;
import cn.cug.sxy.types.exception.AppException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/1/27 10:00
 * @Description 工时控制器
 * @Author jerryhotton
 */

@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/workhour/")
@DubboService(version = "1.0")
public class WorkHourController implements IWorkHourService {

    private final IWorkHourQueryService workHourQueryService;
    private final IWorkHourCommandService workHourCommandService;

    public WorkHourController(
            IWorkHourQueryService workHourQueryService,
            IWorkHourCommandService workHourCommandService) {
        this.workHourQueryService = workHourQueryService;
        this.workHourCommandService = workHourCommandService;
    }

    @RequestMapping(value = "create_main", method = RequestMethod.POST)
    @Override
    public Response<WorkHourVO> createMainWorkHour(@RequestBody @Valid WorkHourCreateRequestDTO requestDTO) {
        try {
            log.info("创建主工时 code={}, description={}, type={}, creator={}",
                    requestDTO.getCode(), requestDTO.getDescription(), requestDTO.getType(), requestDTO.getCreator());
            WorkHourEntity workHourEntity = workHourCommandService.createMainWorkHour(
                    new WorkHourCode(requestDTO.getCode()),
                    requestDTO.getDescription(),
                    requestDTO.getStandardHours(),
                    WorkHourType.fromCode(requestDTO.getType()),
                    requestDTO.getCreator()
            );
            WorkHourVO workHourVO = convertToWorkHourVO(workHourEntity);
            log.info("创建主工时成功 id={}, code={}", workHourEntity.getId().getId(), requestDTO.getCode());

            return Response.<WorkHourVO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(workHourVO)
                    .build();
        } catch (AppException e) {
            log.error("创建主工时失败 code={}", requestDTO.getCode(), e);

            return Response.<WorkHourVO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("创建主工时异常 code={}", requestDTO.getCode(), e);

            return Response.<WorkHourVO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "create_sub", method = RequestMethod.POST)
    @Override
    public Response<WorkHourVO> createSubWorkHour(@RequestBody @Valid WorkHourCreateRequestDTO requestDTO) {
        try {
            log.info("创建子工时 code={}, parentId={}, stepOrder={}, creator={}",
                    requestDTO.getCode(), requestDTO.getParentId(), requestDTO.getStepOrder(), requestDTO.getCreator());
            WorkHourEntity workHourEntity = workHourCommandService.createSubWorkHour(
                    new WorkHourCode(requestDTO.getCode()),
                    requestDTO.getDescription(),
                    requestDTO.getStandardHours(),
                    WorkHourType.fromCode(requestDTO.getType()),
                    new WorkHourId(requestDTO.getParentId()),
                    requestDTO.getStepOrder(),
                    requestDTO.getCreator()
            );
            WorkHourVO workHourVO = convertToWorkHourVO(workHourEntity);
            log.info("创建子工时成功 id={}, code={}", workHourEntity.getId().getId(), requestDTO.getCode());

            return Response.<WorkHourVO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(workHourVO)
                    .build();
        } catch (AppException e) {
            log.error("创建子工时失败 code={}", requestDTO.getCode(), e);

            return Response.<WorkHourVO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("创建子工时异常 code={}", requestDTO.getCode(), e);

            return Response.<WorkHourVO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "batch_upload_sub", method = RequestMethod.POST)
    @Override
    public Response<List<WorkHourBatchUploadResultVO>> batchUploadSubWorkHours(
            @RequestParam("file") MultipartFile file,
            @RequestParam("parentId") Long parentId,
            @RequestParam("creator") String creator) {
        try {
            log.info("批量上传子工时 parentId={}, creator={}, fileName={}, fileSize={}",
                    parentId, creator, file.getOriginalFilename(), file.getSize());
            // 验证文件
            if (file.isEmpty()) {
                return Response.<List<WorkHourBatchUploadResultVO>>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info("上传文件不能为空")
                        .build();
            }
            // 验证文件类型
            String fileName = file.getOriginalFilename();
            if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
                return Response.<List<WorkHourBatchUploadResultVO>>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info("只支持Excel文件格式(.xlsx/.xls)")
                        .build();
            }
            // 验证文件大小（限制为5MB）
            if (file.getSize() > 5 * 1024 * 1024) {
                return Response.<List<WorkHourBatchUploadResultVO>>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info("文件大小不能超过5MB")
                        .build();
            }
            // 调用服务处理批量上传
            List<WorkHourBatchUploadResultEntity> results = workHourCommandService.batchUploadSubWorkHours(
                    file, new WorkHourId(parentId), creator);
            log.info("批量上传子工时成功 parentId={}, 处理记录数={}", parentId, results.size());

            return Response.<List<WorkHourBatchUploadResultVO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(results.stream().map(this::convertToWorkHourBatchUploadResultVO).collect(Collectors.toList()))
                    .build();
        } catch (AppException e) {
            log.error("批量上传子工时失败 parentId={}", parentId, e);

            return Response.<List<WorkHourBatchUploadResultVO>>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("批量上传子工时异常 parentId={}", parentId, e);

            return Response.<List<WorkHourBatchUploadResultVO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "upload_template", method = RequestMethod.POST)
    public Response<String> uploadTemplate(@RequestParam("file") MultipartFile file) {
        try {
            log.info("上传工时批量上传模板 fileName={}, fileSize={}",
                    file.getOriginalFilename(), file.getSize());
            // 验证文件
            if (file.isEmpty()) {
                return Response.<String>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info("上传文件不能为空")
                        .build();
            }
            // 验证文件类型
            String fileName = file.getOriginalFilename();
            if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
                return Response.<String>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info("只支持Excel文件格式(.xlsx/.xls)")
                        .build();
            }
            // 验证文件大小（限制为2MB）
            if (file.getSize() > 2 * 1024 * 1024) {
                return Response.<String>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info("模板文件大小不能超过2MB")
                        .build();
            }
            // 调用服务上传模板
            String result = workHourCommandService.uploadTemplate(file);
            log.info("工时批量上传模板上传成功");

            return Response.<String>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(result)
                    .build();
        } catch (AppException e) {
            log.error("上传工时批量上传模板失败", e);

            return Response.<String>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("上传工时批量上传模板异常", e);

            return Response.<String>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "download_template", method = RequestMethod.GET)
    @Override
    public ResponseEntity<byte[]> downloadTemplate() {
        try {
            log.info("下载工时批量上传模板");
            // 从MinIO获取模板文件
            byte[] templateData = workHourQueryService.getWorkHourTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "工时批量上传模板.xlsx");
            headers.setContentLength(templateData.length);
            log.info("工时批量上传模板下载成功，文件大小: {} bytes", templateData.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(templateData);
        } catch (Exception e) {
            log.error("下载工时批量上传模板失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @RequestMapping(value = "template_info", method = RequestMethod.GET)
    public Response<String> getTemplateInfo() {
        try {
            log.info("获取工时批量上传模板信息");
            String templateInfo = workHourCommandService.getTemplateInfo();

            return Response.<String>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(templateInfo)
                    .build();
        } catch (AppException e) {
            log.error("获取工时批量上传模板信息失败", e);

            return Response.<String>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("获取工时批量上传模板信息异常", e);

            return Response.<String>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @Override
    public Response<WorkHourVO> updateWorkHour(@RequestBody @Valid WorkHourUpdateRequestDTO requestDTO) {
        try {
            log.info("更新工时 workHourId={}", requestDTO.getWorkHourId());
            WorkHourEntity workHourEntity = workHourCommandService.updateWorkHour(
                    new WorkHourId(requestDTO.getWorkHourId()),
                    requestDTO.getDescription(),
                    requestDTO.getStandardHours(),
                    requestDTO.getStepOrder()
            );
            WorkHourVO workHourVO = convertToWorkHourVO(workHourEntity);
            log.info("更新工时成功 workHourId={}", requestDTO.getWorkHourId());

            return Response.<WorkHourVO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(workHourVO)
                    .build();
        } catch (AppException e) {
            log.error("更新工时失败 workHourId={}", requestDTO.getWorkHourId(), e);

            return Response.<WorkHourVO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("更新工时异常 workHourId={}", requestDTO.getWorkHourId(), e);

            return Response.<WorkHourVO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "query_main", method = RequestMethod.GET)
    @Override
    public Response<List<WorkHourVO>> queryAllMainWorkHours() {
        try {
            log.info("查询所有主工时");
            List<WorkHourEntity> workHourEntities = workHourQueryService.getAllMainWorkHours();
            List<WorkHourVO> workHourVOs = workHourEntities.stream()
                    .map(this::convertToWorkHourVO)
                    .collect(Collectors.toList());
            log.info("查询所有主工时成功，数量={}", workHourVOs.size());

            return Response.<List<WorkHourVO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(workHourVOs)
                    .build();
        } catch (Exception e) {
            log.error("查询所有主工时异常", e);

            return Response.<List<WorkHourVO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "query_sub", method = RequestMethod.GET)
    @Override
    public Response<List<WorkHourVO>> querySubWorkHoursByParentId(Long parentId) {
        try {
            log.info("根据父ID查询子工时 parentId={}", parentId);
            List<WorkHourEntity> workHourEntities = workHourQueryService.getByParentId(new WorkHourId(parentId));
            List<WorkHourVO> workHourVOs = workHourEntities.stream()
                    .map(this::convertToWorkHourVO)
                    .collect(Collectors.toList());
            log.info("根据父ID查询子工时成功 parentId={}, 数量={}", parentId, workHourVOs.size());

            return Response.<List<WorkHourVO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(workHourVOs)
                    .build();
        } catch (Exception e) {
            log.error("根据父ID查询子工时异常 parentId={}", parentId, e);

            return Response.<List<WorkHourVO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "query_tree", method = RequestMethod.GET)
    @Override
    public Response<List<WorkHourVO>> queryWorkHourTree(Long workHourId) {
        try {
            log.info("查询工时树结构 workHourId={}", workHourId);
            List<WorkHourEntity> workHourEntities = workHourQueryService.getWorkHourTree(new WorkHourId(workHourId));
            List<WorkHourVO> workHourVOs = workHourEntities.stream()
                    .map(this::convertToWorkHourVO)
                    .collect(Collectors.toList());
            log.info("查询工时树结构成功 workHourId={}, 数量={}", workHourId, workHourVOs.size());

            return Response.<List<WorkHourVO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(workHourVOs)
                    .build();
        } catch (Exception e) {
            log.error("查询工时树结构异常 workHourId={}", workHourId, e);

            return Response.<List<WorkHourVO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "detail", method = RequestMethod.GET)
    @Override
    public Response<WorkHourVO> getWorkHourDetail(Long workHourId) {
        try {
            log.info("查询工时详情 workHourId={}", workHourId);
            WorkHourEntity workHourEntity = workHourQueryService.getById(new WorkHourId(workHourId));
            if (workHourEntity == null) {
                throw new AppException(ResponseCode.WORK_HOUR_NOT_FOUND_ERROR);
            }
            WorkHourVO workHourVO = convertToWorkHourVO(workHourEntity);
            log.info("查询工时详情成功 workHourId={}", workHourId);

            return Response.<WorkHourVO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(workHourVO)
                    .build();
        } catch (Exception e) {
            log.error("查询工时详情异常 workHourId={}", workHourId, e);

            return Response.<WorkHourVO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "enable", method = RequestMethod.POST)
    @Override
    public Response<Boolean> enableWorkHour(Long workHourId) {
        try {
            log.info("启用工时 workHourId={}", workHourId);
            boolean result = workHourCommandService.enableWorkHour(new WorkHourId(workHourId));
            log.info("启用工时成功 workHourId={}", workHourId);

            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(result)
                    .build();
        } catch (AppException e) {
            log.error("启用工时失败 workHourId={}", workHourId, e);

            return Response.<Boolean>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("启用工时异常 workHourId={}", workHourId, e);

            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "disable", method = RequestMethod.POST)
    @Override
    public Response<Boolean> disableWorkHour(Long workHourId) {
        try {
            log.info("禁用工时 workHourId={}", workHourId);
            boolean result = workHourCommandService.disableWorkHour(new WorkHourId(workHourId));
            log.info("禁用工时成功 workHourId={}", workHourId);

            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(result)
                    .build();
        } catch (AppException e) {
            log.error("禁用工时失败 workHourId={}", workHourId, e);
            return Response.<Boolean>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("禁用工时异常 workHourId={}", workHourId, e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @Override
    public Response<Boolean> deleteWorkHour(Long workHourId) {
        try {
            log.info("删除工时 workHourId={}", workHourId);
            boolean result = workHourCommandService.deleteWorkHour(new WorkHourId(workHourId));
            log.info("删除工时成功 workHourId={}", workHourId);

            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(result)
                    .build();
        } catch (AppException e) {
            log.error("删除工时失败 workHourId={}", workHourId, e);
            return Response.<Boolean>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("删除工时异常 workHourId={}", workHourId, e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    /**
     * 转换工时实体为VO
     */
    private WorkHourVO convertToWorkHourVO(WorkHourEntity entity) {
        if (entity == null) {
            return null;
        }
        WorkHourVO workHourVO = WorkHourVO.builder()
                .id(entity.getId() != null ? entity.getId().getId() : null)
                .parentId(entity.getParentId() != null ? entity.getParentId().getId() : null)
                .code(entity.getCode() != null ? entity.getCode().getCode() : null)
                .description(entity.getDescription())
                .standardHours(entity.getStandardHours())
                .type(entity.getType() != null ? entity.getType().getCode() : null)
                .typeDescription(entity.getType() != null ? entity.getType().getDescription() : null)
                .stepOrder(entity.getStepOrder())
                .status(entity.getStatus() != null ? entity.getStatus().getCode() : null)
                .statusDescription(entity.getStatus() != null ? entity.getStatus().getDescription() : null)
                .creator(entity.getCreator())
                .isMainWorkHour(entity.isMainWorkHour())
                .isSubWorkHour(entity.isSubWorkHour())
                .build();
        // 递归转换子工时
        if (entity.getChildren() != null && !entity.getChildren().isEmpty()) {
            List<WorkHourVO> childrenVOs = entity.getChildren().stream()
                    .map(this::convertToWorkHourVO)
                    .collect(Collectors.toList());
            workHourVO.setChildren(childrenVOs);
        }

        return workHourVO;
    }

    /**
     * 转换批量上传结果实体为VO
     */
    private WorkHourBatchUploadResultVO convertToWorkHourBatchUploadResultVO(WorkHourBatchUploadResultEntity entity) {
        if (entity == null) {
            return null;
        }

        return WorkHourBatchUploadResultVO.builder()
                .rowNumber(entity.getRowNumber())
                .code(entity.getCode())
                .description(entity.getDescription())
                .standardHours(entity.getStandardHours())
                .stepOrder(entity.getStepOrder())
                .success(entity.getSuccess())
                .errorMessage(entity.getErrorMessage())
                .workHourId(entity.getWorkHourId())
                .build();
    }

} 