package org.openyu.socklet.message.vo;

import org.openyu.commons.enumz.ByteEnum;
import org.openyu.commons.lang.ByteHelper;

/**
 * 訊息種類類別
 */
public enum CategoryType implements ByteEnum {
	/**
	 * 握手協定-tcp, client->server
	 */
	HANDSHAKE_CLIENT((byte) 1) {
		public byte[] toByteArray() {
			if (byteArray == null) {
				byteArray = ByteHelper.toByteArray(getValue());
			}
			return byteArray;
		}
	},

	/**
	 * 握手協定-tcp, relation->server
	 */
	HANDSHAKE_RELATION((byte) 2) {
		public byte[] toByteArray() {
			if (byteArray == null) {
				byteArray = ByteHelper.toByteArray(getValue());
			}
			return byteArray;
		}
	},

	/**
	 * 握手協定-tcp, server->client, server->relation
	 */
	HANDSHAKE_SERVER((byte) 3) {
		public byte[] toByteArray() {
			if (byteArray == null) {
				byteArray = ByteHelper.toByteArray(getValue());
			}
			return byteArray;
		}
	},

	/**
	 * 訊息協定-tcp, client->server
	 */
	MESSAGE_CLIENT((byte) 11) {
		public byte[] toByteArray() {
			if (byteArray == null) {
				byteArray = ByteHelper.toByteArray(getValue());
			}
			return byteArray;
		}
	},

	/**
	 * 訊息協定-tcp, server->relation, receive
	 */
	MESSAGE_RELATION((byte) 12) {
		public byte[] toByteArray() {
			if (byteArray == null) {
				byteArray = ByteHelper.toByteArray(getValue());
			}
			return byteArray;
		}
	},

	/**
	 * 訊息協定-tcp, server->client, server->server
	 */
	MESSAGE_SERVER((byte) 13) {
		public byte[] toByteArray() {
			if (byteArray == null) {
				byteArray = ByteHelper.toByteArray(getValue());
			}
			return byteArray;
		}
	},

	/**
	 * 訊息協定-tcp, client->server, server->server 不立即處理, 而是放到queue中, 類似mq
	 */
	MESSAGE_QUEUE((byte) 14) {
		public byte[] toByteArray() {
			if (byteArray == null) {
				byteArray = ByteHelper.toByteArray(getValue());
			}
			return byteArray;
		}
	},

	/**
	 * 訊息協定內部用-udp, server->relation,發送用
	 * 
	 * 此為特別協定,只有在acceptor中使用,不開放外部使用
	 */
	MESSAGE_ACCEPTOR((byte) 15) {
		public byte[] toByteArray() {
			if (byteArray == null) {
				byteArray = ByteHelper.toByteArray(getValue());
			}
			return byteArray;
		}
	},

	/**
	 * 訊息協定邏輯用-udp, server->relation,發送用
	 */
	MESSAGE_SYNC((byte) 16) {
		public byte[] toByteArray() {
			if (byteArray == null) {
				byteArray = ByteHelper.toByteArray(getValue());
			}
			return byteArray;
		}
	},

	/**
	 * 
	 */
	KEEP_ALIVE_CLIENT((byte) 31) {
		public byte[] toByteArray() {
			if (byteArray == null) {
				byteArray = ByteHelper.toByteArray(getValue());
			}
			return byteArray;
		}
	},

	/**
	 * 
	 */
	KEEP_ALIVE_RELATIOM((byte) 32) {
		public byte[] toByteArray() {
			if (byteArray == null) {
				byteArray = ByteHelper.toByteArray(getValue());
			}
			return byteArray;
		}
	},

	/**
	 * 
	 */
	KEEP_ALIVE_SERVER((byte) 33) {
		public byte[] toByteArray() {
			if (byteArray == null) {
				byteArray = ByteHelper.toByteArray(getValue());
			}
			return byteArray;
		}
	},

	/**
	 * 檔案協定, tcp
	 */
	FILE_CLIENT((byte) 41) {
		public byte[] toByteArray() {
			if (byteArray == null) {
				byteArray = ByteHelper.toByteArray(getValue());
			}
			return byteArray;
		}
	},

	/**
	 * 檔案協定, tcp
	 */
	FILE_RELATIOM((byte) 42) {
		public byte[] toByteArray() {
			if (byteArray == null) {
				byteArray = ByteHelper.toByteArray(getValue());
			}
			return byteArray;
		}
	},

	/**
	 * 檔案協定, tcp
	 */
	FILE_SERVER((byte) 43) {
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

	private CategoryType(byte value) {
		this.value = value;
	}

	public byte getValue() {
		return value;
	}

	public abstract byte[] toByteArray();
}
