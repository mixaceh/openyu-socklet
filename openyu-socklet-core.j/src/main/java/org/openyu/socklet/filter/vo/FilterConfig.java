package org.openyu.socklet.filter.vo;

import java.util.Enumeration;
import java.util.Map;
import org.openyu.commons.service.BaseService;

public interface FilterConfig extends BaseService
{

	//模組名稱
	String getId();

	void setId(String id);

	String getInitParameter(String name);

	Enumeration<String> getInitParameterNames();

	void setInitParameters(Map<String, String> initParameters);

	Map<String, String> getInitParameters();

}
