package com.example.vvpcommom;

import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;

public class JsonConfigUtil {

	static SerializeConfig config = new SerializeConfig();

	public static SerializeConfig getSnakeCase(){
		config.setPropertyNamingStrategy(PropertyNamingStrategy.SnakeCase);
		return config;
	}

	public SerializeConfig getCamelCase(){
		config.setPropertyNamingStrategy(PropertyNamingStrategy.CamelCase);
		return config;
	}
}
