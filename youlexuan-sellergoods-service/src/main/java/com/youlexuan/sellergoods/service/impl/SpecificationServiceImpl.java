package com.youlexuan.sellergoods.service.impl;
import java.util.List;
import java.util.Map;

import com.youlexuan.mapper.TbSpecificationOptionMapper;
import com.youlexuan.pojo.TbSpecificationOption;
import com.youlexuan.pojo.TbSpecificationOptionExample;
import com.youlexuan.pojogroup.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.youlexuan.mapper.TbSpecificationMapper;
import com.youlexuan.pojo.TbSpecification;
import com.youlexuan.pojo.TbSpecificationExample;
import com.youlexuan.pojo.TbSpecificationExample.Criteria;
import com.youlexuan.sellergoods.service.SpecificationService;

import com.youlexuan.entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;

	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Specification specification) {
		//先增加规格
		specificationMapper.insert(specification.getSpecification());
		//在增加规格选项
		for(TbSpecificationOption specificationOption : specification.getSpecificationOptionList()){
			//获取规格的id给规格选项
			specificationOption.setSpecId(specification.getSpecification().getId());
			//增加规格选项
			specificationOptionMapper.insert(specificationOption);
		}

	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Specification specification){
		//先修改规格
		specificationMapper.updateByPrimaryKey(specification.getSpecification());
		//在修改规格选项
		//删除原来的
		TbSpecificationOptionExample exam = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = exam.createCriteria();
		criteria.andSpecIdEqualTo(specification.getSpecification().getId());
		specificationOptionMapper.deleteByExample(exam);
		//再从新添加
		for(TbSpecificationOption specificationOption : specification.getSpecificationOptionList()){
			//获取规格的id给规格选项
			specificationOption.setSpecId(specification.getSpecification().getId());
			//增加规格选项
			specificationOptionMapper.insert(specificationOption);
		}
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Specification findOne(Long id){
		//先根据id查询规格
		TbSpecification specification = specificationMapper.selectByPrimaryKey(id);
		//再查询规格选项
		TbSpecificationOptionExample exam = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = exam.createCriteria();
		criteria.andSpecIdEqualTo(id);
		List<TbSpecificationOption> specificationOptions = specificationOptionMapper.selectByExample(exam);
		//将查询到的数据封装成对象返回
		Specification spec = new Specification();
		spec.setSpecification(specification);
		spec.setSpecificationOptionList(specificationOptions);
		return spec;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//先删除规格
			specificationMapper.deleteByPrimaryKey(id);
			//删除规格选项
			TbSpecificationOptionExample exam =new TbSpecificationOptionExample();
			TbSpecificationOptionExample.Criteria criteria = exam.createCriteria();
			criteria.andSpecIdEqualTo(id);
			specificationOptionMapper.deleteByExample(exam);
		}		
	}
	
	
	@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 全部规格
	 * @return
	 */
	@Override
	public List<Map> selectOptionList() {
		return specificationMapper.selectOptionList();
	}

}
