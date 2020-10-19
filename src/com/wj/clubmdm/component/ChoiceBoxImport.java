/* 
 * Colors Sports Club 點名資料每一列是否匯入的下拉選單，預設是要匯入
 * @author 黃郁授,吳彥儒
 * @date 2020/10/19
 */

package com.wj.clubmdm.component;

import javafx.scene.control.ChoiceBox;

public class ChoiceBoxImport extends ChoiceBox<String> {
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
