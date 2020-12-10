/* 
 * Colors Sports Club MDM 新增點名紀錄 Controller
 * @author 黃郁授,吳彥儒
 * @date 2020/11/20
 */

package com.wj.clubmdm.application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.log4j.Logger;

import com.wj.clubmdm.vo.RollCallUploadDetail;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import rhinoceros.util.date.SystemTime;
import rhinoceros.util.db.DBConnectionFactory;

public class RollCallInsertController extends Application {
	
	private Logger logger = Logger.getLogger(RollCallInsertController.class);
	@FXML
	private TextField tfStudentNo; //學員編號
	@FXML
	private DatePicker dpChoiceRollCallDate; //選擇點名日期
	@FXML
	private TextField tfRollCallHH; //選擇點名時間(小時)
	@FXML
	private TextField tfRollCallMM; //選擇點名時間(分鐘)	
	@FXML
	private ChoiceBox<String> cbSpecial; //查詢條件-特色課程	
	@FXML
	private Button btnInsertRollCallData; //新增點名資料	
	
	/*
	 * 初始化
	 */
	public void initialize() {

		//設定日期選擇器的格式
		StringConverter<LocalDate> converter = new StringConverter<LocalDate>() {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			@Override 
			public String toString(LocalDate date) {
				if(date !=null ) {
					return formatter.format(date);
				} else {
					return "";
				}
			}
			
			@Override 
			public LocalDate fromString(String string) {
				if (string != null && !string.isEmpty()) {
					return LocalDate.parse(string, formatter);
				} else {
					return null;
				}
			}
		};
		//設定日期選擇器樣式
		dpChoiceRollCallDate.setConverter(converter);
		//設定日期為當日
		dpChoiceRollCallDate.setValue(LocalDate.now());		
		
		//建立 特色課程 下拉選單的清單內容，此下拉選單為「特色課程」欄位，只有N/Y兩個值
		ObservableList<String> specialItems = FXCollections.observableArrayList("01-非特色", "02-馬拉松", "03-基礎動作", "04-外師授課", "05-其它");	
		cbSpecial.autosize();
		cbSpecial.setItems(specialItems);
		cbSpecial.getSelectionModel().select("01-非特色"); //把非特色當成預設值
	}
	
