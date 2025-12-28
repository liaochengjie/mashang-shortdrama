package com.lfy.kcat;

import com.lfy.kcat.user.domain.Users;
import com.lfy.kcat.user.mapper.UsersMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class MapperTest {
    @Autowired
    UsersMapper usersMapper;

    @Test
    void test01(){
        List<Users> users = usersMapper.selectList(null);
        System.out.println(users);

    }

}
