package com.dmts.autocode.service.impl;

import com.dmts.autocode.entity.MethodEntity;
import com.dmts.autocode.entity.OnlineEventConfig;
import com.dmts.autocode.exception.AutocodeGlobalException;
import com.dmts.autocode.exception.R;
import com.dmts.autocode.service.CodeGenerateService;
import com.dmts.autocode.service.IOnlineEventConfigService;
import com.dmts.autocode.utils.CodeDOM;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;

/**
 * @Auther ljn
 * @Date 2020/2/11
 * 利用freemarker实现代码生成器
 */
@Service
@Slf4j
public class CodeGenerateServiceImplInFreemarker implements CodeGenerateService {

    private static final String FILE_NAME = "hello.ftl";

    @Autowired
    private IOnlineEventConfigService iOnlineEventConfigService;

    @Override
    public R generateCode(String id) {
        OnlineEventConfig onlineEventConfig = iOnlineEventConfigService.getById(id);
        
        /* 参数校验 */
        checkFields(onlineEventConfig);

        /* step1 创建freeMarker配置实例 */
        Configuration configuration = new Configuration(Configuration.getVersion());
        Writer out = null;
        // step2 获取模版路径
        configuration.setClassForTemplateLoading(this.getClass(),"/templates");
        // step3 创建数据模型
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("service_package",onlineEventConfig.getServicePackage());
        dataMap.put("method_input_json", onlineEventConfig.getMethodInputJson());
        dataMap.put("feign_package",onlineEventConfig.getFeignPackage());
        dataMap.put("feign_name",onlineEventConfig.getFeignName());
        dataMap.put("feign_mothod",onlineEventConfig.getFeignMethod());
        dataMap.put("service_name",onlineEventConfig.getServiceName());
        dataMap.put("event_id",onlineEventConfig.getEventId());


        processWithReflect(onlineEventConfig,dataMap);
        try {
            // step4 加载模版文件
            Template template = configuration.getTemplate(FILE_NAME);
            // step5 生成数据
            String s = onlineEventConfig.getServicePackage().replaceAll("\\.", Matcher.quoteReplacement(File.separator));
            File docFile = new File(onlineEventConfig.getOutputPath()+File.separator+s);//文件路径
            if(!docFile.exists())
                docFile.mkdirs();
            String path = docFile.getPath();
            String fileName = onlineEventConfig.getServiceName()+".java";
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path+File.separator+fileName)));
            // step6 输出文件
            template.process(dataMap, out);
            log.info(fileName+"模板生成成功");
            return R.ok("生成成功");
        } catch (Exception e) {
            try {
                if (null != out) {
                    out.flush();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            log.error("CodeGenerateServiceImplInFreemarker -> generateCode",e);
            return R.error(e.getMessage());
        }

    }


    private void processWithReflect(OnlineEventConfig onlineEventConfig, Map<String, Object> dataMap){
        String className = onlineEventConfig.getFeignPackage()+"."+onlineEventConfig.getFeignName();//字符串是该类的全限定名
        try {
            Class clazz = Class.forName(className);
            //获取该类的所有方法,不包括父类的方法
            Method[] methods = clazz.getDeclaredMethods();
            //遍历方法
            for(Method m:methods){
                if(m.getName().equals(onlineEventConfig.getFeignMethod())){//找到getData这个方法
                    //获取返回类型
                    Class<?> returnType = m.getReturnType();
                    String returnFullName = returnType.getName();
                    int i = returnFullName.lastIndexOf(".");
                    String returnClassName = returnFullName.substring(i+1);
                    dataMap.put("return_full_name",returnFullName);
                    dataMap.put("return_class_name",returnClassName);

                    /*
                       利用反射处理返回类型中的方法
                     */
                    Class returnClass = Class.forName(returnFullName);
                    Method[] returnClassMethods = returnClass.getDeclaredMethods();
                    List<MethodEntity> list= new ArrayList<>();
                    for (Method method : returnClassMethods){
                        if(method.getName().startsWith("get")){   //getUserDtos
                            String methodName = method.getName();
                            String domainType = onlineEventConfig.getDomainType();
                            domainType = domainType.substring(0,1).toUpperCase().concat(domainType.substring(1).toLowerCase());
                            String cn= "Ods"+domainType+ methodName.substring(3,methodName.length()-3);//表对应的实体类名
                            // 驼峰转下划线, userName -> user_name
                            Converter<String, String> converter = CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE);
                            String tableName = converter.convert(cn).toUpperCase();//数据库的表名A_B_C

                            generateEntity(tableName,onlineEventConfig.getOutputPath(),onlineEventConfig.getServicePackage());

                            String repositoryName = cn+"Repository";
                            String repositoryBeanName = repositoryName.substring(0,1).toLowerCase().concat(repositoryName.substring(1));
                            list.add(new MethodEntity().setMethodName(methodName).setClassName(cn).setRepositoryName(repositoryName).setRepositoryBeanName(repositoryBeanName));
                        }
                    }
                    dataMap.put("method_entity", list);
                    return;
                }
            }
        } catch (ClassNotFoundException e) {
            log.error("CodeGenerateServiceImplInFreemarker -> processWithReflect",e);
            throw new AutocodeGlobalException("包名或者类名填写错误");
        }



    }

    /**
     * 校验字段
     */
    private void checkFields(OnlineEventConfig onlineEventConfig){
        Object[] checkItems  = {onlineEventConfig.getServiceName(),
                                onlineEventConfig.getOutputPath(),
                                onlineEventConfig.getFeignMethod(),
                                onlineEventConfig.getFeignName(),
                                onlineEventConfig.getMethodInputJson(),
                                onlineEventConfig.getServicePackage(),
                                onlineEventConfig.getFeignPackage(),
                                onlineEventConfig.getDomainType(),
                                onlineEventConfig.getEventId()};
        Assert.noNullElements(checkItems,()->{
            return "必要参数不能为空!";
        });
    }

    public Boolean generateEntity(String tableName,String workSpace,String basePackage) {
        try {
			String msg = new CodeDOM(tableName,workSpace,basePackage).create();
			log.info("成功生成=====>{}",msg);
			
			return true;
		} catch (Exception e) {
			log.error("错误",e);
			
			return false;
		}
    	
    }
    
    
    
    
    
}
