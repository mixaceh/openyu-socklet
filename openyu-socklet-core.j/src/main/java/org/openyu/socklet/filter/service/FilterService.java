package org.openyu.socklet.filter.service;

import java.io.IOException;

import org.openyu.commons.service.BaseService;
import org.openyu.socklet.filter.vo.FilterConfig;
import org.openyu.socklet.message.vo.Message;
import org.openyu.socklet.reponse.vo.Response;
import org.openyu.socklet.request.vo.Request;
import org.openyu.socklet.socklet.ex.SockletException;

public interface FilterService extends BaseService
{

	void init(FilterConfig filterConfig) throws SockletException;

	@Deprecated
	/**
	 * 20120101 已不用改用Message處理request,reponse
	 * @param request
	 * @param response
	 * @param chain
	 * @throws IOException
	 * @throws SockletException
	 */
	void doFilter(Request request, Response response, FilterChainService chain) throws IOException,
		SockletException;

	/**
	 * 20120601 改用此方式
	 * 
	 * @param request
	 * @param chain
	 * @throws IOException
	 * @throws SockletException
	 */
	void doFilter(Message request, FilterChainService chain) throws IOException, SockletException;

	void destroy();

}
