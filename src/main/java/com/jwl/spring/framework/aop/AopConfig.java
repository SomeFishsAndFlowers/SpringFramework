package com.jwl.spring.framework.aop;

import lombok.Data;

/**
 * aop配置信息类
 * @author wenlo
 */
@Data
public class AopConfig {

    private String pointCut;
    private String aspectBefore;
    private String aspectAfter;
    private String aspectClass;
    private String aspectAfterThrow;
    private String aspectAfterThrowingName;

}
