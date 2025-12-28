package com.lfy.kcat;

import com.lfy.kcat.interaction.domain.Comments;
import com.lfy.kcat.interaction.mapper.CommentsMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class MapperTest {

    @Autowired
    CommentsMapper commentsMapper;
    @Test
    void Test(){
        List<Comments> comments = commentsMapper.selectList(null);
        System.out.println(comments);
    }
}
