/* 
 * 用於產出Excel中，出勤統計的VO類別
 * @author 黃郁授,吳彥儒
 * @date 2021/03/06
 */

package com.wj.clubmdm.vo;

public class StudentAttendance extends Student {
	private int attendanceCount = 0;      //出勤西元年月的次數
	private String attendanceYYYYMM = ""; //出勤西元年月
	
	public int getAttendanceCount() {
		return attendanceCount;
	}
	public void setAttendanceCount(int attendanceCount) {
		this.attendanceCount = attendanceCount;
	}
	public String getAttendanceYYYYMM() {
		return attendanceYYYYMM;
	}
	public void setAttendanceYYYYMM(String attendanceYYYYMM) {
		this.attendanceYYYYMM = attendanceYYYYMM;
	}
}
