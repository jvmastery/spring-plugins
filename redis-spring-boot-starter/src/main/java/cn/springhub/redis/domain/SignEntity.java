package cn.springhub.redis.domain;

/**
 * 签到实体
 * @author AI
 * @date 2024/12/11 16:28
 * @version 1.0
 * @param success 是否签到成功
 * @param total 总签到次数
 * @param continuous 连续签到次数
**/
public record SignEntity(boolean success, Long total, Long continuous) { }
