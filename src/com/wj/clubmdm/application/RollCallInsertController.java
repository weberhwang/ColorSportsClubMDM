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
import java.util.Optional;

import org.apache.log4j.Logger;

import com.wj.clubmdm.component.BtnDelRollCall;
import com.wj.clubmdm.component.BtnDelRollCallUpload;
import com.wj.clubmdm.component.BtnUpdateRollCall;
import com.wj.clubmdm.vo.RollCallDetail;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
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
	}
	
	//新增點名紀錄	
	public void insertRollCallDetail() {
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
	}	
	
	@Override
	public void start(Stage arg0) throws Exception {
		
	}
	
}
