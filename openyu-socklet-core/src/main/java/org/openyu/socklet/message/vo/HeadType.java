package org.openyu.socklet.message.vo;

import org.openyu.commons.enumz.IntEnum;
import org.openyu.commons.lang.ByteHelper;

public enum HeadType implements IntEnum {

	HANDSHAKE(1) {
		public byte[] toByteArray() {
			if (byteArray == null) {
				byteArray = ByteHelper.toByteArray(getValue());
			}
			return byteArray;
		}
	},

	MESSAGE(11) {
		public byte[] toByteArray() {
			if (byteArray == null) {
				byteArray = ByteHelper.toByteArray(getValue());
			}
			return byteArray;
		}
	},

	KEEP_ALIVE(31) {
		public byte[] toByteArray() {
			if (byteArray == null) {
				byteArray = ByteHelper.toByteArray(getValue());
			}
			return byteArray;
		}
	},

	FILE(41) {
		public byte[] toByteArray() {
			if (byteArray == null) {
				byteArray = ByteHelper.toByteArray(getValue());
			}
			return byteArray;
		}
	},

	//
	;

	private final int value;

	protected byte[] byteArray;

	private HeadType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public abstract byte[] toByteArray();
}
