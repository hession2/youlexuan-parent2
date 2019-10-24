package com.youlexuan.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.youlexuan.entity.PageResult;
import com.youlexuan.entity.Result;
import com.youlexuan.pojo.TbBrand;
import com.youlexuan.sellergoods.service.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author 王大亮
 * @date 2019/9/23 21:56
 * restcontroller表示全部返回json数据
 */
@RestController
@RequestMapping("brand")
public class BrandController {
    //通过dubbo连接服务层用reference
    @Reference
    private BrandService brandService;

    /**
     * 全部商品品牌
     * @return
     */
    @RequestMapping("findAll")
    public List<TbBrand> findAll(){
        return brandService.findAll();
    }

    /**
     * 全部数据分页显示
     * @return
     */
    @RequestMapping("findPage")
    public PageResult findPage(@RequestParam("page") int page, @RequestParam("rows") int rows){
        return brandService.findPage(null,page,rows);
    }

    /**
     * 分页加模糊查询
     * @return
     */
    @RequestMapping("search")
    public PageResult search(@RequestBody TbBrand brand,@RequestParam("page") int page, @RequestParam("rows") int rows){
        return brandService.findPage(brand,page,rows);
    }

    /**
     * 添加商品品牌
     */
    @RequestMapping("add")
    public Result add(@RequestBody TbBrand brand){
        boolean b = brandService.add(brand);
        Result result = new Result();
        if(b){
            result.setSuccess(true);
            result.setMessage("添加成功");
        }else {
            result.setSuccess(false);
            result.setMessage("添加失败");
        }
        return result;
    }

    /**
     * 根据id查询
     * @return
     */
    @RequestMapping("findOne")
    public TbBrand findOne(long id){
        return brandService.findOne(id);
    }

    /**
     * 修改
     * @return
     */
    @RequestMapping("update")
    public Result update(@RequestBody TbBrand brand){
        boolean b = brandService.update(brand);
        Result result = new Result();
        if(b){
            result.setSuccess(true);
            result.setMessage("修改成功");
        }else {
            result.setSuccess(false);
            result.setMessage("修改失败");
        }
        return result;
    }

    /**
     * 根据ids[]删除
     * @return
     */
    @RequestMapping("delete")
    public Result delete(long[] ids){
        boolean b = brandService.delete(ids);
        Result result = new Result();
        if(b){
            result.setSuccess(true);
            result.setMessage("删除成功");
        }else {
            result.setSuccess(false);
            result.setMessage("删除失败");
        }
        return result;
    }

    /**
     * 全部品牌
     * @return
     */
    @RequestMapping("selectOptionList")
    public List<Map> selectOptionList(){
        return brandService.selectOptionList();
    }

}
