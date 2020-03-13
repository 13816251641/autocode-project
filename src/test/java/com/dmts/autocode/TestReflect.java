package com.dmts.autocode;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @Auther ljn
 * @Date 2020/2/12
 * 反射的测试类
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestReflect {

    /**
     *
     */
    @Test
    public void test(){
        String className = "com.lujieni520.cloud.service.SayPortClientService";//字符串是该类的全限定名
        try {
            Class clzz = Class.forName(className);
            //--------------------反射类调用User中的sayHello()方法-----------------------------
            //注意导入正确的Method包名：
            // import java.lang.reflect.Method;
            //获取该类的所有方法
            Method[] methods = clzz.getMethods();
            //遍历方法
            for(Method m:methods){
                if(m.getName().equals("getData")){//找到sayHello这个方法
                    //获取返回类型
                    Type type=m.getGenericReturnType();
                    //如果返回的是类 （比如user）aa显示为：class org.entity.User
                    //如果返回的是普通数据类型（int） aa显示为：int
                    String aa=type.toString().substring(6);
                    System.out.println(aa);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
