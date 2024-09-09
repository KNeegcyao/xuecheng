package com.huanf.content.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huanf.base.model.PageParams;
import com.huanf.base.model.PageResult;
import com.huanf.content.domain.dto.AddCourseDto;
import com.huanf.content.domain.dto.CourseBaseInfoDto;
import com.huanf.content.domain.dto.EditCourseDto;
import com.huanf.content.domain.dto.QueryCourseParamsDto;
import com.huanf.content.domain.entity.CourseBase;
import com.huanf.content.domain.entity.CourseCategory;
import com.huanf.content.domain.entity.CourseMarket;
import com.huanf.content.mapper.CourseBaseMapper;
import com.huanf.content.mapper.CourseCategoryMapper;
import com.huanf.content.mapper.CourseMarketMapper;
import com.huanf.content.service.CourseBaseService;

import com.xuecheng.base.exception.XueChengPlusException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 课程基本信息(CourseBase)表服务实现类
 *
 * @author makejava
 * @since 2024-03-30 22:35:08
 */
@Service
public class CourseBaseServiceImpl extends ServiceImpl<CourseBaseMapper, CourseBase> implements CourseBaseService {

    @Resource
    CourseBaseMapper courseBaseMapper;

    @Resource
    CourseMarketMapper courseMarketMapper;

