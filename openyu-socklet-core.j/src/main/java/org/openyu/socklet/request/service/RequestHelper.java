package org.openyu.socklet.request.service;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openyu.socklet.request.vo.Request;
import org.openyu.commons.helper.supporter.BaseHelperSupporter;
import org.openyu.commons.lang.BooleanHelper;
import org.openyu.commons.lang.CharHelper;
import org.openyu.commons.lang.NumberHelper;

public class RequestHelper extends BaseHelperSupporter
{
	private static transient final Logger log = LogManager.getLogger(RequestHelper.class);

	private static RequestHelper instance;

	public RequestHelper()
	{}

	public static synchronized RequestHelper getInstance()
	{
		if (instance == null)
		{
			instance = new RequestHelper();
		}
		return instance;
	}

	public static String getParameter(Request request, String name)
	{
		return getParameter(request, name, true);
	}

	public static String getParameter(Request request, String name, boolean emptyString)
	{
		if (request != null && name != null)
		{
			String value = request.getParameter(name);
			return (value != null ? value : (emptyString ? "" : null));
		}
		return null;
	}

	public static String[] getParameterValues(Request request, String name)
	{
		return getParameterValues(request, name, true);
	}

	public static String[] getParameterValues(Request request, String name, boolean emptyString)
	{
		if (request != null && name != null)
		{
			String[] paramValues = request.getParameterValues(name);
			if (paramValues == null)
			{
				return null;
			}
			if (paramValues.length < 1)
			{
				return new String[0];
			}
			String[] values = new String[paramValues.length];
			for (int i = 0; i < paramValues.length; i++)
			{
				values[i] = (paramValues[i] != null ? paramValues[i] : (emptyString ? "" : null));
			}
			return values;
		}
		return null;
	}

	public static char getCharParameter(Request request, String name)
	{
		return getCharParameter(request, name, 0);
	}

	public static char getCharParameter(Request request, String name, int index)
	{
		return getCharParameter(request, name, index, CharHelper.DEFAULT_VALUE);
	}

	public static char getCharParameter(Request request, String name, int index, char defaultValue)
	{
		String value = getParameter(request, name);
		return CharHelper.toChar(value, index, defaultValue);
	}

	public static char[] getCharParameterValues(Request request, String name)
	{
		return getCharParameterValues(request, name, 0);
	}

	public static char[] getCharParameterValues(Request request, String name, int index)
	{
		return getCharParameterValues(request, name, index, CharHelper.DEFAULT_VALUE);
	}

	public static char[] getCharParameterValues(Request request, String name, int index,
												char defaultValue)
	{
		String[] paramValues = getParameterValues(request, name);
		if (paramValues == null)
		{
			return null;
		}
		if (paramValues.length < 1)
		{
			return new char[0];
		}
		char[] values = new char[paramValues.length];
		for (int i = 0; i < paramValues.length; i++)
		{
			values[i] = CharHelper.toChar(paramValues[i], index, defaultValue);
		}
		return values;
	}

	public static boolean getBooleanParameter(Request request, String name)
	{
		return getBooleanParameter(request, name, false);
	}

	public static boolean getBooleanParameter(Request request, String name, boolean defaultValue)
	{
		String value = getParameter(request, name);
		return BooleanHelper.toBoolean(value, defaultValue);
	}

	public static boolean[] getBooleanParameterValues(Request request, String name)
	{
		return getBooleanParameterValues(request, name, BooleanHelper.DEFAULT_VALUE);
	}

	public static boolean[] getBooleanParameterValues(Request request, String name,
														boolean defaultValue)
	{
		String[] paramValues = getParameterValues(request, name);
		if (paramValues == null)
		{
			return null;
		}
		if (paramValues.length < 1)
		{
			return new boolean[0];
		}
		boolean[] values = new boolean[paramValues.length];
		for (int i = 0; i < paramValues.length; i++)
		{
			values[i] = BooleanHelper.toBoolean(paramValues[i], defaultValue);
		}
		return values;
	}

	public static byte getByteParameter(Request request, String name)
	{
		return getByteParameter(request, name, 0);
	}

