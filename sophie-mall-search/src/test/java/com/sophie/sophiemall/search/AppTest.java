package com.sophie.sophiemall.search;

import com.sophie.sophiemall.search.domain.EsProduct;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Criteria;

/**
 * Unit test for simple App.
 */
@SpringBootTest
public class AppTest
{
    @Autowired
    ElasticsearchOperations elasticsearchOperations;

    @Test
    public void testConnection() {
        System.out.println("hello");
    }

    @Test
    void textSearch() {

        var expectedDate = "2014-10-29";
        var expectedWord = "java";
        var query = new CriteriaQuery(
                new Criteria("keywords").contains(expectedWord).and(new Criteria("date").greaterThanEqual(expectedDate)));

        var result = elasticsearchOperations.search(query, EsProduct.class);
    }
}
