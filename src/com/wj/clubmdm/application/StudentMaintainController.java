/* 
 * Colors Sports Club MDM 登入頁面 Controller
 * @author 黃郁授,吳彥儒
 * @date 2020/09/15
 */

package com.wj.clubmdm.application;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import com.wj.clubmdm.component.BtnDelRollCall;
import com.wj.clubmdm.vo.RollCallDetail;
import com.wj.clubmdm.vo.Student;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import rhinoceros.util.date.SystemTime;
import rhinoceros.util.db.DBConnectionFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class StudentMaintainController extends Application {

	private Logger logger = Logger.getLogger(StudentMaintainController.class);

	@FXML
	private TextField tfMemberName;// 學員資料新增＿姓名

	@FXML
	private TextField tfMemberID;// 學員資料新增＿ID

	@FXML
	private TextField tfMemberSchool;// 學員資料新增＿就讀學校

	@FXML
	private DatePicker dpMemberBirthday;// 學員資料新增＿生日

	@FXML
	private ChoiceBox<String> cbMemberSex;// 學員資料新增＿性別

	@FXML
	private TextField tfMemberHeight;// 學員資料新增＿身高

	@FXML
	private ChoiceBox<String> cbMemberSchoolDesc;// 學員資料新增＿年級

	@FXML
	private ChoiceBox<String> cbSpecial;// 學員資料新增＿特教生

	@FXML
	private TextField tfMemberAddress;// 學員資料新增＿聯絡地址

	@FXML
	private ChoiceBox<String> cbTransfer;// 學員資料新增＿轉隊屬性

	@FXML
	private CheckBox ckbWinterSummerCamp;// 學員資料新增＿營隊屬性

	@FXML
	private CheckBox ckbSchoolClub;// 學員資料新增＿學校社團屬性

	@FXML
	private CheckBox ckbColorClub;// 學員資料新增＿俱樂部屬性

	@FXML
	private DatePicker dpJoinDate;// 學員資料新增＿入隊日

	@FXML
	private ChoiceBox<String> cbDepartment;// 學員資料新增＿上課分部

	@FXML
	private ChoiceBox<String> cbCourseKind;// 學員資料新增＿課程類別

	@FXML
	private ChoiceBox<String> cbLevel;// 學員資料新增＿程度

	@FXML
	private TextField tfMark;// 學員資料新增＿註記

	@FXML
	private TextField tfGuardian1;// 學員資料新增＿監護人ㄧ

	@FXML
	private TextField tfContectTel1;// 學員資料新增＿監護人ㄧ電話

	@FXML
	private TextField tfGuardian2;// 學員資料新增＿監護人二

	@FXML
	private TextField tfContectTel2;// 學員資料新增＿監護人二電話

	@FXML
	private ImageView imgvMemberImage;// 照片顯示

	@FXML
	private Button btnImgUpload;// 照片上傳

	@FXML
	private Button btnReset;// 資料填寫欄位清空

	@FXML
	private Button btnAdd;// 學員資料新增

	@FXML
	private TextField tfStudentNo_query;// 學員資料查詢條件＿學員編號

	@FXML
	private TextField tfMemberName_query;// 學員資料查詢條件＿姓名

	@FXML
	private ChoiceBox<String> cbMemberSex_query;// 學員資料查詢條件＿性別

	@FXML
	private TextField tfMemberSchool_query;// 學員資料查詢條件＿就讀學校

	@FXML
	private ChoiceBox<String> cbMemberSchoolDesc_query;// 學員資料查詢條件＿年級

	@FXML
	private ChoiceBox<String> cbStatus_query;// 學員資料查詢條件＿狀態

	@FXML
	private CheckBox ckbWinterSumerCamp_query;// 學員資料查詢條件＿營隊屬性

	@FXML
	private CheckBox ckbSchoolClub_query;// 學員資料查詢條件＿社團屬性

	@FXML
	private CheckBox ckbColorClub_query;// 學員資料查詢條件＿俱樂部屬性

	@FXML
	private ChoiceBox<String> cbDepartment_query;// 學員資料查詢條件＿上課分部

	@FXML
	private ChoiceBox<String> cbCourseKind_query;// 學員資料查詢條件＿課程類別

	@FXML
	private ChoiceBox<String> cbLevel_query;// 學員資料查詢條件＿程度

	@FXML
	private DatePicker dpJoinDateStart;// 學員資料查詢條件＿入隊日起

	@FXML
	private DatePicker dpJoinDateEnd;// 學員資料查詢條件＿入隊日迄

	@FXML
	private Button btnQuery;// 學員資料查詢

	@FXML
	private Button btnExport;// 學員資料查詢結果匯出成報表

	@FXML
	private TableView<Student> tvStudentDetail;// 學員資料查詢表

	@FXML
	private TableColumn<Student, String> colSeq;// 學員資料查詢資料序號

	@FXML
	private TableColumn<Student, String> colStudentNo;// 學員資料查詢＿學員編號

	@FXML
	private TableColumn<Student, String> colMemberName;// 學員資料查詢＿姓名

	@FXML
	private TableColumn<Student, String> colMemberSex;// 學員資料查詢＿性別

	@FXML
	private TableColumn<Student, String> colDepartment;// 學員資料查詢＿上課分部

	@FXML
	private TableColumn<Student, String> colCourceKind;// 學員資料查詢＿課程類別

	@FXML
	private TableColumn<Student, String> colLevel;// 學員資料查詢＿程度

	@FXML
	private TableColumn<Student, String> colJoinDate;// 學員資料查詢＿入隊日

	@FXML
	private TableColumn<Student, String> colEdit;// 學員資料查詢＿編輯操作

	@FXML
	private TextField tfResultInfo;// 學員資料查詢＿結果訊息

	/*
	 * 初始化
	 */
	public void initialize() {
		//建立 新增時 性別,年級,特教生,轉隊生,上課分部,類別,程度,入隊日 下拉選單的清單內容
		ObservableList<String> sexItems = FXCollections.observableArrayList("男", "女");	
		ObservableList<String> schoolDescItems = FXCollections.observableArrayList
				("學齡",
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
		ObservableList<String> yesnoItems = FXCollections.observableArrayList("是", "否");	
		ObservableList<String> departmentItems = FXCollections.observableArrayList("陽光", "仁愛", "隨選");	
		ObservableList<String> courseKindItems = FXCollections.observableArrayList("競速", "花式", "雙棲");	
		ObservableList<String> levelItems = FXCollections.observableArrayList("初級", "中級", "高級", "C組", "B組", "A組");	
		
		cbMemberSex.autosize();
		cbMemberSex.setItems(sexItems);
		
		cbMemberSchoolDesc.autosize();
		cbMemberSchoolDesc.setItems(schoolDescItems);

		cbSpecial.autosize();
		cbSpecial.setItems(yesnoItems);
		cbSpecial.getSelectionModel().select("否"); //設定預設值	

		cbTransfer.autosize();
		cbTransfer.setItems(yesnoItems);
		cbTransfer.getSelectionModel().select("否"); //設定預設值	
		
		cbDepartment.autosize();
		cbDepartment.setItems(departmentItems);
		
		cbCourseKind.autosize();
		cbCourseKind.setItems(courseKindItems);
		
		cbLevel.autosize();
		cbLevel.setItems(levelItems);
		imgvMemberImage.setImage(null);
	
	}

	@Override
	public void start(Stage arg0) throws Exception {

	}
	
	public void resetInputColumn() {
		ObservableList<String> yesnoItems = FXCollections.observableArrayList("是", "否");	
		imgvMemberImage.setImage(null);
		tfMemberName.clear();
		tfMemberID.clear();
		tfMemberSchool.clear();
		dpMemberBirthday.setValue(null);
		cbMemberSex.setValue(null);
		tfMemberHeight.clear();
		cbMemberSchoolDesc.setValue(null);
		
		cbSpecial.autosize();
		cbSpecial.setItems(yesnoItems);
		cbSpecial.getSelectionModel().select("否"); //設定預設值	
		cbTransfer.autosize();
		cbTransfer.setItems(yesnoItems);
		cbTransfer.getSelectionModel().select("否"); //設定預設值
		
		tfMemberAddress.clear();
		ckbWinterSummerCamp.setSelected(false);
		ckbSchoolClub.setSelected(false);
		ckbColorClub.setSelected(false);
		dpJoinDate.setValue(null);
		cbDepartment.setValue(null);
		cbCourseKind.setValue(null);
		cbLevel.setValue(null);
		tfMark.clear();
		tfGuardian1.clear();
		tfContectTel1.clear();
		tfGuardian2.clear();
		tfContectTel2.clear();				
		
	}
	// 檢查輸入欄位是否為數字
	public boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);           
        if( !isNum.matches() ){               
            return false;
        }  return true;
    
	}
	
	public void choiceImg() {
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
		File file = filechooser.showOpenDialog((Stage) btnImgUpload.getScene().getWindow());
		if (file != null) {
			//String imagePath = file.getAbsolutePath();
			//Image img = new Image(file.getAbsolutePath()) 不能用;
			Image img = new Image(file.toURI().toString(),150,150,true, false);		
			//System.out.printf(imagePath);
			//System.out.printf(file.toURI().toString());
			imgvMemberImage.setImage(img);
			
		} else {
			imgvMemberImage.setImage(null);
		}
	}
	
	public void saveImg (Image image, String fileName) {
	    File outputFile = new File("Backup/Photo/" + fileName + ".png");
	    BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
	    try {
	      ImageIO.write(bImage, "png", outputFile);
	    } catch (IOException e) {
	    	logger.info(e.getMessage(), e);
	    	//throw new RuntimeException(e);
	    }
	  }

	// 新增學員資料
	public void insertStudentDetail() {
		/*
		 * 暫時不採用另開視窗的方法，若要採新開視窗，而且在關閉視窗時會更新母視窗畫面的話， 記得一定要使用subStage.showAndWait();
		 * 這樣母視窗才會在關閉的時候接著執行，若採用subStage.show()的話，在開啟子視窗時，母視窗的程式已被並行執行。
		 */
		/*
		 * FXMLLoader loader = new
		 * FXMLLoader(getClass().getResource("RollCallInsert.fxml")); AnchorPane root =
		 * null; try { root = (AnchorPane)loader.load(); } catch (IOException e) {
		 * logger.info(e.getMessage(), e); } subStage.setScene(new Scene(root, 346,
		 * 251)); subStage.setTitle("COLOR SPORTS CLUB MDM_V1.0");
		 * subStage.setResizable(false); //不可改變視窗大小 //subStage.show();
		 * subStage.showAndWait(); queryRollCallDetail();
		 * 共有20個欄位其中13個必填 by jimmy
		 */

		String studentBirthday = "";
		String joinDate = "";
		Integer winterSummerCamp = 0;
		Integer schoolClub = 0B000;//Binary 表示
		Integer colorClub = 0B000;
		Integer selectChk = 0B000;
		String  memberBelong = "";
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setHeaderText("輸入錯誤");

		DBConnectionFactory dbf = new DBConnectionFactory();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
        
		// 檢核學員ID是否有輸入
		if (tfMemberID.getText().trim().length() == 0) {
			alert.setContentText("未輸入學員身分證字號");
			alert.showAndWait();
			return;
		} 
		if ( (tfMemberID.getText().trim().length() > 10) || (tfMemberID.getText().trim().length() < 10) ) {
			alert.setContentText("身分證字號長度有誤");
			alert.showAndWait();
			return;
		} else {
			// 確認學員身分證字號是否存在
			String sqlChkStudent = "select count(*) cnt from Student where ID = ?";
			try {
				conn = dbf.getSQLiteCon("", "Club.dll");
				pstmt = conn.prepareStatement(sqlChkStudent);
				pstmt.clearParameters();
				pstmt.setString(1, tfMemberID.getText().trim());
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
		
		// 檢核姓名是否有輸入
		if (tfMemberName.getText().trim().length() == 0) {
			alert.setContentText("請輸入姓名");
			alert.showAndWait();
			return;
		} 
		
		// 檢核生日是否有輸入
		if (dpMemberBirthday.getValue() == null) {
			alert.setContentText("請輸入生日");
			alert.showAndWait();
			return;
		} else {
			for (String s : dpMemberBirthday.getValue().toString().split("-")) {
				studentBirthday += s;
			}					
		}
		
		// 檢核性別是否有輸入
		if (cbMemberSex.getValue() == null ) {
			alert.setContentText("請選擇性別");
			alert.showAndWait();
			return;
		} else {
			if (cbMemberSex.getValue() == "男") {
				cbMemberSex.setValue("M");
			} else {
				cbMemberSex.setValue("F");
			}
		}
		
		
		// 檢核就讀學校是否有輸入
		if (tfMemberSchool.getText().trim().length() == 0) {
			alert.setContentText("請輸入就讀學校");
			alert.showAndWait();
			return;
		}
		
		// 檢核年級是否有輸入
		if (cbMemberSchoolDesc.getValue() == null) {
			alert.setContentText("請選擇年級");
			alert.showAndWait();
			return;
		} else {
			switch (cbMemberSchoolDesc.getValue()) {
				case "學齡":
					cbMemberSchoolDesc.setValue("902");
					break;
				case "幼小":
					cbMemberSchoolDesc.setValue("903");
					break;
				case "幼中":
					cbMemberSchoolDesc.setValue("904");
					break;
				case "幼大":
					cbMemberSchoolDesc.setValue("905");
					break;
				case "大一":
					cbMemberSchoolDesc.setValue("918");
					break;
				case "大二":
					cbMemberSchoolDesc.setValue("919");
					break;
				case "大三":
					cbMemberSchoolDesc.setValue("920");
					break;
				case "大四":
					cbMemberSchoolDesc.setValue("921");
					break;
				case "成人":
					cbMemberSchoolDesc.setValue("922");
					break;
				case "其它":
					cbMemberSchoolDesc.setValue("999");
					break;
				case "小一":
					cbMemberSchoolDesc.setValue("906");
					break;
				case "小二":
					cbMemberSchoolDesc.setValue("907");
					break;
				case "小三":
					cbMemberSchoolDesc.setValue("908");
					break;
				case "小四":
					cbMemberSchoolDesc.setValue("909");
					break;
				case "小五":
					cbMemberSchoolDesc.setValue("910");
					break;
				case "小六":
					cbMemberSchoolDesc.setValue("911");
					break;
				case "國一":
					cbMemberSchoolDesc.setValue("912");
					break;
				case "國二":
					cbMemberSchoolDesc.setValue("913");
					break;
				case "國三":
					cbMemberSchoolDesc.setValue("914");
					break;
				case "高一":
					cbMemberSchoolDesc.setValue("915");
					break;
				case "高二":
					cbMemberSchoolDesc.setValue("916");
					break;
				case "高三":
					cbMemberSchoolDesc.setValue("917");
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
		if (tfMemberHeight.getText().trim().length() > 0) {
			if (!(isNumeric(tfMemberHeight.getText())) ) {
				alert.setContentText("輸入的身高非數字！" + tfMemberHeight.getText());
				alert.showAndWait();
				return;
			}
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
		/*
		//非必填欄位-註記
		if (tfMark.getText().trim().length() > 0) {
			//do something
		}
		//非必填欄位-監護人二
		if (tfGuardian2.getText().trim().length() > 0) {
			//do something
		}
		//非必填欄位-監護人二電話
		if (tfContectTel2.getText().trim().length() > 0) {
			//do something
		}
		//非必填欄位-通訊地址
		if (tfMemberAddress.getText().trim().length() > 0) {
			//do something
		}*/
		
		//取得新的學員編號
		String lastStudentNo = " ";
		String studentNoAlphabet = " ";
		String pattern = "%06d";
		Number studentNoNumber = 0;
		String givenStudentNo = " ";
		String imgFileName = " ";
		String GetlastStuNosql = "select max(StudentNo)  from student";
		try {
			conn = dbf.getSQLiteCon("", "Club.dll");
			pstmt = conn.prepareStatement(GetlastStuNosql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				lastStudentNo = rs.getString("max(StudentNo)");
				studentNoAlphabet = lastStudentNo.substring(0,1);
				studentNoNumber = Integer.parseInt(lastStudentNo.substring(1)) + 1 ; 
				givenStudentNo = studentNoAlphabet + String.format(pattern, studentNoNumber);
				//System.out.printf("last StudentNo :" + lastStudentNo + "\n" + "new StudentNo : "+ givenStudentNo);	
			}
			//System.out.printf("學員的年級為："+cbLevel.getValue()+"\n");//測試印出輸入欄位
			//System.out.printf(st.getNowTime("yyyyMMdd"));
			//System.out.printf(Integer.toBinaryString(winterSummerCamp)+"\n");
			//System.out.printf(Integer.toBinaryString(schoolClub)+"\n");
			//System.out.printf(Integer.toBinaryString(colorClub)+"\n");
			//System.out.print("結果："+ memberBelong +"\n");
			//System.out.print("Join Date : " + joinDate);
			rs.close();
			pstmt.close();
			
			
			//寫入資料庫
			String InsertStudentsql = "INSERT INTO Student "
					+ "(StudentNo, ID, Name, Sex, Address, School, "
					+ "Height, Level, Special, Birthday, SchoolLevel, "
					+ "SchoolConfirmDate, SchoolLevelEstimate, MemberBelong, "
					+ "Transfer, JoinDate, LeaveDate, Department, CourseKind, "
					+ "Guardian_1, ContactTEL_1, Guardian_2, ContactTEL_2, Status, CreateTime, UpdateTime) "
					+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			logger.info("新增語法 :" + InsertStudentsql + "\n");
			pstmt = conn.prepareStatement(InsertStudentsql);
			pstmt.clearParameters();
			SystemTime st = new SystemTime();
			
			pstmt.setString(1, givenStudentNo );
			pstmt.setString(2, tfMemberID.getText().trim());
			pstmt.setString(3, tfMemberName.getText().trim());
			pstmt.setString(4, cbMemberSex.getValue());
			pstmt.setString(5, tfMemberAddress.getText().trim());
			pstmt.setString(6, tfMemberSchool.getText().trim());
			pstmt.setString(7, tfMemberHeight.getText().trim());
			pstmt.setString(8, cbLevel.getValue());
			pstmt.setString(9, cbSpecial.getValue());
			pstmt.setString(10, studentBirthday);
			pstmt.setString(11, cbMemberSchoolDesc.getValue());
			pstmt.setString(12, st.getNowTime("yyyyMMdd"));
			pstmt.setString(13, null);
			pstmt.setString(14, memberBelong);
			pstmt.setString(15, cbTransfer.getValue());
			pstmt.setString(16, joinDate);
			pstmt.setString(17, null);
			pstmt.setString(18, cbDepartment.getValue());
			pstmt.setString(19, cbCourseKind.getValue());
			pstmt.setString(20, tfGuardian1.getText().trim());
			pstmt.setString(21, tfContectTel1.getText().trim());
			pstmt.setString(22, tfGuardian2.getText().trim());
			pstmt.setString(23, tfContectTel2.getText().trim());
			pstmt.setString(24, "N");
			pstmt.setString(25, st.getNowTime("yyyyMMdd"));
			pstmt.setString(26, "SYSTEM");
			logger.info(givenStudentNo + "-" + tfMemberID.getText() + "-" + tfMemberName.getText() + "-"
					+ cbMemberSex.getValue() + "-" + tfMemberAddress.getText() + "-" + tfMemberSchool.getText() + "-"
					+ tfMemberHeight.getText() + "-" + cbLevel.getValue() + "-" + cbSpecial.getValue() + "-" + studentBirthday + "-"
					+ cbMemberSchoolDesc.getValue() + "-" + st.getNowTime("yyyyMMdd") + "-" + null + "-" + memberBelong + "-" + cbTransfer.getValue() + "-"
					+ joinDate + "-" + null + "-" + cbDepartment.getValue() + "-" + cbCourseKind.getValue() + "-" + tfGuardian1.getText() + "-"
					+ tfContectTel1.getText() + "-" + tfGuardian2.getText() + "-" + tfContectTel2.getText() + "-" + "N" + "-" + st.getNowTime("yyyyMMdd") + "-"
					+ "SYSTEM" + "\n");
			pstmt.executeUpdate();
			if (imgvMemberImage.getImage() != null) {
				imgFileName = givenStudentNo + "_" + tfMemberName.getText().trim();
				saveImg(imgvMemberImage.getImage(), imgFileName );
			}
			alert.setHeaderText("訊息");
			alert.setContentText("學員新增成功！");
			alert.showAndWait();
			resetInputColumn();
			
		} catch (Exception e) {
			alert.setContentText("學員資料新增失敗，請檢查資料填寫是否正確！");
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
	
	
	// 查詢學員資料
	public void queryStudentList() {
		
		String joinDateStart = "";
		String joinDateEnd = "";
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle("你看看你在幹嘛！");
		alert.setHeaderText(null);
		
		tvStudentDetail.getItems().clear(); //清除點名TableView裡的資料
		
		String sql = "SELECT " + 
				     " StudentNo," + 
				     //" Id," + 
				     " Name," +
					 " (select ifnull(d.desc, '') from CodeDetail d where d.DetailCode = b.Sex and d.MainCode = '001') Sex, " + 
				     //" Address," +
				     //" School," +
				     //" ifnull(b.Height, '-')," +
					 " (select ifnull(d.desc, '') from CodeDetail d where d.DetailCode = b.Level and d.MainCode = '002') Level, " + 
				     //" Special," +
				     //" Birthday," +
					 //" (select ifnull(d.StudyDesc, '') from StudyAge d where d.StudyAgeCode = b.SchoolLevel) SchoolLevel, " + 
				     //" SchoolConfirmDate," +
					 //" ifnull(b.SchoolLevelEstimate, '-')," +
					 //" case " +
					 //" when substr(MemberBelong,1,3) = '100' then '冬夏令營' " +
					 //" when substr(MemberBelong,1,3) = '010' then '學校社團' " +
					 //" when substr(MemberBelong,1,3) = '001' then '俱樂部' " +
					 //" when substr(MemberBelong,1,3) = '110' then '冬夏令營 學校社團' " +
					 //" when substr(MemberBelong,1,3) = '101' then '冬夏令營 俱樂部' " +
					 //" when substr(MemberBelong,1,3) = '111' then '冬夏令營 學校社團 俱樂部' "+
					 //" when substr(MemberBelong,1,3) = '011' then '學校社團 俱樂部' " +
					 //" ELSE 'Error' " +
					 //" END as MemberBelong, " +
				     //" Transfer," +
				     " JoinDate," +
				     //" LeaveDate," +
					 " (select ifnull(d.desc, '') from CodeDetail d where d.DetailCode = b.Department and d.MainCode = '004') Department, " + 
					 " (select ifnull(d.desc, '') from CodeDetail d where d.DetailCode = b.CourseKind and d.MainCode = '005') CourseKind " + 
				     //" ifnull(b.Guardian_1, '-') Guardian_1," +
				     //" ifnull(b.ContactTEL_1, '-') ContactTEL_1," +
				     //" ifnull(b.Guardian_2, '-') Guardian_2," +
				     //" ifnull(b.ContactTEL_2, '-') ContactTEL_2," +
					 //" (select ifnull(d.desc, '') from CodeDetail d where d.DetailCode = b.Status and d.MainCode = '006') Status, " + 
				     //" CreateTime," +
				     //" UpdateTime " +
				     "FROM " + 
				     "  Student b " + 
				     "WHERE 1 = 1 ";
		/*	     
		if(dpJoinDateStart.getValue() != null) {
			for (String s : dpJoinDateStart.getValue().toString().split("-")) {
				joinDateStart += s;
			}
			for (String e : dpJoinDateEnd.getValue().toString().split("-")) {
				joinDateEnd += e;
			}
		 sql +=  " and b.JoinDate between " + "'" + joinDateStart + "'" + " and " + "'" + joinDateEnd + "'" ;
		} 
		
		if (tfStudentNo_query.getText().trim().length() > 0) {
		 sql += " and b.StudentNo = ?";
		}*/
		/*
		if (cbSpecial.getValue().equalsIgnoreCase("非特色")) {
			sql += " and a.Special = '01' ";
		} else if (cbSpecial.getValue().equalsIgnoreCase("馬拉松")) {
			sql += " and a.Special = '02' ";
		} else if (cbSpecial.getValue().equalsIgnoreCase("基礎動作")) {
			sql += " and a.Special = '03' ";
		} else if (cbSpecial.getValue().equalsIgnoreCase("外師授課")) {
			sql += " and a.Special = '04' ";
		} else if (cbSpecial.getValue().equalsIgnoreCase("其它")) {
			sql += " and a.Special = '99' ";
		}
		
		if (cbLevel.getValue().equalsIgnoreCase("初級")) {
			sql += " and a.Level = '01' ";
		} else if (cbLevel.getValue().equalsIgnoreCase("中級")) {
			sql += " and a.Level = '02' ";			
		} else if (cbLevel.getValue().equalsIgnoreCase("高級")) {
			sql += " and a.Level = '03' ";						
		} else if (cbLevel.getValue().equalsIgnoreCase("C組")) {
			sql += " and a.Level = '04' ";						
		} else if (cbLevel.getValue().equalsIgnoreCase("B組")) {
			sql += " and a.Level = '05' ";						
		} else if (cbLevel.getValue().equalsIgnoreCase("A組")) {
			sql += " and a.Level = '06' ";						
		}
		
		if (tfIDValue.getText().trim().length() > 0) {
			if (cbID.getValue().equalsIgnoreCase("學員編號")) {
				sql += " and a.StudentNo = ?";
			} else if (cbID.getValue().equalsIgnoreCase("身份證字號")) {
				sql += " and b.ID = ?";
			} else if (cbID.getValue().equalsIgnoreCase("姓名")) {
				sql += " and b.Name like %?%";
			}
		}
		
		if (cbDepartment.getValue().equalsIgnoreCase("仁愛")) {
			sql += " and a.Department = '01' ";
		} else if (cbDepartment.getValue().equalsIgnoreCase("陽光")) {
			sql += " and a.Department = '02' ";			
		} else if (cbDepartment.getValue().equalsIgnoreCase("隨選")) {
			sql += " and a.Department = '99' ";						
		} 						
		
		if (cbCourseKind.getValue().equalsIgnoreCase("花式")) {
			sql += " and a.CourseKind = '01' ";
		} else if (cbCourseKind.getValue().equalsIgnoreCase("競速")) {
			sql += " and a.CourseKind = '02' ";			
		} else if (cbCourseKind.getValue().equalsIgnoreCase("雙棲")) {
			sql += " and a.CourseKind = '03' ";
		}
		
		if (ckbMemberBelongAll.isSelected()) {
			
		} else {
			if (ckbMemberBelong1.isSelected() || ckbMemberBelong2.isSelected() || ckbMemberBelong3.isSelected()) {
				sql += " and (";
				if (ckbMemberBelong1.isSelected()) {
					sql += " substr(b.MemberBelong,1,1) = '1' or";
				}
				if (ckbMemberBelong2.isSelected()) {
					sql += " substr(b.MemberBelong,2,1) = '1' or";
				}
				if (ckbMemberBelong3.isSelected()) {
					sql += " substr(b.MemberBelong,3,1) = '1' or";
				}
				//移除最後一個「or」符號
				if (sql.endsWith("or")) {
					sql = sql.substring(0, sql.length() - 2);
				} 
				sql += ")";				
			} else {
				//若沒有||符號出現，代表條件有問題，至少要選一個
				alert.setContentText("「成員所屬」請至少選擇一個條件");
				alert.showAndWait();
				return;
			}
		}*/
		
		DBConnectionFactory dbf = new DBConnectionFactory();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Integer seqNo = 0;
		try {		
			Student stu = null;  
			//BtnDelRollCall btnDel = null;
			conn = dbf.getSQLiteCon("", "Club.dll");
			pstmt = conn.prepareStatement(sql);
			pstmt.clearParameters();
			//pstmt.setString(1, dpChoiceRollCallDateBegin.getValue().toString()); //點名時間(起)
			//pstmt.setString(2, dpChoiceRollCallDateEnd.getValue().toString()); //點名時間(迄)
			//if (tfIDValue.getText().length() > 0) {
			//	pstmt.setString(3, tfIDValue.getText().trim()); //點名篩選條件值				
			//}			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				seqNo++;
				stu = new Student();
				//stu.setSeqNo(String.format("%07d", seqNo));
				stu.setMemberNo(rs.getString("StudentNo"));
				stu.setName(rs.getString("Name"));
				stu.setSex(rs.getString("Sex"));
				stu.setDepartment(rs.getString("Department"));
				stu.setCourseKind(rs.getString("CourseKind"));
				stu.setLevel(rs.getString("Level"));
				stu.setJoinDate(rs.getString("JoinDate"));
				/*
				rcd.setSeqNo(String.format("%07d", seqNo));
				rcd.setStudentNo(rs.getString("StudentNo"));
				rcd.setName(rs.getString("Name"));
				rcd.setDepartment(rs.getString("DepartmentDesc"));
				rcd.setCourseKind(rs.getString("CourseKindDesc"));
				rcd.setLevel(rs.getString("LevelDesc"));
				rcd.setRollCallTime(rs.getString("RollCallTime"));
				rcd.setSpecial(rs.getString("SpecialDesc"));
				rcd.setMemberBelong(rs.getString("MemberBelong"));
				*/
				/*
				//建立刪除點名資料按鈕
				btnDel = new BtnDelRollCall();
				btnDel.autosize();
				btnDel.setText("刪除");
				btnDel.setStudentNo(rs.getString("StudentNo"));
				btnDel.setRollCallTime(rs.getString("RollCallTime"));
				btnDel.setOnAction(new EventHandler<ActionEvent>() {
			        public void handle(ActionEvent event) {
			        	BtnDelRollCall btnDel = (BtnDelRollCall)event.getSource();
			        	delRollCallDetail(btnDel);
			        }
			    });
				rcd.setBtnDelete(btnDel);
				*/
			    tvStudentDetail.getItems().add(stu);
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
	}

} 
