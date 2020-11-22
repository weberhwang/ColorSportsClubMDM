/* 
 * Colors Sports Club MDM 登入頁面 Controller
 * @author 黃郁授,吳彥儒
 * @date 2020/09/15
 */

package com.wj.clubmdm.application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.apache.log4j.Logger;

import com.wj.clubmdm.component.BtnDelRollCall;
import com.wj.clubmdm.component.BtnUpdateRollCall;
import com.wj.clubmdm.vo.RollCallDetail;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import rhinoceros.util.db.DBConnectionFactory;

public class RollCallMaintainController extends Application {
	
	private Logger logger = Logger.getLogger(RollCallMaintainController.class);	
	private Stage subStage; //獨佔彈跳視窗共用Stage
	
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
	private Button btnInsertRollCallData; //新增點名資料	
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
	private TableColumn<RollCallDetail, BtnUpdateRollCall> colUpdate; //點名資料_修改按鈕
	@FXML
	private TableColumn<RollCallDetail, BtnDelRollCall> colDelete; //點名資料_刪除按鈕
	
	
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
		colUpdate.setCellValueFactory(new PropertyValueFactory<>("btnUpdate"));
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
		
		//預設日期區間為當日
		dpChoiceRollCallDateBegin.setValue(LocalDate.now());
		dpChoiceRollCallDateEnd.setValue(LocalDate.now());
		
		//條件預設定學員編號
		cbID.setValue("學員編號");
		//成員所屬條件預設選擇全部
		ckbMemberBelongAll.setSelected(true);
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
		}
		
		DBConnectionFactory dbf = new DBConnectionFactory();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Integer seqNo = 0;
		try {		
			RollCallDetail rcd = null;  
			BtnDelRollCall btnDel = null;
			BtnUpdateRollCall btnUpdate = null;			
			conn = dbf.getSQLiteCon("", "Club.dll");
			pstmt = conn.prepareStatement(sql);
			pstmt.clearParameters();
			pstmt.setString(1, dpChoiceRollCallDateBegin.getValue().toString()); //點名時間(起)
			pstmt.setString(2, dpChoiceRollCallDateEnd.getValue().toString()); //點名時間(迄)
			if (tfIDValue.getText().length() > 0) {
				pstmt.setString(3, tfIDValue.getText().trim()); //點名篩選條件值				
			}			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				seqNo++;
				rcd = new RollCallDetail();
				rcd.setSeqNo(seqNo.toString());
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

				//建立修改點名資料按鈕
				btnUpdate = new BtnUpdateRollCall();
				btnUpdate.autosize();
				btnUpdate.setText("修改");
				btnUpdate.setStudentNo(rs.getString("StudentNo"));
				btnUpdate.setRollCallTime(rs.getString("RollCallTime"));
				btnUpdate.setOnAction(new EventHandler<ActionEvent>() {
			        public void handle(ActionEvent event) {
			        	BtnUpdateRollCall btnUpdate = (BtnUpdateRollCall)event.getSource();
			        	//updateRollCallDetail(btnUpdate);
			        }
			    });
			    rcd.setBtnUpdate(btnUpdate);
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
	public void insert() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("RollCallInsert.fxml"));			
		AnchorPane root = null;
		try {
			root = (AnchorPane)loader.load();
		} catch (IOException e) {
			logger.info(e.getMessage(), e);
		}
		subStage.setScene(new Scene(root, 249, 173));
		subStage.setTitle("COLOR SPORTS CLUB MDM_V1.0");
		subStage.setResizable(false); //不可改變視窗大小
		subStage.show();  
	}	
	
	@Override
	public void start(Stage arg0) throws Exception {
		
	}
	
}
