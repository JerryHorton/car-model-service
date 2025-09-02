package cn.cug.sxy.domain.event.service;

/**
 * @version 1.0
 * @Date 2025/8/13 11:00
 * @Description 消息恢复服务接口
 * @Author jerryhotton
 */

public interface IMessageRecoveryService {

    /**
     * 恢复未发布的消息
     * 系统启动时或定时任务中调用，重新发送未成功发布的消息
     *
     * @return 恢复的消息数量
     */
    int recoverUnpublishedMessages();

    /**
     * 恢复超时的处理中消息
     * 处理那些长时间处于处理中状态的消息
     *
     * @param timeoutSeconds 超时秒数
     * @return 恢复的消息数量
     */
    int recoverTimeoutProcessingMessages(int timeoutSeconds);

    /**
     * 重试失败的消息
     * 重新处理那些可以重试的失败消息
     *
     * @return 重试的消息数量
     */
    int retryFailedMessages();

    /**
     * 全面的消息恢复
     * 包括未发布、超时处理中、可重试失败的所有消息
     *
     * @return 总共恢复的消息数量
     */
    int fullMessageRecovery();

    /**
     * 检查消息一致性
     * 检查数据库中的消息状态与实际处理状态是否一致
     *
     * @return 检查报告
     */
    String checkMessageConsistency();

}
