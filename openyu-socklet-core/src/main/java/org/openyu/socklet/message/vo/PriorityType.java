package org.openyu.socklet.message.vo;

import org.openyu.commons.enumz.ByteEnum;
import org.openyu.commons.lang.ByteHelper;

/**
 * 優先權類別
 */
public enum PriorityType implements ByteEnum {

	URGENT((byte) 1) {
		public byte[] toByteArray() {
			if (byteArray == null) {
				byteArray = ByteHelper.toByteArray(getValue());
			}
			return byteArray;
		}
	},

	HIGH((byte) 2) {
		public byte[] toByteArray() {
			if (byteArray == null) {
				byteArray = ByteHelper.toByteArray(getValue());
			}
			return byteArray;
		}
	},

	MEDIUM((byte) 3) {
		public byte[] toByteArray() {
			if (byteArray == null) {
				byteArray = ByteHelper.toByteArray(getValue());
			}
			return byteArray;
		}
	},

	LOW((byte) 4) {
		public byte[] toByteArray() {
			if (byteArray == null) {
				byteArray = ByteHelper.toByteArray(getValue());
			}
			return byteArray;
		}
	},

	_5((byte) 5) {
		public byte[] toByteArray() {
			if (byteArray == null) {
				byteArray = new byte[] { getValue() };
			}
			return byteArray;
		}
	},

	_6((byte) 6) {
		public byte[] toByteArray() {
			if (byteArray == null) {
				byteArray = new byte[] { getValue() };
			}
			return byteArray;
		}
	},

	_7((byte) 7) {
		public byte[] toByteArray() {
			if (byteArray == null) {
				byteArray = new byte[] { getValue() };
			}
			return byteArray;
		}
	},

	_8((byte) 8) {
		public byte[] toByteArray() {
			if (byteArray == null) {
				byteArray = new byte[] { getValue() };
			}
			return byteArray;
		}
	},

	_9((byte) 9) {
		public byte[] toByteArray() {
			if (byteArray == null) {
				byteArray = new byte[] { getValue() };
			}
			return byteArray;
		}
	},

	_10((byte) 10) {
		public byte[] toByteArray() {
			if (byteArray == null) {
				byteArray = new byte[] { getValue() };
			}
			return byteArray;
		}
	},

	CARELESS(Byte.MAX_VALUE) {
		public byte[] toByteArray() {
			if (byteArray == null) {
				byteArray = ByteHelper.toByteArray(getValue());
			}
			return byteArray;
		}
	},

	;

	private final byte value;

	protected byte[] byteArray;

	private PriorityType(byte value) {
		this.value = value;
	}

	public byte getValue() {
		return value;
	}

	public abstract byte[] toByteArray();
}
