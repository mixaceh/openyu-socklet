package org.openyu.socklet.config.service;

import org.openyu.commons.helper.ex.HelperException;
import org.openyu.commons.helper.supporter.BaseHelperSupporter;
import org.openyu.commons.lang.BooleanHelper;
import org.openyu.commons.lang.CharHelper;
import org.openyu.commons.lang.NumberHelper;
import org.openyu.socklet.config.vo.ListenerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ListenerConfigHelper extends BaseHelperSupporter {

	private static transient final Logger LOGGER = LoggerFactory.getLogger(ListenerConfigHelper.class);

	private ListenerConfigHelper() {
		throw new HelperException(new StringBuilder().append(ListenerConfigHelper.class.getName())
				.append(" can not construct").toString());

	}

	public static String getInitParameter(ListenerConfig config, String name) {
		return getInitParameter(config, name, true);
	}

	public static String getInitParameter(ListenerConfig config, String name, boolean emptyString) {
		if (config != null && name != null) {
			String value = config.getInitParameter(name);
			return (value != null ? value : (emptyString ? "" : null));
		}
		return null;
	}

	public static char getCharInitParameter(ListenerConfig config, String name) {
		return getCharInitParameter(config, name, 0);
	}

	public static char getCharInitParameter(ListenerConfig config, String name, int index) {
		return getCharInitParameter(config, name, index, CharHelper.DEFAULT_VALUE);
	}

	public static char getCharInitParameter(ListenerConfig config, String name, int index, char defaultValue) {
		String value = getInitParameter(config, name);
		return CharHelper.toChar(value, index, defaultValue);
	}

	public static boolean getBooleanInitParameter(ListenerConfig config, String name) {
		return getBooleanInitParameter(config, name, false);
	}

	public static boolean getBooleanInitParameter(ListenerConfig config, String name, boolean defaultValue) {
		String value = getInitParameter(config, name);
		return BooleanHelper.toBoolean(value, defaultValue);
	}

	public static byte getByteInitParameter(ListenerConfig config, String name) {
		return getByteInitParameter(config, name, 0);
	}

	public static byte getByteInitParameter(ListenerConfig config, String name, int defaultValue) {
		String value = getInitParameter(config, name);
		return NumberHelper.toByte(value, defaultValue);
	}

	public static short getShortInitParameter(ListenerConfig config, String name) {
		return getShortInitParameter(config, name, 0);
	}

	public static short getShortInitParameter(ListenerConfig config, String name, int defaultValue) {
		String value = getInitParameter(config, name);
		return NumberHelper.toShort(value, defaultValue);
	}

	public static int getIntInitParameter(ListenerConfig config, String name) {
		return getIntInitParameter(config, name, 0);
	}

	public static int getIntInitParameter(ListenerConfig config, String name, int defaultValue) {
		String value = getInitParameter(config, name);
		return NumberHelper.toInt(value, defaultValue);
	}

	public static long getLongInitParameter(ListenerConfig config, String name) {
		return getLongInitParameter(config, name, 0L);
	}

	public static long getLongInitParameter(ListenerConfig config, String name, long defaultValue) {
		String value = getInitParameter(config, name);
		return NumberHelper.toLong(value, defaultValue);
	}

	public static float getFloatInitParameter(ListenerConfig config, String name) {
		return getFloatInitParameter(config, name, 0f);
	}

	public static float getFloatInitParameter(ListenerConfig config, String name, float defaultValue) {
		String value = getInitParameter(config, name);
		return NumberHelper.toFloat(value, defaultValue);
	}

	public static double getDoubleInitParameter(ListenerConfig config, String name) {
		return getDoubleInitParameter(config, name, 0d);
	}

	public static double getDoubleInitParameter(ListenerConfig config, String name, double defaultValue) {
		String value = getInitParameter(config, name);
		return NumberHelper.toDouble(value, defaultValue);
	}

}
