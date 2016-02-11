package org.openyu.socklet.request.vo.supporter;

import java.io.IOException;
import java.io.InputStream;

import org.openyu.commons.mark.Supporter;

public abstract class RequestSupporter extends InputStream implements Supporter
{

	public RequestSupporter()
	{}

	public int readLine(byte[] b, int off, int len) throws IOException
	{

		if (len <= 0)
		{
			return 0;
		}
		int count = 0, c;

		while ((c = read()) != -1)
		{
			b[off++] = (byte) c;
			count++;
			if (c == '\n' || count == len)
			{
				break;
			}
		}
		return count > 0 ? count : -1;
	}
}
