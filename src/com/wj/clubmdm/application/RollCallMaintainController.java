/* 
 * Colors Sports Club MDM 登入頁面 Controller
 * @author 黃郁授,吳彥儒
 * @date 2020/09/15
 */

package com.wj.clubmdm.application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.wj.clubmdm.component.BtnDelRollCall;
import com.wj.clubmdm.vo.RollCallDetail;
import com.wj.clubmdm.vo.RollCallUploadDetail;
import com.wj.clubmdm.vo.StudentAttendance;

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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import rhinoceros.util.date.SystemTime;
import rhinoceros.util.db.DBConnectionFactory;

public class RollCallMaintainController extends Application {
	
	private Logger logger = Logger.getLogger(RollCallMaintainController.class);	
	private Stage subStage; //獨佔彈跳視窗共用Stage
	//修改前資料暫存物件
	RollCallDetail oldRCD = null;	
	@FXML
	private DatePicker dpChoiceRollCallDateBegin; //選擇點名日期(起日)
	@FXML
	private DatePicker dpChoiceRollCallDateEnd; //選擇點名日期(起日)	
	@FXML
	private ChoiceBox<String> cbID; //查詢條件(學員編號、身份證字號、姓名 三選一)
	@FXML
	private ChoiceBox<String> cbSpecial; //查詢條件-特色課程
	@FXML
	private ChoiceBox<String> cbLevel; //查詢條件-程度
	@FXML 
	private ChoiceBox<String> cbDepartment; //查詢條件-上課分部
	@FXML
	private ChoiceBox<String> cbCourseKind; //查詢條件-課程類別	
	@FXML
	private TextField tfIDValue; //查詢條件值
	@FXML
	private CheckBox ckbMemberBelongAll; //成員所屬_不限
	@FXML
	private CheckBox ckbMemberBelong1; //成員所屬_冬夏令營
	@FXML
	private CheckBox ckbMemberBelong2; //成員所屬_學校社團
	@FXML
	private CheckBox ckbMemberBelong3; //成員所屬_俱樂部	
	@FXML
	private Button btnQueryRollCallData; //查詢點名資料
	@FXML
	private TextField tfStudentNo; //學員編號
	@FXML
	private DatePicker dpChoiceRollCallDateInsert; //選擇點名日期
	@FXML
	private TextField tfRollCallHH; //選擇點名時間(小時)
	@FXML
	private TextField tfRollCallMM; //選擇點名時間(分鐘)	
	@FXML
	private ChoiceBox<String> cbSpecialInsert; //查詢條件-特色課程		
	@FXML
	private Button btnInsertRollCallData; //新增點名資料	
	@FXML
	private Label tfIDUpdate; //修改_ID欄位
	@FXML
	private DatePicker dpChoiceRollCallDateUpdate; //修改_選擇點名日期	
	@FXML
	private TextField tfRollCallHHUpdate; //修改_選擇點名時間(小時)
	@FXML
	private TextField tfRollCallMMUpdate; //修改_選擇點名時間(分鐘)	
	@FXML
	private TextField tfRollCallSSUpdate; //修改_選擇點名時間(秒) 此欄位用來記錄原始的秒數，不顯示，屬隱藏欄位	
	@FXML
	private ChoiceBox<String> cbSpecialUpdate; //修改_特色課程	
	@FXML
	private Button btnUpdateRollCallData; //修改_點名資料	
	@FXML
	private TableView<RollCallDetail> tvRollCallDetail; //點名資料
	@FXML
	private TableColumn<RollCallDetail, String> colSeqNo; //點名資料_序號
	@FXML
	private TableColumn<RollCallDetail, String> colStudentNo; //點名資料_學員編號
	@FXML
	private TableColumn<RollCallDetail, String> colName; //點名資料_姓名
	@FXML
	private TableColumn<RollCallDetail, String> colDepartment; //點名資料_上課分部
	@FXML
	private TableColumn<RollCallDetail, String> colCourseKind; //點名資料_類別
	@FXML
	private TableColumn<RollCallDetail, String> colLevel; //點名資料_程度
	@FXML
	private TableColumn<RollCallDetail, String> colRollCallTime; //點名資料_點名時間
	@FXML
	private TableColumn<RollCallDetail, String> colSpecial; //點名資料_特色
	@FXML
	private TableColumn<RollCallDetail, String> colMemberBelongDesc; //點名資料_成員所屬描述
	@FXML
	private TableColumn<RollCallDetail, BtnDelRollCall> colDelete; //點名資料_刪除按鈕
	@FXML
	private Button btnOutputExcel; //產出Excel	
	/*
	 * 初始化
	 */
	public void initialize() {
		//建立共用的獨佔彈跳視窗專用Stage
		subStage = new Stage();
		subStage.initModality(Modality.APPLICATION_MODAL);
		
		//設定條件下拉選項
		//ObservableList<String> cbIDValues = FXCollections.observableArrayList("學員編號", "身份證字號", "姓名");
		//cbID.setItems(cbIDValues);		
		
		//建立點名資料TableView資料連結
		colSeqNo.setCellValueFactory(new PropertyValueFactory<>("seqNo"));
		colStudentNo.setCellValueFactory(new PropertyValueFactory<>("studentNo"));
		colName.setCellValueFactory(new PropertyValueFactory<>("name"));
		colDepartment.setCellValueFactory(new PropertyValueFactory<>("department"));
		colCourseKind.setCellValueFactory(new PropertyValueFactory<>("courseKind"));
		colLevel.setCellValueFactory(new PropertyValueFactory<>("level"));
		colRollCallTime.setCellValueFactory(new PropertyValueFactory<>("rollCallTime"));
		colSpecial.setCellValueFactory(new PropertyValueFactory<>("special"));
		colMemberBelongDesc.setCellValueFactory(new PropertyValueFactory<>("memberBelongDesc"));
		colDelete.setCellValueFactory(new PropertyValueFactory<>("btnDelete"));
		
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
		dpChoiceRollCallDateBegin.setConverter(converter);
		dpChoiceRollCallDateEnd.setConverter(converter);
		dpChoiceRollCallDateInsert.setConverter(converter);
		dpChoiceRollCallDateUpdate.setConverter(converter);
		
		//預設日期區間為當日
		dpChoiceRollCallDateBegin.setValue(LocalDate.now());
		dpChoiceRollCallDateEnd.setValue(LocalDate.now());
		dpChoiceRollCallDateInsert.setValue(LocalDate.now());
		dpChoiceRollCallDateUpdate.setValue(LocalDate.now());

		
		//條件預設定學員編號
		cbID.setValue("學員編號");
		//成員所屬條件預設選擇全部
		ckbMemberBelongAll.setSelected(true);
		
		//建立 新增時 特色課程 下拉選單的清單內容
		ObservableList<String> specialItems = FXCollections.observableArrayList("01-非特色", "02-馬拉松", "03-基礎動作", "04-外師授課", "99-其它");	
		cbSpecialInsert.autosize();
		cbSpecialInsert.setItems(specialItems);
		cbSpecialInsert.getSelectionModel().select("01-非特色"); //把非特色當成預設值		
		
		//建立 修改時 特色課程 下拉選單的清單內容
		cbSpecialUpdate.autosize();
		cbSpecialUpdate.setItems(specialItems);
			
		//建立tvRollCallDetail TableView Double click 觸發功能
		tvRollCallDetail.setRowFactory( tv -> {
			TableRow<RollCallDetail> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())) {
					oldRCD = row.getItem();
					tfIDUpdate.setText(oldRCD.getStudentNo());
					//0123456789ABCDEFGHI
					//2020-11-25 17:59:00
					tfRollCallHHUpdate.setText(oldRCD.getRollCallTime().substring(11, 13));
					tfRollCallMMUpdate.setText(oldRCD.getRollCallTime().substring(14, 16));
					tfRollCallSSUpdate.setText(oldRCD.getRollCallTime().substring(17, 19));
					LocalDate dateUpdate = LocalDate.of(
							Integer.parseInt(oldRCD.getRollCallTime().substring(0, 4)), 
							Integer.parseInt(oldRCD.getRollCallTime().substring(5, 7)), 
							Integer.parseInt(oldRCD.getRollCallTime().substring(8, 10))
							);
					
					dpChoiceRollCallDateUpdate.setValue(dateUpdate);
					switch(oldRCD.getSpecial()) {
						case "非特色":
							cbSpecialUpdate.setValue("01-非特色");
							break;
						case "馬拉松":
							cbSpecialUpdate.setValue("02-馬拉松");
							break;
						case "基礎動作":
							cbSpecialUpdate.setValue("03-基礎動作");
							break;
						case "外師授課":
							cbSpecialUpdate.setValue("04-外師授課");
							break;
						case "其它":
							cbSpecialUpdate.setValue("99-其它");
							break;
					}
				}
			});
			return row;
		});
		
		//查詢點名資料
		queryRollCallDetail(); 
	}
	
	//查詢點名資料
	public void queryRollCallDetail() {
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle("錯誤");
		alert.setHeaderText(null);
		
		tvRollCallDetail.getItems().clear(); //清除點名TableView裡的資料

		//檢核點名日期是否正確
		if (dpChoiceRollCallDateBegin.getValue() == null) {		
			alert.setContentText("「點名日期區間(起)」有誤");
			alert.showAndWait();
			return;			
		}	
		
		//檢核點名日期是否正確
		if (dpChoiceRollCallDateEnd.getValue() == null) {		
			alert.setContentText("「點名日期區間(迄)」有誤");
			alert.showAndWait();
			return;			
		}
		
		String sql = "SELECT " + 
				     "  a.StudentNo," + 
				     "  ifnull(b.Name, '') Name," + 
					 "  (select ifnull(d.desc, '') from CodeDetail d where d.DetailCode = b.Department and d.MainCode = '004') DepartmentDesc, " + 
					 "  (select ifnull(d.desc, '') from CodeDetail d where d.DetailCode = b.CourseKind and d.MainCode = '005') CourseKindDesc, " + 
					 "  (select ifnull(d.desc, '') from CodeDetail d where d.DetailCode = b.Level and d.MainCode = '002') LevelDesc, " + 
				     "  a.RollCallTime," + 
				     "  (select ifnull(d.desc, '') from CodeDetail d where d.DetailCode = a.Special and d.MainCode = '007') SpecialDesc, " + 
				     "  b.MemberBelong " + 
				     "FROM " + 
				     "  RollCallUploadDetail a " + 
				     "LEFT JOIN " + 
				     "  Student b on a.StudentNo = b.StudentNo " + 
				     "WHERE " + 
				     "  substr(a.RollCallTime, 1, 10) >= ? and substr(a.RollCallTime, 1, 10) <= ?";
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
				//sql += " and b.Name like %?%";
				sql += " and b.Name like ?";
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
		}
		
		DBConnectionFactory dbf = new DBConnectionFactory();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Integer seqNo = 0;
		try {		
			RollCallDetail rcd = null;  
			BtnDelRollCall btnDel = null;
			conn = dbf.getSQLiteCon("", "Club.dll");
			pstmt = conn.prepareStatement(sql);
			pstmt.clearParameters();
			pstmt.setString(1, dpChoiceRollCallDateBegin.getValue().toString()); //點名時間(起)
			pstmt.setString(2, dpChoiceRollCallDateEnd.getValue().toString()); //點名時間(迄)
			//點名篩選條件值(學員編號、身份證字號、姓名)
			if (tfIDValue.getText().length() > 0) {
				if (cbID.getValue().equalsIgnoreCase("姓名")) {
					pstmt.setString(3, "%"+tfIDValue.getText().trim()+"%"); //姓名可用like查詢 
				} else {
					pstmt.setString(3, tfIDValue.getText().trim());
				}
			}
		
			rs = pstmt.executeQuery();
			while (rs.next()) {
				seqNo++;
				rcd = new RollCallDetail();
				//rcd.setSeqNo(seqNo.toString());
				rcd.setSeqNo(String.format("%07d", seqNo));
				rcd.setStudentNo(rs.getString("StudentNo"));
				rcd.setName(rs.getString("Name"));
				rcd.setDepartment(rs.getString("DepartmentDesc"));
				rcd.setCourseKind(rs.getString("CourseKindDesc"));
				rcd.setLevel(rs.getString("LevelDesc"));
				rcd.setRollCallTime(rs.getString("RollCallTime"));
				rcd.setSpecial(rs.getString("SpecialDesc"));
				rcd.setMemberBelong(rs.getString("MemberBelong"));
				
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
			    tvRollCallDetail.getItems().add(rcd);
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

	//刪除指定的點名資料	
	public void delRollCallDetail(BtnDelRollCall btnDel) {	
		/*
		 * 跳出確認刪除的視窗
		 * ★想要預設Button在否，但還試不出來
		 */
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setHeaderText("點名資料刪除確認");
		alert.setContentText("確認刪除此筆點名資料？");
		Optional<ButtonType> buttonType = alert.showAndWait();
		
		if (buttonType.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {

	    } else {
			//點「否」的話，就不會刪除資料。
			return;
		}
		
		String sqlDelDetail = "delete from RollCallUploadDetail where StudentNo = ? and RollCallTime = ?";

		DBConnectionFactory dbf = new DBConnectionFactory();
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {		
			conn = dbf.getSQLiteCon("", "Club.dll");
			pstmt = conn.prepareStatement(sqlDelDetail);
			pstmt.clearParameters();
			pstmt.setString(1, btnDel.getStudentNo());
			pstmt.setString(2, btnDel.getRollCallTime());
			pstmt.executeUpdate();
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
		//重新查詢上傳批次資料
		queryRollCallDetail();
	}	

	//新增點名資料
	public void insertRollCallDetail() {
		/*
		 * 暫時不採用另開視窗的方法，若要採新開視窗，而且在關閉視窗時會更新母視窗畫面的話，
		 * 記得一定要使用subStage.showAndWait();
		 * 這樣母視窗才會在關閉的時候接著執行，若採用subStage.show()的話，在開啟子視窗時，母視窗的程式已被並行執行。
		 */
		/*
		FXMLLoader loader = new FXMLLoader(getClass().getResource("RollCallInsert.fxml"));			
		AnchorPane root = null;
		try {
			root = (AnchorPane)loader.load();
		} catch (IOException e) {
			logger.info(e.getMessage(), e);
		}
		subStage.setScene(new Scene(root, 346, 251));
		subStage.setTitle("COLOR SPORTS CLUB MDM_V1.0");
		subStage.setResizable(false); //不可改變視窗大小
		//subStage.show();
		subStage.showAndWait();
		queryRollCallDetail(); 
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
				//"01-非特色", "02-馬拉松", "03-基礎動作", "04-外師授課", "99-其它"
				switch(cbSpecialInsert.getValue()) {
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
			pstmt.setString(3, dpChoiceRollCallDateInsert.getValue() + " " + tfRollCallHH.getText().trim() + ":" + tfRollCallMM.getText().trim() + ":00");
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
			
			//查詢點名資料
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

	}	
	
	//修改點名資料
	public void updateRollCallDetail() {
		Integer hhTemp = null;
		Integer mmTemp = null;

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setHeaderText("輸入錯誤");
		
		DBConnectionFactory dbf = new DBConnectionFactory();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		//檢核ID是否有值
		if (tfIDUpdate.getText().trim().length() == 0) {
			alert.setContentText("未選擇要修改的資料");
			alert.showAndWait();
			return;			
		} else {
			//確認學員編號是否存在
			String sqlChkStudent = "select * from Student where StudentNo = ?";
			try {		
				conn = dbf.getSQLiteCon("", "Club.dll");
				pstmt = conn.prepareStatement(sqlChkStudent);
				pstmt.clearParameters();
				pstmt.setString(1, tfIDUpdate.getText().trim());
				rs = pstmt.executeQuery();
				if (!rs.next()) {
					alert.setContentText("學員編號不存在");
					alert.show();
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
		}

		if (dpChoiceRollCallDateUpdate.getValue() == null) {		
			alert.setContentText("未選擇點名日期");
			alert.showAndWait();
			return;		
		}
		
		if (tfRollCallHHUpdate.getText().trim().length() == 0) {
			alert.setContentText("未輸入小時(00-23)");
			alert.showAndWait();
			return;					
		} else {
			try {
				hhTemp = Integer.parseInt(tfRollCallHHUpdate.getText().trim());
				if ((hhTemp < 0) || (hhTemp > 23)) {					
					alert.setContentText("輸入「小時」不正確 須為 00-23");
					alert.showAndWait();
					return;														
				} else {
					if (tfRollCallHHUpdate.getText().length() == 1) {
						tfRollCallHHUpdate.setText("0" + tfRollCallHHUpdate.getText()); //若只輸入1碼，則前面補0
					}
				}
			} catch (Exception e) {
				alert.setContentText("輸入「小時」不正確 須為 00-23");
				alert.showAndWait();
				return;									
			}
		}
		
		if (tfRollCallMMUpdate.getText().trim().length() == 0) {
			alert.setContentText("未輸入分鐘(01-59)");
			alert.showAndWait();
			return;					
		} else {
			try {
				mmTemp = Integer.parseInt(tfRollCallMMUpdate.getText().trim());
				if ((mmTemp < 0) || (mmTemp > 59)) {
					alert.setContentText("輸入「分鐘」不正確 須為 00-59");
					alert.showAndWait();
					return;														
				} else {
					if (tfRollCallMMUpdate.getText().length() == 1) {
						tfRollCallMMUpdate.setText("0" + tfRollCallMMUpdate.getText()); //若只輸入1碼，則前面補0
					}
				}
			} catch (Exception e) {
				alert.setContentText("輸入「分鐘」不正確 須為 00-59");
				alert.showAndWait();
				return;									
			}			
		}	

		//檢核是否有異動資料
		Boolean chkChange = false; //預設沒有異動
		//先檢查日期部份是否有異動
		if (!oldRCD.getRollCallTime().substring(0,10).equalsIgnoreCase(dpChoiceRollCallDateUpdate.getValue().toString())) {
			chkChange = true;
		}
		if (!oldRCD.getRollCallTime().substring(11,13).equalsIgnoreCase(tfRollCallHHUpdate.getText())) {
			chkChange = true;
		}
		if (!oldRCD.getRollCallTime().substring(14,16).equalsIgnoreCase(tfRollCallMMUpdate.getText())) {
			chkChange = true;
		}
		String specialDesc = "";
		String specialCode = "";
		switch (cbSpecialUpdate.getValue()) {
			case "01-非特色":
				specialDesc = "非特色";
				specialCode = "01";
				break;
			case "02-馬拉松":
				specialDesc = "馬拉松";
				specialCode = "02";
				break;
			case "03-基礎動作":
				specialDesc = "基礎動作";
				specialCode = "03";
				break;
			case "04-外師授課":
				specialDesc = "外師授課";
				specialCode = "04";
				break;
			case "99-其它":
				specialDesc = "其它";
				specialCode = "99";
				break;
		}
		if (!oldRCD.getSpecial().equalsIgnoreCase(specialDesc)) {
			chkChange = true;
		}
		if (!chkChange) {
			alert.setContentText("資料無異動");
			alert.showAndWait();
			return;												
		} else {
			try {
				//更新資料
				String updateSQL = "UPDATE RollCallUploadDetail SET FileName = ?, RollCallTime = ?, Special = ?, UpdateTime = ? WHERE StudentNo = ? and RollCallTime = ? ";
				conn = dbf.getSQLiteCon("", "Club.dll");
				pstmt = conn.prepareStatement(updateSQL);
				pstmt.clearParameters();
				SystemTime st = new SystemTime();
				pstmt.setString(1, "UserUpdate_" + st.getNowTime("yyyy-MM-dd HH:mm:ss"));
				pstmt.setString(2, dpChoiceRollCallDateUpdate.getValue() + " " + tfRollCallHHUpdate.getText().trim() + ":" + tfRollCallMMUpdate.getText().trim() + ":00");
				pstmt.setString(3, specialCode);
				pstmt.setString(4, st.getNowTime("yyyyMMddHHmmssSSS"));
				pstmt.setString(5, tfIDUpdate.getText());
				pstmt.setString(6, oldRCD.getRollCallTime());
				pstmt.executeUpdate();			

				alert.setHeaderText("");
				alert.setContentText("點名資料修改成功！");
				alert.show();
			
				tfIDUpdate.setText("");
				tfRollCallHHUpdate.setText("");
				tfRollCallMMUpdate.setText("");
				cbSpecialUpdate.setValue("01-非特色");
				dpChoiceRollCallDateUpdate.setValue(LocalDate.now());
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
			//查詢點名資料
			queryRollCallDetail(); 
		}
	}
	
	//產出Excel報表
	public void outputExcel() {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		SystemTime st = new SystemTime();
		/*
		 * 先檢查TableView裡面的筆數
		 */
		if (tvRollCallDetail.getItems() == null || tvRollCallDetail.getItems().size() <= 0) {
			alert.setHeaderText("");
			alert.setContentText("尚未查詢點名紀錄!");
			alert.showAndWait();
			return;			
		}
		
		OutputStream fileOut = null;
		String fileName = "Backup/Report/RollCall_" + st.getNowTime("yyyyMMddHHmmss") + ".xlsx";
		
		//檢查檔案是否存在，若存在先刪除
		File file = new File(fileName);
		if (file != null && file.exists()) {
			file.delete();
		}
		
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("點名資料");
		sheet.setColumnWidth(0, 10*256); //序號
		sheet.setColumnWidth(1, 23*256); //點名時間
		sheet.setColumnWidth(2, 10*256); //學員編號
		sheet.setColumnWidth(3, 12*256); //姓名
		sheet.setColumnWidth(4, 10*256); //上課分部
		sheet.setColumnWidth(5, 10*256); //類別
		sheet.setColumnWidth(6, 10*256); //程度
		sheet.setColumnWidth(7, 15*256); //特色課程
		sheet.setColumnWidth(8, 35*256); //成員所屬
		

		//由wb物件取得可以設定樣式的XSSFCellStyle物件實例
		XSSFCellStyle styleRow1 = (XSSFCellStyle) wb.createCellStyle();
		//設定背景色
		styleRow1.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
		styleRow1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		XSSFRow row; //列
		XSSFCell cell; //行
		int row1 = 0; //sheet1的列數
		row = sheet.createRow(row1++);
		cell = row.createCell(0);
		cell.setCellStyle(styleRow1);
		cell.setCellValue("序號");
		cell = row.createCell(1);
		cell.setCellStyle(styleRow1);
		cell.setCellValue("點名時間");
		cell = row.createCell(2);
		cell.setCellStyle(styleRow1);
		cell.setCellValue("學員編號");
		cell = row.createCell(3);
		cell.setCellStyle(styleRow1);
		cell.setCellValue("姓名");
		cell = row.createCell(4);
		cell.setCellStyle(styleRow1);
		cell.setCellValue("上課分部");
		cell = row.createCell(5);
		cell.setCellStyle(styleRow1);
		cell.setCellValue("類別");
		cell = row.createCell(6);
		cell.setCellStyle(styleRow1);
		cell.setCellValue("程度");
		cell = row.createCell(7);
		cell.setCellStyle(styleRow1);
		cell.setCellValue("特色課程");
		cell = row.createCell(8);
		cell.setCellStyle(styleRow1);
		cell.setCellValue("成員所屬");
		
		for (RollCallDetail rcd : tvRollCallDetail.getItems()) {
			row = sheet.createRow(row1++);
			cell = row.createCell(0);
			cell.setCellValue(rcd.getSeqNo());
			cell = row.createCell(1);
			cell.setCellValue(rcd.getRollCallTime());
			cell = row.createCell(2);
			cell.setCellValue(rcd.getStudentNo());
			cell = row.createCell(3);
			cell.setCellValue(rcd.getName());
			cell = row.createCell(4);
			cell.setCellValue(rcd.getDepartment());
			cell = row.createCell(5);
			cell.setCellValue(rcd.getCourseKind());
			cell = row.createCell(6);
			cell.setCellValue(rcd.getLevel());
			cell = row.createCell(7);
			cell.setCellValue(rcd.getSpecial());
			cell = row.createCell(8);
			cell.setCellValue(rcd.getMemberBelongDesc());
		}
		
		/*
		 * Sheet2 學員每月出勤次數統計
		 */
		XSSFSheet sheet2 = wb.createSheet("出勤統計");
		sheet2.setColumnWidth(0, 10*256); //學員編號
		sheet2.setColumnWidth(1, 14*256); //出勤年月
		sheet2.setColumnWidth(2, 12*256); //姓名
		sheet2.setColumnWidth(3, 10*256);  //出勤次數
		
		int row2 = 0; //sheet2的列數
		row = sheet2.createRow(row2++);
		cell = row.createCell(0);
		cell.setCellStyle(styleRow1);
		cell.setCellValue("學員編號");
		cell = row.createCell(1);
		cell.setCellStyle(styleRow1);
		cell.setCellValue("出勤年月");
		cell = row.createCell(2);
		cell.setCellStyle(styleRow1);
		cell.setCellValue("姓名");
		cell = row.createCell(3);
		cell.setCellStyle(styleRow1);
		cell.setCellValue("出勤次數");
		
		/*
		 * 此TreeMap用於統計學員每月的出勤次數，會以 學員編號 > 出勤年月 的方式進行排序
		 * TreeMap的鍵值為 學員編號+出勤年月(yyyymm)
		 */
		TreeMap<String, StudentAttendance> tmSA = new TreeMap<String, StudentAttendance>();
		
		String studentNo = null;
		String attendanceYYYYMM = null;
		String key = null;
		StudentAttendance sa = null;

		for (RollCallDetail rcd : tvRollCallDetail.getItems()) {
			studentNo = rcd.getStudentNo();
			attendanceYYYYMM = rcd.getRollCallTime().substring(0, 7);
			key = attendanceYYYYMM + studentNo;
			// 若 學員編號+出勤年月(yyyymm)的組合已存在tmSA中，則進行出勤次數+1的動作
			if (tmSA.containsKey(key)) {
				tmSA.get(key).setAttendanceCount(tmSA.get(key).getAttendanceCount() + 1);
			} else {
				sa = new StudentAttendance();
				sa.setMemberNo(studentNo);
				sa.setName(rcd.getName());
				sa.setAttendanceCount(1);
				sa.setAttendanceYYYYMM(attendanceYYYYMM);
				tmSA.put(key, sa);				
			}
		}		
		
		for (String tmSAKey : tmSA.keySet()) {
			row = sheet2.createRow(row2++);
			cell = row.createCell(0);
			cell.setCellValue(tmSA.get(tmSAKey).getMemberNo());
			cell = row.createCell(1);
			cell.setCellValue(tmSA.get(tmSAKey).getAttendanceYYYYMM());
			cell = row.createCell(2);
			cell.setCellValue(tmSA.get(tmSAKey).getName());
			cell = row.createCell(3);
			cell.setCellValue(tmSA.get(tmSAKey).getAttendanceCount());		
		}		
		
		boolean errChk = false;
		try {
			fileOut = new FileOutputStream(fileName);			
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
			errChk = true;
		}
		try {
			wb.write(fileOut);
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
			errChk = true;
		}
		try {
			wb.close();
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
			errChk = true;
		}
		try {
			fileOut.close();
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
			errChk = true;
		}
		if (!errChk) {
			alert.setHeaderText("");
			try {
				alert.setContentText("產檔成功\r\n" + file.getCanonicalPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
			alert.showAndWait();			
		} else {
			alert.setHeaderText("");
			alert.setContentText("產檔失敗！");
			alert.showAndWait();						
		}
	}
	
	
	@Override
	public void start(Stage arg0) throws Exception {
		
	}
	
}
