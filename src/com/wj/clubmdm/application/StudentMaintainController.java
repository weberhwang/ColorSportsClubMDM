/* 
 * Colors Sports Club MDM 登入頁面 Controller
 * @author 黃郁授,吳彥儒
 * @date 2020/09/15
 */

package com.wj.clubmdm.application;




import org.apache.log4j.Logger;

import com.wj.clubmdm.vo.Student;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class StudentMaintainController extends Application {
	
	private Logger logger = Logger.getLogger(StudentMaintainController.class);
	

	    @FXML
	    private TextField tfMemberName;//學員資料新增＿姓名

	    @FXML
	    private TextField tfMemberID;//學員資料新增＿ID

	    @FXML
	    private TextField tfMemberSchool;//學員資料新增＿就讀學校

	    @FXML
	    private DatePicker dpMemberBirthday;//學員資料新增＿生日

	    @FXML
	    private ChoiceBox<String> cbMemberSex;//學員資料新增＿性別

	    @FXML
	    private TextField tfMemberHeight;//學員資料新增＿身高

	    @FXML
	    private ChoiceBox<String> cbMemberSchoolDesc;//學員資料新增＿年級

	    @FXML
	    private ChoiceBox<String> cbSpecial;//學員資料新增＿特教生

	    @FXML
	    private TextField tfMemberAddress;//學員資料新增＿聯絡地址

	    @FXML
	    private ChoiceBox<String> cbTransfer;//學員資料新增＿轉隊屬性

	    @FXML
	    private CheckBox ckbWinterSummerCamp;//學員資料新增＿營隊屬性

	    @FXML
	    private CheckBox ckbSchoolClub;//學員資料新增＿學校社團屬性

	    @FXML
	    private CheckBox ckbColorClub;//學員資料新增＿俱樂部屬性

	    @FXML
	    private DatePicker dpJoinDate;//學員資料新增＿入隊日

	    @FXML
	    private ChoiceBox<String> cbDepartment;//學員資料新增＿上課分部

	    @FXML
	    private ChoiceBox<String> cbCourseKind;//學員資料新增＿課程類別

	    @FXML
	    private ChoiceBox<String> cbLevel;//學員資料新增＿程度

	    @FXML
	    private TextField tfMark;//學員資料新增＿註記

	    @FXML
	    private TextField tfGuardian1;//學員資料新增＿監護人ㄧ

	    @FXML
	    private TextField tfContectTel1;//學員資料新增＿監護人ㄧ電話

	    @FXML
	    private TextField tfGuardian2;//學員資料新增＿監護人二

	    @FXML
	    private TextField tfContectTel2;//學員資料新增＿監護人二電話

	    @FXML
	    private ImageView imgvMemberImage;//照片顯示

	    @FXML
	    private Button btnImgUpload;//照片上傳

	    @FXML
	    private Button btnReset;//資料填寫欄位清空

	    @FXML
	    private Button btnAdd;//學員資料新增

	    @FXML
	    private TextField tfStudentNo_query;//學員資料查詢條件＿學員編號

	    @FXML
	    private TextField tfMemberName_query;//學員資料查詢條件＿姓名

	    @FXML
	    private ChoiceBox<String> cbMemberSex_query;//學員資料查詢條件＿性別

	    @FXML
	    private TextField tfMemberSchool_query;//學員資料查詢條件＿就讀學校

	    @FXML
	    private ChoiceBox<String> cbMemberSchoolDesc_query;//學員資料查詢條件＿年級

	    @FXML
	    private ChoiceBox<String> cbStatus_query;//學員資料查詢條件＿狀態

	    @FXML
	    private CheckBox ckbWinterSumerCamp_query;//學員資料查詢條件＿營隊屬性

	    @FXML
	    private CheckBox ckbSchoolClub_query;//學員資料查詢條件＿社團屬性

	    @FXML
	    private CheckBox ckbColorClub_query;//學員資料查詢條件＿俱樂部屬性

	    @FXML
	    private ChoiceBox<String> cbDepartment_query;//學員資料查詢條件＿上課分部

	    @FXML
	    private ChoiceBox<String> cbCourseKind_query;//學員資料查詢條件＿課程類別

	    @FXML
	    private ChoiceBox<String> cbLevel_query;//學員資料查詢條件＿程度

	    @FXML
	    private DatePicker dpJoinDateStart;//學員資料查詢條件＿入隊日起

	    @FXML
	    private DatePicker dpJoinDateEnd;//學員資料查詢條件＿入隊日迄

	    @FXML
	    private Button btnQuery;//學員資料查詢

	    @FXML
	    private Button btnExport;//學員資料查詢結果匯出成報表

	    @FXML
	    private TableView<Student> tvStudentDetail;//學員資料查詢表

	    @FXML
	    private TableColumn<Student, String> colSeq;//學員資料查詢資料序號

	    @FXML
	    private TableColumn<Student, String> colStudentNo;//學員資料查詢＿學員編號

	    @FXML
	    private TableColumn<Student, String> colMemberName;//學員資料查詢＿姓名

	    @FXML
	    private TableColumn<Student, String> colMemberSex;//學員資料查詢＿性別

	    @FXML
	    private TableColumn<Student, String> colDepartment;//學員資料查詢＿上課分部

	    @FXML
	    private TableColumn<Student, String> colCourceKind;//學員資料查詢＿課程類別

	    @FXML
	    private TableColumn<Student, String> colLevel;//學員資料查詢＿程度

	    @FXML
	    private TableColumn<Student, String> colJoinDate;//學員資料查詢＿入隊日

	    @FXML
	    private TableColumn<Student, String> colEdit;//學員資料查詢＿編輯操作

	    @FXML
	    private TextField tfResultInfo;//學員資料查詢＿結果訊息



	/*
	 * 初始化
	 */
	public void initialize() {

	}

	@Override
	public void start(Stage arg0) throws Exception {
		
	}
	
}
