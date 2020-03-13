package com.dmts.autocode;

import com.dmts.autocode.service.CodeGenerateService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.regex.Matcher;

/**
 * @Auther ljn
 * @Date 2020/2/11
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GenerateCode {
    @Autowired
    private CodeGenerateService codeGenerateService;

    @Test
    public void testGenerateCode(){
        codeGenerateService.generateCode("5db731ce57b55fdfb01e7e5fbe3144c8");
    }

    @Test
    public void test(){
        String name = "a.b.ccc";
        System.out.println(name.replaceAll("\\.", Matcher.quoteReplacement(File.separator)));
    }


}
