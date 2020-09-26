package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.dao.SkuESsMapper;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SkuService;
import entity.Result;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SkuServiceImpl implements SkuService {
    @Autowired
    private SkuFeign skuFeign;
    @Autowired
    private SkuESsMapper skuESsMapper;

    /**
     * ElasticsearchTemplate :可以实现索引库的增删改查(高级搜索)
     */
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override

    public void importData() {
        //Feign调用,查询List<Sku>
        Result<List<Sku>> listResult = skuFeign.findAll();
        //将List<Sku>转成List<SkuInfo>
        List<SkuInfo> skuInfoList = JSON.parseArray(JSON.toJSONString(listResult.getData()), SkuInfo.class);
        //循环当前的skuInfoList
        for (SkuInfo skuInfo : skuInfoList) {
            //获取spec->Map(String) ->Map类型 {"电视音响效果":"立体声","电视屏幕尺寸":"20英寸","尺码":"165"}
            Map<String, Object> specMap = JSON.parseObject(skuInfo.getSpec(), Map.class);
            //生成动态的域.需要将该域存入到一个 Map<String, Object> 对象中即可,该 Map<String, Object> 的key会生成一个域,域的名字为该map的key
            //当前 Map<String, Object> 后面Object的值会作为当前skt对象该域(key)对应的值
            skuInfo.setSpecMap(specMap);
        }
        //调用Dao实现数据的批量导入
        skuESsMapper.saveAll(skuInfoList);
    }

    /**
     * 多条件搜索
     *
     * @param searchMap
     * @return
     */
    @Override
    public Map<String, Object> search(Map<String, String> searchMap) {

        /**
         * 搜索条件构建对象,用于封装各种搜索条件
         */
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        if (null != searchMap && searchMap.size() > 0) {
            //根据关键词搜索
            String keywords = searchMap.get("keywords");
            builder.withQuery(
                    QueryBuilders.queryStringQuery(keywords).field("name")
            );
        }


        /**
         * 执行搜索,返回相应结果
         * 1 builder.build():搜索条件封装对象
         * 2 SkuInfo.class:搜索的结果集(集合数据)需要转换的类型
         * 3 AggregatedPage<SkuInfo>:搜索结果集的封装
         */
        AggregatedPage<SkuInfo> page = elasticsearchTemplate.queryForPage(builder.build(), SkuInfo.class);

        /**
         * 分组查询分类集合
         * addAggregation():添加一个聚合操作
         * terms():取别名
         * field():根据那个域分组查询
         */
        builder.addAggregation(AggregationBuilders.terms("skuCategory").field("categoryName"));
        AggregatedPage<SkuInfo> aggregatedPage = elasticsearchTemplate.queryForPage(builder.build(), SkuInfo.class);

        /**
         * 获取分组数据
         *  aggregatedPage.getAggregations() 获取的是集合,可以根据多个域进行查询
         *  .get("skuCategory"):获取指定域的集合
         */
        StringTerms stringTerms = aggregatedPage.getAggregations().get("skuCategory");
        List<String> categoryList = new ArrayList<>();
        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
            //其中一个分类名字
            String categoryName = bucket.getKeyAsString();
            categoryList.add(categoryName);
        }
        //分析数据
        //分页参数-总记录数
        long totalElement = page.getTotalElements();
        //总页数
        long totalPages = page.getTotalPages();

        //获取结果集
        List<SkuInfo> contents = page.getContent();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("rows", contents);
        resultMap.put("total", totalElement);
        resultMap.put("totalPages", totalPages);
        resultMap.put("categoryList", categoryList);
        return resultMap;
    }

    /*@Override
    public Map search(Map<String, String> searchMap) {
        //1.获取到关键字
        String keywords = searchMap.get("keywords");

        //2.判断是否为空 如果 为空 给一个默认 值:华为
        if (StringUtils.isEmpty(keywords)) {
            keywords = "华为";
        }
        //3.创建 查询构建对象
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //4.设置 查询的条件

        // 4.1 商品分类的列表展示: 按照商品分类的名称来分组
        //terms  指定分组的一个别名
        //field 指定要分组的字段名
        // size 指定查询结果的数量 默认是10个
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuCategorygroup").field("categoryName").size(50));




        //匹配查询  先分词 再查询  主条件查询
        //参数1 指定要搜索的字段
        //参数2 要搜索的值(先分词 再搜索)
        nativeSearchQueryBuilder.withQuery(QueryBuilders.matchQuery("name",keywords));

        //5.构建查询对象(封装了查询的语法)
        NativeSearchQuery nativeSearchQuery = nativeSearchQueryBuilder.build();
        //6.执行查询
        AggregatedPage<SkuInfo> skuInfos = elasticsearchTemplate.queryForPage(nativeSearchQuery, SkuInfo.class);


        // 获取聚合结果  获取商品分类的列表数据
        StringTerms stringTermsCategory = (StringTerms) skuInfos.getAggregation("skuCategorygroup");
        List<String> categoryList = new ArrayList<>();
        if (stringTermsCategory != null) {
            for (StringTerms.Bucket bucket : stringTermsCategory.getBuckets()) {
                String keyAsString = bucket.getKeyAsString();
                System.out.println(keyAsString);//就是商品分类的数据
                categoryList.add(keyAsString);
            }
        }


        //7.获取结果  返回map

        List<SkuInfo> content = skuInfos.getContent();//当前的页的集合
        int totalPages = skuInfos.getTotalPages();//总页数
        long totalElements = skuInfos.getTotalElements();//总记录数

        Map<String,Object> resultMap =new HashMap<>();
        resultMap.put("categoryList",categoryList);//商品分类的列表数据
        resultMap.put("rows",content);
        resultMap.put("total",totalElements);
        resultMap.put("totalPages",totalPages);
        return resultMap;
    }*/
}
