package com.sin.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ScheduledJob {
    private final Logger LOGGER = LoggerFactory.getLogger(ScheduledJob.class);

//    @Autowired
//    private OrderService orderService;

    /**
     * 定时任务关闭超期未支付订单，存在的弊端
     * 1. 时间差：10：39下单，11：00检查不足一小时，超过1小时多余39分钟，时间差上面无法把控，程序不严谨
     * 2. 不支持集群：只需要一个节点去执行，无需每个集群去执行任务，单机OK
     * 解决方案，使用一台计算机节点，单独运行定时任务
     * 3. 会对数据库进行全表搜索，及其影响数据库性能
     * <p>
     * 定时任务只能适用于小型轻量级项目，传统项目
     * 消息队列：MQ -> RabbitMQ, RocketMQ, Kafka, ZeroMQ
     * 演示队列
     * 10：12分下单，非付款状态，11：12分钟检查，如果当前状态还是10，则直接关闭订单即可
     */
//    @Scheduled(cron = "0/1 0 * * * ? *")
//    public void autoCloseOrder() {
//        LOGGER.debug("Auto Close Order every 3 second: " + DateUtil.getCurrentDateString(DateUtil.DATETIME_PATTERN));
//        orderService.closeOrder();
//    }
}
