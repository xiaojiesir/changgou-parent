package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.dao.SkuESsMapper;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SkuService;
import entity.Result;
import org.apache.commons.lang.text.StrBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

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
        //搜索条件封装
        NativeSearchQueryBuilder builder = buildBasicQuery(searchMap);
        //集合搜索
        Map<String, Object> resultMap = searchList(builder);
        searchGroupList(builder, resultMap, searchMap);
       /* //当用户选择了分类,将分类作为搜索条件,则不需要对分类进行分组查询
        if (null == searchMap || StringUtils.isEmpty(searchMap.get("category"))) {
            //分类分组查询
            List<String> categoryList = searchCategoryList(builder);
            resultMap.put("categoryList", categoryList);
        }

        if (null == searchMap || StringUtils.isEmpty(searchMap.get("brand"))) {
            //品牌分组查询
            List<String> brandList = searchBrandList(builder);
            resultMap.put("brandList", brandList);
        }

        //规格分组查询
        resultMap.put("specList", searchSpecList(builder));*/

        return resultMap;
    }

    /**
     * 分组查询 -> 分类分组,品牌分组,规格分组
     *
     * @param builder
     * @return
     */
    private void searchGroupList(NativeSearchQueryBuilder builder, Map<String, Object> resultMap, Map<String, String> searchMap) {
        /**
         * 分组查询分类集合
         * addAggregation():添加一个聚合操作
         * terms():取别名
         * field():根据那个域分组查询
         */
        if (null == searchMap || StringUtils.isEmpty(searchMap.get("category"))) {
            builder.addAggregation(AggregationBuilders.terms("skuCategory").field("categoryName"));
        }
        if (null == searchMap || StringUtils.isEmpty(searchMap.get("brand"))) {
            builder.addAggregation(AggregationBuilders.terms("skuBrand").field("brandName"));
        }

        builder.addAggregation(AggregationBuilders.terms("skuSpec").field("spec.keyword"));
        AggregatedPage<SkuInfo> aggregatedPage = elasticsearchTemplate.queryForPage(builder.build(), SkuInfo.class);

        /**
         * 获取分组数据
         *  aggregatedPage.getAggregations() 获取的是集合,可以根据多个域进行查询
         *  .get("skuCategory"):获取指定域的集合
         */
        if (null == searchMap || StringUtils.isEmpty(searchMap.get("category"))) {
            StringTerms categoryTerms = aggregatedPage.getAggregations().get("skuCategory");
            //获取分类分组集合数据
            resultMap.put("categoryList", getGroupList(categoryTerms));
        }
        if (null == searchMap || StringUtils.isEmpty(searchMap.get("brand"))) {
            StringTerms brandTerms = aggregatedPage.getAggregations().get("skuBrand");
            //获取品牌分组集合数据
            resultMap.put("brandList", getGroupList(brandTerms));
        }

        StringTerms specTerms = aggregatedPage.getAggregations().get("skuSpec");
        //获取规格分组集合数据
        resultMap.put("specList", getGroupMapList(getGroupList(specTerms)));
    }

    public List<String> getGroupList(StringTerms stringTerms) {
        List<String> groupList = new ArrayList<>();
        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
            String filedName = bucket.getKeyAsString();
            groupList.add(filedName);
        }
        return groupList;
    }

    public Map<String, Set<String>> getGroupMapList(List<String> specList) {
        Map<String, Set<String>> allSpec = new HashMap<>();
        for (String spec : specList) {
            Map<String, String> specMap = JSON.parseObject(spec, Map.class);
            for (Map.Entry<String, String> entry : specMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                Set<String> specSet = allSpec.get(key);
                if (null == specSet) {
                    specSet = new HashSet<>();
                }
                specSet.add(value);
                allSpec.put(key, specSet);
            }
        }
        return allSpec;
    }

    /**
     * 搜索条件封装
     *
     * @param searchMap
     * @return
     */
    private NativeSearchQueryBuilder buildBasicQuery(Map<String, String> searchMap) {
        /**
         * 搜索条件构建对象,用于封装各种搜索条件
         */
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();

        //BoolQuery must,must_not,should
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (null != searchMap && searchMap.size() > 0) {
            //根据关键词搜索
            String keywords = searchMap.get("keywords");
            if (!StringUtils.isEmpty(keywords)) {
               /* builder.withQuery(
                        QueryBuilders.queryStringQuery(keywords).field("name")
                );*/
                boolQueryBuilder.must(QueryBuilders.queryStringQuery(keywords).field("name"));
            }
            //输入了分类
            if (!StringUtils.isEmpty(searchMap.get("category"))) {
                boolQueryBuilder.must(QueryBuilders.termQuery("categoryName", searchMap.get("category")));
            }
            //输入了品牌
            if (!StringUtils.isEmpty(searchMap.get("brand"))) {
                boolQueryBuilder.must(QueryBuilders.termQuery("brandName", searchMap.get("brand")));
            }
            //规格过滤实现:spec_网络=联通3G&spec_颜色=红
            for (Map.Entry<String, String> entry : searchMap.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith("spec_")) {
                    //截取规格的名称
                    boolQueryBuilder.filter(QueryBuilders.termQuery("specMap." + key.substring(5) + ".keyword", entry.getValue()));
                }
            }
            // 价格 0-500元  3000元以上
            String price = searchMap.get("price");
            if (!StringUtils.isEmpty(price)) {
                price = price.replace("yuan", "").replace("以上", "");
                //获取值 按照- 切割
                String[] split = price.split("-");
                //过滤范围查询
                //0<=price<=500
                if (null != split && split.length > 0) {
                    boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gt(Integer.parseInt(split[0])));
                    if (split.length == 2) {
                        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").lte(Integer.parseInt(split[1])));
                    }
                }

            }

            //排序操作
            //获取排序的字段 和要排序的规则
            String sortField = searchMap.get("sortField");//price
            String sortRule = searchMap.get("sortRule");//DESC ASC
            if (!StringUtils.isEmpty(sortField) && !StringUtils.isEmpty(sortRule)) {
                //执行排序
                builder.withSort(SortBuilders.fieldSort(sortField).order(sortRule.equalsIgnoreCase("ASC") ? SortOrder.ASC : SortOrder.DESC));
                //nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(sortField).order(SortOrder.valueOf(sortRule)));
            }
        }
        //分页,如果用户不传分页参数,则默认显示第一页
        Integer pageNum = coverterPage(searchMap);
        Integer size = 30;
        builder.withPageable(PageRequest.of(pageNum - 1, size));
        return builder.withQuery(boolQueryBuilder);
    }

    public Integer coverterPage(Map<String, String> searchMap) {
        if (searchMap != null) {
            String pageNum = searchMap.get("pageNum");
            return Integer.parseInt(pageNum);
        }
        return 1;
    }

    /**
     * 集合搜索
     *
     * @param builder
     * @return
     */
    private Map<String, Object> searchList(NativeSearchQueryBuilder builder) {

        //设置高亮的字段 针对 商品的名称进行高亮
        HighlightBuilder.Field field = new HighlightBuilder.Field("name");

        //设置前缀 和 后缀
        field.preTags("<em style=\"color:red\">");
        field.postTags("</em>");
        //碎片长度
        field.fragmentSize(100);
        //添加高亮
        builder.withHighlightFields(field);
        /**
         * 执行搜索,返回相应结果
         * 1 builder.build():搜索条件封装对象
         * 2 SkuInfo.class:搜索的结果集(集合数据)需要转换的类型
         * 3 AggregatedPage<SkuInfo>:搜索结果集的封装
         */
        //AggregatedPage<SkuInfo> page = elasticsearchTemplate.queryForPage(builder.build(), SkuInfo.class);
        AggregatedPage<SkuInfo> page = elasticsearchTemplate.
                queryForPage(
                        builder.build(),//搜索条件封装
                        SkuInfo.class,//数据集合转换类型
                        //SearchResultMapper 执行搜索后,将数据结果集封装到该对象中
                        new SearchResultMapper() {
                            @Override
                            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                                List<T> skuInfoList = new ArrayList<>();
                                //执行查询,获取所有数据->结果集{非高亮数据|高亮数据}
                                for (SearchHit hit : searchResponse.getHits()) {
                                    //分析结果集数据,获取非高亮数据
                                    SkuInfo skuInfo = JSON.parseObject(hit.getSourceAsString(), SkuInfo.class);
                                    //分析结果集数据,获取高亮数据->只有某个域的高亮数据
                                    HighlightField highlightField = hit.getHighlightFields().get("name");

                                    if (null != highlightField && null != highlightField.getFragments()) {

                                        //高亮数据取出来
                                        Text[] fragments = highlightField.getFragments();
                                        StringBuilder stringBuilder = new StringBuilder();
                                        for (Text fragment : fragments) {
                                            stringBuilder.append(fragment.toString());
                                        }
                                        //非高亮数据中指定的域替换为高亮数据
                                        skuInfo.setName(stringBuilder.toString());
                                    }
                                    skuInfoList.add((T) skuInfo);
                                }
                                //将数据返回
                                return new AggregatedPageImpl<T>(skuInfoList, pageable, searchResponse.getHits().getTotalHits());
                            }
                        }
                );


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
        return resultMap;
    }

    /**
     * 分类分组查询
     *
     * @param builder
     * @return
     */
    private List<String> searchCategoryList(NativeSearchQueryBuilder builder) {
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
        return categoryList;
    }

    /**
     * 品牌分组查询
     *
     * @param builder
     * @return
     */
    private List<String> searchBrandList(NativeSearchQueryBuilder builder) {
        /**
         * 分组查询品牌集合
         * addAggregation():添加一个聚合操作
         * terms():取别名
         * field():根据那个域分组查询
         */
        builder.addAggregation(AggregationBuilders.terms("skuBrand").field("brandName"));
        AggregatedPage<SkuInfo> aggregatedPage = elasticsearchTemplate.queryForPage(builder.build(), SkuInfo.class);

        /**
         * 获取分组数据
         *  aggregatedPage.getAggregations() 获取的是集合,可以根据多个域进行查询
         *  .get("skuBrand"):获取指定域的集合
         */
        StringTerms stringTerms = aggregatedPage.getAggregations().get("skuBrand");
        List<String> brandList = new ArrayList<>();
        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
            //其中一个分类名字
            String brandName = bucket.getKeyAsString();
            brandList.add(brandName);
        }
        return brandList;
    }


    /**
     * 规格分组查询
     *
     * @param builder
     * @return
     */
    private Map<String, Set<String>> searchSpecList(NativeSearchQueryBuilder builder) {
        /**
         * 分组查询规格集合
         * addAggregation():添加一个聚合操作
         * terms():取别名
         * field():根据那个域分组查询
         */
        builder.addAggregation(AggregationBuilders.terms("skuSpec").field("spec.keyword").size(1000000));
        AggregatedPage<SkuInfo> aggregatedPage = elasticsearchTemplate.queryForPage(builder.build(), SkuInfo.class);

        /**
         * 获取分组数据
         *  aggregatedPage.getAggregations() 获取的是集合,可以根据多个域进行查询
         *  .get("skuSpec"):获取指定域的集合
         */
        StringTerms stringTerms = aggregatedPage.getAggregations().get("skuSpec");
        List<String> specList = new ArrayList<>();
        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
            //其中一个分类名字
            String spec = bucket.getKeyAsString();
            specList.add(spec);
        }
        Map<String, Set<String>> allSpec = new HashMap<>();
        for (String spec : specList) {
            Map<String, String> specMap = JSON.parseObject(spec, Map.class);
            for (Map.Entry<String, String> entry : specMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                Set<String> specSet = allSpec.get(key);
                if (null == specSet) {
                    specSet = new HashSet<>();
                }
                specSet.add(value);
                allSpec.put(key, specSet);
            }
        }
        return allSpec;
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