    @Resource
    CourseCategoryMapper courseCategoryMapper;
    /**
     * 查询
     * @param pageParams 分页参数
     * @param dto 查询条件
     * @return
     */
    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto dto) {
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(StringUtils.isNotEmpty(dto.getCourseName()),CourseBase::getName,dto.getCourseName());

        //按课程发布状态查询，StringUtils.isNotEmpty()表示该字段可传可不传，非常灵活，不传就不根据这个字段查
        queryWrapper.eq(StringUtils.isNotEmpty(dto.getAuditStatus()), CourseBase::getAuditStatus,dto.getAuditStatus());
        queryWrapper.eq(StringUtils.isNotEmpty(dto.getPublishStatus()), CourseBase::getStatus,dto.getPublishStatus());

        //分页
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);

        //分页里面的每一条具体数据
        List<CourseBase> items = pageResult.getRecords();
        //分页里面的总记录数
        long total = pageResult.getTotal();

        //封装数据
        PageResult<CourseBase> courseBasePageResult = new PageResult<>(items,total,pageParams.getPageNo(), pageParams.getPageSize());
        return courseBasePageResult;
    }

    /**
     * 新增课程
     * @param companyId 机构id
     * @param addCourseDto 课程信息
     * @return
     */
    @Override
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto) {
        //参数合法性校验
        //向课程信息表course_base写入数据
        //合法性校验
/*        if (StringUtils.isBlank(addCourseDto.getName())) {
//            throw new com.xuecheng.base.exception.XueChengPlusException("课程名称为空");
//        }
//
//        if (StringUtils.isBlank(addCourseDto.getMt())) {
//            throw new com.xuecheng.base.exception.XueChengPlusException("课程分类为空");
//        }
//
//        if (StringUtils.isBlank(addCourseDto.getSt())) {
//            throw new com.xuecheng.base.exception.XueChengPlusException("课程分类为空");
//        }
//
//        if (StringUtils.isBlank(addCourseDto.getGrade())) {
//            throw new com.xuecheng.base.exception.XueChengPlusException("课程等级为空");
//        }
//
//        if (StringUtils.isBlank(addCourseDto.getTeachmode())) {
//            throw new com.xuecheng.base.exception.XueChengPlusException("教育模式为空");
//        }
//
//        if (StringUtils.isBlank(addCourseDto.getUsers())) {
//            throw new com.xuecheng.base.exception.XueChengPlusException("适应人群");
//        }
//
//        if (StringUtils.isBlank(addCourseDto.getCharge())) {
//            throw new com.xuecheng.base.exception.XueChengPlusException("收费规则为空");
//        }*/
        CourseBase courseBase = new CourseBase();
        BeanUtils.copyProperties(addCourseDto,courseBase);
        courseBase.setCompanyId(companyId);
        courseBase.setCreateDate(LocalDateTime.now());
        //审核状态默认为未提交
        courseBase.setAuditStatus("202002");
        //发布状态为未发布
        courseBase.setStatus("203001");
        //插入数据库
        int insert = courseBaseMapper.insert(courseBase);
        if(insert<=0){
            throw new RuntimeException("添加课程失败");
        }
        //向课程营销表course_market写入有数据
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(addCourseDto,courseMarket);
        courseMarket.setId(courseBase.getId());
        //保存营销信息
        saveCourseMarket(courseMarket);

        return getCourseBaseInfo(courseBase.getId());

    }


    //查询课程信息
    public CourseBaseInfoDto getCourseBaseInfo(long courseId){
        //从课程信息表查询
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(courseBase==null){
            return null;
        }
        //从课程营销表查询
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
         //组装在一起
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase,courseBaseInfoDto);
        if (courseMarket!=null) {
            BeanUtils.copyProperties(courseMarket,courseBaseInfoDto);
        }
        CourseCategory mtCategory = courseCategoryMapper.selectById(courseBase.getMt());
        if(mtCategory==null){
            throw new XueChengPlusException("无大分类");
        }
        courseBaseInfoDto.setMtName(mtCategory.getName());
        CourseCategory stCategory = courseCategoryMapper.selectById(courseBase.getSt());
        courseBaseInfoDto.setStName(stCategory.getName());
        return courseBaseInfoDto;
    }

    /**
     * 修改课程信息
     * @param editCourseDto
     * @return
     */
    @Override
    public CourseBaseInfoDto modifCourseBase(Long companyID, EditCourseDto editCourseDto) {
        //课程id
        Long courseId = editCourseDto.getId();
        //查询课程信息
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        //数据合法性
        if(courseBase==null){
            XueChengPlusException.cast("课程不存在");
        }
        if(courseMarket==null){
            XueChengPlusException.cast("课程不存在");
        }
        //根据具体的业务逻辑去校验
        //本机构只能修改本机构的课程
        if(!companyID.equals(courseBase.getCompanyId())){
            XueChengPlusException.cast("本机构只能修改本机构的课程");
        }
        BeanUtils.copyProperties(editCourseDto,courseBase);
        BeanUtils.copyProperties(editCourseDto,courseMarket);
        courseBase.setChangeDate(LocalDateTime.now());
        //更新数据库
        int i = courseBaseMapper.updateById(courseBase);
        if(i<=0){
            XueChengPlusException.cast("修改课程失败");
        }
        //更新营销信息
        int i2 = courseMarketMapper.updateById(courseMarket);
        if(i2<=0){
            XueChengPlusException.cast("修改课程失败");
        }

        //查询课程信息
        return getCourseBaseInfo(courseId);
    }

    //保存营销信息
    private void saveCourseMarket(CourseMarket courseMarket) {
        //参数合法性校验
        String charge = courseMarket.getCharge();
        if(StringUtils.isBlank(charge)){
            throw new RuntimeException("收费规则为空");
        }
        //如果课程收费，价格没有填写或者不规范也需要抛出异常
        if (charge.equals("201001")) {
            if (courseMarket.getPrice()==null || courseMarket.getPrice()<= 0) {
                throw new XueChengPlusException("课程的价格不能为空并且必须大于0");
            }
        }

        //从数据库查询营销信息，存在则更新，不存在则添加
        Long id = courseMarket.getId();
        CourseMarket courseMarket1 = courseMarketMapper.selectById(id);
        if(courseMarket1==null){
            //为空 插入
            courseMarketMapper.insert(courseMarket);
        }else{
            BeanUtils.copyProperties(courseMarket,courseMarket1);
            courseMarket1.setId(courseMarket.getId());
            //更新
            int i = courseMarketMapper.updateById(courseMarket1);
        }

    }
}