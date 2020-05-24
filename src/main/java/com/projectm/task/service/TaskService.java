package com.projectm.task.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.projectm.task.domain.Task;
import com.projectm.task.mapper.TaskMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TaskService   extends ServiceImpl<TaskMapper, Task> {


    public Map getTaskByCode(String code){
        return  baseMapper.selectTaskByCode(code);
    }

    public List<Map> getTaskByParams(Map params){
        return baseMapper.selectTaskByParams(params);
    }
    public List<Map> selectTaskToTagByTaskCode(String taskCode){
        return baseMapper.selectTaskToTagByTaskCode(taskCode);
    }
    public Map selectTaskTagByCode(String code){
        return baseMapper.selectTaskTagByCode(code);
    }
    public Map selectHasUnDone(String pcode){
        return baseMapper.selectHasUnDone(pcode);
    }
    public Map selectHasComment(String pcode){
        return baseMapper.selectHasComment(pcode);
    }
    public Map selectHasSource(String pcode){
        return baseMapper.selectHasSource(pcode);
    }
    public Map selectChildCount0(String pcode){
        return baseMapper.selectChildCount0(pcode);
    }
    public Map selectChildCount1(String pcode){
        return baseMapper.selectChildCount1(pcode);
    }
    public Map selectCanRead(String taskCode,String memberCode){
        return baseMapper.selectCanRead(taskCode,memberCode);
    }
    public Map selectParentDone(String pcode){
        return baseMapper.selectParentDone(pcode);
    }

    public IPage<Map> getTaskSelfList(IPage<Map> ipage, Map params,Integer type){
        if(type == 0){
            return baseMapper.selectTaskSelfListNoFinish(ipage,params);
        }else{
            return baseMapper.selectTaskSelfListAll(ipage,params);
        }
    }

    public List<Map> getTaskListByVersionAndDelete(Map params){
        return  baseMapper.selectTaskListByVersionAndDelete(params);
    }




}
