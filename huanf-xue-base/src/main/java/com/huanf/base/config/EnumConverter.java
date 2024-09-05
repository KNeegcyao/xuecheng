package com.huanf.base.config;

import com.huanf.base.enums.CourseEnum;
import org.springframework.core.convert.converter.Converter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author: 35238
 * 功能: 由于前端传给我们的不是枚举值(例如FEE_NO)，而是code或desc，那我们就需要进行转换
 * 时间: 2024-04-05 13:32
 */
public class EnumConverter implements Converter<String, CourseEnum> {

    //这个HashMap在初始状态下可以容纳10个键值对
    private final Map<String, CourseEnum> map1 = new HashMap<>(10);
    private final Map<String, CourseEnum> map2 = new HashMap<>(10);

    public EnumConverter() {
        Arrays.stream(CourseEnum.class.getEnumConstants())
                .forEach(x -> {
                    map1.put(String.valueOf(x.getValue()), x);
                    map2.put(x.toString(), x);
                });
    }

    @Override
    public CourseEnum convert(String source) {
        return Optional.of(source)
                .map(map1::get)
                .orElseGet(() -> map2.get(source));
    }
}