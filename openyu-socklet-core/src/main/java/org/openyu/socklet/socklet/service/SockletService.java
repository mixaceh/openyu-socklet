package org.openyu.socklet.socklet.service;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

import org.openyu.commons.service.BaseService;
import org.openyu.socklet.message.vo.Message;
import org.openyu.socklet.reponse.vo.Response;
import org.openyu.socklet.request.vo.Request;
import org.openyu.socklet.socklet.ex.SockletException;
import org.openyu.socklet.socklet.vo.SockletConfig;

/**
 * socket的小程式,此為控制器
 * 
 * 邏輯處理,會放在xxxService中
 */
public interface SockletService extends BaseService {

	/**
	 * 模組id
	 * 
	 * @return
	 */
	String getId();

	void setId(String id);

	/**
	 * 初始化
	 * 
	 * @param sockletConfig
	 * @throws SockletException
	 */
	void init(SockletConfig sockletConfig) throws SockletException;

	/**
	 * 取得socklet設定
	 * 
	 * @return
	 */
	SockletConfig getSockletConfig();

	void setSockletConfig(SockletConfig sockletConfig);

	/**
	 * 取得初始化參數
	 * 
	 * @param name
	 * @return
	 */
	String getInitParameter(String name);

	Enumeration<String> getInitParameterNames();

	Map<String, String> getInitParameters();

	void setInitParameters(Map<String, String> initParameters);

	/**
	 * 註冊的acceptor id, ex:master,slave1
	 * 
	 * @return
	 */
	Set<String> getAcceptors();

	void setAcceptors(Set<String> acceptors);

	@Deprecated
	/**
	 * 服務
	 * 
	 * 2012/01/01 已不用改用message,取代request,reponse
	 * 
	 * replaced by service(Message message)
	 * 
	 * @param request
	 * @param response
	 * @throws SockletException
	 * @throws IOException
	 */
	void service(Request request, Response response) throws SockletException, IOException;

	/**
	 * 服務
	 * 
	 * 2012/01/01 改用此方式,取代舊有的request,reponse
	 * 
	 * @param message
	 */
	void service(Message message);

	/**
	 * 加入訊息
	 * 
	 * @param message
	 * @return
	 */
	boolean addMessage(Message message);
}
