package org.openyu.socklet.session.service;

import org.openyu.commons.helper.ex.HelperException;
import org.openyu.commons.helper.supporter.BaseHelperSupporter;
import org.openyu.commons.lang.BooleanHelper;
import org.openyu.commons.lang.CharHelper;
import org.openyu.commons.lang.NumberHelper;
import org.openyu.socklet.session.vo.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SessionHelper extends BaseHelperSupporter {

	private static final transient Logger LOGGER = LoggerFactory.getLogger(SessionHelper.class);

	private SessionHelper() {
		throw new HelperException(
				new StringBuilder().append(SessionHelper.class.getName()).append(" can not construct").toString());
	}

	public static Object getAttribute(Session session, String name) {
		if (session != null && name != null) {
			return session.getAttribute(name);
		}
		return null;
	}

	public static String getStringAttribute(Session session, String name) {
		return getStringAttribute(session, name, true);
	}

	public static String getStringAttribute(Session session, String name, boolean emptyString) {
		String value = (String) getAttribute(session, name);
		return (value != null ? value : (emptyString ? "" : null));
	}

	public static char getCharAttribute(Session session, String name) {
		return getCharAttribute(session, name, 0);
	}

	public static char getCharAttribute(Session session, String name, int index) {
		return getCharAttribute(session, name, index, CharHelper.DEFAULT_VALUE);
	}

	public static char getCharAttribute(Session session, String name, int index, char defaultValue) {
		return CharHelper.toChar(getStringAttribute(session, name), index, defaultValue);
	}

	public static boolean getBooleanAttribute(Session session, String name) {
		return getBooleanAttribute(session, name, BooleanHelper.DEFAULT_VALUE);
	}

	public static boolean getBooleanAttribute(Session session, String name, boolean defaultValue) {
		return BooleanHelper.toBoolean(getStringAttribute(session, name), defaultValue);
	}

	public static byte getByteAttribute(Session session, String name) {
		return getByteAttribute(session, name, NumberHelper.DEFAULT_BYTE);
	}

	public static byte getByteAttribute(Session session, String name, int defaultValue) {
		return NumberHelper.toByte(getStringAttribute(session, name), defaultValue);
	}

	public static short getShortAttribute(Session session, String name) {
		return getShortAttribute(session, name, NumberHelper.DEFAULT_SHORT);
	}

	public static short getShortAttribute(Session session, String name, int defaultValue) {
		return NumberHelper.toShort(getStringAttribute(session, name), defaultValue);
	}

	public static int getIntAttribute(Session session, String name) {
		return getIntAttribute(session, name, NumberHelper.DEFAULT_INT);
	}

	public static int getIntAttribute(Session session, String name, int defaultValue) {
		return NumberHelper.toInt(getStringAttribute(session, name), defaultValue);
	}

	public static long getLongAttribute(Session session, String name) {
		return getLongAttribute(session, name, NumberHelper.DEFAULT_LONG);
	}

	public static long getLongAttribute(Session session, String name, long defaultValue) {
		return NumberHelper.toLong(getStringAttribute(session, name), defaultValue);
	}

	public static float getFloatAttribute(Session session, String name) {

		return getFloatAttribute(session, name, NumberHelper.DEFAULT_FLOAT);
	}

	public static float getFloatAttribute(Session session, String name, float defaultValue) {
		return NumberHelper.toFloat(getStringAttribute(session, name), defaultValue);
	}

	public static double getDoubleAttribute(Session session, String name) {

		return getDoubleAttribute(session, name, NumberHelper.DEFAULT_DOUBLE);
	}

	public static double getDoubleAttribute(Session session, String name, double defaultValue) {
		return NumberHelper.toDouble(getStringAttribute(session, name), defaultValue);
	}

	// -------------------------------------------------------------
	public static void setAttribute(Session session, String name, Object value) {
		if (session != null && name != null) {
			synchronized (session) {
				session.setAttribute(name, value);
			}
		}
		/*
		 * if (value != null) { if (name != null) { synchronized (session) {
		 * session.setAttribute(name, value); } } } else {
		 * removeAttribute(session, name); }
		 */
	}

	public static void removeAttribute(Session session, String name) {
		if (session != null && name != null) {
			synchronized (session) {
				session.removeAttribute(name);
			}
		}
	}

	public static boolean isAttributeSpecified(Session session, String name) {
		return getAttribute(session, name) != null;
	}

}
