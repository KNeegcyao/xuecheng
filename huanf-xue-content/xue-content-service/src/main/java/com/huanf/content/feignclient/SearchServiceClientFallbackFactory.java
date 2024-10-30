package com.huanf.content.feignclient;

import com.huanf.content.mapper.CoursePublishMapper;
import com.huanf.content.po.CourseIndex;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class SearchServiceClientFallbackFactory implements FallbackFactory<SearchServiceClient> {
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
