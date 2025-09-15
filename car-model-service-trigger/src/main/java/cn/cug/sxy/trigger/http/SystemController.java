package cn.cug.sxy.trigger.http;

import cn.cug.sxy.api.ISystemService;
import cn.cug.sxy.api.dto.SystemCategoryCreateRequestDTO;
import cn.cug.sxy.api.dto.SystemCategoryUpdateRequestDTO;
import cn.cug.sxy.api.dto.SystemGroupCreateRequestDTO;
import cn.cug.sxy.api.dto.SystemGroupUpdateRequestDTO;
import cn.cug.sxy.api.response.Response;
import cn.cug.sxy.api.vo.SystemCategoryVO;
import cn.cug.sxy.api.vo.SystemGroupVO;
import cn.cug.sxy.domain.system.model.entity.SystemCategoryEntity;
import cn.cug.sxy.domain.system.model.entity.SystemGroupEntity;
import cn.cug.sxy.domain.system.model.valobj.CategoryCode;
import cn.cug.sxy.domain.system.model.valobj.CategoryId;
import cn.cug.sxy.domain.system.model.valobj.GroupCode;
import cn.cug.sxy.domain.system.model.valobj.GroupId;
import cn.cug.sxy.domain.system.service.ISystemCategoryService;
import cn.cug.sxy.domain.system.service.ISystemGroupService;
import cn.cug.sxy.trigger.http.converter.ToVOConverter;
import cn.cug.sxy.types.enums.ResponseCode;
import cn.cug.sxy.types.exception.AppException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/9/10 10:16
 * @Description 汽车系统领域控制器
 * @Author jerryhotton
 */

@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/system/")
public class SystemController implements ISystemService {

    private final ISystemCategoryService systemCategoryService;
    private final ISystemGroupService systemGroupService;
    private final ToVOConverter toVOConverter;

    public SystemController(
            ISystemCategoryService systemCategoryService,
            ISystemGroupService systemGroupService,
            ToVOConverter toVOConverter) {
        this.systemCategoryService = systemCategoryService;
        this.systemGroupService = systemGroupService;
        this.toVOConverter = toVOConverter;
    }

