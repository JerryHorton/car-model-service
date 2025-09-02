package cn.cug.sxy.domain.event.service;

/**
 * @version 1.0
 * @Date 2025/8/15 14:32
 * @Description 死信处理决策策略接口
 * @Author jerryhotton
 */

public interface IDeadLetterDecisionStrategy {

    /**
     * 决定告警级别
     *
     * @param context 死信上下文
     * @return 告警级别
     */
    IDeadLetterActionExecutor.AlertLevel determineAlertLevel(IDeadLetterHandler.DeadLetterContext context);

    /**
     * 决定目标队列
     *
     * @param context 死信上下文
     * @return 目标队列名称
     */
    String determineTargetQueue(IDeadLetterHandler.DeadLetterContext context);

    /**
     * 决定自定义处理器
     *
     * @param context 死信上下文
     * @return 自定义处理器名称
     */
    String determineCustomHandler(IDeadLetterHandler.DeadLetterContext context);

    /**
     * 决定存储类型
     *
     * @param context 死信上下文
     * @return 存储类型
     */
    IDeadLetterActionExecutor.StorageType determineStorageType(IDeadLetterHandler.DeadLetterContext context);

    /**
     * 决定重新入队延迟时间
     *
     * @param context 死信上下文
     * @return 延迟时间（毫秒），0表示立即入队，-1表示不允许入队
     */
    long determineRequeueDelay(IDeadLetterHandler.DeadLetterContext context);

    /**
     * 决定自定义处理参数
     *
     * @param context 死信上下文
     * @param handlerName 处理器名称
     * @return 自定义参数
     */
    Object determineCustomParams(IDeadLetterHandler.DeadLetterContext context, String handlerName);

}
