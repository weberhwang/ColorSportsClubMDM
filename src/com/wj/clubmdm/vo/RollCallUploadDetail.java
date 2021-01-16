/* 
 * Colors Sports Club 點名資料每一列的對應資料
 * @author 黃郁授,吳彥儒
 * @date 2020/09/22
 */

package com.wj.clubmdm.vo;

import com.wj.clubmdm.component.ChoiceBoxImport;
import com.wj.clubmdm.component.ChoiceBoxSpecial;

public class RollCallUploadDetail {
	private String seqNo; //序號
	private String studentNo; //學員編號
	private String rollCallTime; //點名時間
	private String name; //姓名
	private String department; //上課分部(代號)
	private String courseKind; //課程類別(代號) 
	private String level; //程度(代號)
	private String memberBelong; //成員所屬(代號)
	private String departmentDesc; //上課分部(中文)
	private String courseKindDesc; //課程類別(中文) 
	private String levelDesc; //程度(中文)	
	private ChoiceBoxSpecial cbSpecial; //特色課程(下拉選單 代號-中文)
	private ChoiceBoxImport cbImport; //是否匯入(下拉選單 Y/N)
	
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
	public ChoiceBoxSpecial getCbSpecial() {
		return cbSpecial;
	}
	public void setCbSpecial(ChoiceBoxSpecial cbSpecial) {
		this.cbSpecial = cbSpecial;
	}
	public ChoiceBoxImport getCbImport() {
		return cbImport;
	}
	public void setCbImport(ChoiceBoxImport cbImport) {
		this.cbImport = cbImport;
	}
	public String getDepartmentDesc() {
		return departmentDesc;
	}
	public void setDepartmentDesc(String departmentDesc) {
		this.departmentDesc = departmentDesc;
	}
	public String getCourseKindDesc() {
		return courseKindDesc;
	}
	public void setCourseKindDesc(String courseKindDesc) {
		this.courseKindDesc = courseKindDesc;
	}
	public String getLevelDesc() {
		return levelDesc;
	}
	public void setLevelDesc(String levelDesc) {
		this.levelDesc = levelDesc;
	}
	public String getMemberBelong() {
		return memberBelong;
	}
	public void setMemberBelong(String memberBelong) {
		this.memberBelong = memberBelong;
	}	
}
