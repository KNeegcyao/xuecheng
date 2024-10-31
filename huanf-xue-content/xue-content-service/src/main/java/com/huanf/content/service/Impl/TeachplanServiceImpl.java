package com.huanf.content.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huanf.base.exception.XueChengPlusException;
import com.huanf.content.domain.dto.BindTeachplanMediaDto;
import com.huanf.content.domain.dto.SaveTeachplanDto;
import com.huanf.content.domain.dto.TeachplanDto;
import com.huanf.content.domain.entity.Teachplan;
import com.huanf.content.domain.entity.TeachplanMedia;
import com.huanf.content.mapper.TeachplanMapper;
import com.huanf.content.mapper.TeachplanMediaMapper;
import com.huanf.content.service.TeachplanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Wrapper;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class TeachplanServiceImpl implements TeachplanService {
    @Resource
    TeachplanMapper teachplanMapper;
    @Resource
    TeachplanMediaMapper teachplanMediaMapper;

    /**
     * 查询课程计划
     * @param courseId
     * @return
     */
    @Override
    public List<TeachplanDto> findTeachplanTree(long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }

    /**
     * 新增或修改课程计划
     * @param saveTeachplanDto
     */
    @Override
    public void SaveTeachplan(Teachplan saveTeachplanDto) {
        if (saveTeachplanDto == null) {
            XueChengPlusException.cast("课程计划对象不能为空");
        }

        Long teachplanId = saveTeachplanDto.getId();
        if(teachplanId==null){
            //新增
            Teachplan teachplan = new Teachplan();
            log.info("复制属性之前的 saveTeachplanDto: {}", saveTeachplanDto);
            BeanUtils.copyProperties(saveTeachplanDto,teachplan);
            //确定排序字段
            Long courseId = teachplan.getCourseId();
            Long parentId = teachplan.getParentid();
            int maxOrderBy  = getMaxOrderBy(courseId, parentId);
            teachplan.setOrderby(maxOrderBy+1);
            int insert = teachplanMapper.insert(teachplan);
            if(insert<=0){
                XueChengPlusException.cast("新增失败");
            }
        }else{
            //修改
            Teachplan teachplan = teachplanMapper.selectById(teachplanId);
            if (teachplan == null) {
                throw new IllegalArgumentException("Teachplan not found with ID: " + teachplanId);
            }
            BeanUtils.copyProperties(saveTeachplanDto,teachplan,"id","createDate");
            teachplan.setChangeDate(LocalDateTime.now());
            int i = teachplanMapper.updateById(teachplan);
            if(i<=0){
                XueChengPlusException.cast("修改失败");
            }
        }
    }

    /**
     * 删除课程计划
     * @param teachplanId
     */
    @Override
    public void deleteTeachplan(Long teachplanId) {
        //查询课程
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        if(teachplan==null){
            throw new XueChengPlusException("该课程不存在");
        }
        //判断是大小章节
        if(teachplan.getGrade().equals(1)){
            //为大章节
            Long parentid = teachplan.getId();
            LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper= queryWrapper.eq(Teachplan::getParentid, parentid);
            int count = teachplanMapper.selectCount(queryWrapper);
            if(count==0){
                //没有小章节，可以删除
                int i = teachplanMapper.deleteById(teachplanId);
                if(i==0){
                    throw new XueChengPlusException("删除失败");
                }
            }else{
                //有小章节，不能删除
                throw new XueChengPlusException("课程计划信息还有子级信息，无法操作");
            }
        }else{
            //为小章节
            teachplanMapper.deleteById(teachplanId);
            //同时删除关联的teachplan_media表
            LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper=queryWrapper.eq(TeachplanMedia::getTeachplanId,teachplanId);
            TeachplanMedia teachplanMedia = teachplanMediaMapper.selectOne(queryWrapper);
            if(teachplanMedia==null){
                return ;
            }
            int i = teachplanMediaMapper.deleteById(teachplanMedia.getId());
            if(i==0){
                throw new XueChengPlusException("删除失败");
            }

        }
    }

    /**
     * 上下移动
     * @param down
     * @param teachplanId
     */
    @Override
    public void movedown(String down, Long teachplanId) {
        //查询课程信息
        Teachplan currentTeachplan = teachplanMapper.selectById(teachplanId);
        if (currentTeachplan == null) {
            throw new XueChengPlusException("当前课程不存在");
        }

        if (down.equals("moveup")) {
            //上移
            LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Teachplan::getParentid, currentTeachplan.getParentid())  //同一父节点
                    .lt(Teachplan::getOrderby, currentTeachplan.getOrderby())  //orderby小于当前项
                    .orderByDesc(Teachplan::getOrderby)                      //按排序倒序排序
                    .last("limit 1");
            Teachplan previousTeachplan = teachplanMapper.selectOne(queryWrapper);
            if (previousTeachplan == null) {
                throw new XueChengPlusException("已经是第一个了，无法上移");
            }
            exchange(currentTeachplan, previousTeachplan);
        }else if(down.equals("movedown")){
            //下移
            LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Teachplan::getParentid, currentTeachplan.getParentid())  //同一父节点
                    .gt(Teachplan::getOrderby, currentTeachplan.getOrderby())  //orderby大于当前项
                    .orderByAsc(Teachplan::getOrderby)                      //按排序倒序正序
                    .last("limit 1");
            Teachplan nextTeachplan = teachplanMapper.selectOne(queryWrapper);
            if (nextTeachplan == null) {
                throw new XueChengPlusException("已经是最后一个了，无法下移");
            }
            exchange(currentTeachplan, nextTeachplan);
        }else{
            throw new XueChengPlusException("无此类操作，请重试！");
        }
    }

    /**
     * 教学计划绑定媒资
     * @param bindTeachplanMediaDto
     */
    @Override
    @Transactional
    public void associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto) {
        //先删除原有记录，根据课程计划id删除他所绑定的媒资
        Long teachplanId = bindTeachplanMediaDto.getTeachplanId();
        //课程id
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        if(teachplan==null){
            XueChengPlusException.cast("教学计划不存在");
        }
        Long courseId = teachplan.getCourseId();

        Integer grade = teachplan.getGrade();
        if(grade!=2){
            XueChengPlusException.cast("只允许第二级教学计划绑定媒资文件");
        }
        int delete = teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getTeachplanId, teachplanId));
        //在添加
        TeachplanMedia teachplanMedia = new TeachplanMedia();
        BeanUtils.copyProperties(bindTeachplanMediaDto,teachplanMedia);
        teachplanMedia.setCourseId(courseId);
        teachplanMedia.setCreateDate(LocalDateTime.now());
        teachplanMedia.setMediaFilename(bindTeachplanMediaDto.getFileName());
        teachplanMediaMapper.insert(teachplanMedia);
    }

    //交换orderby并保存数据库
    private void exchange(Teachplan currentTeachplan, Teachplan Teachplan) {
        int orderby = currentTeachplan.getOrderby();
        currentTeachplan.setOrderby(Teachplan.getOrderby());
        Teachplan.setOrderby(orderby);
        teachplanMapper.updateById(Teachplan);
        teachplanMapper.updateById(currentTeachplan);

    }


    /**
     * 找到最大的orderby
     * @param courseId
     * @param parentId
     * @return
     */
    private int getMaxOrderBy(Long courseId,Long parentId){
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper=queryWrapper
                .eq(Teachplan::getCourseId,courseId)
                .eq(Teachplan::getParentid,parentId)
                .orderByDesc(Teachplan::getOrderby)
                .last("limit 1"); //只取一个
        Teachplan maxTeachplan = teachplanMapper.selectOne(queryWrapper);
        return maxTeachplan!=null?maxTeachplan.getOrderby():0;
    }
}
