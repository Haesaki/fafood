package com.sin.search.controller;

import com.sin.search.service.ItemsESService;
import com.sin.util.HttpJSONResult;
import com.sin.util.PagedGridResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("items")
public class ItemsController {
    @Autowired
    private ItemsESService itemsESService;

    @GetMapping("/es/search")
    public HttpJSONResult esSearch(String keyword, String sort, Integer page, Integer pageSize){
        if(StringUtils.isBlank(keyword))
            return HttpJSONResult.errorMsg(null);
        if(page == null)
            page = 1;
        if(pageSize == null)
            pageSize = 20;
        PagedGridResult gridResult = itemsESService.searchItems(keyword, sort, page, pageSize);
        return HttpJSONResult.ok(gridResult);
    }
}