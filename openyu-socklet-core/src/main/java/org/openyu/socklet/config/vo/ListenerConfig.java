package org.openyu.socklet.config.vo;

import java.util.Enumeration;
import java.util.Map;

import org.openyu.commons.model.BaseModel;

/**
 * listener 設定
 * 
 * ContextListener
 * 
 * ContextAttributeListener
 * 
 * SessionListener
 * 
 * 共用
 */
public interface ListenerConfig extends BaseModel
{
	String KEY = ListenerConfig.class.getName();

	String getInitParameter(String name);

	Enumeration<String> getInitParameterNames();

	void setInitParameters(Map<String, String> initParameters);

	Map<String, String> getInitParameters();

}
