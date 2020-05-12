package com.leyou.search.listener;

import com.leyou.search.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.leyou.common.constants.RocketMQConstants.TAGS.ITEM_DOWN_TAGS;
import static com.leyou.common.constants.RocketMQConstants.TOPIC.ITEM_TOPIC_NAME;

/**
 * 商品下架消息
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = ITEM_TOPIC_NAME,
                        selectorExpression = ITEM_DOWN_TAGS,
                        consumerGroup = "ITEM_SEARCH_DOWN")
public class ItemDownListener implements RocketMQListener<Long> {
    @Autowired
    private SearchService searchService;

    @Override
    public void onMessage(Long spuId) {
        log.info("[搜索服务]- (商品下架) -接收消息，spuId={}", spuId);
        searchService.removeIndex(spuId);
    }
}
