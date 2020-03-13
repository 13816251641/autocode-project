package com.dmts.autocode;

import com.dmts.autocode.entity.OnlineEventConfig;
import com.dmts.autocode.service.IOnlineEventConfigService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Auther ljn
 * @Date 2020/2/10
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestCRUD {
    @Autowired
    private IOnlineEventConfigService iOnlineEventConfigService;
    @Test
    public void save(){
        iOnlineEventConfigService.saveOrUpdate(new OnlineEventConfig().
                setFeignName("lujieni123").
                setId("5db731ce57b55fdfb01e7e5fbe3144c8"));
    }
}
