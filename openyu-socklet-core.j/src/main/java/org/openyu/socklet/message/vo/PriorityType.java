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

	CARELESS((byte) 5) {
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
