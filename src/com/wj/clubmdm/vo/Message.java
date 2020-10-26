/* 
 * Colors Sports Club 訊息TableView每一列的資料
 * @author 黃郁授,吳彥儒
 * @date 2020/10/21
 */
package com.wj.clubmdm.vo;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
	private String msgTime;
	private String msgContent;
	public Message() {
		super();
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss:SSS");
		this.msgTime = sdf.format(date);
	}
	
	public String getMsgContent() {
		return msgContent;
	}
	
	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}
	
	public String getMsgTime() {
		return msgTime;
	}
}
