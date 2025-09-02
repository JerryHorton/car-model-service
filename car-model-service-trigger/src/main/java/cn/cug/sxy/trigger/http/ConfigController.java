package cn.cug.sxy.trigger.http;

import cn.cug.sxy.api.IConfigService;
import cn.cug.sxy.api.dto.ConfigCategoryCreateRequestDTO;
import cn.cug.sxy.api.dto.ConfigCategoryUpdateRequestDTO;
import cn.cug.sxy.api.dto.ConfigItemCreateRequestDTO;
import cn.cug.sxy.api.dto.SearchConfigCategoryRequestDTO;
import cn.cug.sxy.api.response.Response;
import cn.cug.sxy.api.vo.ConfigCategoryVO;
import cn.cug.sxy.api.vo.ConfigItemVO;
import cn.cug.sxy.domain.usage.model.entity.ConfigCategoryEntity;
import cn.cug.sxy.domain.usage.model.entity.ConfigItemEntity;
import cn.cug.sxy.domain.usage.model.valobj.ConfigCategoryId;
import cn.cug.sxy.domain.usage.model.valobj.ConfigItemId;
import cn.cug.sxy.domain.usage.service.ConfigQueryService;
import cn.cug.sxy.domain.usage.service.IConfigManagementService;
import cn.cug.sxy.types.enums.ResponseCode;
import cn.cug.sxy.types.exception.AppException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/8/6 19:32
 * @Description
 * @Author jerryhotton
 */

@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/config/")
public class ConfigController implements IConfigService {

    private final IConfigManagementService configManagementService;
    private final ConfigQueryService configQueryService;

    public ConfigController(IConfigManagementService configManagementService, ConfigQueryService configQueryService) {
        this.configManagementService = configManagementService;
        this.configQueryService = configQueryService;
    }

