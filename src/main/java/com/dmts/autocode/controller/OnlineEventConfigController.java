package com.dmts.autocode.controller;


import com.dmts.autocode.entity.OnlineEventConfig;
import com.dmts.autocode.exception.R;
import com.dmts.autocode.service.CodeGenerateService;
import com.dmts.autocode.service.IOnlineEventConfigService;
import org.apache.ibatis.javassist.compiler.CodeGen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lujieni
 * @since 2020-02-10
 */
@RestController
@RequestMapping("/online-event-config")
@CrossOrigin("*")
public class OnlineEventConfigController {
    @Autowired
    private CodeGenerateService codeGenerateService;

    @Autowired
    private IOnlineEventConfigService iOnlineEventConfigService;

    @GetMapping("/generate-code")
    public R generateCode(String id){
        return codeGenerateService.generateCode(id);
    }

    @PostMapping("/save-update")
    public R save(@RequestBody OnlineEventConfig onlineEventConfig){
         iOnlineEventConfigService.saveOrUpdate(onlineEventConfig);
         return R.ok("ok");
    }

    @PostMapping("/list")
    public R list(){
        List<OnlineEventConfig> list = iOnlineEventConfigService.list();
        return R.ok("ok").put("data",list);
    }


}
