/* 
 * Colors Sports Club 點名資料上傳 Controller
 * 
 * @author 黃郁授,吳彥儒
 * @date 2020/09/22
 * 
 * 備忘：
 * 1.因為已經有了特色課程下拉選單，原本的RollCallDetail裡面的special相關程式碼是否仍要保留(等要寫資料庫時再決定)
 * 2.上傳批次記錄那邊增加刪除已寫入資料庫的點名資料(整批刪除)
 * 3.寫入資料庫後，記得於上傳批次紀錄新增一筆
 * 4.上傳檔時，檢核是否已上傳過該日的資料
 */

package com.wj.clubmdm.application;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.wj.clubmdm.component.BtnDelRollCallUpload;
import com.wj.clubmdm.component.ChoiceBoxImport;
import com.wj.clubmdm.component.ChoiceBoxSpecial;
import com.wj.clubmdm.vo.Message;
import com.wj.clubmdm.vo.RollCallUploadBatch;
import com.wj.clubmdm.vo.RollCallUploadDetail;

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
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import rhinoceros.util.date.SystemTime;
import rhinoceros.util.db.DBConnectionFactory;
import rhinoceros.util.file.UTF8FileReader;


public class RollCallUploadController extends Application {
	
	private Logger logger = Logger.getLogger(RollCallUploadController.class);
	//暫存點名資料用
	private TreeMap<String, RollCallUploadDetail> rollCallDetails = null;
	//暫存讀取時的點名檔絕對路徑
	private String fromPath = null;
	//備份時點名檔的目錄名稱
	private String backupFolder = "backup";
	//暫存選擇的點名日期
	private String rollCallDate = "";
	
	@FXML
	private DatePicker dpChoiceRollCallDate; //選擇點名日期
	@FXML
	private TextField tfFilePath; //點名檔絕對路徑
	@FXML
	private Button btnChoiceRollCallFile; //選擇點名檔
	@FXML
	private Button btnCheckData; //檢查檔案資料
	@FXML
	private Button btnImport; //確認匯入點名檔
	@FXML
	private TableView<RollCallUploadDetail> tvRollCallDetail; //點名資料
	@FXML
	private TableColumn<RollCallUploadDetail, String> colSeqNo; //點名資料_流水號
	@FXML
	private TableColumn<RollCallUploadDetail, String> colStudentNo; //點名資料_學員編號
	@FXML
	private TableColumn<RollCallUploadDetail, String> colName; //點名資料_姓名
	@FXML
	private TableColumn<RollCallUploadDetail, String> colDepartment; //點名資料_上課分部
	@FXML
	private TableColumn<RollCallUploadDetail, String> colCourseKind; //點名資料_類別
	@FXML
	private TableColumn<RollCallUploadDetail, String> colLevel; //點名資料_程度
	@FXML
	private TableColumn<RollCallUploadDetail, String> colRollCallDate; //點名資料_點名日期	
	@FXML
	private TableColumn<RollCallUploadDetail, ChoiceBoxSpecial> colRollSpecial; //點名資料_特色課程	
	//private TableColumn<RollCallDetail, String> colRollSpecial; //點名資料_特色課程	
	@FXML
	private TableColumn<RollCallUploadDetail, ChoiceBoxImport> colRollImport; //點名資料_是否匯入	
	@FXML
	private TableView<RollCallUploadBatch> tvRollCallBatch; //點名批次
	@FXML
	private TableColumn<RollCallUploadBatch, String> colBatchRollCallDate; //點名批次_點名日期
	@FXML
	private TableColumn<RollCallUploadBatch, String> colBatchRollCreateTime; //點名批次_匯入時間
	@FXML
	private TableColumn<RollCallUploadBatch, BtnDelRollCallUpload> colBatchDel; //點名批次_刪除
	@FXML
	private TableView<Message> tvMsg; //訊息TableView
	@FXML
	private TableColumn<Message, String> colMsgTime; //訊息時間
	@FXML
	private TableColumn<Message, String> colMsgContent; //訊息內容
	