    @RequestMapping(value = "create_category", method = RequestMethod.POST)
    @Override
    public Response<ConfigCategoryVO> createCategory(@RequestBody @Valid ConfigCategoryCreateRequestDTO requestDTO) {
        String categoryName = requestDTO.getCategoryName();
        String categoryCode = requestDTO.getCategoryCode();
        String creator = requestDTO.getCreator();
        try {
            log.info("创建配置类别 categoryName={}, categoryCode={}, creator={}", categoryName, categoryCode, creator);
            ConfigCategoryEntity category = configManagementService.createCategory(categoryCode, categoryName, requestDTO.getSortOrder(), creator);
            ConfigCategoryVO categoryVO = convertToCategoryVO(category);
            log.info("创建配置类别成功 categoryId={}, categoryName={}, categoryCode={}, creator={}",
                    category.getId().getId(), categoryName, categoryCode, creator);

            return Response.<ConfigCategoryVO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(categoryVO)
                    .build();
        } catch (AppException e) {
            log.error("创建配置类别失败 categoryName={}, categoryCode={}, creator={}",
                    categoryName, categoryCode, creator, e);

            return Response.<ConfigCategoryVO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("创建配置类别异常 categoryName={}, categoryCode={}, creator={}",
                    categoryName, categoryCode, creator, e);

            return Response.<ConfigCategoryVO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "update_category", method = RequestMethod.POST)
    @Override
    public Response<ConfigCategoryVO> updateCategory(@RequestBody @Valid ConfigCategoryUpdateRequestDTO requestDTO) {
        Long categoryId = requestDTO.getCategoryId();
        String categoryCode = requestDTO.getCategoryCode();
        String categoryName = requestDTO.getCategoryName();
        Integer sortOrder = requestDTO.getSortOrder();
        try {
            log.info("更新配置类别 categoryId={}, categoryCode={}, categoryName={}, sortOrder={}",
                    categoryId, categoryCode, categoryName, sortOrder);
            ConfigCategoryEntity category = configManagementService.updateCategory(
                    new ConfigCategoryId(categoryId),
                    categoryCode,
                    categoryName,
                    sortOrder
            );
            ConfigCategoryVO categoryVO = convertToCategoryVO(category);
            log.info("更新配置类别成功 categoryId={}, categoryName={}, categoryCode={}, sortOrder={}",
                    category.getId().getId(), categoryName, categoryCode, sortOrder);

            return Response.<ConfigCategoryVO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(categoryVO)
                    .build();
        } catch (AppException e) {
            log.error("更新配置类别失败 categoryId={}, categoryCode={}, categoryName={}, sortOrder={}",
                    categoryId, categoryCode, categoryName, sortOrder, e);

            return Response.<ConfigCategoryVO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("更新配置类别异常 categoryId={}, categoryCode={}, categoryName={}, sortOrder={}",
                    categoryId, categoryCode, categoryName, sortOrder, e);

            return Response.<ConfigCategoryVO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "get_categories", method = RequestMethod.GET)
    @Override
    public Response<List<ConfigCategoryVO>> getCategories() {
        try {
            log.info("查询所有可用配置类别");
            List<ConfigCategoryEntity> categories = configQueryService.findAllEnabledCategories();
            List<ConfigCategoryVO> categoryVOS = categories.stream()
                    .map(this::convertToCategoryVO)
                    .collect(Collectors.toList());
            log.info("查询所有可用配置类别成功");

            return Response.<List<ConfigCategoryVO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(categoryVOS)
                    .build();
        } catch (AppException e) {
            log.error("查询所有可用配置类别失败", e);

            return Response.<List<ConfigCategoryVO>>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("查询所有可用配置类别异常", e);

            return Response.<List<ConfigCategoryVO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "search_categories", method = RequestMethod.POST)
    @Override
    public Response<List<ConfigCategoryVO>> searchCategories(@RequestBody SearchConfigCategoryRequestDTO requestDTO) {
        String categoryCode = requestDTO.getCategoryCode();
        String nameKeyword = requestDTO.getNameKeyword();
        try {
            log.info("查询配置类别 categoryCode={}, nameKeyword={}", categoryCode, nameKeyword);
            List<ConfigCategoryEntity> categories = configQueryService.searchCategories(categoryCode, nameKeyword);
            List<ConfigCategoryVO> categoryVOS = categories.stream()
                    .map(this::convertToCategoryVO)
                    .collect(Collectors.toList());
            log.info("查询配置类别成功 categoryCode={}, nameKeyword={}, resultSize={}", categoryCode, nameKeyword, categoryVOS.size());

            return Response.<List<ConfigCategoryVO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(categoryVOS)
                    .build();
        } catch (AppException e) {
            log.error("查询配置类别失败 categoryCode={}, nameKeyword={}", categoryCode, nameKeyword, e);

            return Response.<List<ConfigCategoryVO>>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("查询配置类别异常 categoryCode={}, nameKeyword={}", categoryCode, nameKeyword, e);

            return Response.<List<ConfigCategoryVO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "delete_category", method = RequestMethod.POST)
    @Override
    public Response<Boolean> deleteCategory(@RequestParam Long categoryId) {
        try {
            if (categoryId == null) {
                throw new IllegalArgumentException("配置类别ID不能为空");
            }
            log.info("删除配置类别 categoryId={}", categoryId);
            boolean deleted = configManagementService.deleteCategory(new ConfigCategoryId(categoryId));
            log.info("删除配置类别成功 categoryId={}, deleted={}", categoryId, deleted);

            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(deleted)
                    .build();
        } catch (AppException e) {
            log.error("删除配置类别失败 categoryId={}", categoryId, e);

            return Response.<Boolean>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("删除配置类别异常 categoryId={}", categoryId, e);

            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "create_config_item", method = RequestMethod.POST)
    @Override
    public Response<ConfigItemVO> createConfigItem(@RequestBody @Valid ConfigItemCreateRequestDTO requestDTO) {
        Long categoryId = requestDTO.getCategoryId();
        String itemCode = requestDTO.getItemCode();
        String itemName = requestDTO.getItemName();
        String itemValue = requestDTO.getItemValue();
        String creator = requestDTO.getCreator();
        try {
            ConfigItemEntity configItemEntity = configManagementService.createConfigItem(
                    new ConfigCategoryId(categoryId),
                    itemCode,
                    itemName,
                    itemValue,
                    creator
            );
            ConfigItemVO configItemVO = convertToItemVO(configItemEntity);
            log.info("创建配置项成功 categoryId={}, itemId={}, itemCode={}, itemName={}, itemValue={}, creator={}",
                    categoryId, configItemVO.getId(), itemCode, itemName, itemValue, creator);

            return Response.<ConfigItemVO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(configItemVO)
                    .build();
        } catch (AppException e) {
            log.error("创建配置项失败 categoryId={}, itemCode={}, itemName={}, itemValue={}, creator={}",
                    categoryId, itemCode, itemName, itemValue, creator, e);

            return Response.<ConfigItemVO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("创建配置项异常 categoryId={}, itemCode={}, itemName={}, itemValue={}, creator={}",
                    categoryId, itemCode, itemName, itemValue, creator, e);

            return Response.<ConfigItemVO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "get_config_items", method = RequestMethod.GET)
    @Override
    public Response<List<ConfigItemVO>> getConfigItems(@RequestParam Long categoryId) {
        try {
            if (categoryId == null) {
                throw new IllegalArgumentException("配置类别ID不能为空");
            }
            log.info("查询配置项 categoryId={}", categoryId);
            List<ConfigItemEntity> items = configQueryService.findEnabledItemsByCategoryId(new ConfigCategoryId(categoryId));
            List<ConfigItemVO> itemVOS = items.stream()
                    .map(this::convertToItemVO)
                    .collect(Collectors.toList());
            log.info("查询配置项成功 categoryId={}, resultSize={}", categoryId, itemVOS.size());

            return Response.<List<ConfigItemVO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(itemVOS)
                    .build();
        } catch (AppException e) {
            log.error("查询配置项失败 categoryId={}", categoryId, e);

            return Response.<List<ConfigItemVO>>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("查询配置项异常 categoryId={}", categoryId, e);

            return Response.<List<ConfigItemVO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "search_config_items", method = RequestMethod.GET)
    @Override
    public Response<List<ConfigItemVO>> searchConfigItems(@RequestParam String keyword) {
        try {
            if (StringUtils.isBlank(keyword)) {
                throw new IllegalArgumentException("搜索关键词不能为空");
            }
            log.info("搜索配置项 keyword={}", keyword);
            List<ConfigItemEntity> items = configQueryService.searchItemsByKeyword(keyword);
            List<ConfigItemVO> itemVOS = items.stream()
                    .map(this::convertToItemVO)
                    .collect(Collectors.toList());
            log.info("搜索配置项成功 keyword={}, resultSize={}", keyword, itemVOS.size());

            return Response.<List<ConfigItemVO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(itemVOS)
                    .build();
        } catch (AppException e) {
            log.error("搜索配置项失败 keyword={}", keyword, e);

            return Response.<List<ConfigItemVO>>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("搜索配置项异常 keyword={}", keyword, e);

            return Response.<List<ConfigItemVO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "delete_config_item", method = RequestMethod.POST)
    @Override
    public Response<Boolean> deleteConfigItem(@RequestParam Long itemId) {
        try {
            if (itemId == null) {
                throw new IllegalArgumentException("配置项ID不能为空");
            }
            log.info("删除配置项 itemId={}", itemId);
            boolean result = configManagementService.deleteConfigItem(new ConfigItemId(itemId));
            log.info("删除配置项成功 itemId={}, result={}", itemId, result);

            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(result)
                    .build();
        } catch (AppException e) {
            log.error("删除配置项失败 itemId={}", itemId, e);

            return Response.<Boolean>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("删除配置项异常 itemId={}", itemId, e);

            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    /**
     * /**
     * 转换配置类别实体为VO
     */
    private ConfigCategoryVO convertToCategoryVO(ConfigCategoryEntity category) {
        ConfigCategoryVO vo = new ConfigCategoryVO();
        vo.setId(category.getId().getId());
        vo.setCategoryCode(category.getCategoryCode());
        vo.setCategoryName(category.getCategoryName());
        vo.setSortOrder(category.getSortOrder());
        vo.setStatus(category.getStatus().getCode());
        vo.setCreator(category.getCreator());
        vo.setCreatedTime(category.getCreatedTime());
        vo.setUpdatedTime(category.getUpdatedTime());

        return vo;
    }

    /**
     * 转换配置项实体为VO
     */
    private ConfigItemVO convertToItemVO(ConfigItemEntity item) {
        ConfigItemVO vo = new ConfigItemVO();
        vo.setId(item.getId().getId());
        vo.setCategoryId(item.getCategoryId().getId());
        vo.setItemCode(item.getItemCode());
        vo.setItemName(item.getItemName());
        vo.setItemValue(item.getItemValue());
        vo.setStatus(item.getStatus().name());
        vo.setCreator(item.getCreator());
        vo.setCreatedTime(item.getCreatedTime());
        vo.setUpdatedTime(item.getUpdatedTime());

        return vo;
    }

}
