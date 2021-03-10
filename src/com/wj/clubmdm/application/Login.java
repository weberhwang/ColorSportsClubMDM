/* 
 * Colors Sports Club MDM 登入頁面
 * @author 黃郁授,吳彥儒
 * @date 2020/09/15
 */

package com.wj.clubmdm.application;
	
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.wj.clubmdm.function.CalculateStudyAge;
import com.wj.clubmdm.vo.Student;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;

import rhinoceros.util.db.DBConnectionFactory;
import rhinoceros.util.qrcode.QRCodeUtils;

public class Login extends Application {
	private Logger logger = Logger.getLogger(Login.class);
	@Override
	public void start(Stage primaryStage) {
		try {
			//檢查目錄結構
			checkFolderStructure();			
			//QRCode預產出
			preGetQRCode();
			//重新計算學員目前的學齡
			CalculateStudyAge csa = new CalculateStudyAge();
			csa.run();
			
			AnchorPane root = (AnchorPane)FXMLLoader.load(getClass().getResource("Login.fxml"));
			Scene scene = new Scene(root,1200,730);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setTitle("COLOR SPORTS CLUB MDM_V1.0");
			primaryStage.setScene(scene);
			primaryStage.setResizable(false); //不可調整視窗大小，因為即便視窗放大，裡面的元件不會跟著放大
			primaryStage.show();
		} catch(Exception e) {
			logger.info(e.getMessage(), e);
		}
	}
	/*
	 * 檢核 ColorSportsClubMDM 目錄下，是否有下列目錄
	 * log                   (放log檔) 這個Folder不用檢查，log4j會自己建立
	 * image                 (放logo圖檔)
	 * Backup                (備份目錄)
	 * Backup/QRCode         (放學員QRCode圖檔)
	 * Backup/Report         (放報表檔)
	 * Backup/RollCallUpload (放點名檔)
	 */
	private void checkFolderStructure() {
		File file;
		file = new File("image");
		if (!file.exists() && !file.isDirectory()) {
			file.mkdir();
		}
		
		file = new File("Backup");
		if (!file.exists() && !file.isDirectory()) {
			file.mkdir();
		}
		
		file = new File("Backup/QRCode");
		if (!file.exists() && !file.isDirectory()) {
			file.mkdir();
		}
		
		file = new File("Backup/Report");
		if (!file.exists() && !file.isDirectory()) {
			file.mkdir();
		}
		
		file = new File("Backup/RollCallUpload");
		if (!file.exists() && !file.isDirectory()) {
			file.mkdir();
		}
		
		file = new File("Backup/Photo");
		if (!file.exists() && !file.isDirectory()) {
			file.mkdir();
		}
	}
	
	/*
	 * 預先抓取學員QRCode並存至Local端
	 * 只會針對不存在的檔案下載
	 */
	public void preGetQRCode() {
		//先將學員編號取出暫存至alStudentNo
		DBConnectionFactory dbf = new DBConnectionFactory();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		//ArrayList<String> alStudentNo = new ArrayList<String>();
		ArrayList<Student> alStudent = new ArrayList<Student>();
		try {		
			conn = dbf.getSQLiteCon("", "Club.dll");
			pstmt = conn.prepareStatement("select StudentNo, Name from Student");
			rs = pstmt.executeQuery();
			Student student = null;
			while (rs.next()) {
				student = new Student();
				student.setMemberNo(rs.getString("StudentNo"));
				student.setName(rs.getString("Name"));
				alStudent.add(student);
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
		
		/*
		 * 下面的作法是至Google雲端取QRCode的作法，不支援中間壓入logo的方式 
		 */
		//DownloadFile df = new DownloadFile();
		//for (String no: alStudentNo) {
	    //	df.download("https://chart.apis.google.com/chart?cht=qr&chs=200x200&chl=" + no, "backup/QRCode/", no + ".png");
		//}
		
		//不連線雲端，由Local端產出QRCode的作法(支援logo圖檔)
		File file = null;
		for (Student s : alStudent) {
			int width = 85; //二維碼寬度
			int height = 85; //二維碼高度
			int margin = 0; //二維碼邊距
			String logoPath = "image/logo.png";
			int logoSizeMultiple = 3; //二維碼與LOGO的大小比例
			String filePath = "Backup/QRCode"; //指定生成圖片文件的保存路徑
			file = new File(filePath + "/" + s.getMemberNo() + "_" + s.getName() +".png");
			if (!file.exists()) {
				try {
					// 生成二維碼
					BufferedImage qrcode = QRCodeUtils.createQRCode(s.getMemberNo(), width, height, margin);
					// 添加LOGO
					qrcode = QRCodeUtils.createQRCodeWithLogo(qrcode, width, height, logoPath, logoSizeMultiple);
					/*
					 * 導出指定路徑
					 * 參數1：QRCode物件
					 * 參數2：產出目錄
					 * 參數3：產出的檔名
					 * 參數4：產出的副檔名
					 */
					QRCodeUtils.generateQRCodeToPath(qrcode, filePath, s.getMemberNo() + "_" + s.getName(), "png");
				} catch (Exception e) {
					e.printStackTrace();
				}			
			}
		}		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
