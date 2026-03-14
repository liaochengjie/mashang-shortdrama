package com.lfy.kcat;

import com.lfy.kcat.interaction.constant.RedisFromUserServiceConst;
import com.lfy.kcat.interaction.domain.Comments;
import com.lfy.kcat.interaction.mapper.CommentsMapper;
import org.dromara.common.core.dto.home.HomeFeaturedDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

@SpringBootTest
public class MapperTest {

    @Autowired
    CommentsMapper commentsMapper;

    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Test
    void Test(){
        List<Comments> comments = commentsMapper.selectList(null);
        System.out.println(comments);
    }
    @Test
    void Test2(){
        String homeFeatured = stringRedisTemplate.opsForValue().get("home:feature:1:15");
        System.out.println(homeFeatured);
    }
}
