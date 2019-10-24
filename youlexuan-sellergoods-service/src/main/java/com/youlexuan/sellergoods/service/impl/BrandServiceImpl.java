package com.youlexuan.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.youlexuan.entity.PageResult;
import com.youlexuan.mapper.TbBrandMapper;
import com.youlexuan.pojo.TbBrand;
import com.youlexuan.pojo.TbBrandExample;
import com.youlexuan.sellergoods.service.BrandService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**商品品牌的实现类
 * @author 王大亮
 * @date 2019/9/23 21:50
 * 注意service是dubbo中的
 */
@Service
public class BrandServiceImpl implements BrandService {

    @Resource
    private TbBrandMapper brandMapper;

    /**
     * 查询全部商品品牌
     * @return
     */
    @Override
    @Transactional
    public List<TbBrand> findAll() {
        TbBrandExample exam = new TbBrandExample();
        return brandMapper.selectByExample(exam);
    }

    /**
     * 分页查询数据 + 模糊
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    @Transactional
    public PageResult findPage(TbBrand brand,int pageNum, int pageSize) {
        //分页查询数据+模糊
        PageHelper.startPage(pageNum,pageSize);
        TbBrandExample exam = new TbBrandExample();
        TbBrandExample.Criteria criteria = exam.createCriteria();
        //判断模糊查询条件是否为空
        if(brand!=null){
            //如果名称不为空 就按名字去模糊查询
            if(brand.getName()!=null && brand.getName().length()>0){
                criteria.andNameLike("%"+brand.getName()+"%");
            }
            //如果首字母不为空,就按首字母去模糊查询
            if(brand.getFirstChar()!=null && brand.getFirstChar().length()>0){
                criteria.andFirstCharLike("%"+brand.getFirstChar()+"%");
            }
        }
        Page<TbBrand> page = (Page<TbBrand>)brandMapper.selectByExample(exam);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 添加商品品牌
     * @param brand
     * @return
     */
    @Override
    @Transactional
    public boolean add(TbBrand brand) {
        return brandMapper.insert(brand)>0;
    }

    /**
     *根据id查询
     * @param id
     * @return
     */
    @Override
    @Transactional
    public TbBrand findOne(long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    /**
     * 修改
     * @param brand
     * @return
     */
    @Override
    @Transactional
    public boolean update(TbBrand brand) {
        return brandMapper.updateByPrimaryKey(brand)>0;
    }

    /**
     * 根据ids[]删除
     * @param ids
     * @return
     */
    @Override
    @Transactional
    public boolean delete(long[] ids) {
        for(long id : ids){
            brandMapper.deleteByPrimaryKey(id);
        }
        return true;
    }

    /**
     * 全部的品牌
     * @return
     */
    @Override
    @Transactional
    public List<Map> selectOptionList() {
        return brandMapper.selectOptionList();
    }
}
