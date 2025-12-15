package com.project.finance;

import com.project.finance.mapper.UsersMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FinanceApplicationTests {
    @Autowired
    UsersMapper usersMapper;
    @Test
    void contextLoads() {
        usersMapper.selectList(null);
    }

}
