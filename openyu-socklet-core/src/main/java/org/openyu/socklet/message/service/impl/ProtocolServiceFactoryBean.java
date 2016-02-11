package org.openyu.socklet.message.service.impl;

import org.openyu.commons.enumz.EnumHelper;
import org.openyu.commons.security.SecurityType;
import org.openyu.commons.service.supporter.BaseServiceFactoryBeanSupporter;
import org.openyu.commons.util.ChecksumType;
import org.openyu.commons.util.CompressType;
import org.openyu.socklet.message.service.ProtocolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ProtocolService工廠
 */
public final class ProtocolServiceFactoryBean<T extends ProtocolService>
		extends BaseServiceFactoryBeanSupporter<ProtocolService> {

	private static final long serialVersionUID = -549481762947318694L;

	private static final transient Logger LOGGER = LoggerFactory.getLogger(ProtocolServiceFactoryBean.class);

	/**
	 * checksum
	 */
	public static final String CHECKSUM = "checksum";

	/**
	 * 預設是否檢查碼
	 */
	public static final boolean DEFAULT_CHECKSUM = true;

	public static final String CHECKSUM_TYPE = "checksumType";

	/**
	 * 預設檢查碼類型
	 */
	public static final ChecksumType DEFAULT_CHECKSUM_TYPE = ChecksumType.CRC32;

	public static final String CHECKSUM_KEY = "checksumKey";
	/**
	 * 預設檢查碼key
	 */
	public static final String DEFAULT_CHECKSUM_KEY = "checksumKey";

	/**
	 * security
	 */
	public static final String SECURITY = "security";

	/**
	 * 預設是否加密
	 */
	public static final boolean DEFAULT_SECURITY = true;

	public static final String SECURITY_TYPE = "securityType";

	/**
	 * 預設加密類型
	 */
	public static final SecurityType DEFAULT_SECURITY_TYPE = SecurityType.AES_ECB_PKCS5Padding;

	public static final String SECURITY_KEY = "securityKey";
	/**
	 * 預設加密key
	 */
	public static final String DEFAULT_SECURITY_KEY = "securityKey";

	/**
	 * compress
	 */
	public static final String COMPRESS = "compress";

	/**
	 * 預設是否加密
	 */
	public static final boolean DEFAULT_COMPRESS = true;

	public static final String COMPRESS_TYPE = "compressType";

	/**
	 * 預設加密類型
	 */
	public static final CompressType DEFAULT_COMPRESS_TYPE = CompressType.GZIP;

	/**
	 * 所有屬性
	 */
	public static final String[] ALL_PROPERTIES = { CHECKSUM, CHECKSUM_TYPE, CHECKSUM_KEY, SECURITY, SECURITY_TYPE,
			SECURITY_KEY, COMPRESS, COMPRESS_TYPE };

	public ProtocolServiceFactoryBean() {
	}

	/**
	 * 建構
	 * 
	 * @return
	 */
	protected ProtocolService createService() throws Exception {
		ProtocolServiceImpl result = null;
		try {
			result = new ProtocolServiceImpl();
			//
			result.setApplicationContext(applicationContext);
			result.setBeanFactory(beanFactory);
			result.setResourceLoader(resourceLoader);
			//
			result.setCreateInstance(true);

			/**
			 * extendedProperties
			 */
			// checksum
			result.setChecksum(extendedProperties.getBoolean(CHECKSUM, DEFAULT_CHECKSUM));
			String checksumTypeValue = extendedProperties.getString(CHECKSUM_TYPE, DEFAULT_CHECKSUM_TYPE.name());
			ChecksumType checksumType = EnumHelper.valueOf(ChecksumType.class, checksumTypeValue);
			result.setChecksumType(checksumType);
			result.setChecksumKey(extendedProperties.getString(CHECKSUM_KEY, DEFAULT_CHECKSUM_KEY));

			// security
			result.setSecurity(extendedProperties.getBoolean(SECURITY, DEFAULT_SECURITY));
			String securityTypeValue = extendedProperties.getString(SECURITY_TYPE, DEFAULT_SECURITY_TYPE.getValue());
			SecurityType securityType = EnumHelper.valueOf(SecurityType.class, securityTypeValue);
			result.setSecurityType(securityType);
			result.setSecurityKey(extendedProperties.getString(SECURITY_KEY, DEFAULT_SECURITY_KEY));

			// compress
			result.setCompress(extendedProperties.getBoolean(COMPRESS, DEFAULT_COMPRESS));
			String compressTypeValue = extendedProperties.getString(COMPRESS_TYPE, DEFAULT_COMPRESS_TYPE.name());
			CompressType compressType = EnumHelper.valueOf(CompressType.class, compressTypeValue);
			result.setCompressType(compressType);

			/**
			 * injectiion
			 */

			// 啟動
			result.start();
		} catch (Exception e) {
			LOGGER.error(new StringBuilder("Exception encountered during createService()").toString(), e);
			try {
				result = (ProtocolServiceImpl) shutdownService();
			} catch (Exception e2) {
				throw e2;
			}
			throw e;
		}
		return result;
	}
}
