package com.wj.clubmdm.application;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.log4j.Logger;

import com.wj.clubmdm.component.BtnUpdateStu;
import com.wj.clubmdm.vo.RollCallDetail;
import com.wj.clubmdm.application.StudentMaintainController;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import rhinoceros.util.date.SystemTime;
import rhinoceros.util.db.DBConnectionFactory;

public class StudentDetailUpdateController extends Application{
	
	private Logger logger = Logger.getLogger(StudentDetailUpdateController.class);
	private BtnUpdateStu btnUpd;//用來接收其他controller傳遞的值	
	
	@FXML
    private ImageView imgvMember;

    @FXML
    private ImageView imgvQrCode;

    @FXML
    private Button btnImgUpdate;
    
    @FXML
    private Button btnConfirmUpdate;

    @FXML
    private TextField tfName;

    @FXML
    private TextField tfSchool;

    @FXML
    private TextField tfBirthday;

    @FXML
    private TextField tfSex;

    @FXML
    private TextField tfHeight;

    @FXML
    private TextField tfStudentNo;

    @FXML
    private TextField tfAddress;

    @FXML
    private TextField tfGuardian2;

    @FXML
    private TextField tfContectTel2;

    @FXML
    private TextField tfContectTel1;

    @FXML
    private TextField tfGuardian1;

    @FXML
    private TextField tfMark;

    @FXML
    private TextField tfId;
    
    @FXML
    private TextField tfSchoolLevelEstimate;

    @FXML
    private CheckBox ckbColorClub;

    @FXML
    private CheckBox ckbSchoolClub;

    @FXML
    private CheckBox ckbWinterSummerCamp;

    @FXML
    private TextField tfResultInfo;

    @FXML
    private DatePicker dpLeaveDate;
    
    @FXML
    private ChoiceBox<String> cbSchoolDesc;

    @FXML
    private ChoiceBox<String> cbLevel;

    @FXML
    private ChoiceBox<String> cbCourseKind;

    @FXML
    private ChoiceBox<String> cbDepartment;

    @FXML
    private ChoiceBox<String> cbSpecial;

    @FXML
    private ChoiceBox<String> cbTransfer;

    @FXML
    private DatePicker dpJoinDate;

    @FXML
    private ChoiceBox<String> cbStatus;
    
