package cn.cug.sxy.trigger.http;

import cn.cug.sxy.api.IUsagePartService;
import cn.cug.sxy.api.dto.ClearPartsRequestDTO;
import cn.cug.sxy.api.dto.UnBindPartRequestDTO;
import cn.cug.sxy.api.dto.UsageBindPartRequestDTO;
import cn.cug.sxy.api.response.Response;
import cn.cug.sxy.api.vo.UsageBindPartResultVO;
import cn.cug.sxy.api.vo.UsagePartVO;
import cn.cug.sxy.domain.part.model.entity.UsageBindPartResultEntity;
import cn.cug.sxy.domain.part.model.valobj.PartId;
import cn.cug.sxy.domain.part.service.IPartUsageManageService;
import cn.cug.sxy.domain.usage.model.entity.UsagePartEntity;
import cn.cug.sxy.domain.usage.model.valobj.UsageId;
import cn.cug.sxy.trigger.http.converter.ToVOConverter;
import cn.cug.sxy.types.enums.ResponseCode;
import cn.cug.sxy.types.exception.AppException;
import cn.cug.sxy.types.utils.TemplateFileUtil;
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
 * @Date 2025/9/5 10:56
 * @Description
 * @Author jerryhotton
 */

@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/usage_part/")
@DubboService(version = "1.0")
public class UsagePartController implements IUsagePartService {

    private final IPartUsageManageService partUsageManageService;
    private final ToVOConverter toVOConverter;

    public UsagePartController(IPartUsageManageService partUsageManageService, ToVOConverter toVOConverter) {
        this.partUsageManageService = partUsageManageService;
        this.toVOConverter = toVOConverter;
    }

