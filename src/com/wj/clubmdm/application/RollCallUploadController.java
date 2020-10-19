/* 

 * Colors Sports Club 點名資料上傳 Controller
 * 
 * @author 黃郁授,吳彥儒
 * @date 2020/09/22
 */

package com.wj.clubmdm.application;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

import org.apache.log4j.Logger;


import com.wj.clubmdm.vo.RollCallDetail;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import rhinoceros.util.db.DBConnectionFactory;
import rhinoceros.util.file.UTF8FileReader;


public class RollCallUploadController extends Application {
	
	private Logger logger = Logger.getLogger(RollCallUploadController.class);
	//暫存點名資料用
	private TreeMap<String, RollCallDetail> rollCallDetails = null;
	
	@FXML
	private DatePicker dpChoiceRollCallDate; //選擇點名日期
	@FXML
	private TextField tfRollCallDate; //點名日期
	@FXML
	private TextField tfFilePath; //點名檔絕對路徑
	@FXML
	private Button btnChoiceRollCallFile; //選擇點名檔
	@FXML
	private Button btnCheckData; //檢查檔案資料
	@FXML
	private TableView<RollCallDetail> tvRollCallDetail; //點名資料
	@FXML
	private TableColumn<RollCallDetail, String> colSeqNo; //點名資料_流水號
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
	private TableColumn<RollCallDetail, String> colRollCallDate; //點名資料_點名日期	
	@FXML
	private TableColumn<RollCallDetail, String> colRollSpecial; //點名資料_特色課程	

	
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
		colRollSpecial.setCellValueFactory(new PropertyValueFactory<>("special"));
		//★還缺刪除button及Special要改成下拉選單
		
		
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
	 * 將TSV檔案吃入
	 */
	public void checkRollCallFile() {
		//提示錯誤的對話框
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setHeaderText("資料輸入有誤");
		
		chkField(); //檢核欄位值
		//檢核Excel檔案是否存在
		File file = new File(tfFilePath.getText().trim());
		if (!file.exists()) {
			alert.setContentText("點名檔不存在，請確認檔案路徑！");
			alert.showAndWait();
			return;		
		}
		//建立用來存TSV檔裡面內容的暫存變數
		ArrayList<String> data = null; 
		//建立之後要給點名資料ViewTable用的暫存變數
		rollCallDetails = new TreeMap<String, RollCallDetail>();
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
			RollCallDetail rcd = null;
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
							logger.info(rcd.getRollCallTime() + " " + rcd.getStudentNo() + "點名資料重覆，自動排除(只保留1筆)。");
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
			alert.setHeaderText("點名檔內容格式有誤！");
			alert.setContentText("請確認點名檔內文！");
			alert.showAndWait();
			logger.info(e.getMessage(), e);
			return;		
		}
		// 能到這邊代表點名檔讀取成功
		showRollCallDetail();
		// 若有問題筆數>0，則提醒
		if (errorCount > 0) {
			alert.setHeaderText("提醒");
			alert.setContentText("有 " + errorCount.toString() + " 筆 點名資料有誤，請確認點名檔內文格式，修正後，重新執行「檢查檔案資料」！");
			alert.showAndWait();			
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
	private RollCallDetail turnIntoRollCallDetail(String rawData) {
		RollCallDetail rcd = new RollCallDetail();
		String[] arrayData = rawData.split("\\|"); //像「|」這種特殊字元做為分隔符號，前面要加\\，split才辨認的出來
		//第1個欄位的值的格式必須是 A + 6碼流水號，才處理
		if (arrayData[0].substring(0, 1).equalsIgnoreCase("A") && arrayData[0].length() == 7) {
			rcd.setStudentNo(arrayData[0]);
		} else {
			logger.info("點名檔員編格式不正確 [" + rawData + "]");
			return null;
		}
		
		//轉換日期格式 由 2020/9/4 轉成 2020-09-04
		String[] tempDate = arrayData[1].split("/");
		
		//年份欄位若不是4碼代表資料有問題
		if (tempDate[0].length() != 4) {
			logger.info("點名檔年份不正確 [" + rawData + "]");
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
			logger.info("點名檔時間格式有誤，缺少上午、下午字樣 [" + rawData + "]");
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
					logger.info("點名檔時間格式有誤 上午為12:00-11:59 下午為12:00-11:59 [" + rawData + "]");
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
			} catch(Exception e) {
				logger.info("點名檔時間格式有誤 " + rawData);
				return null;				
			}
		}
		
		//到這邊代表時間格式正確，將其組成 yyyy-mm-dd hh(24小時制):mm:ss
		rcd.setRollCallTime(arrayData[1] + " " + arrayData[3]);
		//先預設特色課程為N
		rcd.setSpecial("N");
		//★特色課程要用下拉選單(之後再加)
		//★操作要用下拉選單(之後再加)
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
			}
			//若沒有找到資料時
			if (count <= 0) {
				rcd.setStudentNo(rs.getString("無學員資料"));
				rcd.setName(rs.getString(""));
				rcd.setDepartment(rs.getString(""));
				rcd.setCourseKind(rs.getString(""));
				rcd.setLevel(rs.getString(""));				
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
		//提示錯誤的對話框
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setHeaderText("資料輸入有誤");
				
		//檢核日期是否正確
		if (dpChoiceRollCallDate.getValue() == null) {			
			alert.setContentText("點名日期不正確！");
			alert.showAndWait();
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
		
	@Override
	public void start(Stage arg0) throws Exception {
		
	}
	
}
