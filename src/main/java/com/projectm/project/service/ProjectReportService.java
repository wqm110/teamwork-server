package com.projectm.project.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.framework.common.utils.StringUtils;
import com.projectm.common.CommUtils;
import com.projectm.common.DateUtil;
import com.projectm.common.DateUtils;
import com.projectm.member.domain.Member;
import com.projectm.member.service.MemberService;
import com.projectm.project.domain.Project;
import com.projectm.project.domain.ProjectLog;
import com.projectm.project.domain.ProjectReport;
import com.projectm.project.mapper.ProjectLogMapper;
import com.projectm.project.mapper.ProjectReportMapper;
import com.projectm.system.domain.Notify;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class ProjectReportService extends ServiceImpl<ProjectReportMapper, ProjectReport> {

    /**
     *
     * 计算最近n天的数据
     * @param String $projectCode 项目code
     * @param Integer day 近n天
     */
    public Map getReportByDay(String projectCode,Integer day){
        Map result  = new HashMap();
        List<String> dateMList = new ArrayList();
        List<Integer> taskList = new ArrayList();
        List<Integer> undoneTaskList = new ArrayList();
        List<Integer> baseLineList = new ArrayList();
        Integer max = 0;
        Date now = new Date();
        for(int i=day;i>=1;i--){
            Date d = DateUtil.add(now,5,day*-1);
            String dateYMD = DateUtil.format("yyyy-MM-dd",d);
            String dateMD = DateUtil.format("MM-dd",d);
            dateMList.add(dateMD);
            ProjectReport pr = getProjectReportByProjectCodeAndDate(projectCode,dateYMD);
            //未完成
            if(null != pr){
                /**
                 if ($report) {
                     $task = get_object_vars($report['content']);
                     $taskList[] = $task['task'];
                     $undoneTaskList[] = $task['task:undone'];
                     if ($task['task:undone'] > $max) {
                        $max = $task['task:undone'];
                     }
                 }
                 */
            }else{
                taskList.add(0);
                undoneTaskList.add(0);
            }
        }
        if(max >0){
            BigDecimal a =new BigDecimal(max/(day-1)).setScale(1, BigDecimal.ROUND_HALF_UP);
            Integer current = max;
            for(int i=0;i<=day;i++){
                if(current<0 || day == i){
                    current =0;
                }
                baseLineList.add(current);
                current -= a.intValue();
            }
        }
        result.put("date",dateMList);
        result.put("task",taskList);
        result.put("undoneTask",undoneTaskList);
        result.put("baseLineList",baseLineList);
        return result;
    }

    public ProjectReport getProjectReportByProjectCodeAndDate(String projectCode,String date){
        LambdaQueryWrapper<ProjectReport> reportWQ = new LambdaQueryWrapper<>();
        reportWQ.eq(ProjectReport::getProject_code, projectCode);
        reportWQ.eq(ProjectReport::getDate,date);
        return baseMapper.selectOne(reportWQ);
    }


}
