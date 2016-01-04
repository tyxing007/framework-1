package com.lb.framework.core.log.sensitive;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.lb.framework.core.log.CryptoConvertConfig.DefaultEmailCryptoConvertor;
import com.lb.framework.core.log.ICryptoConvertor;
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD})
public @interface SensitiveEmail {
	public Class<? extends ICryptoConvertor> convertor() default DefaultEmailCryptoConvertor.class; 
}
