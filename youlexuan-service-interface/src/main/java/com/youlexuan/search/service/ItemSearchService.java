package com.youlexuan.search.service;

import java.util.List;
import java.util.Map;

/**
 * @author 王大亮
 * @date 2019/10/9 21:45
 */
public interface ItemSearchService {

    /**
     * 搜索
     * @return
     */
    public Map<String,Object> search(Map searchMap);

    /**
     * 导入数据
     * @param list
     */
    public void importList(List list);

    /**
     * 删除数据
     */
    public void deleteByGoodsIds(List goodsIdList);

}