	public void initialize() {
		LocalDate joinDate = LocalDate.parse(this.btnUpd.getJoinDate());//String to LocalDate type
		//logger.info("StudentDetailUpdateController Initialize Okay!" + "\n");
		System.out.print("StudentDetailUpdateController Initialize Okay!" + "\n");
		ObservableList<String> yesnoItems = FXCollections.observableArrayList("是", "否");	
		ObservableList<String> departmentItems = FXCollections.observableArrayList("陽光", "仁愛", "隨選");	
		ObservableList<String> courseKindItems = FXCollections.observableArrayList("競速", "花式", "雙棲");	
		ObservableList<String> levelItems = FXCollections.observableArrayList("初級", "中級", "高級", "C組", "B組", "A組");	
		ObservableList<String> StatusItems = FXCollections.observableArrayList("停課","正式","退遂");
		ObservableList<String> schoolDescItems = FXCollections.observableArrayList
				(null,
				 "學齡",
				 "幼小",
				 "幼中",
				 "幼大",
				 "小一",
				 "小二",
				 "小三",
				 "小四",
				 "小五",
				 "小六",
				 "國一",
				 "國二",
				 "國三",
				 "高一",
				 "高二",
				 "高三",
				 "大一",
				 "大二",
				 "大三",
				 "大四",
				 "成人",
				 "其它");
		File imgFileDefault = new File("backup/Photo/avatar.png");	
		File imgFileMember = new File("backup/Photo/" + this.btnUpd.getStudentNo() + "_" + this.btnUpd.getName() + ".png");		
		if(imgFileMember.exists()) {
			Image memberImg = new Image(imgFileMember.toURI().toString());
			imgvMember.setImage(memberImg);
		} else {
			Image memberImg = new Image(imgFileDefault.toURI().toString());
			imgvMember.setImage(memberImg);
		}
		File imgFile = new File("backup/QRCode/" + this.btnUpd.getStudentNo() + "_" + this.btnUpd.getName() + ".png");
		Image qrImg = new Image(imgFile.toURI().toString());
		imgvQrCode.setImage(qrImg);
		cbLevel.autosize();
		cbLevel.setItems(levelItems);
		cbSpecial.autosize();
		cbSpecial.setItems(yesnoItems);
		cbTransfer.autosize();
		cbTransfer.setItems(yesnoItems);
		cbDepartment.autosize();
		cbDepartment.setItems(departmentItems);
		cbCourseKind.autosize();
		cbCourseKind.setItems(courseKindItems);
		cbSchoolDesc.autosize();
		cbSchoolDesc.setItems(schoolDescItems);
		cbStatus.autosize();
		cbStatus.setItems(StatusItems);
		tfResultInfo.setText("學員編號：" + this.btnUpd.getStudentNo() + "-" + this.btnUpd.getName() + "的詳細資料被載入");
		tfResultInfo.setDisable(true);
		tfMark.setText(this.btnUpd.getRemark());
		tfStudentNo.setText(this.btnUpd.getStudentNo());
		tfName.setText(this.btnUpd.getName());
		tfName.setDisable(true);//先鎖著暫時不讓改名,之後開放改名要即時更改舊QR code 圖檔名稱與大頭照名稱
		tfId.setText(this.btnUpd.getID());
		if (tfId.getText() == null) {
			tfId.setText("");
		} 					
		tfSchool.setText(this.btnUpd.getSchool());
		tfBirthday.setText(this.btnUpd.getBirthday());
		tfSex.setText(this.btnUpd.getSex());
		tfHeight.setText(this.btnUpd.getHEIGHT());
		tfAddress.setText(this.btnUpd.getAddress());
		tfGuardian1.setText(this.btnUpd.getGuardian_1());
		tfContectTel1.setText(this.btnUpd.getContactTEL_1());
		tfGuardian2.setText(this.btnUpd.getGuardian_2());
		tfContectTel2.setText(this.btnUpd.getContactTEL_2());
		dpJoinDate.setValue(joinDate);
		//e04 這邊要注意更新資料我上面宣告 leaveDate 是空白, 更新完資料後要再更點擊更新就會跳error進不去
		// 補增加後面＆＆條件判斷 讓空白不會做處理
	    if(this.btnUpd.getLeaveDate() != null && this.btnUpd.getLeaveDate().trim().length() == 8 ) {
	    	int leaveYear = Integer.valueOf(this.btnUpd.getLeaveDate().substring(0, 4));
	    	int leaveMon= Integer.valueOf(this.btnUpd.getLeaveDate().substring(4, 6));
	    	int leaveDay = Integer.valueOf(this.btnUpd.getLeaveDate().substring(6));
	    	dpLeaveDate.setValue(LocalDate.of(leaveYear, leaveMon, leaveDay));

	    }
	    
		System.out.print("成員屬性：" + this.btnUpd.getMemberBelong() + "\n" + "Loading QR Image from " +imgFile.toURI().toString() + "\n");
		//注意要比對兩邊的值是否相同要用equals 而非 == , == 是用來比較兩個是否references 到同一個物件
		if(this.btnUpd.getSpecial().equals("Y")) {
			cbSpecial.getSelectionModel().select("是");
		} else {
			cbSpecial.getSelectionModel().select("否");
		}
		
		if(this.btnUpd.getTransfer().equals("Y")) {
			cbTransfer.getSelectionModel().select("是");
		} else {
			cbTransfer.getSelectionModel().select("否");
		}
		
		if(this.btnUpd.getStatus().equals("正式")) {
			cbStatus.getSelectionModel().select("正式");
		} else if(this.btnUpd.getStatus().equals("停課")) {
			cbStatus.getSelectionModel().select("停課");
		} else {
			cbStatus.getSelectionModel().select("退遂");
		}
		
		
		if(this.btnUpd.getMemberBelong().substring(0,1).equals("1")) {
			ckbWinterSummerCamp.setSelected(true);
		}
		if(this.btnUpd.getMemberBelong().substring(1,2).equals("1")) {
			ckbSchoolClub.setSelected(true);
		}
		if(this.btnUpd.getMemberBelong().substring(2).equals("1")) {
			ckbColorClub.setSelected(true);
		}
		
		
		if(this.btnUpd.getDepartment().equals("仁愛")) {
			cbDepartment.getSelectionModel().select("仁愛");
		} else if(this.btnUpd.getDepartment().equals("陽光")) {
			cbDepartment.getSelectionModel().select("陽光");
		} else {
			cbDepartment.getSelectionModel().select("隨選");
		}
		
		if(this.btnUpd.getCourseKind().equals("花式")) {
			cbCourseKind.getSelectionModel().select("花式");
		} else if(this.btnUpd.getCourseKind().equals("競速")) {
			cbCourseKind.getSelectionModel().select("競速");
		} else {
			cbCourseKind.getSelectionModel().select("雙棲");
		}
		
		if(this.btnUpd.getLevel().equals("初級")) {
			cbLevel.getSelectionModel().select("初級");
		} else if(this.btnUpd.getLevel().equals("中級")) {
			cbLevel.getSelectionModel().select("中級");
		} else if(this.btnUpd.getLevel().equals("高級")) {
			cbLevel.getSelectionModel().select("高級");
		} else if(this.btnUpd.getLevel().equals("C組")) {
			cbLevel.getSelectionModel().select("C組");
		} else if(this.btnUpd.getLevel().equals("B組")) {
			cbLevel.getSelectionModel().select("B組");
		} else {
			cbLevel.getSelectionModel().select("A組");
		}
		
		
		
		if(this.btnUpd.getSchoolLevel().equals("902")) {
			cbSchoolDesc.getSelectionModel().select("學齡");
		}
		else if(this.btnUpd.getSchoolLevel().equals("903")){
			cbSchoolDesc.getSelectionModel().select("幼小");
		}
		else if(this.btnUpd.getSchoolLevel().equals("904")){
			cbSchoolDesc.getSelectionModel().select("幼中");
		}
		else if(this.btnUpd.getSchoolLevel().equals("905")){
			cbSchoolDesc.getSelectionModel().select("幼大");
		}
		else if(this.btnUpd.getSchoolLevel().equals("906")){
			cbSchoolDesc.getSelectionModel().select("小一");
		}
		else if(this.btnUpd.getSchoolLevel().equals("907")){
			cbSchoolDesc.getSelectionModel().select("小二");
		}
		else if(this.btnUpd.getSchoolLevel().equals("908")){
			cbSchoolDesc.getSelectionModel().select("小三");
		}
		else if(this.btnUpd.getSchoolLevel().equals("909")){
			cbSchoolDesc.getSelectionModel().select("小四");
		}
		else if(this.btnUpd.getSchoolLevel().equals("910")){
			cbSchoolDesc.getSelectionModel().select("小五");
		}
		else if(this.btnUpd.getSchoolLevel().equals("911")){
			cbSchoolDesc.getSelectionModel().select("小六");
		}
		else if(this.btnUpd.getSchoolLevel().equals("912")){
			cbSchoolDesc.getSelectionModel().select("國一");
		}
		else if(this.btnUpd.getSchoolLevel().equals("913")){
			cbSchoolDesc.getSelectionModel().select("國二");
		}
		else if(this.btnUpd.getSchoolLevel().equals("914")){
			cbSchoolDesc.getSelectionModel().select("國三");
		}
		else if(this.btnUpd.getSchoolLevel().equals("915")){
			cbSchoolDesc.getSelectionModel().select("高一");
		}
		else if(this.btnUpd.getSchoolLevel().equals("916")){
			cbSchoolDesc.getSelectionModel().select("高二");
		}
		else if(this.btnUpd.getSchoolLevel().equals("917")){
			cbSchoolDesc.getSelectionModel().select("高三");
		}
		else if(this.btnUpd.getSchoolLevel().equals("918")){
			cbSchoolDesc.getSelectionModel().select("大一");
		}
		else if(this.btnUpd.getSchoolLevel().equals("919")){
			cbSchoolDesc.getSelectionModel().select("大二");
		}
		else if(this.btnUpd.getSchoolLevel().equals("920")){
			cbSchoolDesc.getSelectionModel().select("大三");
		}
		else if(this.btnUpd.getSchoolLevel().equals("921")){
			cbSchoolDesc.getSelectionModel().select("大四");
		}
		else if(this.btnUpd.getSchoolLevel().equals("922")){
			cbSchoolDesc.getSelectionModel().select("成人");
		}
		else {
			cbSchoolDesc.getSelectionModel().select("其它");
		}
		
		if(this.btnUpd.getSchoolLevelEstimate() == null) {
			tfSchoolLevelEstimate.setText("無法估算");
		} else {
			if(this.btnUpd.getSchoolLevelEstimate().equals("902")) {
				tfSchoolLevelEstimate.setText("學齡");
			}
			else if(this.btnUpd.getSchoolLevelEstimate().equals("903")){
				tfSchoolLevelEstimate.setText("幼小");
			}
			else if(this.btnUpd.getSchoolLevelEstimate().equals("904")){
				tfSchoolLevelEstimate.setText("幼中");
			}
			else if(this.btnUpd.getSchoolLevelEstimate().equals("905")){
				tfSchoolLevelEstimate.setText("幼大");
			}
			else if(this.btnUpd.getSchoolLevelEstimate().equals("906")){
				tfSchoolLevelEstimate.setText("小一");
			}
			else if(this.btnUpd.getSchoolLevelEstimate().equals("907")){
				tfSchoolLevelEstimate.setText("小二");
			}
			else if(this.btnUpd.getSchoolLevelEstimate().equals("908")){
				tfSchoolLevelEstimate.setText("小三");
			}
			else if(this.btnUpd.getSchoolLevelEstimate().equals("909")){
				tfSchoolLevelEstimate.setText("小四");
			}
			else if(this.btnUpd.getSchoolLevelEstimate().equals("910")){
				tfSchoolLevelEstimate.setText("小五");
			}
			else if(this.btnUpd.getSchoolLevelEstimate().equals("911")){
				tfSchoolLevelEstimate.setText("小六");
			}
			else if(this.btnUpd.getSchoolLevelEstimate().equals("912")){
				tfSchoolLevelEstimate.setText("國一");
			}
			else if(this.btnUpd.getSchoolLevelEstimate().equals("913")){
				tfSchoolLevelEstimate.setText("國二");
			}
			else if(this.btnUpd.getSchoolLevelEstimate().equals("914")){
				tfSchoolLevelEstimate.setText("國三");
			}
			else if(this.btnUpd.getSchoolLevelEstimate().equals("915")){
				tfSchoolLevelEstimate.setText("高一");
			}
			else if(this.btnUpd.getSchoolLevelEstimate().equals("916")){
				tfSchoolLevelEstimate.setText("高二");
			}
			else if(this.btnUpd.getSchoolLevelEstimate().equals("917")){
				tfSchoolLevelEstimate.setText("高三");
			}
			else if(this.btnUpd.getSchoolLevelEstimate().equals("918")){
				tfSchoolLevelEstimate.setText("大一");
			}
			else if(this.btnUpd.getSchoolLevelEstimate().equals("919")){
				tfSchoolLevelEstimate.setText("大二");
			}
			else if(this.btnUpd.getSchoolLevelEstimate().equals("920")){
				tfSchoolLevelEstimate.setText("大三");
			}
			else if(this.btnUpd.getSchoolLevelEstimate().equals("921")){
				tfSchoolLevelEstimate.setText("大四");
			}
			else if(this.btnUpd.getSchoolLevelEstimate().equals("922")){
				tfSchoolLevelEstimate.setText("成人");
			} else {
				tfSchoolLevelEstimate.setText("未知");
			}
		}
	}
	
