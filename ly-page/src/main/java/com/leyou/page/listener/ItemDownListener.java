package com.leyou.page.listener;

import com.leyou.page.service.PageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.leyou.common.constants.RocketMQConstants.TAGS.ITEM_DOWN_TAGS;
import static com.leyou.common.constants.RocketMQConstants.TOPIC.ITEM_TOPIC_NAME;

@Slf4j
@Component
@RocketMQMessageListener(topic = ITEM_TOPIC_NAME,
        selectorExpression = ITEM_DOWN_TAGS,
        consumerGroup = "ITEM_PAGE_DOWN_CONSUMER")
public class ItemDownListener implements RocketMQListener<Long> {

    @Resource
    private PageService pageService;

    @Override
    public void onMessage(Long spuId) {

        log.info("[静态页服务]- (商品下架) -接收消息，spuId={}", spuId);
        pageService.deleteItemHtml(spuId);
    }
}