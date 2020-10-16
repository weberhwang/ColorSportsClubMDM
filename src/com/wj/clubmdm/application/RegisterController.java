/* 
 * Colors Sports Club MDM 註冊 Controller
 * 
 * @author 黃郁授,吳彥儒
 * @date 2020/10/15
 */

package com.wj.clubmdm.application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import rhinoceros.util.aes.AESTool;
import rhinoceros.util.db.DBConnectionFactory;
import rhinoceros.util.machine.ChkMachineSN;

import org.apache.log4j.Logger;


public class RegisterController extends Application {

	
	private Logger logger = Logger.getLogger(RegisterController.class);
	
	@FXML
	private Button btnSNQuery; //查詢系統序號
	@FXML
	private Label labelDesc; //說明
	@FXML
	private Label labelSN; //系統序號(Windows為主機板序號、MACBook為MAC作業系統序號)
	@FXML
	private TextField tfSN; //註冊碼
	@FXML
	private Button btnSubmit; //註冊Button
	@FXML
	private Label labelMsg; //系統訊息
	
	/*
	 * 初始化
	 */
	public void initialize() {
	}
		
	/*
	 * 查詢 Windows的主機板序號 或 MacOS的作業系統序號
	 */
	public void snQuery() {
		ChkMachineSN cmsn = new ChkMachineSN();
		try {
			labelSN.setText(cmsn.getMachineSN());
			labelMsg.setText("");
		} catch (Exception e) {
			logger.info("查無代碼，請洽軟體開發人員");
			logger.info(e.getMessage(), e);
		}		
	}
	
	/*
	 * 檢核送出的資料
	 */
	public void chkSubmit() {
		if (tfSN.getText().trim().length() <= 0) {
			labelMsg.setText("請輸入註冊碼");
			return;
		}
		
		/*
		 * 將註冊碼解密成明文後再跟系統序號比較，如不一致，則透註冊碼有誤
		 */
		AESTool aesTool = new AESTool(); //建立加解密工具
		ChkMachineSN cmsn = new ChkMachineSN(); //建立取得系統序號物件		
		try {
			if (aesTool.decrypt(tfSN.getText().trim(), aesTool.getKey()).equalsIgnoreCase(cmsn.getMachineSN())) {			
			} else {
				labelMsg.setText("註冊碼有誤！");
				return;
			}
		} catch (Exception e) {
			labelMsg.setText("註冊碼有誤！");
			logger.info(e.getMessage(), e);
			return;
		}
				
		DBConnectionFactory dbcf = new DBConnectionFactory();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
				
		//檢查註冊碼是否已存在
		int cnt = 0;
		String sqlSNChk = "select count(*) CNT from SerialNumber where upper(SerialNumber) = upper(?)";
		try {
			conn = dbcf.getSQLiteCon("", "Club.dll");
			pstmt = conn.prepareStatement(sqlSNChk);
			pstmt.setString(1, tfSN.getText().trim());
			rs = pstmt.executeQuery();
			while (rs.next()) {
				cnt = rs.getInt("CNT");
			}		
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				logger.info(e.getMessage(), e);				
			}
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
		
		if (cnt > 0) {
			labelMsg.setText("註冊成功，請重啟程式！");
			return;
		}

		String sqlInsertSN = "insert into SerialNumber values(?)";
		try {
			conn = dbcf.getSQLiteCon("", "Club.dll");
			pstmt = conn.prepareStatement(sqlInsertSN);
			pstmt.setString(1, tfSN.getText().trim());
			pstmt.executeUpdate();				
			labelMsg.setText("註冊成功，請重啟程式！");
		} catch (Exception e) {
			labelMsg.setText("註冊失敗！");
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

	@Override
	public void start(Stage arg0) throws Exception {		
	}
	
}
