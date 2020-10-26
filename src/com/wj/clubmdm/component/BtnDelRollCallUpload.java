/* 
 * Colors Sports Club 上傳點名批次的刪除按鈕
 * @author 黃郁授,吳彥儒
 * @date 2020/10/20
 */

package com.wj.clubmdm.component;

import javafx.scene.control.Button;

public class BtnDelRollCallUpload extends Button {
	private String fileName; //檔案名稱

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