	/*
	 * 初始化
	 */
	public void initialize() {
		//建立上傳登錄明細TableView資料連結
		colSeqNo.setCellValueFactory(new PropertyValueFactory<>("seqNo"));
		colStudentNo.setCellValueFactory(new PropertyValueFactory<>("studentNo"));
		colName.setCellValueFactory(new PropertyValueFactory<>("name"));
		colDepartment.setCellValueFactory(new PropertyValueFactory<>("department"));
		colCourseKind.setCellValueFactory(new PropertyValueFactory<>("courseKind"));
		colLevel.setCellValueFactory(new PropertyValueFactory<>("level"));
		colRollCallDate.setCellValueFactory(new PropertyValueFactory<>("rollCallTime"));
		colRollSpecial.setCellValueFactory(new PropertyValueFactory<>("cbSpecial"));
		//colRollSpecial.setCellValueFactory(new PropertyValueFactory<>("special"));
		colRollImport.setCellValueFactory(new PropertyValueFactory<>("cbImport"));

		//建立上傳批次TableView資料連結
		colBatchRollCallDate.setCellValueFactory(new PropertyValueFactory<>("rollCallDate"));
		colBatchRollCreateTime.setCellValueFactory(new PropertyValueFactory<>("createTime"));
		colBatchDel.setCellValueFactory(new PropertyValueFactory<>("btnDelBatch"));
		
		//建立訊息TableView資料連結
		colMsgTime.setCellValueFactory(new PropertyValueFactory<>("msgTime"));
		colMsgContent.setCellValueFactory(new PropertyValueFactory<>("msgContent"));
		
		//查詢匯入批次紀錄
		queryUploadBatch();
		
		//設定日期選擇器的格式
		StringConverter<LocalDate> converter = new StringConverter<LocalDate>() {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			@Override public String toString(LocalDate date) {
				if(date !=null ) {
					return formatter.format(date);
				} else {
					return "";
				}
			}
			
			@Override public LocalDate fromString(String string) {
				if (string != null && !string.isEmpty()) {
					return LocalDate.parse(string, formatter);
				} else {
					return null;
				}
			}
		};
		dpChoiceRollCallDate.setConverter(converter);		
	}

	/*
	 * tvMsg.getItems().clear(); 
	 * 用來寫入訊息至TableView
	 * @param msgContent 訊息內容
	 */
	public void insertMsg(String msgContent) {
		// 避免訊息寫入過快，造成TableView因鍵值Dup問題，秀不出資料
		try {
			Thread.currentThread();
			Thread.sleep(10);
		} catch (InterruptedException e) {
			logger.info(e.getMessage(), e);
		}
		Message msg = new Message();
		msg.setMsgContent(msgContent);		
		tvMsg.getItems().add(msg);
	}
	