    @RequestMapping(value = "bind", method = RequestMethod.POST)
    @Override
    public Response<UsagePartVO> bindPart(@RequestBody UsageBindPartRequestDTO requestDTO) {
        Long usageId = requestDTO.getUsageId();
        Long partId = requestDTO.getPartId();
        Integer count = requestDTO.getCount();
        try {
            log.info("绑定备件到用法 usageId={}, partId={}, count={}",
                    usageId, partId, count);
            UsagePartEntity entity = partUsageManageService.bindPart(
                    new UsageId(usageId),
                    new PartId(partId),
                    count
            );
            UsagePartVO vo = toVOConverter.convertToUsagePartVO(entity);
            log.info("绑定备件到用法成功 usageId={}, partId={}", usageId, partId);

            return Response.<UsagePartVO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(vo)
                    .build();
        } catch (AppException e) {
            log.error("绑定备件到用法失败 usageId={}, partId={}", usageId, partId, e);

            return Response.<UsagePartVO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("绑定备件到用法异常 usageId={}, partId={}", usageId, partId, e);

            return Response.<UsagePartVO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "unbind", method = RequestMethod.POST)
    @Override
    public Response<Boolean> unbindPart(@RequestBody UnBindPartRequestDTO requestDTO) {
        Long usageId = requestDTO.getUsageId();
        Long partId = requestDTO.getPartId();
        try {
            log.info("解绑备件 usageId={}, partId={}", usageId, partId);
            boolean result = partUsageManageService.unbindPart(
                    new UsageId(usageId),
                    new PartId(partId)
            );
            log.info("解绑备件成功 usageId={}, partId={}", usageId, partId);

            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(result)
                    .build();
        } catch (AppException e) {
            log.error("解绑备件失败 usageId={}, partId={}", usageId, partId, e);

            return Response.<Boolean>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("解绑备件异常 usageId={}, partId={}", usageId, partId, e);

            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "clear", method = RequestMethod.POST)
    @Override
    public Response<Boolean> clearParts(@RequestBody ClearPartsRequestDTO requestDTO) {
        Long usageId = requestDTO.getUsageId();
        try {
            log.info("清空用法关联的所有备件 usageId={}", usageId);
            boolean result = partUsageManageService.clearParts(new UsageId(usageId));
            log.info("清空用法关联的所有备件成功 usageId={}", usageId);

            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(result)
                    .build();
        } catch (AppException e) {
            log.error("清空用法关联的所有备件失败 usageId={}", usageId, e);

            return Response.<Boolean>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("清空用法关联的所有备件异常 usageId={}", usageId, e);

            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "list", method = RequestMethod.GET)
    @Override
    public Response<List<UsagePartVO>> getPartsByUsageId(@RequestParam("usageId") Long usageId) {
        try {
            log.info("查询用法关联的所有备件 usageId={}", usageId);
            List<UsagePartEntity> entities = partUsageManageService.getPartsByUsageId(new UsageId(usageId));
            List<UsagePartVO> vos = entities.stream()
                    .map(toVOConverter::convertToUsagePartVO)
                    .collect(Collectors.toList());
            log.info("查询用法关联的所有备件成功 usageId={}, 数量={}", usageId, vos.size());

            return Response.<List<UsagePartVO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(vos)
                    .build();
        } catch (AppException e) {
            log.error("查询用法关联的所有备件失败 usageId={}", usageId, e);

            return Response.<List<UsagePartVO>>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("查询用法关联的所有备件异常 usageId={}", usageId, e);

            return Response.<List<UsagePartVO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "batch_upload", method = RequestMethod.POST)
    @Override
    public Response<List<UsageBindPartResultVO>> batchUpload(@RequestParam("usageId") Long usageId, @RequestParam("file") MultipartFile file) {
        try {
            log.info("批量上传备件 usageId={}, fileName={}, fileSize={}",
                    usageId, file.getOriginalFilename(), file.getSize());
            // 验证文件
            TemplateFileUtil.validateTemplateFile(file);
            // 调用服务处理批量上传
            List<UsageBindPartResultEntity> results = partUsageManageService.batchUpload(
                    new UsageId(usageId), file);
            log.info("批量上传备件成功 usageId={}, 处理记录数={}", usageId, results.size());

            return Response.<List<UsageBindPartResultVO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(results.stream()
                            .map(toVOConverter::convertToUsageBindPartResultVO)
                            .collect(Collectors.toList()))
                    .build();
        } catch (AppException e) {
            log.error("批量上传备件失败 usageId={}", usageId, e);

            return Response.<List<UsageBindPartResultVO>>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("批量上传备件异常 usageId={}", usageId, e);

            return Response.<List<UsageBindPartResultVO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "upload_template", method = RequestMethod.POST)
    @Override
    public Response<String> uploadTemplate(@RequestParam("file") MultipartFile file) {
        try {
            log.info("上传用法备件关联模板 fileName={}, fileSize={}",
                    file.getOriginalFilename(), file.getSize());
            // 验证文件
            TemplateFileUtil.validateTemplateFile(file);
            // 调用服务上传模板
            String result = partUsageManageService.uploadTemplate(file);
            log.info("用法备件关联模板上传成功");

            return Response.<String>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(result)
                    .build();
        } catch (AppException e) {
            log.error("上传用法备件关联模板失败", e);

            return Response.<String>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("上传用法备件关联模板异常", e);

            return Response.<String>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "template_info", method = RequestMethod.GET)
    @Override
    public Response<String> getTemplateInfo() {
        try {
            log.info("获取用法备件关联模板信息");
            String templateInfo = partUsageManageService.getTemplateInfo();

            return Response.<String>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(templateInfo)
                    .build();
        } catch (AppException e) {
            log.error("获取用法备件关联模板信息失败", e);

            return Response.<String>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("获取用法备件关联模板信息异常", e);

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
            log.info("下载用法备件关联模板");
            // 从MinIO获取模板文件
            byte[] templateData = partUsageManageService.getTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            String filename = "用法备件关联模板.xlsx";
            String encodedFilename = UriUtils.encode(filename, StandardCharsets.UTF_8);
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename);
            headers.setContentLength(templateData.length);
            log.info("用法备件关联模板下载成功，文件大小: {} bytes", templateData.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(templateData);
        } catch (Exception e) {
            log.error("下载用法备件关联模板失败", e);

            return ResponseEntity.internalServerError().build();
        }
    }

}