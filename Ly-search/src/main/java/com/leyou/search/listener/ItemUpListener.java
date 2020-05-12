package com.leyou.search.listener;

import com.leyou.search.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.leyou.common.constants.RocketMQConstants.TAGS.ITEM_UP_TAGS;
import static com.leyou.common.constants.RocketMQConstants.TOPIC.ITEM_TOPIC_NAME;

/**
 * 商品上架消费
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = ITEM_TOPIC_NAME,
                        selectorExpression = ITEM_UP_TAGS,
                        consumerGroup = "ITEM_SEARCH_UP")
public class ItemUpListener implements RocketMQListener<Long> {

    @Resource
    private SearchService searchService;
    @Override
    public void onMessage(Long spuId) {
        log.info("[搜索服务]- (商品上架) -接收消息，spuId={}", spuId);
        searchService.createIndex(spuId);
    }
}