package org.openyu.socklet.reponse.vo.supporter;


import java.io.IOException;
import java.io.OutputStream;

import org.openyu.commons.mark.Supporter;

public abstract class ResponseSupporter extends OutputStream implements Supporter
{

//	private static final String BASE_NAME = "org.openyu.socklet.core.resources.i18n.Messages";
//
//	private static ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME);

	public ResponseSupporter()
	{}

	public void print(String s) throws IOException
	{
		if (s == null)
			s = "null";
		int len = s.length();
		for (int i = 0; i < len; i++)
		{
			char c = s.charAt(i);
			write(c);
		}
	}

	public void print(boolean b) throws IOException
	{
		//		String msg;
		//		if (b)
		//		{
		//			msg = bundle.getString("value.true");
		//		}
		//		else
		//		{
		//			msg = bundle.getString("value.false");
		//		}
		print(String.valueOf(b));
	}

	public void print(char c) throws IOException
	{
		print(String.valueOf(c));
	}

	public void print(int i) throws IOException
	{
		print(String.valueOf(i));
	}

	public void print(long l) throws IOException
	{
		print(String.valueOf(l));
	}

	public void print(float f) throws IOException
	{
		print(String.valueOf(f));
	}

	public void print(double d) throws IOException
	{
		print(String.valueOf(d));
	}

	public void println() throws IOException
	{
		print("\r\n");
	}

	public void println(String s) throws IOException
	{
		print(s);
		println();
	}

	public void println(boolean b) throws IOException
	{
		print(b);
		println();
	}

	public void println(char c) throws IOException
	{
		print(c);
		println();
	}

	public void println(int i) throws IOException
	{
		print(i);
		println();
	}

	public void println(long l) throws IOException
	{
		print(l);
		println();
	}

	public void println(float f) throws IOException
	{
		print(f);
		println();
	}

	public void println(double d) throws IOException
	{
		print(d);
		println();
	}
}
