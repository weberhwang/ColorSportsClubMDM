package com.wj.clubmdm.vo;

public class StudyAge {
	private String studentNo; //學員編號
	private Integer schoolLevel; //當時填寫的學齡
	private String confirmDate; //當時填寫學齡的西元年月日8碼
	private Integer oldEstSchoolAge; //舊的估算學齡
	private Integer newEstSchoolAge; //估計後的現在學齡

	public String getStudentNo() {
		return studentNo;
	}
	public void setStudentNo(String studentNo) {
		this.studentNo = studentNo;
	}
	public Integer getSchoolLevel() {
		return schoolLevel;
	}
	public void setSchoolLevel(Integer schoolLevel) {
		this.schoolLevel = schoolLevel;
	}
	public String getConfirmDate() {
		return confirmDate;
	}
	public void setConfirmDate(String confirmDate) {
		this.confirmDate = confirmDate;
	}
	public Integer getOldEstSchoolAge() {
		return oldEstSchoolAge;
	}
	public void setOldEstSchoolAge(Integer oldEstSchoolAge) {
		this.oldEstSchoolAge = oldEstSchoolAge;
	}
	public Integer getNewEstSchoolAge() {
		return newEstSchoolAge;
	}
	public void setNewEstSchoolAge(Integer newEstSchoolAge) {
		this.newEstSchoolAge = newEstSchoolAge;
	}
}
