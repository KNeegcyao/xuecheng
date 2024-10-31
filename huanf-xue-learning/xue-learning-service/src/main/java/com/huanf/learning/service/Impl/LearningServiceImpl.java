package com.huanf.learning.service.Impl;

import com.huanf.base.exception.XueChengPlusException;
import com.huanf.base.model.RestResponse;
import com.huanf.base.utils.JsonUtil;
import com.huanf.base.utils.StringUtil;
import com.huanf.content.domain.dto.TeachplanDto;
import com.huanf.content.domain.entity.CoursePublish;
import com.huanf.learning.feignclient.ContentServiceClient;
import com.huanf.learning.feignclient.MediaServiceClient;
import com.huanf.learning.model.dto.XcCourseTablesDto;
import com.huanf.learning.service.LearningService;
import com.huanf.learning.service.MyCourseTableService;
import freemarker.template.utility.SecurityUtilities;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 学习过程管理接口
 */
@Service
public class LearningServiceImpl implements LearningService {
    @Autowired
    MyCourseTableService myCourseTableService;
    @Autowired
    ContentServiceClient contentServiceClient;
    @Autowired
    MediaServiceClient mediaServiceClient;
    /**
     * @description 获取教学视频
     * @param courseId 课程id
     * @param teachplanId 课程计划id
     * @param mediaId 视频文件id
     */
    @Override
    public RestResponse<String> getVideo(String userId, Long courseId, Long teachplanId, String mediaId) {
        //查询课程信息
        CoursePublish coursepublish = contentServiceClient.getCoursepublish(courseId);
        if(coursepublish==null){
            return RestResponse.validfail("课程不存在");
        }
//        String teachplan = coursepublish.getTeachplan();
//        List<TeachplanDto> teachplanDtos = JsonUtil.jsonToList(teachplan, TeachplanDto.class);
//        ArrayList<TeachplanDto> teachPlanTreeNodesList = new ArrayList<>();
//        teachplanDtos.forEach(teachplanDto -> {
//            List<TeachplanDto> teachPlanTreeNodes = teachplanDto.getTeachPlanTreeNodes();
//            teachPlanTreeNodesList.addAll(teachPlanTreeNodes);
//        });
//        Map<Long, TeachplanDto> teachplanMap = teachPlanTreeNodesList.stream().collect(Collectors.toMap(TeachplanDto::getId, t -> t));
//        TeachplanDto teachplanDto = teachplanMap.get(teachplanId);
//        if(teachplanDto==null){
//            XueChengPlusException.cast("该章节没有视频");
//        }
//        String isPreview = teachplanDto.getIsPreview();
//        if(isPreview.equals("1")){
//            //试看
//            RestResponse<String> playUrlByMediaId = mediaServiceClient.getPlayUrlByMediaId(mediaId);
//            return playUrlByMediaId;
//        }

        if(StringUtil.isNotEmpty(userId)){
            //获取学习资格
            XcCourseTablesDto learningStatus = myCourseTableService.getLearningStatus(userId, courseId);
            String learnStatus = learningStatus.getLearnStatus();
            if(learnStatus.equals("702002")){
                return RestResponse.validfail("无法学习，因为没有选课或选课后没有支付");
            }else if(learnStatus.equals("702003")){
                return RestResponse.validfail("已过期需要申请续期或重新支付");
            }else{
                //有资格学习
                RestResponse<String> playUrlByMediaId = mediaServiceClient.getPlayUrlByMediaId(mediaId);
                return playUrlByMediaId;
            }
        }
        //如果用户没有登录
        //收费规则
        String status = coursepublish.getStatus();
        if(status.equals("201000")){
            //免费
            //有资格学习
            RestResponse<String> playUrlByMediaId = mediaServiceClient.getPlayUrlByMediaId(mediaId);
            return playUrlByMediaId;
        }
        return RestResponse.validfail("该课程没有选课");
    }
}
