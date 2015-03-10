package org.openyu.socklet.filter.service;

import java.io.IOException;

import org.openyu.socklet.message.vo.Message;
import org.openyu.socklet.reponse.vo.Response;
import org.openyu.socklet.request.vo.Request;
import org.openyu.socklet.socklet.ex.SockletException;

public interface FilterChainService
{
	@Deprecated
	/**
	 * 20120101 已不用改用Message處理request,reponse
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws SockletException
	 */
	void doFilter(Request request, Response response) throws IOException, SockletException;

	/**
	 * 20120601 改用此方式
	 * 
	 * @param request
	 * @throws IOException
	 * @throws SockletException
	 */
	void doFilter(Message request) throws IOException, SockletException;
}
