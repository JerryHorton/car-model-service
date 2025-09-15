package cn.cug.sxy.api;

import cn.cug.sxy.api.dto.UsageCreateRequestDTO;
import cn.cug.sxy.api.dto.UsageUpdateRequestDTO;
import cn.cug.sxy.api.response.Response;
import cn.cug.sxy.api.vo.UsageBaseVO;
import cn.cug.sxy.api.vo.UsageCreationVO;
import cn.cug.sxy.api.vo.UsageDetailVO;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/8/6 18:46
 * @Description 用法服务接口
 * @Author jerryhotton
 */

public interface IUsageService {

    /**
     * 创建用法
     *
     * @param requestDTO 用法创建请求DTO
     * @return 用法创建结果VO
     */
    Response<UsageCreationVO> createUsage(UsageCreateRequestDTO requestDTO);

    /**
     * 查询可用用法
     *
     * @param groupNodeId 分组节点ID
     * @return 用法基础信息VO列表
     */
    Response<List<UsageBaseVO>> queryUsages(Long groupNodeId);


    /**
     * 查询所有用法
     *
     * @param groupNodeId 分组节点ID
     * @return 用法基础信息VO列表
     */
    Response<List<UsageBaseVO>> queryAllUsages(Long groupNodeId);

    /**
     * 查询用法详情
     *
     * @param usageId 用法ID
     * @return 用法详情
     */
    Response<UsageDetailVO> getUsageDetail(Long usageId);

    /**
     * 更新用法
     *
     * @param requestDTO 更新请求DTO
     * @return 更新结果
     */
    Response<UsageBaseVO> updateUsage(UsageUpdateRequestDTO requestDTO);

    /**
     * 删除配置组合
     *
     * @param combinationId 配置组合ID
     * @return 删除结果
     */
    Response<Boolean> deleteCombination(Long combinationId);

    /**
     * 删除用法
     *
     * @param usageId 用法ID
     * @return 删除结果
     */
    Response<Boolean> deleteUsage(Long usageId);

    /**
     * 恢复用法
     *
     * @param usageId 用法ID
     * @return 恢复结果
     */
    Response<Boolean> restoreUsage(Long usageId);

}
