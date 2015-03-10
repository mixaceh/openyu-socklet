package org.openyu.socklet.message.vo;

import org.openyu.commons.model.BaseModel;

/**
 * cluster訊息
 */
public interface ClusterMessage extends BaseModel {

	/**
	 * id, 唯一碼
	 * 
	 * use UUID.randomUUID()
	 * 
	 * @return
	 */
	String getId();

	void setId(String id);

	/**
	 * 時間戳
	 * 
	 * @return
	 */
	long getTimeStamp();

	public CategoryType getCategoryType();

	public void setCategoryType(CategoryType categoryType);

	byte[] getBody();

	void setBody(byte[] body);
}
