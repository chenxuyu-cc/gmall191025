package com.atguigu.gmall191025.list.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall191025.bean.SkuLsInfo;
import com.atguigu.gmall191025.bean.SkuLsParams;
import com.atguigu.gmall191025.bean.SkuLsResult;
import com.atguigu.gmall191025.config.RedisUtil;
import com.atguigu.gmall191025.service.ListService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.Update;
import io.searchbox.core.search.aggregation.MetricAggregation;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ListServiceImpl implements ListService {

    @Autowired
    private JestClient jestClient;

    @Autowired
    private RedisUtil redisUtil;

    public static final String ES_INDEX = "gmall0513";

    public static final String ES_TYPE = "SkuInfo0513";


    @Override
    public void SkuLsInfo(SkuLsInfo skuLsInfo) {

        //创建索引，索引，类型
        Index build = new Index.Builder(skuLsInfo).index(ES_INDEX).type(ES_TYPE).id(skuLsInfo.getId()).build();

        try {
            jestClient.execute(build);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public SkuLsResult search(SkuLsParams skuLsParams) {

        String query = makeQueryStringForSearch(skuLsParams);

        Search search = new Search.Builder(query).addIndex(ES_INDEX).addType(ES_TYPE).build();

        SearchResult searchResult = null;

        try {
            searchResult = jestClient.execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }

        SkuLsResult skuLsResult = makeResultForSearch(skuLsParams, searchResult);

        return skuLsResult;
    }

    @Override
    public void incrHotScore(String skuId) {
        Jedis jedis = redisUtil.getJedis();

        Double hotScore = jedis.zincrby("hotScore", 1, "skuId:" + skuId);

        if(hotScore%10==0){
            updateHotScore(skuId,  Math.round(hotScore));
        }
    }

    private void updateHotScore(String skuId, long hotScore) {
        String updateScore = "{\n" +
                             "   \"doc\":{\n" +
                             "     \"hotScore\":"+hotScore+"\n" +
                             "   }\n" +
                             "}";


        Update update = new Update.Builder(updateScore).index(ES_INDEX).type(ES_TYPE).id(skuId).build();

        try {
            jestClient.execute(update);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SkuLsResult makeResultForSearch(SkuLsParams skuLsParams, SearchResult searchResult) {
        //List<SkuLsInfo> skuLsInfoList;
        //创建一个skuLsResult对象
        SkuLsResult skuLsResult = new SkuLsResult();

        //创建一个集合接受skuInfo
        ArrayList<SkuLsInfo> arrayList = new ArrayList<>();

        //把结果封装到集合中
        List<SearchResult.Hit<SkuLsInfo, Void>> hits = searchResult.getHits(SkuLsInfo.class);

        //判断并遍历集合
        if (hits != null && hits.size() > 0) {
            for (SearchResult.Hit<SkuLsInfo, Void> hit : hits) {
                SkuLsInfo skuLsInfo = hit.source;
                //判断高亮
                if (hit.highlight != null && hit.highlight.size() > 0) {
                    List<String> skuName = hit.highlight.get("skuName");
                    String skuNameHit = skuName.get(0);
                    skuLsInfo.setSkuName(skuNameHit);
                }
                arrayList.add(skuLsInfo);
            }
        }
        skuLsResult.setSkuLsInfoList(arrayList);

        //total
        skuLsResult.setTotal(skuLsResult.getTotal());

        //totalPage
        long totalPage = searchResult.getTotal() % skuLsParams.getPageSize() == 0 ? searchResult.getTotal() / skuLsParams.getPageSize() : searchResult.getTotal() / skuLsParams.getPageSize() + 1;
        //公式
        //long totalPages = (searchResult.getTotal() + skuLsParams.getPageSize() - 1) / skuLsParams.getPageSize();
        skuLsResult.setTotalPages(totalPage);

        //聚合
        //List<String> attrValueIdList;
        //创建一个集合存储平台属性id
        ArrayList<String> stringArrayList = new ArrayList<>();
        //通过聚合获取平台属性id
        MetricAggregation aggregations = searchResult.getAggregations();
        //通过aggregations获取groupby_att
        TermsAggregation groupby_att = aggregations.getTermsAggregation("groupby_att");
        //通过groupby_att获取buckets
        List<TermsAggregation.Entry> buckets = groupby_att.getBuckets();
        //判断并遍历
        if(buckets != null && buckets.size()>0){
            for (TermsAggregation.Entry bucket : buckets) {
                String key = bucket.getKey();
                stringArrayList.add(key);
            }
        }
        skuLsResult.setAttrValueIdList(stringArrayList);

        return skuLsResult;
    }

    private String makeQueryStringForSearch(SkuLsParams skuLsParams) {

        //定义一个查询器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //query - bool
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //定义query
        searchSourceBuilder.query(boolQueryBuilder);
        //三级分类es查询
        if (skuLsParams.getCatalog3Id() != null && skuLsParams.getCatalog3Id().length() > 0) {
            //{"term":{"catalog3Id": "61"}
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id", skuLsParams.getCatalog3Id());
            //bool - filter
            boolQueryBuilder.filter(termQueryBuilder);
        }

        //平台属性id es查询
        if (skuLsParams.getValueId() != null && skuLsParams.getValueId().length > 0) {
            for (String valueId : skuLsParams.getValueId()) {
                //{"term":{"skuAttrValueList.valueId":"14"}}
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId", valueId);
                //bool - filter
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }

        //关键字查询
        if (skuLsParams.getKeyword() != null && skuLsParams.getKeyword().length() > 0) {
            //must - match
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", skuLsParams.getKeyword());
            //bool - must
            boolQueryBuilder.must(matchQueryBuilder);

            //设置高亮
            HighlightBuilder highlighter = searchSourceBuilder.highlighter();
            highlighter.preTags("<span style=color:red>");
            highlighter.field("skuName");
            highlighter.postTags("</span>");
            //将高亮放入查询器
            searchSourceBuilder.highlight(highlighter);
        }

        //分页
        int from = (skuLsParams.getPageNo() - 1) * skuLsParams.getPageSize();
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(skuLsParams.getPageSize());
        searchSourceBuilder.sort("hotScore", SortOrder.DESC);

        //聚合
        TermsBuilder groupby_att = AggregationBuilders.terms("groupby_att").field("skuAttrValueList.valueId");
        searchSourceBuilder.aggregation(groupby_att);

        //转换字符串
        String query = searchSourceBuilder.toString();
        System.out.println(query);

        return query;
    }
}
