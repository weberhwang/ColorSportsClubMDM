/* 
 * Colors Sports Club MDM 重設密碼 Controller
 * 
 * @author 黃郁授,吳彥儒
 * @date 2020/09/17
 */

package com.wj.clubmdm.application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import rhinoceros.util.db.DBConnectionFactory;

import org.apache.log4j.Logger;


public class ChangePWDController extends Application {
	private String question;
	private String account;
	
	private Logger logger = Logger.getLogger(ChangePWDController.class);

	@FXML
	private Label labelQuestion; //問題
	@FXML
	private TextField tfAnswer; //答案
	@FXML
	private PasswordField pfPWD; //密碼
	@FXML
	private PasswordField pfPWDAgain; //再輸入一次密碼	
	@FXML
	private Button btnSubmit; //送出密碼Button
	@FXML
	private Label labelMsg; //系統訊息
	
	/*
	 * 初始化
	 */
	public void initialize() {
	}
		
	/*
	 * 檢核送出的資料
	 */
	public void chkSubmit() {	
		DBConnectionFactory dbcf = new DBConnectionFactory();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		boolean chkError = false; //預設沒有錯誤
		if (tfAnswer.getText().trim().length() <= 0) {
			chkError = true;
			labelMsg.setText("請輸入答案！");
			return;
		}
		
		//正規表示法 ^(\-|\+)?\d+(\.\d+)?$ 用來檢查正數、負數、小數
		if (tfAnswer.getText().matches("^(\\-|\\+)?\\d+(\\.\\d+)?$")) {				
		} else {
			chkError = true;
			labelMsg.setText("請以數字為答案！");
			return;				
		}
			
		//檢查答案是否正確
		String answer = "";
		Boolean dataExist = false;
		String sqlLoginChk = "select Answer from Account where upper(Account) = upper(?)";
		try {
			conn = dbcf.getSQLiteCon("", "Club.dll");
			pstmt = conn.prepareStatement(sqlLoginChk);
			pstmt.setString(1, account);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				dataExist = true;
				answer = rs.getString("Answer");
			}
			
			if (dataExist && answer.equalsIgnoreCase(tfAnswer.getText().trim())) {				
			} else {
				chkError = true;
				labelMsg.setText("答案不正確！");
				return;	
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
		
		//檢核密碼是否有輸入
		if (pfPWD.getText().trim().length() <= 0) {
			chkError = true;
			labelMsg.setText("請輸入密碼！");
			return;	
		}
		
		//確認密碼是否一致
		if (pfPWDAgain.getText().trim().length() <= 0) {
			chkError = true;
			labelMsg.setText("兩次密碼不一致！");
			return;	
		}
		
		if (!pfPWD.getText().equalsIgnoreCase(pfPWDAgain.getText())) {
			chkError = true;
			labelMsg.setText("兩次密碼不一致！");
			return;				
		}
		
		if (!chkError) {
			String sqlUpdPWD = "update Account set Password = ? where upper(Account) = upper(?)";
			try {
				conn = dbcf.getSQLiteCon("", "Club.dll");
				pstmt = conn.prepareStatement(sqlUpdPWD);
				pstmt.setString(1, pfPWD.getText().trim());
				pstmt.setString(2, account);
				pstmt.executeUpdate();				
				labelMsg.setText("密碼重設完成，請關閉程式重新登入。");
			} catch (Exception e) {
				labelMsg.setText("密碼重設失敗，請治系統人員。");
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
		}
	}
	
	
	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		labelQuestion.setText(question);
		this.question = question;
	}
	
	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	@Override
	public void start(Stage arg0) throws Exception {
		
	}
	
}
