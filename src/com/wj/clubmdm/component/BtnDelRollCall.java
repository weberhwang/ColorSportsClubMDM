/* 
 * Colors Sports Club 點名資料刪除按鈕
 * @author 黃郁授,吳彥儒
 * @date 2020/10/31
 */

package com.wj.clubmdm.component;

import javafx.scene.control.Button;

public class BtnDelRollCall extends Button {
	private String studentNo; //學員編號
	private String rollCallTime; //點名時間
	public String getStudentNo() {
		return studentNo;
	}
	public void setStudentNo(String studentNo) {
		this.studentNo = studentNo;
	}
	public String getRollCallTime() {
		return rollCallTime;
	}
	public void setRollCallTime(String rollCallTime) {
		this.rollCallTime = rollCallTime;
	}
}
