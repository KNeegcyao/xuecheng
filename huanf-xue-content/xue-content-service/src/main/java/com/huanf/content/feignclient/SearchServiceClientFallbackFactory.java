package com.huanf.content.feignclient;

import com.huanf.base.exception.XueChengPlusException;
import com.huanf.content.domain.entity.CoursePublish;
import com.huanf.content.mapper.CoursePublishMapper;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class SearchServiceClientFallbackFactory implements FallbackFactory<SearchServiceClient> {

    @Resource
    CoursePublishMapper coursePublishMapper;
    @Resource
    SearchServiceClient searchServiceClient;

    @Override
    public SearchServiceClient create(Throwable throwable) {
        return new SearchServiceClient() {
            @Override
            public Boolean add(CourseIndex courseIndex) {
                log.error("添加课程索引，发生熔断，索引信息：{}，熔断异常：{}",courseIndex,throwable);
                //走降级返回false
                return false;
            }

        };
    }
}
