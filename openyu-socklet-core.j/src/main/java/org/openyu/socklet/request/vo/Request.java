package org.openyu.socklet.request.vo;

import java.io.*;
import java.net.Socket;
import java.util.*;

import org.openyu.commons.model.BaseModel;
import org.openyu.socklet.context.service.ContextService;
import org.openyu.socklet.request.vo.supporter.RequestSupporter;
import org.openyu.socklet.session.vo.Session;

public interface Request extends BaseModel
{

	String KEY = Request.class.getName();

	void setAttribute(String name, Object value);

	Object getAttribute(String name);

	void removeAttribute(String name);

	RequestSupporter getInputStream() throws IOException;

	//  BufferedReader getReader() throws IOException;

	String getRemoteHost();

	Session getSession();

	Session getSession(boolean create);

	String getParameter(String name);

	Enumeration getParameterNames();

	String[] getParameterValues(String name);

	Map getParameterMap();

	void setCharacterEncoding(String characterEncoding);

	String getCharacterEncoding();

	String getRemoteAddr();

	//client
	void setRemoteAddr(String remoteAddr);

	//server
	void setLocalAddr(String localAddr);

	String getLocalAddr();

	void setSocket(Socket socket);

	Socket getSocket();

	//add at 2006/08/29
	void setSockletContext(ContextService sockletContext);

	ContextService getSockletContext();

	//add at 2006/10/03

	void setInputStream(RequestSupporter inputStream) throws IOException;

	//void setReader(BufferedReader reader) throws IOException;

	//add at 2006/10/09

	void setSession(Session session);

	//add at 2006/11/01
	void setAttributes(Map attributes);

	Map getAttributes();

	void clearAttributes();

	//

	void setParameterMap(Map parameters);

	void clearParameterMap();

	//----------------------------------------------------

	int read() throws IOException;

	int read(byte b[]) throws IOException;

	int read(byte b[], int off, int len) throws IOException;

	void close() throws IOException;
}
