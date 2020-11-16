/* 
 * Colors Sports Club 點名資料每一列的對應資料
 * @author 黃郁授,吳彥儒
 * @date 2020/10/30
 */

package com.wj.clubmdm.vo;

import com.wj.clubmdm.component.BtnDelRollCall;
import com.wj.clubmdm.component.BtnUpdateRollCall;

public class RollCallDetail {
	private String seqNo; //序號
	private String studentNo; //學員編號
	private String name; //姓名
	private String department; //上課分部
	private String courseKind; //課程類別 
	private String level; //程度
	private String rollCallTime; //點名時間
	private String special; //特色課程
	private String memberBelong; //成員所屬	
	private String memberBelongDesc; //成員所屬中文說明
	private BtnUpdateRollCall btnUpdate; //更新按鈕
	private BtnDelRollCall btnDelete; //刪除按鈕
	public String getSeqNo() {
		return seqNo;
	}
	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}
	public String getStudentNo() {
		return studentNo;
	}
	public void setStudentNo(String studentNo) {
		this.studentNo = studentNo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getCourseKind() {
		return courseKind;
	}
	public void setCourseKind(String courseKind) {
		this.courseKind = courseKind;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getRollCallTime() {
		return rollCallTime;
	}
	public void setRollCallTime(String rollCallTime) {
		this.rollCallTime = rollCallTime;
	}
	public String getSpecial() {
		return special;
	}
	public void setSpecial(String special) {
		this.special = special;
	}
	public BtnUpdateRollCall getBtnUpdate() {
		return btnUpdate;
	}
	public void setBtnUpdate(BtnUpdateRollCall btnUpdate) {
		this.btnUpdate = btnUpdate;
	}
	public BtnDelRollCall getBtnDelete() {
		return btnDelete;
	}
	public void setBtnDelete(BtnDelRollCall btnDelete) {
		this.btnDelete = btnDelete;
	}
	public String getMemberBelong() {
		return memberBelong;
	}
	public void setMemberBelong(String memberBelong) {
		this.memberBelong = memberBelong;
		this.memberBelongDesc = "";
		if (memberBelong.substring(0, 1).equalsIgnoreCase("1")) {
			this.memberBelongDesc += "冬夏令營,";
		}
		if (memberBelong.substring(1, 2).equalsIgnoreCase("1")) {
			this.memberBelongDesc += "學校社團,";
		}
		if (memberBelong.substring(2, 3).equalsIgnoreCase("1")) {
			this.memberBelongDesc += "俱樂部,";
		}
		//若是以逗號結尾，把最後一個逗號移除
		if (this.memberBelongDesc.endsWith(",")) {
			this.memberBelongDesc = this.memberBelongDesc.substring(0, this.memberBelongDesc.length() - 1);
		}
	}
	public String getMemberBelongDesc() {
		return memberBelongDesc;
	}
	public void setMemberBelongDesc(String memberBelongDesc) {
		this.memberBelongDesc = memberBelongDesc;
	}
	
}
