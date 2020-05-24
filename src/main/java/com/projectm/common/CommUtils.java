package com.projectm.common;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.*;

public class CommUtils {
    public static final String loginMember = "LOGIN_MEMBER";//记录登录用户的常量名

    /**
     * 当前记录起始索引
     */
    public static final String PAGE_NUM = "page";

    /**
     * 每页显示记录数
     */
    public static final String PAGE_SIZE = "pageSize";

    public static String getUUID(){
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString().replaceAll("-","");
        // 去掉"-"符号
        String temp = str.substring(0, 8) + str.substring(9, 13) + str.substring(14, 18) + str.substring(19, 23) + str.substring(24);
        return str;
    }

    public static Map getMapField(Map map, String [] fields){
        Map result = new HashMap();
        for(String field:fields){
            result.put(field, MapUtils.getString(map,field));
        }
        return result;
    }

    public static List stringArrayToList(String[] str){
        List<String> result = new ArrayList<>();
        if(null != str){
            for(String s : str){
                result.add(s);
            }
        }
        return result;
    }

    public static List<String> getListStringForListMapField(List<Map> list,String key){
        List<String> result = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(list)){
            for(Map m:list){
                result.add(String.valueOf(m.get(key)));
            }
        }
        return result;
    }

    public static List<Map> listGetStree(List<Map> list,String node,String pnode) {
        List<Map> treeList = new ArrayList<Map>();
        for (Map tree : list) {
            //找到根
            if ("".equals(MapUtils.getString(tree,pnode))) {
                treeList.add(tree);
            }
            //找到子
            for (Map treeNode : list) {
                //if (treeNode.getPid() == tree.getId()) {
                if (MapUtils.getString(treeNode,pnode).equals(MapUtils.getString(tree,node))) {
                    if (MapUtils.getObject(tree,"children") == null) {
                        tree.put("children",new ArrayList<Map>());
                    }
                    List tmp = (List)tree.get("children");
                    tmp.add(treeNode);
                    tree.put("children",tmp);
                }
            }
        }
        return treeList;
    }

    public static boolean isEmpty(String str){
        return str == null || "".equals(str);
    }

}
