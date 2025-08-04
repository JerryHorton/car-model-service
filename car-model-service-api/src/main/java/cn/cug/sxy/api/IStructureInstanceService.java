package cn.cug.sxy.api;

import cn.cug.sxy.api.dto.*;
import cn.cug.sxy.api.response.Response;
import cn.cug.sxy.api.vo.*;

/**
 * @version 1.0
 * @Date 2025/7/31 15:53
 * @Description 车型结构树实例服务接口
 * @Author jerryhotton
 */

public interface IStructureInstanceService {

    /**
     * 创建实例
     *
     * @param requestDTO 实例创建请求DTO
     * @return 实例详情VO
     */
    Response<InstanceDetailVO> createInstance(InstanceCreateRequestDTO requestDTO);

    /**
     * 获取实例详情
     *
     * @param instanceId 实例ID
     * @return 实例详情VO
     */
    Response<InstanceDetailVO> getInstanceDetail(Long instanceId);

    /**
     * 查询实例列表
     *
     * @param requestDTO 查询实例请求DTO
     * @return 实例基础分页VO
     */
    Response<InstanceBasePageVO> queryInstances(InstanceQueryRequestDTO requestDTO);

    /**
     * 创建实例新版本
     *
     * @param requestDTO 新版本请求DTO
     * @return 实例详情VO
     */
    Response<InstanceDetailVO> createNewVersion(InstanceNewVersionRequestDTO requestDTO);

    /**
     * 添加节点
     *
     * @param requestDTO 添加节点请求DTO
     * @return 实例节点VO
     */
    Response<InstanceNodeVO> addNode(InstanceAddNodeRequestDTO requestDTO);

    /**
     * 移动节点
     *
     * @param requestDTO 移动节点请求DTO
     * @return 移动结果
     */
    Response<Boolean> moveNode(InstanceMoveNodeRequestDTO requestDTO);

    /**
     * 删除节点
     *
     * @param nodeId 节点ID
     * @return 删除的节点数量
     */
    Response<Integer> deleteNode(Long nodeId);

    /**
     * 校验实例结构
     *
     * @param instanceId 实例ID
     * @return 校验结果VO
     */
    Response<InstanceValidateResultVO> validateStructure(Long instanceId);

    /**
     * 发布实例
     *
     * @param requestDTO 发布实例请求DTO
     * @return 发布结果
     */
    Response<Boolean> publishInstance(InstancePublishRequestDTO requestDTO);

    /**
     * 取消发布实例
     *
     * @param instanceId 实例ID
     * @return 取消发布结果
     */
    Response<Boolean> unpublishInstance(Long instanceId);

    /**
     * 启用实例
     *
     * @param instanceId 实例ID
     * @return 启用结果
     */
    Response<Boolean> enableInstance(Long instanceId);

    /**
     * 禁用实例
     *
     * @param instanceId 实例ID
     * @return 禁用结果
     */
    Response<Boolean> disableInstance(Long instanceId);

    /**
     * 比较实例差异
     *
     * @param instanceId1 实例ID1
     * @param instanceId2 实例ID2
     * @return 实例差异VO
     */
    Response<InstanceCompareResultVO> compareInstances(Long instanceId1, Long instanceId2);

}
