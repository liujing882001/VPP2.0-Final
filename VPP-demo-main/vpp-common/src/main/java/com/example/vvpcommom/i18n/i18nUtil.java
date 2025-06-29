package com.example.vvpcommom.i18n;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class i18nUtil {

	private static MessageSource messageSource;

	public i18nUtil(MessageSource messageSource) {
		i18nUtil.messageSource = messageSource;
	}

	// 根据当前语言环境获取本地化消息
	public static String getMessage(String code) {
		Locale locale = LocaleContextHolder.getLocale();  // 获取当前上下文语言环境
		return messageSource.getMessage(code, null, locale);
	}

	// 根据指定的语言环境获取本地化消息
	public static String getMessage(String code, Locale locale) {
		return messageSource.getMessage(code, null, locale);
	}

	// 带有参数的消息获取
	public static String getMessage(String code, Object[] args) {
		Locale locale = LocaleContextHolder.getLocale();
		return messageSource.getMessage(code, args, locale);
	}
}
