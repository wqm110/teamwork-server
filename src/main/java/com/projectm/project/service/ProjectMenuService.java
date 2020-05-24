package com.projectm.project.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.projectm.project.domain.ProjectMenu;
import com.projectm.project.mapper.ProjectMenuMapper;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Service
public class ProjectMenuService extends ServiceImpl<ProjectMenuMapper, ProjectMenu> {

    public List<Map> getAllProjectMenuTree(){
        List<Map> pMenuList = baseMapper.selectAllProjectMenu();
        List<Map> menusBase = baseMapper.selectProjectMenuByPid(0);
        List<Map> menuLNotBase = baseMapper.selectAllNotBase();
        for (Map menu : menusBase) {
            List<Map> menus = iterateMenus(menuLNotBase, MapUtils.getInteger(menu,"id"));
            menu.put("children",menus);
        }
        return menusBase;
    }

    protected List<Map> iterateMenus(List<Map> menuVoList,Integer pid){
        List<Map> result = new ArrayList<Map>();
        for (Map menu : menuVoList) {
            //获取菜单的id
            Integer menuid = MapUtils.getInteger(menu,"id");
            //获取菜单的父id
            Integer parentid = MapUtils.getInteger(menu,"pid",-1);
            if(-1 != parentid){
                if(parentid.equals(pid)){
                    //递归查询当前子菜单的子菜单
                    List<Map> iterateMenu = iterateMenus(menuVoList,menuid);
                    menu.put("children",iterateMenu);
                    result.add(menu);
                }
            }
        }
        return result;
    }

    /**
     * 获取子目录的ID，并删除（问题）
     * @param id
     * @return
     */
    @Transactional
    public Integer menuDelete(Integer id){
        List<Integer> ids = new ArrayList<>();
        ids.add(id);
        List<Map> childMenu = baseMapper.selectProjectMenuByPid(id);

        List<Map> pMenuList = baseMapper.selectAllProjectMenu();
        for(Map m:childMenu){
            ids.add(MapUtils.getInteger(m,"id"));
            ids.addAll(getChildIds(pMenuList,MapUtils.getInteger(m,"id")));
        }
        return baseMapper.deleteBatchIds(ids);

    }

    protected  List<Integer> getChildIds(List<Map> menu,Integer pid){
        List<Integer> result = new ArrayList<>();
        for(Map m:menu){
            if(pid == MapUtils.getInteger(m,"pid")){
                result.add(MapUtils.getInteger(m,"id"));
            }
        }
        return result;
    }
}
