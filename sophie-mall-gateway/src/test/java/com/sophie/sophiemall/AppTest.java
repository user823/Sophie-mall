package com.sophie.sophiemall;

import cn.dev33.satoken.oauth2.logic.SaOAuth2Util;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Unit test for simple App.
 */
@SpringBootTest
public class AppTest
{

    @Test
    public void test() {
        System.out.println(SaOAuth2Util.checkAccessToken("Hqc7hW9k5riOUmrKKwyWBw5lBx9JJvuPoHqZCJ2MSSgnblOX0N4mUs4vwLsS"));
    }
}
