/* 
 * Colors Sports Club MDM 登入頁面 Controller
 * @author 黃郁授,吳彥儒
 * @date 2020/09/15
 */

package com.wj.clubmdm.application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.apache.log4j.Logger;

import com.wj.clubmdm.component.BtnDelRollCall;
import com.wj.clubmdm.component.BtnDelRollCallUpload;
import com.wj.clubmdm.component.BtnUpdateRollCall;
import com.wj.clubmdm.vo.RollCallDetail;

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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import rhinoceros.util.date.SystemTime;
import rhinoceros.util.db.DBConnectionFactory;

public class RollCallMaintainController extends Application {
	
	private Logger logger = Logger.getLogger(RollCallMaintainController.class);
	private LocalDate ld;
	@FXML
	private DatePicker dpChoiceRollCallDateBegin; //選擇點名日期(起日)
	@FXML
	private DatePicker dpChoiceRollCallDateEnd; //選擇點名日期(起日)	
	@FXML
	private ChoiceBox<String> cbCondition; //欄位條件(學員編號、身份證字號、姓名 三選一)
	@FXML
	private TextField tfConditionValue; //查詢條件值
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
	private TableColumn<RollCallDetail, BtnUpdateRollCall> colUpdate; //點名資料_修改按鈕
	@FXML
	private TableColumn<RollCallDetail, BtnDelRollCall> colDelete; //點名資料_刪除按鈕
	
	
	/*
	 * 初始化
	 */
	public void initialize() {
		//設定條件下拉選項
		ObservableList<String> cbConditionValues = FXCollections.observableArrayList("學員編號", "身份證字號", "姓名");
		cbCondition.setItems(cbConditionValues);
		
		//建立點名資料TableView資料連結
		colSeqNo.setCellValueFactory(new PropertyValueFactory<>("seqNo"));
		colStudentNo.setCellValueFactory(new PropertyValueFactory<>("studentNo"));
		colName.setCellValueFactory(new PropertyValueFactory<>("name"));
		colDepartment.setCellValueFactory(new PropertyValueFactory<>("department"));
		colCourseKind.setCellValueFactory(new PropertyValueFactory<>("courseKind"));
		colLevel.setCellValueFactory(new PropertyValueFactory<>("level"));
		colRollCallTime.setCellValueFactory(new PropertyValueFactory<>("rollCallTime"));
		colSpecial.setCellValueFactory(new PropertyValueFactory<>("special"));
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
		dpChoiceRollCallDateBegin.setValue(ld.now());
		dpChoiceRollCallDateEnd.setValue(ld.now());
		
		//條件預設定學員編號
		cbCondition.setValue("學員編號");
	}
	
	//查詢點名資料
	public void queryRollCallDetail() {
		tvRollCallDetail.getItems().clear(); //清除點名TableView裡的資料

		//檢核點名日期是否正確
		if (dpChoiceRollCallDateBegin.getValue() == null) {		
			//tvMsg.getItems().clear(); 
			//insertMsg("「點名日期(起)」輸入有誤！");
			return;			
		}	
		
		//檢核點名日期是否正確
		if (dpChoiceRollCallDateEnd.getValue() == null) {		
			//tvMsg.getItems().clear(); 
			//insertMsg("「點名日期(迄)」輸入有誤！");
			return;			
		}
		
		String sql = "SELECT " + 
				     "  a.StudentNo," + 
				     "  ifnull(b.Name, '') Name," + 
				     "  (select ifnull(d.desc, '') from Student c left join CodeDetail d on c.Department = d.DetailCode and d.MainCode = '004') DepartmentDesc," + 
				     "  (select ifnull(d.desc, '') from Student c left join CodeDetail d on c.CourseKind = d.DetailCode and d.MainCode = '005') CourseKindDesc," + 
				     "  (select ifnull(d.desc, '') from Student c left join CodeDetail d on c.Level = d.DetailCode and d.MainCode = '002') LevelDesc," + 
				     "  a.RollCallTime," + 
				     "  ifnull(a.Special, '') Special " + 
				     "FROM " + 
				     "  RollCallUploadDetail a " + 
				     "LEFT JOIN " + 
				     "  Student b on a.StudentNo = b.StudentNo " + 
				     "WHERE " + 
				     "  substr(a.RollCallTime, 1, 10) >= ? and substr(a.RollCallTime, 1, 10) <= ?";
		
		if (tfConditionValue.getText().trim().length() > 0) {
			if (cbCondition.getValue().equalsIgnoreCase("學員編號")) {
				sql = sql + " and a.StudentNo = ?";
			} else if (cbCondition.getValue().equalsIgnoreCase("身份證字號")) {
				sql = sql + " and b.ID = ?";
			} else if ((cbCondition.getValue().equalsIgnoreCase("姓名"))) {
				sql = sql + " and b.Name = ?";
			}
		}
		sql = sql + " order by a.RollCallTime desc";

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
			if (tfConditionValue.getText().length() > 0) {
				pstmt.setString(3, tfConditionValue.getText().trim()); //點名篩選條件值				
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
				rcd.setSpecial(rs.getString("Special"));
				
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

	//刪除指定批次的所有資料	
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
	
	@Override
	public void start(Stage arg0) throws Exception {
		
	}
	
}
