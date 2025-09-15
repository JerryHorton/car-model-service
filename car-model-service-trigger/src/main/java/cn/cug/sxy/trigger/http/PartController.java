package cn.cug.sxy.trigger.http;

import cn.cug.sxy.api.IPartService;
import cn.cug.sxy.api.dto.ChangePartStatusRequestDTO;
import cn.cug.sxy.api.dto.PartCreateRequestDTO;
import cn.cug.sxy.api.dto.PartUpdateRequestDTO;
import cn.cug.sxy.api.response.Response;
import cn.cug.sxy.api.vo.PartBindHourResultVO;
import cn.cug.sxy.api.vo.PartDetailVO;
import cn.cug.sxy.api.vo.PartVO;
import cn.cug.sxy.api.vo.WorkHourTreeVO;
import cn.cug.sxy.domain.part.model.entity.PartBindHourResultEntity;
import cn.cug.sxy.domain.part.model.entity.PartEntity;
import cn.cug.sxy.domain.part.model.valobj.PartCode;
import cn.cug.sxy.domain.part.model.valobj.PartId;
import cn.cug.sxy.domain.part.service.IPartCommandService;
import cn.cug.sxy.domain.part.service.IPartQueryService;
import cn.cug.sxy.domain.workhour.model.entity.WorkHourEntity;
import cn.cug.sxy.domain.workhour.model.valobj.WorkHourId;
import cn.cug.sxy.domain.workhour.service.IWorkHourQueryService;
import cn.cug.sxy.trigger.http.converter.ToVOConverter;
import cn.cug.sxy.types.enums.ResponseCode;
import cn.cug.sxy.types.exception.AppException;
import cn.cug.sxy.types.utils.TemplateFileUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/9/3
 * @Description 备件控制器
 * @Author jerryhotton
 */
@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/part/")
@DubboService(version = "1.0")
public class PartController implements IPartService {

    private final IPartQueryService partQueryService;
    private final IPartCommandService partCommandService;
    private final IWorkHourQueryService workHourQueryService;
    private final ToVOConverter toVOConverter;

    public PartController(
            final IPartQueryService partQueryService,
            final IPartCommandService partCommandService,
            final IWorkHourQueryService workHourQueryService,
            final ToVOConverter toVOConverter) {
        this.partQueryService = partQueryService;
        this.partCommandService = partCommandService;
        this.workHourQueryService = workHourQueryService;
        this.toVOConverter = toVOConverter;
    }

