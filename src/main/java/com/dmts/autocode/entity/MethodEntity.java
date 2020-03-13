package com.dmts.autocode.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Auther ljn
 * @Date 2020/2/12
 */
@Data
@Accessors(chain = true)
public class MethodEntity {

    /*
      方法的名字
     */
    private String methodName;


    private String className;

    /*
        类的全路径(包名+类名)
     */
    private String repositoryName;

    /*

     */
    private String repositoryBeanName;

}
