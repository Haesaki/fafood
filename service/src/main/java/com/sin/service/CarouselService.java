package com.sin.service;

import com.sin.pojo.Carousel;

import java.util.List;

public interface CarouselService {
    // 查询所有轮播图列表
    public List<Carousel> queryAllCarousels(Integer isShow);
}
