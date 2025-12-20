package com.project.finance;

import com.project.finance.mapper.SavingsAccountsMapper;
import com.project.finance.mapper.UsersMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FinanceApplicationTests {
    @Autowired
    SavingsAccountsMapper usersMapper;
    @Test
    void contextLoads() {
        System.out.println(usersMapper.selectList(null));
    }

}