	public static byte getByteParameter(Request request, String name, int defaultValue)
	{
		String value = getParameter(request, name);
		return NumberHelper.toByte(value, defaultValue);
	}

	public static byte[] getByteParameterValues(Request request, String name, int defaultValue)
	{
		String[] paramValues = getParameterValues(request, name);
		if (paramValues == null)
		{
			return null;
		}
		if (paramValues.length < 1)
		{
			return new byte[0];
		}
		byte[] values = new byte[paramValues.length];
		for (int i = 0; i < paramValues.length; i++)
		{
			values[i] = NumberHelper.toByte(paramValues[i], defaultValue);
		}
		return values;
	}

	public static short getShortParameter(Request request, String name)
	{
		return getShortParameter(request, name, 0);
	}

	public static short getShortParameter(Request request, String name, int defaultValue)
	{
		String value = getParameter(request, name);
		return NumberHelper.toShort(value, defaultValue);
	}

	public static short[] getShortParameterValues(Request request, String name, int defaultValue)
	{
		String[] paramValues = getParameterValues(request, name);
		if (paramValues == null)
		{
			return null;
		}
		if (paramValues.length < 1)
		{
			return new short[0];
		}
		short[] values = new short[paramValues.length];
		for (int i = 0; i < paramValues.length; i++)
		{
			values[i] = NumberHelper.toShort(paramValues[i], defaultValue);
		}
		return values;
	}

	public static int getIntParameter(Request request, String name)
	{
		return getIntParameter(request, name, 0);
	}

	public static int getIntParameter(Request request, String name, int defaultValue)
	{
		String value = getParameter(request, name);
		return NumberHelper.toInt(value, defaultValue);
	}

	public static int[] getIntParameterValues(Request request, String name, int defaultValue)
	{
		String[] paramValues = getParameterValues(request, name);
		if (paramValues == null)
		{
			return null;
		}
		if (paramValues.length < 1)
		{
			return new int[0];
		}
		int[] values = new int[paramValues.length];
		for (int i = 0; i < paramValues.length; i++)
		{
			values[i] = NumberHelper.toInt(paramValues[i], defaultValue);
		}
		return values;
	}

	public static long getLongParameter(Request request, String name)
	{
		return getLongParameter(request, name, 0L);
	}

	public static long getLongParameter(Request request, String name, long defaultValue)
	{
		String value = getParameter(request, name);
		return NumberHelper.toLong(value, defaultValue);
	}

	public static long[] getLongParameterValues(Request request, String name, long defaultValue)
	{
		String[] paramValues = getParameterValues(request, name);
		if (paramValues == null)
		{
			return null;
		}
		if (paramValues.length < 1)
		{
			return new long[0];
		}
		long[] values = new long[paramValues.length];
		for (int i = 0; i < paramValues.length; i++)
		{
			values[i] = NumberHelper.toLong(paramValues[i], defaultValue);
		}
		return values;
	}

	public static float getFloatParameter(Request request, String name)
	{
		return getFloatParameter(request, name, 0f);
	}

	public static float getFloatParameter(Request request, String name, float defaultValue)
	{
		String value = getParameter(request, name);
		return NumberHelper.toFloat(value, defaultValue);
	}

	public static float[] getFloatParameterValues(Request request, String name, float defaultValue)
	{
		String[] paramValues = getParameterValues(request, name);
		if (paramValues == null)
		{
			return null;
		}
		if (paramValues.length < 1)
		{
			return new float[0];
		}
		float[] values = new float[paramValues.length];
		for (int i = 0; i < paramValues.length; i++)
		{
			values[i] = NumberHelper.toFloat(paramValues[i], defaultValue);
		}
		return values;
	}

	public static double getDoubleParameter(Request request, String name)
	{
		return getDoubleParameter(request, name, 0d);
	}

	public static double getDoubleParameter(Request request, String name, double defaultValue)
	{
		String value = getParameter(request, name);
		return NumberHelper.toDouble(value, defaultValue);
	}

