package com.jwl.spring.framework.aop;

import lombok.Getter;
import lombok.Setter;

/**
 * aop配置信息类
 * @author wenlo
 */
@Getter
@Setter
public class AopConfig {

    private String pointCut;
    private String aspectBefore;
    private String aspectAfter;
    private String aspectClass;
    private String aspectAfterThrow;
    private String aspectAfterThrowingName;

}
