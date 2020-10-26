/* 
 * Colors Sports Club 點名資料每一列使用的特色課程的下拉選單
 * @author 黃郁授,吳彥儒
 * @date 2020/10/19
 */

package com.wj.clubmdm.component;

import javafx.scene.control.ChoiceBox;

public class ChoiceBoxSpecial extends ChoiceBox<String> {
	private String rollCallTime;
	private String studentNo;
	public String getRollCallTime() {
		return rollCallTime;
	}
	public void setRollCallTime(String rollCallTime) {
		this.rollCallTime = rollCallTime;
	}
	public String getStudentNo() {
		return studentNo;
	}
	public void setStudentNo(String studentNo) {
		this.studentNo = studentNo;
	}
}
