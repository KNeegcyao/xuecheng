package com.huanf.content.api;

import com.huanf.content.domain.dto.SaveTeachplanDto;
import com.huanf.content.domain.dto.TeachplanDto;
import com.huanf.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Api(value = "课程计划编辑接口",tags = "课程计划编辑接口")
public class TeachplanController {

    @Resource
    TeachplanService teachplanService;
    //查询课程计划
    @ApiOperation("查询课程计划树形结构")
    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachplanDto> getTreeNodes(@PathVariable Long courseId){
        return teachplanService.findTeachplanTree(courseId);
    }

    @ApiOperation("课程计划创建或修改")
    @PostMapping("/teachplan")
    public void SaveTeachplan(@RequestBody SaveTeachplanDto saveTeachplanDto){
        teachplanService.SaveTeachplan(saveTeachplanDto);
    }

    @ApiOperation("删除课程计划")
    @DeleteMapping("/teachplan/{teachplanId}")
    public void deleteTeachplan(@PathVariable Long teachplanId){
        teachplanService.deleteTeachplan(teachplanId);
    }

    @ApiOperation("向下移动")
    @PostMapping("/teachplan/{down}/{teachplanId}")
    public void movedown(@PathVariable String down,@PathVariable Long teachplanId){
        teachplanService.movedown(down,teachplanId);
    }
}
