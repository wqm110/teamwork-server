package com.projectm.project.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.projectm.project.domain.ProjectVersion;
import com.projectm.project.domain.ProjectVersionLog;
import com.projectm.project.mapper.ProjectVersionLogMapper;
import com.projectm.project.mapper.ProjectVersionMapper;
import com.projectm.task.domain.Task;
import com.projectm.task.mapper.TaskMapper;
import com.projectm.task.service.TaskService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProjectVersionService  extends ServiceImpl<ProjectVersionMapper, ProjectVersion> {

    @Autowired
    private ProjectVersionLogMapper projectVersionLogMapper;
    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskService taskService;

    @Transactional
    public Integer addVersionTask(List<Task> tasks, List<ProjectVersion> projectVersions){
        Integer i1= 0,i2=0;
        for(Task t:tasks){
            taskMapper.insert(t);
            i1++;
        }
        for(ProjectVersion pv:projectVersions){
            baseMapper.insert(pv);
            i2++;
        }
        return i1;
    }

    //根据任务完成情况，获取任务进度
    public Integer getScheduleByVersion(String versionCode){
        Map projectVersion = baseMapper.selectProjectVersionByCode(versionCode);
        List<Map> taskList = taskMapper.selectTaskListByVersionAndDelete(new HashMap(){{
            put("versionCode",versionCode);
            put("deleted",0);
        }});
        Integer doneTotal = 0;
        if(CollectionUtils.isNotEmpty(taskList)){
            for(Map taskMap:taskList){
                if(MapUtils.getInteger(taskMap,"done",0) > 0){
                    doneTotal ++;
                }
            }
            return  Math.round(doneTotal/taskList.size() * 100);
        }
        return 0;
    }

    public Map gettProjectVersionByNameAndFeaturesCode(String name,String featuresCode){
        return baseMapper.selectProjectVersionByNameAndFeaturesCode(name,featuresCode);
    }

    public List<Map> getProjectVersion(String featuresCode){
        return baseMapper.selectProjectVersionByFeaturesCode(featuresCode);
    }

    public Map getProjectVersionByCode(String code){
        return baseMapper.selectProjectVersionByCode(code);
    }

    @Transactional
    public Integer addProjectVersionAndVersionLog(ProjectVersion pv, ProjectVersionLog pvl){
        Integer i = baseMapper.insert(pv);
        Integer j = projectVersionLogMapper.insert(pvl);
        return i+j;
    }

    @Transactional
    public Integer delProjectVersion(String versionCode){
        Integer i1 = baseMapper.deleteProjectVersionByCode(versionCode);
        Integer i2 = taskMapper.updateTaskFeaAndVerByVerCode(versionCode);
        return  i1+i2;
    }

}
