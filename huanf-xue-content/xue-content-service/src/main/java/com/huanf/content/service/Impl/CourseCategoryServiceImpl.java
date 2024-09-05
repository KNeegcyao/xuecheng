package com.huanf.content.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huanf.content.mapper.CourseCategoryMapper;
import com.huanf.content.service.CourseCategoryService;
import com.huanf.domain.dto.CourseCategoryDto;
import com.huanf.domain.entity.CourseCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CourseCategoryServiceImpl extends ServiceImpl<CourseCategoryMapper, CourseCategory> implements CourseCategoryService {

    @Autowired
    private CourseCategoryMapper courseCategoryMapper;
    @Override
    public List<CourseCategoryDto> queryTreeNodes(String id) {
        //递归查询出分类信息
        List<CourseCategoryDto> list = courseCategoryMapper.selectTreeNodes(id);
        //找到每个节点的子节点，最终封装成List<CourseCategoryDto>
        //先将list转换map,key就是结点的id，value就是CourseCategoryDto对象，目的就是为了方便从map中获取结点
        //filter(item->!id.equals(item.getId()))目的是把根节点排除
        Map<String, CourseCategoryDto> map =
                list.stream().filter(item->!id.equals(item.getId())).collect(Collectors.toMap(
                        CourseCategory::getId, value -> value, (key1, key2) -> key2
        ));
        //定义一个最终返回的list
        List<CourseCategoryDto> resultList=new ArrayList<>();
        //从头遍历List<CourseCategoryDto>,一边遍历一边找子节点放在父节点的childrenTreeNodes
        list.stream().filter(item->!id.equals(item.getId())).forEach(item-> {
                     //id是一级节点（根节点）
                    if (item.getParentid().equals(id)) {
                        //存入二级节点
                        resultList.add(item);
                    }
                    //找到节点的父节点
                    //找到当前节点的父节点是存在的(此时这个父节点就是二级节点，也就是下面为了存入三级节点或更多级节点
                    CourseCategoryDto courseCategoryParent = map.get(item.getParentid());
                    if (courseCategoryParent != null) {
                        if (courseCategoryParent.getChildrenTreeNode()==null) {
                            //如果该父节点的childrenTreeNodes属性为空要new一个集合，因为要向该集合中放入他的子节点
                            courseCategoryParent.setChildrenTreeNode(new ArrayList<CourseCategoryDto>());
                        }
                        //找到每个节点的子节点放在父节点的childrenTreeNodes属性中
                        courseCategoryParent.getChildrenTreeNode().add(item);
                    }
                }
                );
        return resultList;
    }
}