	public void updateImg() {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setHeaderText("訊息");
		alert.setContentText("學員照片更換成功！");	
		String fileName = this.btnUpd.getStudentNo() + "_" + this.btnUpd.getName();
		FileChooser filechooser = new FileChooser();
		filechooser.setTitle("選擇 點名檔(*.png)...");
		filechooser.setInitialDirectory(new File("."));
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("PNG", "*.png");
		filechooser.getExtensionFilters().add(filter);
		/*
		 * 跳出對話框時，有兩種方法取得獨佔視窗的對話框
		 * (1)透過button去取得Stage，這樣的效果會是獨佔模式
		 * (2)上一層在建立controller時，把stage當成參數傳入給controller的屬性，這樣可以直接透過屬性去取得stage
		 */
		File file = filechooser.showOpenDialog((Stage) btnUpd.getScene().getWindow());
		if (file != null) {
			//String imagePath = file.getAbsolutePath();
			//Image img = new Image(file.getAbsolutePath()) 不能用;
			Image img = new Image(file.toURI().toString(),150,150,true, false);		
			//System.out.printf(imagePath);
			//System.out.printf(file.toURI().toString());
			imgvMember.setImage(img);
			saveImg(img,fileName);
			alert.showAndWait();			
		} 
	}
	
	public void saveImg (Image image, String fileName) {
	    File outputFile = new File("backup/Photo/" + fileName + ".png");
	    BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
	    try {
	      ImageIO.write(bImage, "png", outputFile);
	    } catch (IOException e) {
	    	logger.info(e.getMessage(), e);
	    	//throw new RuntimeException(e);
	    }
	  }
	/* 20210901 開始動工 */
	public void confirmUpdate() {
		String joinDate = "";
		String leaveDate = "";
		Integer winterSummerCamp = 0;
		Integer schoolClub = 0B000;//Binary 表示
		Integer colorClub = 0B000;
		Integer selectChk = 0B000;
		String  memberBelong = "";
		
		Alert alert = new Alert(Alert.AlertType.WARNING);
		Alert info = new Alert(Alert.AlertType.INFORMATION);
		alert.setHeaderText("警告");
		
		DBConnectionFactory dbf = new DBConnectionFactory();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		//欄位檢查是否有填
		if(tfName.getText().trim().length() == 0) {
			alert.setContentText("姓名不能空白！");
			alert.showAndWait();
			return;
		}
		
		if (tfId.getText().trim().length() > 0) {
			if ( (tfId.getText().trim().length() > 10) || (tfId.getText().trim().length() < 10) ) {
				alert.setContentText("身分證字號長度有誤");
				alert.showAndWait();
				return;
			} else {
				// 確認學員身分證字號是否存在
				String sqlChkStudent = "select count(*) cnt from Student where ID = ? and StudentNo <> ?";
				try {
					conn = dbf.getSQLiteCon("", "Club.dll");
					pstmt = conn.prepareStatement(sqlChkStudent);
					pstmt.clearParameters();
					pstmt.setString(1, tfId.getText().trim());
					pstmt.setString(2, tfStudentNo.getText().trim());
					rs = pstmt.executeQuery();
					while (rs.next()) {
						if(rs.getInt("cnt") > 0) {
							alert.setContentText("學員身分證字號已存在");
							alert.showAndWait();
							return;
						}
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
		} 

		
		if(tfSchool.getText().trim().length() == 0) {
			alert.setContentText("請輸入就讀學校");
			alert.showAndWait();
			return;
		}
		
		if (cbSchoolDesc.getValue() == null) {
			alert.setContentText("請選擇年級");
			alert.showAndWait();
			return;
		} else {
			switch (cbSchoolDesc.getValue()) {
				case "學齡":
					cbSchoolDesc.setValue("902");
					break;
				case "幼小":
					cbSchoolDesc.setValue("903");
					break;
				case "幼中":
					cbSchoolDesc.setValue("904");
					break;
				case "幼大":
					cbSchoolDesc.setValue("905");
					break;
				case "大一":
					cbSchoolDesc.setValue("918");
					break;
				case "大二":
					cbSchoolDesc.setValue("919");
					break;
				case "大三":
					cbSchoolDesc.setValue("920");
					break;
				case "大四":
					cbSchoolDesc.setValue("921");
					break;
				case "成人":
					cbSchoolDesc.setValue("922");
					break;
				case "其它":
					cbSchoolDesc.setValue("999");
					break;
				case "小一":
					cbSchoolDesc.setValue("906");
					break;
				case "小二":
					cbSchoolDesc.setValue("907");
					break;
				case "小三":
					cbSchoolDesc.setValue("908");
					break;
				case "小四":
					cbSchoolDesc.setValue("909");
					break;
				case "小五":
					cbSchoolDesc.setValue("910");
					break;
				case "小六":
					cbSchoolDesc.setValue("911");
					break;
				case "國一":
					cbSchoolDesc.setValue("912");
					break;
				case "國二":
					cbSchoolDesc.setValue("913");
					break;
				case "國三":
					cbSchoolDesc.setValue("914");
					break;
				case "高一":
					cbSchoolDesc.setValue("915");
					break;
				case "高二":
					cbSchoolDesc.setValue("916");
					break;
				case "高三":
					cbSchoolDesc.setValue("917");
					break;
			
			}
		}
		
		// 檢核監護人一是否有輸入
		if (tfGuardian1.getText().trim().length() == 0) {
			alert.setContentText("請輸入監護人一");
			alert.showAndWait();
			return;
		}
		// 檢核監護人一電話是否有輸入
		if (tfContectTel1.getText().trim().length() == 0) {
			alert.setContentText("請輸入聯絡電話一");
			alert.showAndWait();
			return;
		}
		
		// 檢核上課分部是否有輸入
		if (cbDepartment.getValue() == null) {
			alert.setContentText("請選擇上課分部");
			alert.showAndWait();
			return;
		} else {
			switch (cbDepartment.getValue()) {
			case "仁愛" :
				cbDepartment.setValue("01");
				break;
			case "陽光" :
				cbDepartment.setValue("02");
				break;
			case "隨選" :
				cbDepartment.setValue("99");
				break;
			}
		}
				
		// 檢核類別是否有輸入
		if (cbCourseKind.getValue() == null) {
			alert.setContentText("請選擇類別");
			alert.showAndWait();
			return;
		} else {
			switch (cbCourseKind.getValue()) {
			case "花式" :
				cbCourseKind.setValue("01");
				break;
			case "競速" :
				cbCourseKind.setValue("02");
				break;
			case "雙棲" :
				cbCourseKind.setValue("03");
				break;
			}			
		}
		
		// 檢核程度是否有輸入
		if (cbLevel.getValue()  == null) {
			alert.setContentText("請選擇程度");
			alert.showAndWait();
			return;
		} else {
			switch (cbLevel.getValue()) {
				case "初級":
					cbLevel.setValue("01");
					break;
				case "中級":
					cbLevel.setValue("02");
					break;
				case "高級":
					cbLevel.setValue("03");
					break;
				case "C組":
					cbLevel.setValue("04");
					break;
				case "B組":
					cbLevel.setValue("05");
					break;
				case "A組":
					cbLevel.setValue("06");
					break;
			}
		}
		
		// 檢核入隊日是否有輸入
		if (dpJoinDate.getValue() == null) {
			alert.setContentText("請輸入入隊日");
			alert.showAndWait();
			return;
		} else {
			for (String s : dpJoinDate.getValue().toString().split("-")) {
				joinDate += s;
			}
		}
		
		//檢核學員狀態
		if (cbStatus.getValue() == null) {
			alert.setContentText("請選擇狀態");
			alert.showAndWait();
			return;
		} else {
			switch (cbStatus.getValue()) {
			case "停課":
				cbStatus.setValue("S");
				break;
			case "正式":
				cbStatus.setValue("N");
				break;
			case "退遂":
				cbStatus.setValue("L");
				break;
			}
		}
		
		// 檢核成員所屬是否有輸入
		if (ckbWinterSummerCamp.isSelected() || ckbSchoolClub.isSelected() || ckbColorClub.isSelected()) {
		   if (ckbWinterSummerCamp.isSelected()) {
			   winterSummerCamp = 0B100;
		   }
		   
		   if (ckbSchoolClub.isSelected()) {
			   schoolClub = 0B010;
		   }
		   
		   if (ckbColorClub.isSelected()) {
			   colorClub = 0B001;
		   }
		selectChk = (winterSummerCamp | schoolClub | colorClub);
		memberBelong = String.format("%3s", Integer.toBinaryString(selectChk)).replace(' ', '0') + "0000000";//後面七位保留
		} else {
			alert.setContentText("請至少選取一個成員所屬");
			alert.showAndWait();
			return;
		}
		
		//非必填欄位-身高 
		if (tfHeight.getText() != null && tfHeight.getText() != "") {
			if (!(isNumeric(tfHeight.getText())) ) {
				alert.setContentText("輸入的身高非數字！" + tfHeight.getText());
				alert.showAndWait();
				return;
			}
		} else {
			tfHeight.setText(""); //Update 不能有欄位是null
		}
		
		//監護人二
		if (tfGuardian2.getText() == null) {
			tfGuardian2.setText("");
		}
		
		//監護人二電話
		if(tfContectTel2.getText() == null) {
			tfContectTel2.setText("");
		}
		
		//註記
		if(tfMark.getText() == null) {
			tfMark.setText("");
		}
		
		// 非必填欄位-地址
		if (tfAddress.getText() == null ) {
			tfAddress.setText("");
		}
		
					
		//非必填欄位-特教生
		if (cbSpecial.getValue() != null ) {
			if (cbSpecial.getValue() == "是") {
				cbSpecial.setValue("Y");
			} else {
				cbSpecial.setValue("N");
			}
		}
		//非必填欄位-轉隊生
		if (cbTransfer.getValue() != null) {
			if (cbTransfer.getValue()== "是") {
				cbTransfer.setValue("Y");
			} else {
				cbTransfer.setValue("N");
			}
		}
		
		// 非必填欄位-退隊日
		if (dpLeaveDate.getValue() != null) {
			for (String s : dpLeaveDate.getValue().toString().split("-")) {
				leaveDate += s;
			}
		}
			
		
			
		//更新學員資料 update 不允許有null 因此有些欄位沒有必填 資料庫內為null
		//因此帶出來也是null 須先轉換成空白,我寫在前面欄位檢查的地方
		try {
			conn = dbf.getSQLiteCon("", "Club.dll");			
			//寫入資料庫
			String ＵpdateStudentsql = "UPDATE Student SET " 
					+ "ID = ?, Name = ?, Address = ?, School = ?, "
					+ "Height = ?, Level = ?, Special = ?, SchoolLevel = ?, "
					+ "MemberBelong = ?, Transfer =?, JoinDate = ?, LeaveDate = ?, "
					+ "Department = ?, CourseKind = ?, Guardian_1 = ?, ContactTEL_1 = ?, "
					+ "Guardian_2 = ?, ContactTEL_2 = ?, Status = ?, UpdateTime = ?, Remark = ? "
					+ "Where StudentNo = ? ";
			logger.info("更新語法 :" + ＵpdateStudentsql + "\n");
			pstmt = conn.prepareStatement(ＵpdateStudentsql);
			pstmt.clearParameters();
			SystemTime st = new SystemTime();
			
			pstmt.setString(1, tfId.getText().trim() );
			pstmt.setString(2, tfName.getText().trim());
			pstmt.setString(3, tfAddress.getText().trim());
			pstmt.setString(4, tfSchool.getText().trim());
			pstmt.setString(5, tfHeight.getText().trim());
			pstmt.setString(6, cbLevel.getValue());
			pstmt.setString(7, cbSpecial.getValue());
			pstmt.setString(8, cbSchoolDesc.getValue());
			pstmt.setString(9, memberBelong);
			pstmt.setString(10, cbTransfer.getValue());
			pstmt.setString(11, joinDate);
			pstmt.setString(12, leaveDate);
			pstmt.setString(13, cbDepartment.getValue());
			pstmt.setString(14, cbCourseKind.getValue());
			pstmt.setString(15, tfGuardian1.getText().trim());
			pstmt.setString(16, tfContectTel1.getText().trim());
			pstmt.setString(17, tfGuardian2.getText().trim());
			pstmt.setString(18, tfContectTel2.getText().trim());
			pstmt.setString(19, cbStatus.getValue());
			pstmt.setString(20, st.getNowTime("yyyyMMdd"));
			pstmt.setString(21, tfMark.getText().trim());
			pstmt.setString(22, tfStudentNo.getText().trim());
			
			//update before data log		
			logger.info("更新前資料 > " + 
			"姓名:" + this.btnUpd.getName() +
			"-學員編號:" + this.btnUpd.getStudentNo() +
			"-性別:" + this.btnUpd.getSex() +
			"-身分證號:" + this.btnUpd.getID() +
			"-生日:" + this.btnUpd.getBirthday() +
			"-身高:" + this.btnUpd.getHEIGHT() +
			"-學校:" + this.btnUpd.getSchool() +
			"-年級:" + this.btnUpd.getSchoolLevel() +
			"-特教生:" + this.btnUpd.getSpecial() +
			"-地址:" + this.btnUpd.getAddress() +
			"-轉隊生:" + this.btnUpd.getTransfer() +
			"-監護人一:" + this.btnUpd.getGuardian_1() +
			"-電話一:" + this.btnUpd.getContactTEL_1() +
			"-監護人二:" + this.btnUpd.getGuardian_2() +
			"-電話二:" + this.btnUpd.getContactTEL_2() +
			"-成員所屬:" + this.btnUpd.getMemberBelong() +
			"-分部:" + this.btnUpd.getDepartment() +
			"-類別:" + this.btnUpd.getCourseKind() +
			"-程度:" + this.btnUpd.getLevel() +
			"-入隊日:" + this.btnUpd.getJoinDate() +
			"-狀態:" + this.btnUpd.getStatus() +
			"-退隊日:" + this.btnUpd.getLeaveDate() +
			"-註記:" + this.btnUpd.getRemark() + "\n");
			
			
			//update data log
			logger.info("更新後資料 > " + 
			"姓名:" + tfName.getText() +
			"-學員編號:" + tfStudentNo.getText() +
			"-性別:" + tfSex.getText() +
			"-身分證號:" + tfId.getText() +
			"-生日:" +  tfBirthday.getText() +
			"-身高:" + tfHeight.getText() +
			"-學校:" + tfSchool.getText() +
			"-年級:" + cbSchoolDesc.getValue() +
			"-特教生:" + cbSpecial.getValue() +
			"-地址:" + tfAddress.getText() +
			"-轉隊生:" + cbTransfer.getValue() +
			"-監護人一:" + tfGuardian1.getText() +
			"-電話一:" + tfContectTel1.getText() +
			"-監護人二:" + tfGuardian2.getText() +
			"-電話二:" + tfContectTel2.getText() +
			"-成員所屬:" + memberBelong +
			"-分部:" + cbDepartment.getValue() +
			"-類別:" + cbCourseKind.getValue() +
			"-程度:" + cbLevel.getValue() +
			"-入隊日:" + joinDate +
			"-狀態:" + cbStatus.getValue() +
			"-退隊日:" + leaveDate +
			"-註記:" + tfMark.getText() + "\n");
			pstmt.executeUpdate();
			info.setHeaderText("訊息");
			info.setContentText("學員資料成功！" + "\n" + "請關閉視窗後重新點選查詢");
			info.showAndWait();
		} catch (Exception e) {
			alert.setContentText("學員資料更新失敗，請檢查資料填寫是否正確！");
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
	
	// 檢查輸入欄位是否為數字
		public boolean isNumeric(String str){
	        Pattern pattern = Pattern.compile("[0-9]*");
	        Matcher isNum = pattern.matcher(str);           
	        if( !isNum.matches() ){               
	            return false;
	        }  return true;
	    
		}
	@Override
	public void start(Stage arg0) throws Exception {
	}
	
	public void setBtnUpd(BtnUpdateStu btnUpd) {
		this.btnUpd = btnUpd;
	}
	
	

}
