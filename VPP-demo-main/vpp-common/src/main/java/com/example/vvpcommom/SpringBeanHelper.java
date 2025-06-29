package com.example.vvpcommom;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringBeanHelper<T> implements ApplicationContextAware {
	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringBeanHelper.applicationContext = applicationContext;
	}

	public static <T> T getBean(Class<T> clazz) {
		return applicationContext == null ? null : applicationContext.getBean(clazz);
	}

	public static <T> T getBeanOrThrow(Class<T> clazz) {
		if (applicationContext == null) {
			throw new RuntimeException("get application context failed");
		}
		return applicationContext.getBean(clazz);
	}
}