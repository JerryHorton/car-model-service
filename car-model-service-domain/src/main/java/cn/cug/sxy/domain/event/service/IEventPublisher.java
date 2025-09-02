package cn.cug.sxy.domain.event.service;

import cn.cug.sxy.types.event.BaseEvent;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/8/12 15:39
 * @Description 事件发布服务接口
 * @Author jerryhotton
 */

public interface IEventPublisher {

    /**
     * 发布事件到消息队列（带持久化保证）
     *
     * @param <T>          事件数据类型
     * @param eventMessage 事件消息对象
     * @return 是否发布成功
     */
    <T> boolean publishEvent(BaseEvent.EventMessage<T> eventMessage);

    /**
     * 发布事件到指定交换机和路由键（带持久化保证）
     *
     * @param eventMessage 事件消息对象
     * @param exchange   交换机名称
     * @param routingKey 路由键
     * @param <T>        事件数据类型
     * @return 是否发布成功
     */
    <T> boolean publishEvent(BaseEvent.EventMessage<T> eventMessage, String exchange, String routingKey);

    /**
     * 发布延迟事件（用于重试，带持久化保证）
     *
     * @param eventMessage 事件消息对象
     * @param delayMillis 延迟毫秒数
     * @param <T>         事件数据类型
     * @return 是否发布成功
     */
    <T> boolean publishDelayedEvent(BaseEvent.EventMessage<T> eventMessage, long delayMillis);

    /**
     * 发布持久化事件（事务性发布）
     * 确保消息在数据库事务提交后才发送到队列
     *
     * @param eventMessage 事件消息对象
     * @param <T>   事件数据类型
     * @return 是否发布成功
     */
    <T> boolean publishPersistentEvent(BaseEvent.EventMessage<T> eventMessage);

    /**
     * 批量发布事件（提高性能）
     *
     * @param eventMessages 事件消息列表
     * @param <T>    事件数据类型
     * @return 成功发布的事件数量
     */
    <T> int publishBatchEvents(List<BaseEvent.EventMessage<T>> eventMessages);

    /**
     * 重新发布失败的事件
     * 用于故障恢复场景
     *
     * @param messageId 消息ID
     * @return 是否重新发布成功
     */
    boolean republishFailedEvent(String messageId);

}