    @RequestMapping(value = "create_part", method = RequestMethod.POST)
    @Override
    public Response<PartVO> createPart(@RequestBody @Valid PartCreateRequestDTO requestDTO) {
        try {
            log.info("创建备件 partCode={}, partName={}, creator={}",
                    requestDTO.getPartCode(), requestDTO.getPartName(), requestDTO.getCreator());
            PartEntity partEntity = partCommandService.createPart(
                    new PartCode(requestDTO.getPartCode()),
                    requestDTO.getPartName(),
                    requestDTO.getCreator(),
                    requestDTO.getRemark()
            );
            PartVO partVO = toVOConverter.convertToPartVO(partEntity);
            log.info("创建备件成功 id={}, code={}", partEntity.getId().getId(), requestDTO.getPartCode());

            return Response.<PartVO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(partVO)
                    .build();
        } catch (AppException e) {
            log.error("创建备件失败 partCode={}", requestDTO.getPartCode(), e);
            return Response.<PartVO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("创建备件异常 partCode={}", requestDTO.getPartCode(), e);
            return Response.<PartVO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "upload_template", method = RequestMethod.POST)
    @Override
    public Response<String> uploadTemplate(@RequestParam("file") MultipartFile file) {
        try {
            log.info("上传备件工时关联模板 fileName={}, fileSize={}",
                    file.getOriginalFilename(), file.getSize());

            // 调用服务上传模板
            String result = partCommandService.uploadTemplate(file);
            log.info("备件工时关联模板上传成功");

            return Response.<String>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(result)
                    .build();
        } catch (AppException e) {
            log.error("上传备件工时关联模板失败", e);

            return Response.<String>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("上传备件工时关联模板异常", e);

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
            log.info("下载备件工时关联模板");
            // 从MinIO获取模板文件
            byte[] templateData = partQueryService.getPartHourTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            String filename = "备件工时关联模板.xlsx";
            String encodedFilename = UriUtils.encode(filename, StandardCharsets.UTF_8);
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename);
            headers.setContentLength(templateData.length);
            log.info("备件工时关联模板下载成功，文件大小: {} bytes", templateData.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(templateData);
        } catch (Exception e) {
            log.error("下载备件工时关联模板失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @RequestMapping(value = "template_info", method = RequestMethod.GET)
    @Override
    public Response<String> getTemplateInfo() {
        try {
            log.info("获取备件工时关联模板信息");
            String templateInfo = partCommandService.getTemplateInfo();
            log.info("获取备件工时关联模板信息成功 templateInfo={}", templateInfo);
            return Response.<String>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(templateInfo)
                    .build();
        } catch (AppException e) {
            log.error("获取备件工时关联模板信息失败", e);

            return Response.<String>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("获取备件工时关联模板信息异常", e);

            return Response.<String>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @Override
    public Response<PartVO> updatePart(@RequestBody @Valid PartUpdateRequestDTO requestDTO) {
        try {
            log.info("更新备件 id={}", requestDTO.getId());
            PartEntity partEntity = partCommandService.updatePart(
                    new PartId(requestDTO.getId()),
                    requestDTO.getPartName(),
                    requestDTO.getRemark()
            );
            PartVO partVO = toVOConverter.convertToPartVO(partEntity);
            log.info("更新备件成功 id={}", requestDTO.getId());

            return Response.<PartVO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(partVO)
                    .build();
        } catch (AppException e) {
            log.error("更新备件失败 id={}", requestDTO.getId(), e);
            return Response.<PartVO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("更新备件异常 id={}", requestDTO.getId(), e);
            return Response.<PartVO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "get_part_detail", method = RequestMethod.GET)
    @Override
    public Response<PartDetailVO> getPartDetail(@RequestParam Long partId) {
        try {
            log.info("查询备件详情 partId={}", partId);
            if (partId == null) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER);
            }
            // 查询备件信息
            PartEntity partEntity = partQueryService.getPartById(new PartId(partId));
            if (partEntity == null) {
                throw new AppException(ResponseCode.PART_NOT_FOUND_ERROR);
            }
            // 查询备件关联工时信息
            List<WorkHourEntity> workHourEntityList = workHourQueryService.getWorkHourTreeByPartId(new PartId(partId));
            List<WorkHourTreeVO> workHourTreeVOList = workHourEntityList.stream()
                    .map(toVOConverter::convertToWorkHourTreeVO)
                    .toList();
            PartVO partVO = toVOConverter.convertToPartVO(partEntity);
            PartDetailVO partDetailVO = PartDetailVO.builder()
                    .partVO(partVO)
                    .workHourTreeVOList(workHourTreeVOList)
                    .build();
            log.info("查询备件详情成功 partId={}", partId);

            return Response.<PartDetailVO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(partDetailVO)
                    .build();
        } catch (AppException e) {
            log.error("查询备件详情失败 partId={}", partId, e);
            return Response.<PartDetailVO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("查询备件详情异常 partId={}", partId, e);
            return Response.<PartDetailVO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "list_parts", method = RequestMethod.GET)
    @Override
    public Response<List<PartVO>> listParts() {
        try {
            log.info("查询所有备件");
            List<PartEntity> partEntities = partQueryService.getAllParts();
            List<PartVO> partVOs = toVOConverter.convertToPartVOList(partEntities);
            log.info("查询所有备件成功，数量={}", partVOs.size());

            return Response.<List<PartVO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(partVOs)
                    .build();
        } catch (Exception e) {
            log.error("查询所有备件异常", e);
            return Response.<List<PartVO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "enable_part", method = RequestMethod.POST)
    @Override
    public Response<Boolean> enablePart(@RequestBody @Valid ChangePartStatusRequestDTO requestDTO) {
        Long partId = requestDTO.getPartId();
        try {
            log.info("启用备件 partId={}", partId);
            boolean result = partCommandService.enablePart(new PartId(partId));
            log.info("启用备件成功 partId={}", partId);

            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(result)
                    .build();
        } catch (AppException e) {
            log.error("启用备件失败 partId={}", partId, e);
            return Response.<Boolean>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("启用备件异常 partId={}", partId, e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "disable_part", method = RequestMethod.POST)
    @Override
    public Response<Boolean> disablePart(@RequestBody @Valid ChangePartStatusRequestDTO requestDTO) {
        Long partId = requestDTO.getPartId();
        try {
            log.info("禁用备件 partId={}", partId);
            boolean result = partCommandService.disablePart(new PartId(partId));
            log.info("禁用备件成功 partId={}", partId);

            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(result)
                    .build();
        } catch (AppException e) {
            log.error("禁用备件失败 partId={}", partId, e);
            return Response.<Boolean>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("禁用备件异常 partId={}", partId, e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "delete_part", method = RequestMethod.POST)
    @Override
    public Response<Boolean> deletePart(@RequestBody @Valid ChangePartStatusRequestDTO requestDTO) {
        Long partId = requestDTO.getPartId();
        try {
            if (partId == null) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER);
            }
            log.info("删除备件 partId={}", partId);
            boolean result = partCommandService.deletePart(new PartId(partId));
            log.info("删除备件成功 partId={}", partId);

            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(result)
                    .build();
        } catch (AppException e) {
            log.error("删除备件失败 partId={}", partId, e);
            return Response.<Boolean>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("删除备件异常 partId={}", partId, e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "batch_bind_hours", method = RequestMethod.POST)
    @Override
    public Response<List<PartBindHourResultVO>> batchBindHours(
            @RequestParam("file") MultipartFile file,
            @RequestParam("creator") String creator) {
        try {
            log.info("批量绑定备件工时关系 creator={}, fileName={}, fileSize={}",
                    creator, file.getOriginalFilename(), file.getSize());
            // 验证文件
            TemplateFileUtil.validateTemplateFile(file);
            // 调用服务处理批量绑定
            List<PartBindHourResultEntity> results = partCommandService.batchBindHours(file, creator);
            log.info("批量绑定备件工时关系成功，处理记录数={}", results.size());
            // 转换为VO
            List<PartBindHourResultVO> resultVOs = results.stream()
                    .map(toVOConverter::convertToPartBindHourResultVO)
                    .collect(Collectors.toList());

            return Response.<List<PartBindHourResultVO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(resultVOs)
                    .build();
        } catch (AppException e) {
            log.error("批量绑定备件工时关系失败", e);

            return Response.<List<PartBindHourResultVO>>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("批量绑定备件工时关系异常", e);

            return Response.<List<PartBindHourResultVO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "unbind_hour", method = RequestMethod.POST)
    @Override
    public Response<Boolean> unbindHour(Long partId, Long hourId) {
        try {
            log.info("解绑工时 partId={}, hourId={}", partId, hourId);
            boolean result = partCommandService.unbindHour(new PartId(partId), new WorkHourId(hourId));
            log.info("解绑工时成功 partId={}, hourId={}", partId, hourId);

            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(result)
                    .build();
        } catch (AppException e) {
            log.error("解绑工时失败 partId={}, hourId={}", partId, hourId, e);

            return Response.<Boolean>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("解绑工时异常 partId={}, hourId={}", partId, hourId, e);

            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

}