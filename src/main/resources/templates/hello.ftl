package com.winning.dcg.event.collector.service.impl;

import javax.transaction.Transactional;
import ${return_full_name};
import ${feign_package}.${feign_name};
<#list method_entity as entity>
    import com.winning.dcg.event.collector.repository.${entity.repositoryName};
</#list>
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import com.winning.dcg.event.collector.service.EventCommonService;
import com.winning.dcg.event.notifier.api.dto.EventNotifierInputDTO;
import com.winning.base.akso.utils.spring.BeanMapper;

@Component
@Slf4j
public class ${service_name} implements EventCommonService {
    @Autowired
    private ${feign_name} service;
<#list method_entity as entity>
    @Autowired
    private ${entity.repositoryName} ${entity.repositoryBeanName};
</#list>

    //TODO 需要动态读取 不需要,一旦注册永不改变
    public final static Long EVENT_ID = ${event_id}L;



    @Override
    public Long getEventId() {
        return EVENT_ID;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void handleRequest(EventNotifierInputDTO dto){
        try{
                <#--JSONObject jsonObject = JSONObject.parseObject(${method_input_json});-->
                ${return_class_name} data = service.${feign_mothod}(null);
            <#list method_entity as entity>
                data.${entity.methodName}().forEach(e->{
                     ${entity.className} bean = BeanMapper.map(e, ${entity.className}.class);
                     ${entity.repositoryBeanName}.save(bean);
                });
            </#list>
            }catch(Exception e){
                throw new RuntimeException();
           }
    }

}
