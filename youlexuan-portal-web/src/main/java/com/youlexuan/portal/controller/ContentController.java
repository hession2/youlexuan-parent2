package com.youlexuan.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.youlexuan.content.service.ContentService;
import com.youlexuan.pojo.TbContent;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 王大亮
 * @date 2019/10/9 17:04
 */
@RestController
@RequestMapping("/content")
public class ContentController {

    @Reference
    private ContentService contentService;
    /**
     * 根据广告分类ID查询广告列表
     */
    @RequestMapping("/findByCategoryId")
    public List<TbContent> findByCategoryId(Long categoryId) {
        return contentService.findByCategoryId(categoryId);
    }
}

