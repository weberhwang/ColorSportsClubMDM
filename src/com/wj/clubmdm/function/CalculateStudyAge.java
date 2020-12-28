/* 
 * Colors Sports Club 學齡計算
 * @author 黃郁授,吳彥儒
 * @date 2020/12/27
 */

package com.wj.clubmdm.function;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Set;
import java.util.TreeMap;

import com.wj.clubmdm.vo.StudyAge;

import rhinoceros.util.date.SystemTime;
import rhinoceros.util.db.DBConnectionFactory;

public class CalculateStudyAge {

	/*
	 * 必要條件
	 * 1.Student在新增時，
	 *   SchoolLevel須鍵入學員當下的學歷，
	 *   SchoolConfirmDate當下的系統日期
	 *   SchoolLevelEstimate估計學齡，請填入null
	 * 2.StudentLevel如有異動，才可以去變更SchoolConfirmDate
	 * 
	 * 計算方式
	 * Step1. 先讀出 Student 中 SchoolLevel 在 StudyAge 裡面 Calculate 的值為'Y' 的資料
	 * Step2. SchoolLevel, SchoolConfirmDate 丟給 calculateStudyAge 計算 (會回傳Integer 新的估計學歷)
	 * Step3. calculateStudyAge 的計算邏輯如下
	 *        找出 StudyCalendar 中 StudyCalendar.StudyDate > Student.SchoolConfirmDate and 系統時間 >= StudyCalendar.StudyDate desc StudyDate，中最大的一筆
	 *        若找不到資料，則代表該學齡本次無須異動
	 *        若有找到，先計算有找到幾筆，若有4筆，代表該學員的學齡要+4年
	 *        然後以新的學齡更新舊的Student資料。  
	 */
	
	//用來暫存須要計算的學員資料
	private TreeMap<String, StudyAge> studentAges = new TreeMap<String, StudyAge>();
	
	/*
	 * @param schoolLevel 學員填寫時的學歷
	 * @param schoolConfirmDate 學員填寫學歷的覆核日期
	 * @return 新的學歷估計值，若回傳時為-1，代表估算學歷失敗，不可拿來修改預估學歷
	 */
	public int calculateStudyAge(Integer schoolLevel, String schoolConfirmDate) {
		int estLevel = -1; //預估學歷預設為-1，若回傳
		SystemTime st = new SystemTime(); 
		String sysdate = st.getNowTime("yyyyMMdd"); //取得系統日期

		String sql = "SELECT count(*) cnt FROM StudyCalendar WHERE StudyDate > ? and ? >= StudyDate";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {	
			DBConnectionFactory dbf = new DBConnectionFactory();
			conn = dbf.getSQLiteCon("", "Club.dll");
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, schoolConfirmDate);
			pstmt.setString(2, sysdate);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				estLevel = schoolLevel + rs.getInt("cnt");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
		
		return estLevel;
	}
	
	/*
	 * 找出須要估算的學員資料
	 */
	public void findStudent() {
		//取學員基本資料(只抓取當時紀錄)
		String sql = 
				"SELECT " +
				"  a.StudentNo," + 
				"  a.SchoolLevel," +
				"  a.SchoolConfirmDate," +
				"  ifnull(a.SchoolLevelEstimate, 0) SchoolLevelEstimate " +
				"FROM " + 
				"  Student a" +
				"  left join StudyAge b on a.SchoolLevel = b.StudyAgeCode " +
				"WHERE " + 
				"  b.calculate = 'Y'";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {	
			DBConnectionFactory dbf = new DBConnectionFactory();
			conn = dbf.getSQLiteCon("", "Club.dll");
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			StudyAge sa = null;
			while (rs.next()) {
				sa = new StudyAge();
				sa.setStudentNo(rs.getString("StudentNo"));
				sa.setSchoolLevel(rs.getInt("SchoolLevel"));
				sa.setConfirmDate(rs.getString("SchoolConfirmDate"));
				sa.setOldEstSchoolAge(rs.getInt("SchoolLevelEstimate"));
				studentAges.put(rs.getString("StudentNo"), sa);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}			
	}
	
	//主程序
	public void run() {
		findStudent(); //取得須要估算學齡的學員資料
		//逐一跑迴圈
		Set<String> keys = studentAges.keySet();
		StudyAge sa = null;
		Integer newStudyAge = null;
		for (String key : keys) {
			sa = studentAges.get(key);
			newStudyAge = calculateStudyAge(sa.getSchoolLevel(), sa.getConfirmDate());
			//若是估計後的學齡跟原始的不同，就更新資料庫
			if (newStudyAge >= 0 && !newStudyAge.equals(sa.getOldEstSchoolAge())) {
				String sql = "UPDATE Student SET SchoolLevelEstimate = ? WHERE StudentNo = ?";
				Connection conn = null;
				PreparedStatement pstmt = null;
				try {	
					DBConnectionFactory dbf = new DBConnectionFactory();
					conn = dbf.getSQLiteCon("", "Club.dll");
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, newStudyAge.toString());
					pstmt.setString(2, sa.getStudentNo());
					pstmt.executeUpdate();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (pstmt != null) {
							pstmt.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						if (conn != null) {
							conn.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}	
			}
		}
	}
	
	public static void main(String[] args) {
		// 測試用
		CalculateStudyAge csa = new CalculateStudyAge();
		csa.run();
		
	}

}
