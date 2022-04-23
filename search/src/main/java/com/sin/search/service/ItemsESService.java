package com.sin.search.service;

import com.sin.util.PagedGridResult;

public interface ItemsESService{
    public PagedGridResult searchItems(String keywords,
                                       String sort,
                                       Integer page,
                                       Integer pageSize);
}
