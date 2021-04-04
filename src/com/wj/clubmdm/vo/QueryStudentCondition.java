/* 
 * Colors Sports Club MDM 查詢學員的條件類別(做為參數使用)
 * 
 * @author 黃郁授,吳彥儒
 * @date 2021/03/22
 */

package com.wj.clubmdm.vo;

public class QueryStudentCondition {
	
	private String studentNo; //學員編號
	private String name; //學員姓名(在SQL中採用like)
	private String sex; //姓別
	private String school; //就讀學校
	private int schoolLevel; //學齡
	private String status; //狀態 N正式 S停課 L退遂
	private String memberBelong; //成員所屬
	private String department; //上課分部
	private String courseKind; //課程類別
	private String level; //程度
	private String joinDateStr; //入隊日(起)
	private String joinDateEnd; //入隊日(迄)	

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

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public int getSchoolLevel() {
		return schoolLevel;
	}

	public void setSchoolLevel(int schoolLevel) {
		this.schoolLevel = schoolLevel;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMemberBelong() {
		return memberBelong;
	}

	public void setMemberBelong(String memberBelong) {
		this.memberBelong = memberBelong;
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

	public String getJoinDateStr() {
		return joinDateStr;
	}

	public void setJoinDateStr(String joinDateStr) {
		this.joinDateStr = joinDateStr;
	}

	public String getJoinDateEnd() {
		return joinDateEnd;
	}

	public void setJoinDateEnd(String joinDateEnd) {
		this.joinDateEnd = joinDateEnd;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
