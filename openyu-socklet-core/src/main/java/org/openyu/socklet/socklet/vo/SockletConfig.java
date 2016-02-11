package org.openyu.socklet.socklet.vo;

import java.util.Enumeration;
import java.util.Map;
import org.openyu.commons.model.BaseModel;
import org.openyu.socklet.socklet.service.SockletService;

public interface SockletConfig extends BaseModel
{

	String KEY = SockletConfig.class.getName();

	//模組名稱
	String getId();

	void setId(String id);

	String getInitParameter(String name);

	Enumeration<String> getInitParameterNames();

	Map<String, String> getInitParameters();

	void setInitParameters(Map<String, String> initParameters);

	//
	SockletService getSockletService();

	void setSockletService(SockletService sockletService);

}
