package com.dmts.autocode.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author lujieni
 * @since 2020-02-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
//@TableName("ODS.ONLINE_EVENT_CONFIG")
public class OnlineEventConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建日期
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新人
     */
    private String updateBy;


    /**
     * 更新日期
     */
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    /**
     * 事件id号
     */
    private String eventId;

    /**
     * feign方法
     */
    private String feignMethod;

    /**
     * feign的名称
     */
    private String feignName;

    /**
     * feign的包名
     */
    private String feignPackage;

    /**
     * service的名称
     */
    private String serviceName;

    /**
     * 输出文件的地址
     */
    private String outputPath;

    /**
     *入参格式
     */
    private String methodInputJson;


    /**
     * 包名
     */
    private String servicePackage;

    /**
     * 域类型
     */
    private String domainType;




}
