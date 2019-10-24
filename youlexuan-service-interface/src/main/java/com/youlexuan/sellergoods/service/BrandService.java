package com.youlexuan.sellergoods.service;

import com.youlexuan.entity.PageResult;
import com.youlexuan.pojo.TbBrand;

import java.util.List;
import java.util.Map;

/**商品品牌服务接口
 * @author 王大亮
 * @date 2019/9/23 21:46
 */
public interface BrandService {

    List<TbBrand> findAll();

    PageResult findPage(TbBrand brand,int pageNum,int pageSize);

    boolean add(TbBrand tbBrand);

    TbBrand findOne(long id);

    boolean update(TbBrand brand);

    boolean delete(long[] ids);

    List<Map> selectOptionList();
}
