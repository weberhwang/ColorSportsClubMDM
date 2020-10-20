/* 
 * Colors Sports Club 點名上傳批次每一列的資料
 * @author 黃郁授,吳彥儒
 * @date 2020/09/22
 */

package com.wj.clubmdm.vo;

import com.wj.clubmdm.component.BtnDelRollCallUpload;

public class RollCallUploadBatch {
	private String fileName; //檔名
	private String rollCallDate; //點名日期
	private String createTime; //建檔時間
	private BtnDelRollCallUpload btnDelBatch; //點名批次刪除按鈕
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getRollCallDate() {
		return rollCallDate;
	}
	public void setRollCallDate(String rollCallDate) {
		this.rollCallDate = rollCallDate;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public BtnDelRollCallUpload getBtnDelBatch() {
		return btnDelBatch;
	}
	public void setBtnDelBatch(BtnDelRollCallUpload btnDelBatch) {
		this.btnDelBatch = btnDelBatch;
	}
}