	public static double[] getDoubleParameterValues(Request request, String name,
													double defaultValue)
	{
		String[] paramValues = getParameterValues(request, name);
		if (paramValues == null)
		{
			return null;
		}
		if (paramValues.length < 1)
		{
			return new double[0];
		}
		double[] values = new double[paramValues.length];
		for (int i = 0; i < paramValues.length; i++)
		{
			values[i] = NumberHelper.toDouble(paramValues[i], defaultValue);
		}
		return values;
	}

	//-------------------------------------------------------------
	public static Object getAttribute(Request request, String name)
	{
		if (request != null && name != null)
		{
			return request.getAttribute(name);
		}
		return null;
	}

	public static String getStringAttribute(Request request, String name)
	{
		return getStringAttribute(request, name, true);
	}

	public static String getStringAttribute(Request request, String name, boolean emptyString)
	{
		String value = (String) getAttribute(request, name);
		return (value != null ? value : (emptyString ? "" : null));
	}

	public static char getCharAttribute(Request request, String name)
	{
		return getCharAttribute(request, name, 0);
	}

	public static char getCharAttribute(Request request, String name, int index)
	{
		return getCharAttribute(request, name, index, CharHelper.DEFAULT_VALUE);
	}

	public static char getCharAttribute(Request request, String name, int index, char defaultValue)
	{
		return CharHelper.toChar(getStringAttribute(request, name), index, defaultValue);
	}

	public static boolean getBooleanAttribute(Request request, String name)
	{
		return getBooleanAttribute(request, name, BooleanHelper.DEFAULT_VALUE);
	}

	public static boolean getBooleanAttribute(Request request, String name, boolean defaultValue)
	{
		String value = (String) getAttribute(request, name);
		return BooleanHelper.toBoolean(value, defaultValue);
	}

	public static byte getByteAttribute(Request request, String name)
	{
		return getByteAttribute(request, name, 0);
	}

	public static byte getByteAttribute(Request request, String name, int defaultValue)
	{
		String value = (String) getAttribute(request, name);
		return NumberHelper.toByte(value, defaultValue);
	}

	public static short getShortAttribute(Request request, String name)
	{
		return getShortAttribute(request, name, 0);
	}

	public static short getShortAttribute(Request request, String name, int defaultValue)
	{
		String value = (String) getAttribute(request, name);
		return NumberHelper.toShort(value, defaultValue);
	}

	public static int getIntAttribute(Request request, String name)
	{
		return getIntAttribute(request, name, 0);
	}

	public static int getIntAttribute(Request request, String name, int defaultValue)
	{
		String value = (String) getAttribute(request, name);
		return NumberHelper.toInt(value, defaultValue);
	}

	public static long getLongAttribute(Request request, String name)
	{
		return getLongAttribute(request, name, 0L);
	}

	public static long getLongAttribute(Request request, String name, long defaultValue)
	{
		String value = (String) getAttribute(request, name);
		return NumberHelper.toLong(value, defaultValue);
	}

	public static float getFloatAttribute(Request request, String name)
	{

		return getFloatAttribute(request, name, 0f);
	}

	public static float getFloatAttribute(Request request, String name, float defaultValue)
	{
		String value = (String) getAttribute(request, name);
		return NumberHelper.toFloat(value, defaultValue);
	}

	public static double getDoubleAttribute(Request request, String name)
	{

		return getDoubleAttribute(request, name, 0d);
	}

	public static double getDoubleAttribute(Request request, String name, double defaultValue)
	{
		String value = (String) getAttribute(request, name);
		return NumberHelper.toDouble(value, defaultValue);
	}

	//-------------------------------------------------------------
	public static void setAttribute(Request request, String name, Object value)
	{
		if (value != null)
		{
			if (name != null)
			{
				request.setAttribute(name, value);
			}
		}
		else
		{
			removeAttribute(request, name);
		}
	}

	public static void removeAttribute(Request request, String name)
	{
		if (name != null)
		{
			request.removeAttribute(name);
		}
	}

	public static boolean isAttributeSpecified(Request request, String name)
	{
		return getAttribute(request, name) != null;
	}

}
