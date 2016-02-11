package org.openyu.socklet.reponse.vo;

import java.io.*;
import java.net.Socket;

import org.openyu.commons.model.BaseModel;
import org.openyu.socklet.context.service.ContextService;
import org.openyu.socklet.reponse.vo.supporter.ResponseSupporter;

public interface Response extends BaseModel
{

	String KEY = Response.class.getName();

	ResponseSupporter getOutputStream() throws IOException;

	//  BufferedWriter getWriter() throws IOException;

	void setCharacterEncoding(String characterEncoding);

	String getCharacterEncoding();

	//
	void setSocket(Socket socket);

	Socket getSocket();

	//add at 2006/10/03

	void setOutputStream(ResponseSupporter outputStream) throws IOException;

	//  void setWriter(BufferedWriter writer) throws IOException;

	//add at 2006/11/01
	void setSockletContext(ContextService sockletContext);

	ContextService getSockletContext();

	//----------------------------------------------------

	void write(int b) throws IOException;

	void write(byte b[]) throws IOException;

	void write(byte b[], int off, int len) throws IOException;

	void flush() throws IOException;

	void close() throws IOException;

	//----------------------------------------------------

	void write(String str) throws IOException;
}
