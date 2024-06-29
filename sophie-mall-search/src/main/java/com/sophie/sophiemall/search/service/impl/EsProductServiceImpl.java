package com.sophie.sophiemall.search.service.impl;

import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import com.alibaba.nacos.client.auth.ram.identify.StsConfig;
import com.sophie.sophiemall.search.dao.EsProductDao;
import com.sophie.sophiemall.search.domain.EsProduct;
import com.sophie.sophiemall.search.domain.EsProductRelatedInfo;
import com.sophie.sophiemall.search.repository.EsProductRepository;
import com.sophie.sophiemall.search.service.EsProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.client.elc.*;
import org.springframework.data.elasticsearch.client.elc.QueryBuilders;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EsProductServiceImpl implements EsProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EsProductServiceImpl.class);
    @Autowired
    private EsProductDao productDao;
    @Autowired
    private EsProductRepository productRepository;
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;
    @Override
    public int importAll() {
        List<EsProduct> esProductList = productDao.getAllEsProductList(null);
        Iterable<EsProduct> esProductIterable = productRepository.saveAll(esProductList);
        Iterator<EsProduct> iterator = esProductIterable.iterator();
        int result = 0;
        while (iterator.hasNext()) {
            result++;
            iterator.next();
        }
        return result;
    }

    @Override
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public EsProduct create(Long id) {
        EsProduct result = null;
        List<EsProduct> esProductList = productDao.getAllEsProductList(id);
        if (esProductList.size() > 0) {
            EsProduct esProduct = esProductList.get(0);
            result = productRepository.save(esProduct);
        }
        return result;
    }

    @Override
    public void delete(List<Long> ids) {
        if (!CollectionUtils.isEmpty(ids)) {
            List<EsProduct> esProductList = new ArrayList<>();
            for (Long id : ids) {
                EsProduct esProduct = new EsProduct();
                esProduct.setId(id);
                esProductList.add(esProduct);
            }
            productRepository.deleteAll(esProductList);
        }
    }

    @Override
    public Page<EsProduct> search(String keyword, Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        return productRepository.findByNameOrSubTitleOrKeywords(keyword, keyword, keyword, pageable);
    }

    @Override
    public Page<EsProduct> search(String keyword, Long brandId, Long productCategoryId, Integer pageNum, Integer pageSize,Integer sort) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        NativeQueryBuilder nb = new NativeQueryBuilder();
        // 分页
        nb.withPageable(pageable);
        // 过滤
        if (brandId != null || productCategoryId != null) {
            BoolQuery.Builder b = new BoolQuery.Builder();
            if (brandId != null) {
                b.must(QueryBuilders.termQueryAsQuery("brandId", brandId.toString()));
            }
            if (productCategoryId != null) {
                b.must(QueryBuilders.termQueryAsQuery("productCategoryId", productCategoryId.toString()));
            }
            nb.withFilter(b.build()._toQuery());
        }
        // 搜索
        if (StringUtils.isEmpty(keyword)) {
            nb.withQuery(QueryBuilders.matchAllQuery()._toQuery());
        } else {
            FunctionScoreQuery.Builder fb = new FunctionScoreQuery.Builder();
            fb.functions(new FunctionScore.Builder().filter(QueryBuilders.matchQueryAsQuery("name", keyword, null, null)).weight(10.0).build());
            fb.functions(new FunctionScore.Builder().filter(QueryBuilders.matchQueryAsQuery("subTitle", keyword, null, null)).weight(5.0).build());
            fb.functions(new FunctionScore.Builder().filter(QueryBuilders.matchQueryAsQuery("keywords", keyword, null, null)).weight(2.0).build());
            fb.scoreMode(FunctionScoreMode.Sum);
            fb.minScore(2.0);
            nb.withQuery(fb.build()._toQuery());
        }
        // 排序
        if (sort == 1) {
            // 按新品从新到旧排序
            nb.withSort(Sort.by(Sort.Direction.DESC, "id"));
        } else if (sort == 2) {
            // 按销量从高到低
            nb.withSort(Sort.by(Sort.Direction.DESC, "sale"));
        } else if (sort == 3) {
            // 价格从低到高
            nb.withSort(Sort.by(Sort.Direction.ASC, "price"));
        } else if (sort == 4) {
            // 价格从高到低
            nb.withSort(Sort.by(Sort.Direction.DESC, "price"));
        } else {
            nb.withSort(Sort.by(Sort.Direction.DESC, "_score"));
        }

        var query = nb.build();
        LOGGER.info("DSL:{}", query);
        var result = elasticsearchOperations.search(query, EsProduct.class);
        if (result.getTotalHits() <= 0) {
            return new PageImpl<>(null,pageable,0);
        }
        List<EsProduct> searchProductList = result.stream().map(SearchHit::getContent).collect(Collectors.toList());
        return new PageImpl<>(searchProductList,pageable,result.getTotalHits());
    }

    @Override
    public Page<EsProduct> recommend(Long id, Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        List<EsProduct> esProductList = productDao.getAllEsProductList(id);
        if (esProductList.size() > 0) {
            EsProduct esProduct = esProductList.get(0);
            String keyword = esProduct.getName();
            Long brandId = esProduct.getBrandId();
            Long ProductCategoryId = esProduct.getProductCategoryId();
            // 根据商品标题、品牌、分类搜索
            FunctionScoreQuery.Builder fb = new FunctionScoreQuery.Builder();
            fb.functions(new FunctionScore.Builder().filter(QueryBuilders.matchQueryAsQuery("name", keyword, null, null)).weight(8.0).build());
            fb.functions(new FunctionScore.Builder().filter(QueryBuilders.matchQueryAsQuery("subTitle", keyword, null, null)).weight(2.0).build());
            fb.functions(new FunctionScore.Builder().filter(QueryBuilders.matchQueryAsQuery("keywords", keyword, null, null)).weight(2.0).build());
            fb.functions(new FunctionScore.Builder().filter(QueryBuilders.matchQueryAsQuery("brandId", brandId.toString(), null, null)).weight(5.0).build());
            fb.functions(new FunctionScore.Builder().filter(QueryBuilders.matchQueryAsQuery("productCategoryId", ProductCategoryId.toString(), null, null)).weight(3.0).build());
            fb.scoreMode(FunctionScoreMode.Sum);
            fb.minScore(2.0);
            // 过滤相同的商品
            BoolQuery.Builder b = new BoolQuery.Builder();
            b.mustNot(QueryBuilders.termQueryAsQuery("id", id.toString()));
            // 构筑查询条件
            NativeQueryBuilder nb = new NativeQueryBuilder();
            nb.withQuery(fb.build()._toQuery());
            nb.withFilter(b.build()._toQuery());
            nb.withPageable(pageable);
            NativeQuery query = nb.build();
            LOGGER.info("DSL:{}", query);
            SearchHits<EsProduct> searchHits = elasticsearchOperations.search(query, EsProduct.class);
            if(searchHits.getTotalHits()<=0){
                return new PageImpl<>(null,pageable,0);
            }
            List<EsProduct> searchProductList = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
            return new PageImpl<>(searchProductList,pageable,searchHits.getTotalHits());
        }
        return new PageImpl<>(null);
    }

    @Override
    public EsProductRelatedInfo searchRelatedInfo(String keyword) {
        NativeQueryBuilder nb = new NativeQueryBuilder();
        // 搜索条件
        if (StringUtils.isEmpty(keyword)) {
            nb.withQuery(QueryBuilders.matchAllQuery()._toQuery());
        } else {
            nb.withQuery(new MultiMatchQuery.Builder().query(keyword).fields("name", "subTitle", "keywords").build()._toQuery());
        }
        // 聚合搜索品牌名称
        nb.withAggregation("brandNames", new TermsAggregation.Builder().field("brandName").build()._toAggregation());
        // 聚合搜索分类名称
        nb.withAggregation("productCategoryNames", new TermsAggregation.Builder().field("productCategoryName").build()._toAggregation());
        //聚合搜索商品属性，去除type = 1 属性
        Map<String, Aggregation> subAggregations = new HashMap<>();
        subAggregations.put("attrValues", new Aggregation.Builder().terms(new TermsAggregation.Builder().field("attrValueList.value").build()).build());
        subAggregations.put("attrNames", new Aggregation.Builder().terms(new TermsAggregation.Builder().field("attrValueList.name").build()).build());
        Aggregation termsAggregation = new Aggregation.Builder().terms(new TermsAggregation.Builder().field("attrValueList.productAttributeId").build()).aggregations(subAggregations).build();
        Aggregation filterAggregation = new Aggregation.Builder().filter(QueryBuilders.termQueryAsQuery("attrValueList.type", "1")).aggregations("attrIds", termsAggregation).build();
        nb.withAggregation(
            "allAttrValues",
                new Aggregation.Builder().nested(new NestedAggregation.Builder().path("attrValueList").build()).aggregations("productAttrs", filterAggregation).build()
        );
        NativeQuery query = nb.build();
        SearchHits<EsProduct> result = elasticsearchOperations.search(query, EsProduct.class);
        return convertProductRelatedInfo(result);
    }

    /**
     * 将返回结果转换为对象
     */
    private EsProductRelatedInfo convertProductRelatedInfo(SearchHits<EsProduct> response) {
        EsProductRelatedInfo productRelatedInfo = new EsProductRelatedInfo();
        Map<String, Aggregate> mp = new HashMap<>();
        for (var item: (ArrayList) response.getAggregations().aggregations()) {
            org.springframework.data.elasticsearch.client.elc.Aggregation a = ((ElasticsearchAggregation) item).aggregation();
            mp.put(a.getName(), a.getAggregate());
        }

        // 设置品牌名称
        Aggregate brandNames = mp.get("brandNames");
        List<String> brandNameList = new ArrayList<>();
        if (brandNames != null && brandNames.isSterms()) {
            for (var b : brandNames.sterms().buckets().array()) {
                brandNameList.add(b.key().stringValue());
            }
        }
        productRelatedInfo.setBrandNames(brandNameList);

        // 设置分类
        Aggregate productCategoryNames = mp.get("productCategoryNames");
        List<String> productCategoryNameList = new ArrayList<>();
        if (productCategoryNames != null && productCategoryNames.isSterms()) {
            for (var p : productCategoryNames.sterms().buckets().array()) {
                productCategoryNameList.add(p.key().stringValue());
            }
        }
        productRelatedInfo.setProductCategoryNames(productCategoryNameList);

        // 设置参数
        Aggregate allAttrValues = mp.get("allAttrValues");
        List<EsProductRelatedInfo.ProductAttr> attrList = new ArrayList<>();
        if (allAttrValues != null && allAttrValues.isNested()) {
            Aggregate productAttrs = allAttrValues.nested().aggregations().get("productAttrs");
            if (productAttrs != null && productAttrs.isFilter()) {
                Aggregate attrIds = productAttrs.filter().aggregations().get("attrIds");
                if (attrIds != null && attrIds.isLterms()) {
                    for (var b : attrIds.lterms().buckets().array()) {
                        EsProductRelatedInfo.ProductAttr attr = new EsProductRelatedInfo.ProductAttr();
                        attr.setAttrId(b.key());

                        List<String> attrValueList = new ArrayList<>();
                        Aggregate c = b.aggregations().get("attrValues");
                        Aggregate d = b.aggregations().get("attrNames");

                        // 设置属性值
                        if (c != null && c.isSterms()) {
                            for (var e : c.sterms().buckets().array()) {
                                attrValueList.add(e.key().stringValue());
                            }
                        }
                        attr.setAttrValues(attrValueList);

                        // 设置属性名
                        if (d != null && d.isSterms()) {
                            for (var f : d.sterms().buckets().array()) {
                                attr.setAttrName(f.key().stringValue());
                                break;
                            }
                        }
                        attrList.add(attr);
                    }
                }
            }
        }

        productRelatedInfo.setProductAttrs(attrList);
        return productRelatedInfo;
    }
}