	//新增點名紀錄	
	public void insertRollCallDetail() {
		Integer hhTemp = null;
		Integer mmTemp = null;
	    String studentName = "";	
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setHeaderText("輸入錯誤");
				
		DBConnectionFactory dbf = new DBConnectionFactory();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		//檢核學員編號是否有輸入
		if (tfStudentNo.getText().trim().length() == 0) {
			alert.setContentText("未輸入學員編號");
			alert.showAndWait();
			return;
		} else {
			//確認學員編號是否存在
			String sqlChkStudent = "select Name from Student where StudentNo = ?";
			try {		
				conn = dbf.getSQLiteCon("", "Club.dll");
				pstmt = conn.prepareStatement(sqlChkStudent);
				pstmt.clearParameters();
				pstmt.setString(1, tfStudentNo.getText().trim());
				rs = pstmt.executeQuery();
				while (rs.next()) {
					studentName = rs.getString("Name");
				}
				if (studentName.trim().equalsIgnoreCase("")) {
					alert.setContentText("學員編號不存在");
					alert.showAndWait();
					return;					
				}
			} catch (Exception e) {
				logger.info(e.getMessage(), e);
			} finally {
				try {
					pstmt.close();
				} catch (Exception e) {
					logger.info(e.getMessage(), e);
				}
				try {
					conn.close();
				} catch (Exception e) {
					logger.info(e.getMessage(), e);
				}						
			}
		}
		
		if (dpChoiceRollCallDate.getValue() == null) {		
			alert.setContentText("未選擇點名日期");
			alert.showAndWait();
			return;		
		}
		
		if (tfRollCallHH.getText().trim().length() == 0) {
			alert.setContentText("未輸入小時(00-23)");
			alert.showAndWait();
			return;					
		} else {
			try {
				hhTemp = Integer.parseInt(tfRollCallHH.getText().trim());
				if ((hhTemp < 0) || (hhTemp > 23)) {					
					alert.setContentText("輸入「小時」不正確 須為 00-23");
					alert.showAndWait();
					return;														
				} else {
					if (tfRollCallHH.getText().length() == 1) {
						tfRollCallHH.setText("0" + tfRollCallHH.getText()); //若只輸入1碼，則前面補0
					}
				}
			} catch (Exception e) {
				alert.setContentText("輸入「小時」不正確 須為 00-23");
				alert.showAndWait();
				return;									
			}
		}
		
		if (tfRollCallMM.getText().trim().length() == 0) {
			alert.setContentText("未輸入分鐘(01-59)");
			alert.showAndWait();
			return;					
		} else {
			try {
				mmTemp = Integer.parseInt(tfRollCallMM.getText().trim());
				if ((mmTemp < 0) || (mmTemp > 59)) {
					alert.setContentText("輸入「分鐘」不正確 須為 00-59");
					alert.showAndWait();
					return;														
				} else {
					if (tfRollCallMM.getText().length() == 1) {
						tfRollCallMM.setText("0" + tfRollCallMM.getText()); //若只輸入1碼，則前面補0
					}
				}
			} catch (Exception e) {
				alert.setContentText("輸入「分鐘」不正確 須為 00-59");
				alert.showAndWait();
				return;									
			}			
		}	
		
		try {
			RollCallUploadDetail rcd = new RollCallUploadDetail();
			String specialCode = "";
			//取學員基本資料
			String sql = 
					"SELECT " +
					"  Name," + 
					"  Department," +
					"  CourseKind," +
					"  Level, " +
					"  MemberBelong " +
					"FROM " + 
					"  Student " + 
					"WHERE " + 
					"  StudentNo = ?";
			conn = dbf.getSQLiteCon("", "Club.dll");
			pstmt = conn.prepareStatement(sql);
			pstmt.clearParameters();
			pstmt.setString(1, tfStudentNo.getText().trim());			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				rcd.setStudentNo(tfStudentNo.getText().trim());
				rcd.setName(rs.getString("Name"));
				rcd.setDepartment(rs.getString("Department"));
				rcd.setCourseKind(rs.getString("CourseKind"));
				rcd.setLevel(rs.getString("Level"));
				rcd.setMemberBelong(rs.getString("MemberBelong"));
				//"01-非特色", "02-馬拉松", "03-基礎動作", "04-外師授課", "05-其它"
				switch(cbSpecial.getValue()) {
					case "01-非特色":
						specialCode = "01";
						break;
					case "02-馬拉松":
						specialCode = "02";
						break;
					case "03-基礎動作":
						specialCode = "03";
						break;
					case "04-外師授課":
						specialCode = "04";
						break;
					case "05-其它":
						specialCode = "05";
						break;
				}
				if (specialCode.equalsIgnoreCase("")) {
					alert.setContentText("特色課程下拉選單資料有問題，請與開發人員確認。");
					alert.showAndWait();
					return;						
				}	
			}

			rs.close();
			pstmt.close();

			//寫入資料庫
			sql = "INSERT INTO RollCallUploadDetail "
					+ "(FileName, StudentNo, RollCallTime, Special, CreateTime, Name, Level, MemberBelong, Department, CourseKind) "
					+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			conn = dbf.getSQLiteCon("", "Club.dll");
			pstmt = conn.prepareStatement(sql);
			pstmt.clearParameters();
			SystemTime st = new SystemTime();
			pstmt.setString(1, "UserKeyIn_" + st.getNowTime("yyyy-MM-dd HH:mm:ss"));
			pstmt.setString(2, tfStudentNo.getText().trim());
			pstmt.setString(3, dpChoiceRollCallDate.getValue() + " " + tfRollCallHH.getText().trim() + ":" + tfRollCallMM.getText().trim() + ":00");
			pstmt.setString(4, specialCode);
			pstmt.setString(5, st.getNowTime("yyyyMMddHHmmssSSS"));
			pstmt.setString(6, rcd.getName());
			pstmt.setString(7, rcd.getLevel());
			pstmt.setString(8, rcd.getMemberBelong());
			pstmt.setString(9, rcd.getDepartment());
			pstmt.setString(10, rcd.getCourseKind());
			pstmt.executeUpdate();			

			alert.setHeaderText("");
			alert.setContentText("點名資料新增成功！");
			alert.showAndWait();
			
			tfStudentNo.setText("");
			tfRollCallHH.setText("");
			tfRollCallMM.setText("");
			cbSpecial.setValue("01-非特色");

		} catch (Exception e) {
			alert.setContentText("點名資料新增失敗！");
			alert.showAndWait();
			logger.info(e.getMessage(), e);
			return;	
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				logger.info(e.getMessage(), e);
			}
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				logger.info(e.getMessage(), e);
			}
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				logger.info(e.getMessage(), e);
			}
		}	
	}	
	
	@Override
	public void start(Stage arg0) throws Exception {
		
	}
	
}
