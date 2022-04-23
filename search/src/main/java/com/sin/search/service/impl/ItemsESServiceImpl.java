package com.sin.search.service.impl;

import com.sin.search.pojo.Items;
import com.sin.search.service.ItemsESService;
import com.sin.util.PagedGridResult;
import lombok.val;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.dfs.AggregatedDfs;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

public class ItemsESServiceImpl implements ItemsESService {
    @Autowired
    private ElasticsearchRestTemplate esTemplate;
    private final String preTag = "<font color='red'>";
    private final String postTag = "</font>";

    @Override
    public PagedGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        SortBuilder sortBuilder = null;
        if (sort.equals("c")) {
            sortBuilder = new FieldSortBuilder("sellCounts").order(SortOrder.DESC);
        } else if (sort.equals("p")) {
            sortBuilder = new FieldSortBuilder("price").order(SortOrder.ASC);
        } else {
            sortBuilder = new FieldSortBuilder("itemName.keyword").order(SortOrder.ASC);
        }

        String itemNameField = "itemName";
        QueryBuilder itemNameQuery = QueryBuilders.matchQuery(itemNameField, keywords);
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(itemNameQuery)
                .withSorts(sortBuilder)
                .withPageable(pageable)
                .build();
//        AggregatedPage<Items> pagedItems = esTemplate.search(query, Items.class, IndexCoordinates.of(itemNameField));
        return null;
    }
}
