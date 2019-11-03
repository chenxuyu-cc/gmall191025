package com.atguigu.gmall191025.list;

import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallListServiceApplicationTests {

    @Autowired
    private JestClient jestClient;

    @Test
    public void testES() throws IOException {
        //定义语句
        String query = "{\n" +
                "  \"query\": {\n" +
                "    \"match\": {\n" +
                "      \"actorList.name\": \"zhang yi\"\n" +
                "    }\n" +
                "  }\n" +
                "}\n";
        //在哪个index和type中执行
        Search search = new Search.Builder(query).addIndex("movie_index").addType("movie").build();

        //执行search操作
        SearchResult result = jestClient.execute(search);

        //获取结果
        List<SearchResult.Hit<HashMap, Void>> hits = result.getHits(HashMap.class);
        for (SearchResult.Hit<HashMap, Void> hit : hits) {
            Map map = hit.source;
            System.out.println(map);
        }

    }

}
