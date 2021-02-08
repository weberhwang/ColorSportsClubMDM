/* 
 * Colors Sports Club MDM 登入頁面 Controller
 * @author 黃郁授,吳彥儒
 * @date 2020/09/15
 */

package com.wj.clubmdm.application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import com.wj.clubmdm.vo.RollCallUploadDetail;
import com.wj.clubmdm.vo.Student;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.image.ImageView;
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

	}

	@Override
	public void start(Stage arg0) throws Exception {

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
		 */

		Integer hhTemp = null;
		Integer mmTemp = null;
		String studentName = "";
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
  /*
		if (dpChoiceRollCallDateInsert.getValue() == null) {
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
						tfRollCallHH.setText("0" + tfRollCallHH.getText()); // 若只輸入1碼，則前面補0
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
						tfRollCallMM.setText("0" + tfRollCallMM.getText()); // 若只輸入1碼，則前面補0
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
			// 取學員基本資料
			String sql = "SELECT " + "  Name," + "  Department," + "  CourseKind," + "  Level, " + "  MemberBelong "
					+ "FROM " + "  Student " + "WHERE " + "  StudentNo = ?";
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
				// "01-非特色", "02-馬拉松", "03-基礎動作", "04-外師授課", "99-其它"
				switch (cbSpecialInsert.getValue()) {
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
				case "99-其它":
					specialCode = "99";
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

			// 寫入資料庫
			sql = "INSERT INTO RollCallUploadDetail "
					+ "(FileName, StudentNo, RollCallTime, Special, CreateTime, Name, Level, MemberBelong, Department, CourseKind) "
					+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			conn = dbf.getSQLiteCon("", "Club.dll");
			pstmt = conn.prepareStatement(sql);
			pstmt.clearParameters();
			SystemTime st = new SystemTime();
			pstmt.setString(1, "UserKeyIn_" + st.getNowTime("yyyy-MM-dd HH:mm:ss"));
			pstmt.setString(2, tfStudentNo.getText().trim());
			pstmt.setString(3, dpChoiceRollCallDateInsert.getValue() + " " + tfRollCallHH.getText().trim() + ":"
					+ tfRollCallMM.getText().trim() + ":00");
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
			cbSpecialInsert.setValue("01-非特色");

			// 查詢點名資料
			queryRollCallDetail();

		} catch (Exception e) {
			alert.setContentText("點名資料新增失敗(資料已存在)！");
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
      */
	} 

} 
