package org.openyu.socklet.reponse.vo.impl;

import java.io.*;

import org.junit.Test;

import org.openyu.commons.junit.supporter.BaseTestSupporter;
import org.openyu.socklet.reponse.vo.supporter.ResponseSupporter;

public class ResponseImplTest extends BaseTestSupporter
{

	@Test
	public void print() throws IOException
	{
		ResponseStreamImpl reponse = new ResponseStreamImpl();
		reponse.println(randomString());
		reponse.println(randomChar());
		reponse.println(randomBoolean());
		//
		reponse.println(randomInt());
		reponse.println(randomLong());
		reponse.println(randomFloat());
		reponse.println(randomDouble());
		reponse.flush();

		//flush之後就沒用了
		reponse.println("after flush");
	}

	//------------------------------------------
	protected static class ResponseStreamImpl extends ResponseSupporter
	{

		BufferedWriter out;

		//ByteArrayOutputStream out;

		ResponseStreamImpl()
		{
			out = new BufferedWriter(new OutputStreamWriter(System.out));
			//out = new ByteArrayOutputStream();
		}

		public void write(int b) throws IOException
		{
			out.write(b);
		}

		public void flush() throws IOException
		{
			out.flush();
		}

		public void close() throws IOException
		{
			out.close();
		}

	}

}