    @RequestMapping(value = "create_category", method = RequestMethod.POST)
    @Override
    public Response<SystemCategoryVO> createCategory(@RequestBody @Valid SystemCategoryCreateRequestDTO requestDTO) {
        String categoryCode = requestDTO.getCategoryCode();
        String categoryName = requestDTO.getCategoryName();
        String categoryNameEn = requestDTO.getCategoryNameEn();
        Integer sortOrder = requestDTO.getSortOrder();
        String creator = requestDTO.getCreator();
        try {
            log.info("创建系统大类 categoryCode={}, categoryName={}, categoryNameEn={}, sortOrder={}, creator={}",
                    categoryCode, categoryName, categoryNameEn, sortOrder, creator);
            SystemCategoryEntity entity = systemCategoryService.createCategory(
                    new CategoryCode(categoryCode),
                    categoryName,
                    categoryNameEn,
                    sortOrder,
                    creator
            );
            SystemCategoryVO vo = toVOConverter.convertToCategoryVO(entity);
            log.info("创建系统大类成功 categoryId={}, categoryCode={}, categoryName={}",
                    entity.getCategoryId().getId(), categoryCode, categoryName);

            return Response.<SystemCategoryVO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(vo)
                    .build();
        } catch (AppException e) {
            log.error("创建系统大类失败 categoryCode={}, categoryName={}, categoryNameEn={}, sortOrder={}, creator={}",
                    categoryCode, categoryName, categoryNameEn, sortOrder, creator, e);

            return Response.<SystemCategoryVO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("创建系统大类异常 categoryCode={}, categoryName={}, categoryNameEn={}, sortOrder={}, creator={}",
                    categoryCode, categoryName, categoryNameEn, sortOrder, creator, e);

            return Response.<SystemCategoryVO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "update_category", method = RequestMethod.POST)
    @Override
    public Response<SystemCategoryVO> updateCategory(@RequestBody @Valid SystemCategoryUpdateRequestDTO requestDTO) {
        Long categoryId = requestDTO.getCategoryId();
        String categoryCode = requestDTO.getCategoryCode();
        String categoryName = requestDTO.getCategoryName();
        String categoryNameEn = requestDTO.getCategoryNameEn();
        Integer sortOrder = requestDTO.getSortOrder();
        try {
            log.info("更新系统大类 categoryId={}, categoryCode={}, categoryName={}, categoryNameEn={}, sortOrder={}",
                    categoryId, categoryCode, categoryName, categoryNameEn, sortOrder);
            boolean result = systemCategoryService.updateCategory(
                    new CategoryId(categoryId),
                    new CategoryCode(categoryCode),
                    categoryName,
                    categoryNameEn,
                    sortOrder
            );
            log.info("更新系统大类成功 categoryId={}, result={}", categoryId, result);

            return Response.<SystemCategoryVO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .build();
        } catch (AppException e) {
            log.error("更新系统大类失败 categoryId={}, categoryCode={}, categoryName={}, categoryNameEn={}, sortOrder={}",
                    categoryId, categoryCode, categoryName, categoryNameEn, sortOrder, e);

            return Response.<SystemCategoryVO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("更新系统大类异常 categoryId={}, categoryCode={}, categoryName={}, categoryNameEn={}, sortOrder={}",
                    categoryId, categoryCode, categoryName, categoryNameEn, sortOrder, e);

            return Response.<SystemCategoryVO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "get_all_categories", method = RequestMethod.GET)
    @Override
    public Response<List<SystemCategoryVO>> getAllCategories() {
        try {
            log.info("获取所有系统大类");
            List<SystemCategoryEntity> entities = systemCategoryService.findAll();
            List<SystemCategoryVO> vos = entities.stream()
                    .map(toVOConverter::convertToCategoryVO)
                    .collect(Collectors.toList());
            log.info("获取所有系统大类成功，数量={}", vos.size());

            return Response.<List<SystemCategoryVO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(vos)
                    .build();
        } catch (AppException e) {
            log.error("获取所有系统大类失败", e);

            return Response.<List<SystemCategoryVO>>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("获取所有系统大类异常", e);

            return Response.<List<SystemCategoryVO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "create_group", method = RequestMethod.POST)
    @Override
    public Response<SystemGroupVO> createGroup(@RequestBody @Valid SystemGroupCreateRequestDTO requestDTO) {
        Long categoryId = requestDTO.getCategoryId();
        String groupCode = requestDTO.getGroupCode();
        String groupName = requestDTO.getGroupName();
        String groupNameEn = requestDTO.getGroupNameEn();
        Integer sortOrder = requestDTO.getSortOrder();
        String creator = requestDTO.getCreator();

        try {
            log.info("创建系统分组 categoryId={}, groupCode={}, groupName={}, groupNameEn={}, sortOrder={}, creator={}",
                    categoryId, groupCode, groupName, groupNameEn, sortOrder, creator);

            SystemGroupEntity entity = systemGroupService.createGroup(
                    new CategoryId(categoryId),
                    new GroupCode(groupCode),
                    groupName,
                    groupNameEn,
                    sortOrder,
                    creator
            );

            SystemGroupVO vo = toVOConverter.convertToGroupVO(entity);
            log.info("创建系统分组成功 groupId={}, categoryId={}, groupCode={}, groupName={}",
                    entity.getGroupId().getId(), categoryId, groupCode, groupName);

            return Response.<SystemGroupVO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(vo)
                    .build();
        } catch (AppException e) {
            log.error("创建系统分组失败 categoryId={}, groupCode={}, groupName={}, groupNameEn={}, sortOrder={}, creator={}",
                    categoryId, groupCode, groupName, groupNameEn, sortOrder, creator, e);

            return Response.<SystemGroupVO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("创建系统分组异常 categoryId={}, groupCode={}, groupName={}, groupNameEn={}, sortOrder={}, creator={}",
                    categoryId, groupCode, groupName, groupNameEn, sortOrder, creator, e);

            return Response.<SystemGroupVO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "update_group", method = RequestMethod.POST)
    @Override
    public Response<SystemGroupVO> updateGroup(@RequestBody @Valid SystemGroupUpdateRequestDTO requestDTO) {
        Long groupId = requestDTO.getGroupId();
        Long categoryId = requestDTO.getCategoryId();
        String groupCode = requestDTO.getGroupCode();
        String groupName = requestDTO.getGroupName();
        String groupNameEn = requestDTO.getGroupNameEn();
        Integer sortOrder = requestDTO.getSortOrder();
        try {
            log.info("更新系统分组 groupId={}, categoryId={}, groupCode={}, groupName={}, groupNameEn={}, sortOrder={}",
                    groupId, categoryId, groupCode, groupName, groupNameEn, sortOrder);
            boolean result = systemGroupService.updateGroup(
                    new GroupId(groupId),
                    new CategoryId(categoryId),
                    new GroupCode(groupCode),
                    groupName,
                    groupNameEn,
                    sortOrder
            );
            log.info("更新系统分组成功 groupId={}, result={}", groupId, result);

            return Response.<SystemGroupVO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .build();
        } catch (AppException e) {
            log.error("更新系统分组失败 groupId={}, categoryId={}, groupCode={}, groupName={}, groupNameEn={}, sortOrder={}",
                    groupId, categoryId, groupCode, groupName, groupNameEn, sortOrder, e);

            return Response.<SystemGroupVO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("更新系统分组异常 groupId={}, categoryId={}, groupCode={}, groupName={}, groupNameEn={}, sortOrder={}",
                    groupId, categoryId, groupCode, groupName, groupNameEn, sortOrder, e);

            return Response.<SystemGroupVO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "get_groups_by_category", method = RequestMethod.GET)
    @Override
    public Response<List<SystemGroupVO>> getGroupsByCategoryId(@RequestParam Long categoryId) {
        try {
            log.info("根据大类ID获取系统分组 categoryId={}", categoryId);
            List<SystemGroupEntity> entities = systemGroupService.findByCategoryId(new CategoryId(categoryId));
            List<SystemGroupVO> vos = entities.stream()
                    .map(toVOConverter::convertToGroupVO)
                    .collect(Collectors.toList());
            log.info("根据大类ID获取系统分组成功 categoryId={}, 数量={}", categoryId, vos.size());

            return Response.<List<SystemGroupVO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(vos)
                    .build();
        } catch (AppException e) {
            log.error("根据大类ID获取系统分组失败 categoryId={}", categoryId, e);

            return Response.<List<SystemGroupVO>>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("根据大类ID获取系统分组异常 categoryId={}", categoryId, e);

            return Response.<List<SystemGroupVO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "delete_category", method = RequestMethod.POST)
    @Override
    public Response<Boolean> deleteCategory(@RequestParam Long categoryId) {
        try {
            log.info("删除系统大类 categoryId={}", categoryId);
            boolean result = systemCategoryService.deleteCategory(new CategoryId(categoryId));

            log.info("删除系统大类成功 categoryId={}, result={}", categoryId, result);

            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(result)
                    .build();
        } catch (AppException e) {
            log.error("删除系统大类失败 categoryId={}", categoryId, e);

            return Response.<Boolean>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("删除系统大类异常 categoryId={}", categoryId, e);

            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "delete_group", method = RequestMethod.POST)
    @Override
    public Response<Boolean> deleteGroup(@RequestParam Long groupId) {
        try {
            log.info("删除系统分组 groupId={}", groupId);
            // 物理删除
            boolean result = systemGroupService.deleteGroup(new GroupId(groupId));
            log.info("删除系统分组成功 groupId={}, result={}", groupId, result);

            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(result)
                    .build();
        } catch (AppException e) {
            log.error("删除系统分组失败 groupId={}", groupId, e);

            return Response.<Boolean>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("删除系统分组异常 groupId={}", groupId, e);

            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

}
