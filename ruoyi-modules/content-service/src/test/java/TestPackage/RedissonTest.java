package TestPackage;

import com.lfy.kcat.content.biz.BloomFilterTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RedissonTest {

    @Autowired
    BloomFilterTemplate bloomFilterTemplate;

    @Test
    void RedissonTest01(){
        boolean result = bloomFilterTemplate.checkDramaIdBloomFilter(2018626608650907650L);
        System.out.println(result);
    }
}
