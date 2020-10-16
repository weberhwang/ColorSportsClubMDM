/* 
 * Colors Sports Club MDM 登入頁面 Controller
 * 
 * @author 黃郁授,吳彥儒
 * @date 2020/09/15
 */

package com.wj.clubmdm.application;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import rhinoceros.util.aes.AESTool;
import rhinoceros.util.db.DBConnectionFactory;
import rhinoceros.util.machine.ChkMachineSN;

public class LoginController extends Application {
	
	private Logger logger = Logger.getLogger(LoginController.class);
	private Stage primaryStage;
	@FXML
	private ImageView logo; //Logo
	@FXML
	private TextField tfAccount; //帳號
	@FXML
	private PasswordField pfPassword; //密碼
	@FXML
	private Button btnLogin; //登入Button
	@FXML
	private Button btnForgetPWD; //忘記密碼Button
	@FXML
	private Button btnRegister; //註冊
	@FXML
	private Label labelMsg; //訊息
	
	/*
	 * 初始化
	 */
	public void initialize() {
		URL imageURL = this.getClass().getResource("/logo.png");
		Image image = null;
		try {
			image = new Image(imageURL.toURI().toString());
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		}
		logo.setImage(image);
		
		chkMotherBoardSN(); //檢核是否為註冊電腦
	}

	/*
	 * 檢核主機板序號
	 * @return true 序號符合 false 序號不符合
	 */
	private boolean chkMotherBoardSN() {
		ArrayList<String> snPlainTexts = new ArrayList<String>(); 
		//取得資料庫中已註冊的亂碼序號並將其解碼後存入ArrayList<String>
		DBConnectionFactory dbcf = new DBConnectionFactory();
		String sql = "select * from SerialNumber";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		AESTool aesTool = new AESTool(); //建立加解密工具
		try {
			conn = dbcf.getSQLiteCon("", "Club.dll");
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				try {
					snPlainTexts.add(aesTool.decrypt(rs.getString("SerialNumber"),aesTool.getKey()));
				} catch (Exception e) {
					logger.info("SerialNumber欄位之資料長度須為16的倍數。");					
				}
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
		
		//檢核 Windows的主機板序號 或 MacOS的作業系統序號 是否符合註冊的序號(隨機版驗證)
		ChkMachineSN cmsn = new ChkMachineSN();
		for (String sn : snPlainTexts) {
			try {
				/*
				 * 資料庫SerialNumber Table中任一筆解密後的明文，若與系統的序號相同，則為合法註冊者，
				 * 直接return true離開迴圈。
				 */
				if (cmsn.getMachineSN().equalsIgnoreCase(sn)) {
					return true;
				}
			} catch (Exception e) {
				logger.info(e.getMessage(), e);
			}
		}
		
		labelMsg.setText("此電腦非合法註冊者，無法使用本軟體，請註冊！");
		return false;
	}
	
	/*
	 * 進入註冊頁面
	 */
	public void register() {
		primaryStage = (Stage) btnForgetPWD.getScene().getWindow();
		FXMLLoader loader = null;
		AnchorPane root = null;
		Scene scene = null;
		try {
			loader = new FXMLLoader(getClass().getResource("Register.fxml"));			
			root = (AnchorPane)loader.load();
			scene = new Scene(root, 1200, 700);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setTitle("COLOR SPORTS CLUB MDM_V1.0");
			primaryStage.setScene(scene);
			primaryStage.setResizable(false); //不可改變視窗大小
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}			
	}
	
	
	/*
	 * 檢查帳號密碼
	 */
	public void chkAccount() {
		boolean chklogin = false; //預設登入失敗
		
		if (!chkMotherBoardSN()) {			
			labelMsg.setText("此電腦非合法註冊者，無法使用本軟體，請註冊！");
			return;
		}
		
		if (tfAccount.getText().trim().length() <= 0) {
			labelMsg.setText("請輸入帳號！");
			return;
		}
		
		if (pfPassword.getText().trim().length() <= 0) {
			labelMsg.setText("請輸入密碼！");
			return;
		}
		
		DBConnectionFactory dbcf = new DBConnectionFactory();
		String sqlLoginChk = "select count(*) cnt from Account where upper(Account) = upper(?) and Password = ?";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = dbcf.getSQLiteCon("", "Club.dll");
			pstmt = conn.prepareStatement(sqlLoginChk);
			pstmt.setString(1, tfAccount.getText());
			pstmt.setString(2, pfPassword.getText());
			rs = pstmt.executeQuery();
			while (rs.next()) {
				if (rs.getInt("cnt") == 1) {
					chklogin = true;
				} else {
					labelMsg.setText("帳號或密碼有誤！");
					return;
				}
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
		
		if (chklogin) {
			labelMsg.setText("登入成功！");
			primaryStage = (Stage) btnLogin.getScene().getWindow();
			AnchorPane root = null;
			Scene scene = null;
			try {
				root = (AnchorPane) FXMLLoader.load(getClass().getResource("PageRoot.fxml"));
				scene = new Scene(root, 1200, 710);
				scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				primaryStage.setTitle("COLOR SPORTS CLUB MDM_V1.0");
				primaryStage.setScene(scene);
				primaryStage.setResizable(false); //不可改變視窗大小
				primaryStage.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * 忘記密碼
	 */
	public void forgetPWD() {

		//檢查帳號是否有輸入
		if (tfAccount.getText().trim().length() <= 0) {
			labelMsg.setText("請輸入帳號！");
			return;
		}
		
		//檢查帳號是否存在資料庫
		boolean chkAccount = false; //預設帳號不存在
		String question = ""; //問題
		DBConnectionFactory dbcf = new DBConnectionFactory();
		String sqlLoginChk = "select Question from Account where upper(Account) = upper(?)";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = dbcf.getSQLiteCon("", "Club.dll");
			pstmt = conn.prepareStatement(sqlLoginChk);
			pstmt.setString(1, tfAccount.getText());
			rs = pstmt.executeQuery();
			while (rs.next()) {
				chkAccount = true;
				question = rs.getString("Question");
			}			
			if (!chkAccount) {
				labelMsg.setText("帳號不存在！");
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
		
		primaryStage = (Stage) btnForgetPWD.getScene().getWindow();
		FXMLLoader loader = null;
		AnchorPane root = null;
		Scene scene = null;
		try {
			loader = new FXMLLoader(getClass().getResource("ChangePWD.fxml"));			
			root = (AnchorPane)loader.load();
			scene = new Scene(root, 1200, 700);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		    ChangePWDController controller = loader.getController();
	        controller.setQuestion(question);
	        controller.setAccount(tfAccount.getText().trim());
			primaryStage.setTitle("COLOR SPORTS CLUB MDM_V1.0");
			primaryStage.setScene(scene);
			primaryStage.setResizable(false); //不可改變視窗大小
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}			
	}
	
	@Override
	public void start(Stage arg0) throws Exception {
		
	}
	
}