	/*
	 * 查詢點名檔上傳批次
	 */
	public void queryUploadBatch() {
		tvRollCallBatch.getItems().clear(); //清除點名批次TableView
		String sql = 
				"SELECT " +
			    "   FileName," +
		        "   RollCallDate," +
				"	CreateTime " + 
				"FROM " + 
				"  RollCallUploadBatch " + 
				"ORDER BY CreateTime DESC";
		DBConnectionFactory dbf = new DBConnectionFactory();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {		
			RollCallUploadBatch rcub = null;  
			String rollCallDateTemp = null; //yyyy-mm-dd
			String rollCallImportTimeTemp = null; //只秀到秒 yyyy-mm-dd HH:mm:ss
			BtnDelRollCallUpload btnDel = null;
			conn = dbf.getSQLiteCon("", "Club.dll");
			pstmt = conn.prepareStatement(sql);
			pstmt.clearParameters();
			rs = pstmt.executeQuery();
			/*
			 * 01234567890ABC
			 * yyyymmddHHmmssSSS
			 */
			while (rs.next()) {
				rcub = new RollCallUploadBatch();
				rcub.setFileName(rs.getString("FileName"));
				
				//把點名日期由yyyymmdd轉成yyyy-mm-dd格式秀至TableView
				rollCallDateTemp = rs.getString("RollCallDate");
				rollCallDateTemp = rollCallDateTemp.substring(0, 4) + "-" + rollCallDateTemp.substring(4, 6) + "-" + rollCallDateTemp.substring(6);				
				rcub.setRollCallDate(rollCallDateTemp);
				
				//把匯入時間由yyyymmddHHmmssSSS轉成yyyy-mm-dd HH:mm:ss(捨棄毫秒)
				rollCallImportTimeTemp = rs.getString("CreateTime");
				rollCallImportTimeTemp = 
						rollCallImportTimeTemp.substring(0, 4) + "-" +
						rollCallImportTimeTemp.substring(4, 6) + "-" +
						rollCallImportTimeTemp.substring(6, 8) + " " +
						rollCallImportTimeTemp.substring(8, 10) + ":" +
						rollCallImportTimeTemp.substring(10, 12) + ":" +
						rollCallImportTimeTemp.substring(12, 14);
						
				rcub.setCreateTime(rollCallImportTimeTemp);
				btnDel = new BtnDelRollCallUpload();
				btnDel.autosize();
				btnDel.setText("刪除");
				btnDel.setFileName(rs.getString("FileName"));
				btnDel.setOnAction(new EventHandler<ActionEvent>() {
			        public void handle(ActionEvent event) {
			        	BtnDelRollCallUpload btnDel = (BtnDelRollCallUpload)event.getSource();
			        	delUploadBatch(btnDel);
			        }
			    });
				rcub.setBtnDelBatch(btnDel);
				tvRollCallBatch.getItems().add(rcub);
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
	public void delUploadBatch(BtnDelRollCallUpload btnDel) {
		
		/*
		 * 跳出確認刪除的視窗
		 * ★想要預設Button在否，但還試不出來
		 */
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setHeaderText("點名批次刪除確認");
		alert.setContentText("確認刪除此批次所有點名資料？");
		Optional<ButtonType> buttonType = alert.showAndWait();
		if (buttonType.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {

	    } else {
			//點「否」的話，就不會刪除資料。
			return;
		}
		
		String sqlDelBatch = "delete from RollCallUploadBatch where FileName = ?";
		String sqlDelDetail = "delete from RollCallUploadDetail where FileName = ?";

		DBConnectionFactory dbf = new DBConnectionFactory();
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {		
			conn = dbf.getSQLiteCon("", "Club.dll");
			pstmt = conn.prepareStatement(sqlDelBatch);
			pstmt.clearParameters();
			pstmt.setString(1, btnDel.getFileName());
			pstmt.executeUpdate();
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		} 
		
		try {
			pstmt = conn.prepareStatement(sqlDelDetail);
			pstmt.clearParameters();
			pstmt.setString(1, btnDel.getFileName());
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
		queryUploadBatch();
	}
	
	/*
	 * 將TSV檔案吃入
	 */
	public void checkRollCallFile() {
		//清除訊息
		tvMsg.getItems().clear(); 
		chkField(); //檢核欄位值
		//檢核Excel檔案是否存在
		File file = new File(tfFilePath.getText().trim());

		//提示檔案不存在
		if (!file.exists()) {
			insertMsg("點名檔不存在，請確認檔案路徑！");
			return;
		}
				
		//把讀檔來源的絕對路徑先暫存
		fromPath = tfFilePath.getText().trim(); 
		
		//暫存所選擇的日期，轉成yyyyMMdd這個格式
		rollCallDate = dpChoiceRollCallDate.getValue().toString().replace("-", "");		
		
		//建立用來存TSV檔裡面內容的暫存變數
		ArrayList<String> data = null; 
		//建立之後要給點名資料ViewTable用的暫存變數
		rollCallDetails = new TreeMap<String, RollCallUploadDetail>();
		//將檔案內容讀出
		UTF8FileReader u8r = new UTF8FileReader();
		//用來判斷第一筆(欄位標題)不吃的計算器
		Integer rowCount = 0;
		//流水號(不含標題列)
		Integer seqNo = 0;
		//有問題的筆數
		Integer errorCount = 0;
		try {
			data = u8r.replace(tfFilePath.getText().trim(), "\\s+", "|", true);
			RollCallUploadDetail rcd = null;
			//逐一整理
			for (String s : data) {
				rowCount++;
				/*
				 * 第1筆為標題列 學員編號	上課日期 >> 略過不處理
				 * 第2筆開始為資料列，樣式如下
				 * A000001|2020/9/24|上午|11:09:01
				 * A000002|2020/9/24|下午|12:08:59
				 */
				if (rowCount > 1) {
					rcd = turnIntoRollCallDetail(s);
					if (rcd != null) {
						//先確認資料是否有重覆
						if (rollCallDetails.containsKey(rcd.getRollCallTime() + rcd.getStudentNo())) {
							errorCount++;
							insertMsg(s + " 資料重覆，自動排除(只保留1筆)。");
						} else {
							seqNo++;
							rcd.setSeqNo(seqNo.toString());
							rollCallDetails.put(rcd.getRollCallTime() + rcd.getStudentNo(), rcd);							
						}
					} else {
						errorCount++; //累計有問題筆數
					}
				}
			}
		} catch (Exception e) {
			tvMsg.getItems().clear(); 
			insertMsg("點名檔內容格式有誤，請確認點名檔內容，修正後，");
			insertMsg("重新「選擇」>「檢查檔案資料」");
			logger.info(e.getMessage(), e);
			return;		
		}
		// 能到這邊代表點名檔讀取成功
		showRollCallDetail();
		// 若有問題筆數>0，則提醒
		if (errorCount > 0) {
			insertMsg("");
			insertMsg("共 " + errorCount.toString() + " 筆 讀取失敗，請修正點名檔內文後，");		
			insertMsg("重新執行「檢查檔案資料」再執行「將下方表格內容匯入資料庫」。");		
		}
	}

	/*
	 * 將上傳已整理至TreeMap的資料秀至TableView裡面
	 */
	private void showRollCallDetail() {
		tvRollCallDetail.getItems().clear(); //先清除TableView中的明細
		Iterator<String> iter = rollCallDetails.keySet().iterator();
		while (iter.hasNext()) {
			tvRollCallDetail.getItems().add(rollCallDetails.get((String)iter.next()));
		}
	}
	
	/*
	 * 將 A000002|2020/9/24|上午|12:01:01 轉成 RollCallDetail 物件後回傳
	 */
	private RollCallUploadDetail turnIntoRollCallDetail(String rawData) {
		RollCallUploadDetail rcd = new RollCallUploadDetail();
		String[] arrayData = rawData.split("\\|"); //像「|」這種特殊字元做為分隔符號，前面要加\\，split才辨認的出來
		//第1個欄位的值的格式必須是 A + 6碼流水號，才處理
		if (arrayData[0].substring(0, 1).equalsIgnoreCase("A") && arrayData[0].length() == 7) {
			rcd.setStudentNo(arrayData[0]);
		} else {
			insertMsg(rawData + " 學員編號格式不正確");
			return null;
		}
		
		//轉換日期格式 由 2020/9/4 轉成 2020-09-04
		String[] tempDate = arrayData[1].split("/");
		
		//年份欄位若不是4碼代表資料有問題
		if (tempDate[0].length() != 4) {
			insertMsg(rawData + " 年份不正確");
			return null;
		}
		
		//月份左邊補0
		if (tempDate[1].length() <= 1) {
			tempDate[1] = "0" + tempDate[1];
		}
		
		//日期左邊補0
		if (tempDate[2].length() <= 1) {
			tempDate[2] = "0" + tempDate[2];
		}
		arrayData[1] = tempDate[0] + "-" + tempDate[1] + "-" + tempDate[2];
		
		//第3個欄位欄位必須是 上午 or 下午 這兩個字樣
		if (arrayData[2].equalsIgnoreCase("上午") || arrayData[2].equalsIgnoreCase("下午")) {		
		} else {
			insertMsg(rawData + " 點名時間格式有誤，缺少上午、下午字樣");
			return null;			
		}
				
		//第4個欄位為HH:MM:SS
		if (arrayData[3].length() != 8) {
			return null;
		} else {
			//把小時的部份改為24小時制
			String[] tempTime = arrayData[3].split(":");
			Integer hour = Integer.parseInt(tempTime[0]);
			try {
				if (hour >= 13 || hour == 0) {
					insertMsg("");
					insertMsg(rawData + " 點名時間格式有誤 時間須介於12:00-11:59");
					insertMsg("補充說明：");
					insertMsg("(1)上午 12:01 為 24小時制的 00:01");
					insertMsg("(2)上午 11:59 為 24小時制的 11:59");
					insertMsg("(3)下午 12:01 為 24小時制的 12:01");
					insertMsg("(4)下午 11:59 為 24小時制的 23:59");
					insertMsg("");
					return null;
				}
	
				// 上午12:01:59，若轉成24小時制的話，是00:01:59，故只有在上午且小時為12時，要改小時為0
				if (arrayData[2].equalsIgnoreCase("上午") && hour == 12) {
					hour = 0;
				}	
				
				if (hour.toString().length() < 2) {
					tempTime[0] = "0" + hour.toString();										
				} else {
					tempTime[0] = hour.toString();					
				}
				arrayData[3] = tempTime[0] + ":" + tempTime[1] + ":" + tempTime[2];
			} catch (Exception e) {
				insertMsg(rawData + " 點名時間格式有誤 ");
				return null;				
			}
		}
		
		//若資料的點名日期與上方選擇的匯入日不符合時，則不列入TableView中
		if (!arrayData[1].equalsIgnoreCase(dpChoiceRollCallDate.getValue().toString())) {
			insertMsg(rawData + "資料 點名日期 不符，已忽略。");
			return null;
		}
		
		//到這邊代表時間格式正確，將其組成 yyyy-mm-dd hh(24小時制):mm:ss
		rcd.setRollCallTime(arrayData[1] + " " + arrayData[3]);
		//★(可能要刪)先預設特色課程為N
		rcd.setSpecial("N");
		//取得學員相關資料		
		//ifnull用法：ifnull(Note,'') 當Note為null時，以空白取代
		String sql = 
				"SELECT " +
		        "   f.StudentNo," +
				"	f.Name," + 
				"	(select d.desc from Student s left join CodeDetail d on s.Department = d.DetailCode and d.MainCode = '004') DepartmentDesc," + 
				"	(select d.desc from Student s left join CodeDetail d on s.CourseKind = d.DetailCode and d.MainCode = '005') CourseKindDesc," + 
				"	(select d.desc from Student s left join CodeDetail d on s.Level = d.DetailCode and d.MainCode = '002') LevelDesc " +				
				"FROM " + 
				"  Student f " + 
				"WHERE " + 
				"  f.StudentNo = ?";
		DBConnectionFactory dbf = new DBConnectionFactory();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int count = 0; //用來判斷是否有提到學生的對應資料
		try {
			ChoiceBoxSpecial cbSpecial = null;
			//建立 特色課程 下拉選單的清單內容，此下拉選單為「特色課程」欄位，只有N/Y兩個值
			ObservableList<String> specialItems = FXCollections.observableArrayList("N", "Y");			

			ChoiceBoxImport cbImport = null;
			//建立 是否匯入 下拉選單的清單內容，此下拉選單為「是否匯入」欄位，只有N/Y兩個值
			ObservableList<String> importItems = FXCollections.observableArrayList("Y", "N");			
			
			conn = dbf.getSQLiteCon("", "Club.dll");
			pstmt = conn.prepareStatement(sql);
			pstmt.clearParameters();
			pstmt.setString(1, arrayData[0]);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				count++;
				rcd.setStudentNo(rs.getString("StudentNo"));
				rcd.setName(rs.getString("Name"));
				rcd.setDepartment(rs.getString("DepartmentDesc"));
				rcd.setCourseKind(rs.getString("CourseKindDesc"));
				rcd.setLevel(rs.getString("LevelDesc"));
				
				//建立特色課程的下拉選單(預設N)
				cbSpecial = new ChoiceBoxSpecial();
				cbSpecial.autosize();
				cbSpecial.setItems(specialItems);
				cbSpecial.setRollCallTime(rcd.getRollCallTime());
				cbSpecial.setStudentNo(rcd.getStudentNo());
				cbSpecial.getSelectionModel().select("N"); //把N當成預設值

				//建立是否匯入的下拉選單(預設y)
				cbImport = new ChoiceBoxImport();
				cbImport.autosize();
				cbImport.setItems(importItems);
				cbImport.setRollCallTime(rcd.getRollCallTime());
				cbImport.setStudentNo(rcd.getStudentNo());
				cbImport.getSelectionModel().select("Y"); //把N當成預設值
				
				rcd.setCbSpecial(cbSpecial); //把特色課程的下拉選單加給RollCallDetail物件，當作屬性
				rcd.setCbImport(cbImport); //把匯入的下拉選單加給RollCallDetail物件，當作屬性
			}
			//若沒有找到資料時
			if (count <= 0) {
				rcd.setStudentNo(rs.getString("無學員資料"));
				rcd.setName(rs.getString(""));
				rcd.setDepartment(rs.getString(""));
				rcd.setCourseKind(rs.getString(""));
				rcd.setLevel(rs.getString(""));				
				rcd.setCbSpecial(cbSpecial); //把特色課程的下拉選單加給RollCallDetail物件，當作屬性
				rcd.setCbImport(cbImport); //把匯入的下拉選單加給RollCallDetail物件，當作屬性
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
		return rcd;
	}
		
	/*
	 * 檢查輸入欄位
	 */
	private boolean chkField() {				
		//檢核日期是否正確
		if (dpChoiceRollCallDate.getValue() == null) {		
			tvMsg.getItems().clear(); 
			insertMsg("「點名日期」輸入有誤！");
			return false;			
		}
		return true;
	}
	
	/*
	 * 選擇點名檔(.tsv)
	 */
	public void choiceFile() {
		FileChooser filechooser = new FileChooser();
		filechooser.setTitle("選擇 點名檔(*.tsv)...");
		filechooser.setInitialDirectory(new File("."));
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("TSV", "*.tsv");
		filechooser.getExtensionFilters().add(filter);
		/*
		 * 跳出對話框時，有兩種方法取得獨佔視窗的對話框
		 * (1)透過button去取得Stage，這樣的效果會是獨佔模式
		 * (2)上一層在建立controller時，把stage當成參數傳入給controller的屬性，這樣可以直接透過屬性去取得stage
		 */
		File file = filechooser.showOpenDialog((Stage) btnChoiceRollCallFile.getScene().getWindow());
		if (file != null) {
			tfFilePath.setText(file.getAbsolutePath());			
		} else {
			tfFilePath.setText("");						
		}
	}
		
	/*
	 * 讀取TableVeiw資料，寫入資料庫
	 */
	public void readTableView() {
		//判斷TableView的筆數，若=0，則代表要重新再檢查資料
		if (tvRollCallDetail.getItems().size() <= 0) {
			tvMsg.getItems().clear(); 
			insertMsg("表格中無資料，請重新執行「檢查檔案資料」！");
			return;
		}
		
		//取得現在的系統時間做為檔名
		SystemTime st = new SystemTime();
		String backupFileName = "color_rollcall_" + rollCallDate + "_" + st.getNowTime("yyyyMMddHHmmss") + ".tsv";
	    String backupPath = backupFolder + "/" + backupFileName;

	    //複製檔案
	    try {
	    	FileOutputStream fos = new FileOutputStream(new File(backupPath));
	    	Path inputPath = new File(fromPath).toPath();
	    	Files.copy(inputPath, fos);
	    } catch(Exception e) {
	    	tvMsg.getItems().clear(); 
	    	insertMsg("檔案備份失敗，請洽系統開發人員！");
	    	logger.info(e.getMessage(), e);
	    }
	    
	    //寫一筆到資料庫 RollCallUploadBatch Table(批次上傳記錄)中 
	    String sqlInsertBatch = "insert into RollCallUploadBatch values(?, ?, ?)";
		DBConnectionFactory dbcf = new DBConnectionFactory();
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = dbcf.getSQLiteCon("", "Club.dll");
			pstmt = conn.prepareStatement(sqlInsertBatch);
			pstmt.setString(1, backupFileName);
			pstmt.setString(2, rollCallDate);
			pstmt.setString(3, st.getNowTime("yyyyMMddHHmmssSSS"));
			pstmt.executeUpdate();				
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
	    
		//取得TableView各列資料並寫入資料庫
		String insertSystemTime = st.getNowTime("yyyyMMddHHmmssSSS");
		String sqlInsertDetail = "insert into RollCallUploadDetail values(?, ?, ?, ?, ?, null)";
		ObservableList<RollCallUploadDetail> rcds = tvRollCallDetail.getItems();
		try {
			conn = dbcf.getSQLiteCon("", "Club.dll");
		} catch (Exception e) {
			logger.info(e.getMessage(), e);				
		}
		
		for(RollCallUploadDetail data : rcds) {
			try {
				//是否匯入=Y的才寫入資料庫
				if (data.getCbImport().getValue().equalsIgnoreCase("Y")) {
					pstmt = conn.prepareStatement(sqlInsertDetail);
					pstmt.setString(1, backupFileName);
					pstmt.setString(2, data.getStudentNo());
					pstmt.setString(3, data.getRollCallTime());
					pstmt.setString(4, data.getCbSpecial().getValue());
					pstmt.setString(5, insertSystemTime);
					pstmt.executeUpdate();
				}
			} catch (Exception e) {
				insertMsg(data.getStudentNo() + " " + data.getRollCallTime() + " 已存在資料庫!");
				logger.info(e.getMessage(), e);
			} 	
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
		
		//秀訊息寫入成功
		insertMsg("點名資料 寫入 資料庫成功!");
		//清除表格
		tvRollCallDetail.getItems().clear();
		//清除暫存變數TreeMap
		rollCallDetails.clear();
		
	}
	
	@Override
	public void start(Stage arg0) throws Exception {
		
	}
	
}
