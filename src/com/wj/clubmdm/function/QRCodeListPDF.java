/* 
 * Colors Sports Club MDM 產出指定條件的學員QRCode清單PDF檔
 * 
 * @author 黃郁授,吳彥儒
 * @date 2021/03/22
 */

package com.wj.clubmdm.function;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.wj.clubmdm.vo.QueryStudentCondition;

import rhinoceros.util.date.SystemTime;
import rhinoceros.util.db.DBConnectionFactory;

public class QRCodeListPDF {
	private Logger logger = Logger.getLogger(QRCodeListPDF.class);	
	private QueryStudentCondition queryStudentCondition;
	private Document document;
	private BaseFont baseFont;
	private Font fontBlackBigCN; //黑色12號字體的樣式
	private Font fontBlackMidCN; //黑色10號字體的樣式
	private Font fontBlackSmallCN; //黑色8號字體的樣式

	private ArrayList<String> alStudents = new ArrayList<String>(); //用來存放查詢到的「學員編號_姓名」
	
	private PdfWriter writer;
	
	Image logo; //Logo圖檔

	private OutputStream os;
	private File file;
	private String pdfFileName;
	

	/*
	 * 設定查詢條件
	 * @param qsc 設定查詢條件
	 * @return trun 設定成功 false 設定失敗
	 */
	public boolean setCondition(QueryStudentCondition qsc) {
		if (qsc != null) {
			this.queryStudentCondition = qsc;
		} else {
			logger.info("查詢條件有誤。");
			return false;
		}
		return true;
	}

	/*
	 * 依查詢條件查出學員資料
	 * @return true 有查到資料 false 沒查到資料
	 */
	private boolean queryStudent() {
		// 有使用到的條件數
		StringBuilder sql = new StringBuilder("Select StudentNo, Name From Student Where ");
		/*
		 * 測試
		 * QueryStudentCondition queryStudentCondition = new QueryStudentCondition();
		 * queryStudentCondition.setStatus("N"); 
		 */
		
		// 判斷是否為第1個條件
		boolean firstCondition = true;
		
		// 學員狀態
		if (queryStudentCondition.getStatus() == null || queryStudentCondition.getStatus().trim().length() <=0) {			
		} else {
			if (!firstCondition) {
				sql.append(" and ");
			}
			sql.append(" Status = '" + queryStudentCondition.getStatus().trim() + "' ");
			firstCondition = false;
		}
			
		// 課程類別
		if (queryStudentCondition.getCourseKind() == null || queryStudentCondition.getCourseKind().trim().length() <=0) {			
		} else {
			if (!firstCondition) {
				sql.append(" and ");
			}
			sql.append(" CourseKind = '" + queryStudentCondition.getCourseKind().trim() + "' ");
			firstCondition = false;
		}
		
		// 上課分部
		if (queryStudentCondition.getDepartment() == null || queryStudentCondition.getDepartment().trim().length() <=0) {			
		} else {
			if (!firstCondition) {
				sql.append(" and ");
			}
			sql.append(" Department = '" + queryStudentCondition.getDepartment().trim() + "' ");
			firstCondition = false;
		}
		
		// 入隊日區間
		if (queryStudentCondition.getJoinDateStr() == null || queryStudentCondition.getJoinDateStr().trim().length() <=0 ||
		    queryStudentCondition.getJoinDateEnd() == null || queryStudentCondition.getJoinDateEnd().trim().length() <=0) {			
		} else {
			if (!firstCondition) {
				sql.append(" and ");
			}
			sql.append(" JoinDate >= '" + queryStudentCondition.getJoinDateStr().trim() + "' and JoinDate <= '" + queryStudentCondition.getJoinDateEnd().trim() + "' ");				
			firstCondition = false;
		}
		
        // 程度
		if (queryStudentCondition.getLevel() == null || queryStudentCondition.getLevel().trim().length() <=0 ) {			
	    } else {
			if (!firstCondition) {
				sql.append(" and ");
			}
	    	sql.append(" Level = '" + queryStudentCondition.getLevel().trim() + "' ");
	    }
		
		// 成員所屬
		if (queryStudentCondition.getMemberBelong() == null || queryStudentCondition.getMemberBelong().trim().length() <=0) {			
		} else {
			if (!firstCondition) {
				sql.append(" and ");
			}
			sql.append(" MemberBelong = '" + queryStudentCondition.getMemberBelong().trim() + "' ");
			firstCondition = false;
		}
		
		// 姓名(用like)
		if (queryStudentCondition.getName() == null || queryStudentCondition.getName().trim().length() <=0) {			
		} else {
			if (!firstCondition) {
				sql.append(" and ");
			}
			sql.append(" Name like '%" + queryStudentCondition.getName().trim() + "%' ");
			firstCondition = false;
		}
		
		// 學校(用like)
		if (queryStudentCondition.getSchool() == null || queryStudentCondition.getSchool().trim().length() <=0) {			
		} else {
			if (!firstCondition) {
				sql.append(" and ");
			}
			sql.append(" School like '%" + queryStudentCondition.getSchool().trim() + "%' ");
			firstCondition = false;
		}
		
		// 學齡
		if (queryStudentCondition.getSchoolLevel() <= 0) {			
		} else {
			if (!firstCondition) {
				sql.append(" and ");
			}
			sql.append(" SchoolLevel = " + queryStudentCondition.getSchoolLevel() + " ");
			firstCondition = false;
		}
		
		// 性別
		if (queryStudentCondition.getSex() == null || queryStudentCondition.getSex().trim().length() <=0) {			
		} else {
			if (!firstCondition) {
				sql.append(" and ");
			}
			sql.append(" Sex = '" + queryStudentCondition.getSex().trim() + "' ");
			firstCondition = false;
		}
		
		// 學員編號
		if (queryStudentCondition.getStudentNo() == null || queryStudentCondition.getStudentNo().trim().length() <=0) {			
		} else {
			if (!firstCondition) {
				sql.append(" and ");
			}
			sql.append(" StudentNo = '" + queryStudentCondition.getStudentNo().trim() + "' ");
			firstCondition = false;
		}
		
		sql.append(" ORDER BY StudentNo ");
				
		DBConnectionFactory dbf = new DBConnectionFactory();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {		
			conn = dbf.getSQLiteCon("", "Club.dll");
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.clearParameters();
			rs = pstmt.executeQuery();
			alStudents.clear();
			while (rs.next()) {
				alStudents.add(rs.getString("StudentNo").trim() + "_" + rs.getString("Name"));				
			}	
			//若查不到資料，則回覆false
			if (alStudents.isEmpty()) {
				return false;
			}
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
			return false;
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
		return true;
	}
	
	/*
	 * 設定PDF文件格式
	 */
	public void createPDF() {
		SystemTime st = new SystemTime(); // 取得系統時間
		
		// 建立PDF檔
	    pdfFileName = "Backup/Report/QRCodeList_" + st.getNowTime("yyyyMMddHHmmss") + ".pdf";
		file = new File(pdfFileName);
		if (file.exists()) {
			file.delete();
		}
		
		/*
		 * 設定字型
		 */
		try {
			// 設定中文字型
			baseFont = BaseFont.createFont("YaHei Consolas Hybrid 1.12.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
			fontBlackBigCN = new Font(baseFont, 12, Font.NORMAL, BaseColor.BLACK);
			fontBlackMidCN = new Font(baseFont, 10, Font.NORMAL, BaseColor.BLACK);
			fontBlackSmallCN = new Font(baseFont, 8, Font.NORMAL, BaseColor.BLACK);
		} catch (Exception e) {
			logger.info("字型設定失敗。");
		}

		/*
		 * Document(紙張格式, 上邊距, 下邊距, 左邊距, 右邊距)
		 * 邊距是以磅為單位
		 * 72磅 = 2.54 cm，故 1cm 約 28.346 cm。但目前假定 28 = 1cm
		 * 如要設定A4橫式，可以用 PageSize.A4.rotate() 做為第1個參數
		 */
		document = new Document(PageSize.A4, 42, 42, 42, 42);		
		try {
			writer = PdfWriter.getInstance(document, new FileOutputStream(file));
		} catch (Exception e) {
			logger.info("建立PDF檔失敗。");
		}
	}
	
	/*
	 * 列印Logo、條件及頁碼
	 * @param currentPageNumber 目前頁碼
	 * @param totalPageNumber 全部頁數
	 */
	private void printCondition(int currentPageNumber, int totalPageNumber) {
		Paragraph emptyLine = new Paragraph(new Chunk(" ", fontBlackSmallCN)); //10號字體空白列

		try {
			logo = Image.getInstance("image/logo.png");
			logo.scaleToFit(85, 85);
			logo.setAbsolutePosition(37, 734);
		} catch (Exception e) {
			logger.info("logo圖檔讀取失敗");
		}
		
		StringBuffer sbLine1 = new StringBuffer();
		sbLine1.append("　　　　　　　檔案: " + pdfFileName + "　　　　頁碼:" + currentPageNumber + "/" + totalPageNumber);
		Paragraph pgLine1 = new Paragraph(new Chunk(sbLine1.toString(), fontBlackBigCN));
		
		ArrayList<String> desc = translationOfCondition();
        
		try {
			document.add(logo);
			document.add(pgLine1);
			document.add(emptyLine);
			int cntConditionRow = 5;
			for (String data : desc) {
				document.add(new Paragraph(new Chunk(data, fontBlackSmallCN)));
				cntConditionRow--;
			}
			for (int i = 1; i <= cntConditionRow; i++) {
				document.add(emptyLine);				
			}
    	} catch (Exception e) {
			logger.info(e.getMessage(), e);		
		}
	}
	
	/*
	 * 將條件翻譯成中文
	 */
	public ArrayList<String> translationOfCondition(){
		ArrayList<String> conditionDesc = new ArrayList<String>();
		StringBuffer sb1 = new StringBuffer();
		sb1.append("　　　　　　　　　　【條件】　");
		
		// 上課分部
		if (queryStudentCondition.getDepartment() == null || queryStudentCondition.getDepartment().trim().length() <= 0) {			
			sb1.append("上課分部:[無]  ");
		} else {
			sb1.append("上課分部:");
			sb1.append(queryCodeDesc("004", queryStudentCondition.getDepartment().trim()) + "  ");
		}

		// 課程類別
		if (queryStudentCondition.getCourseKind() == null || queryStudentCondition.getCourseKind().trim().length() <= 0) {			
			sb1.append("課程類別:[無]  ");
		} else {
			sb1.append("課程類別:");
			sb1.append(queryCodeDesc("005", queryStudentCondition.getCourseKind().trim()) + "  ");
		}		

		// 程度
		if (queryStudentCondition.getLevel() == null || queryStudentCondition.getLevel().trim().length() <= 0) {
			sb1.append("程度:[無]  ");
		} else {
			sb1.append("程度:");
			sb1.append(queryCodeDesc("002", queryStudentCondition.getLevel().trim()) + "  ");			
		}
		

		// 列印條件第1列
		conditionDesc.add(sb1.toString());

		StringBuffer sb2 = new StringBuffer();
		
		// 學員編號
		if (queryStudentCondition.getStudentNo() == null || queryStudentCondition.getStudentNo().trim().length() <= 0) {			
			sb2.append("　　　　　　　　　　　　　　　學員編號:[無]  ");			
		} else {
			sb2.append("　　　　　　　　　　　　　　　學員編號:" + queryStudentCondition.getStudentNo() + "  ");			
		}		

		// 姓名
		if (queryStudentCondition.getName() == null || queryStudentCondition.getName().trim().length() <= 0) {			
			sb2.append("姓名:[無]  ");
		} else {
			sb2.append("姓名:" + queryStudentCondition.getName() + "  ");	
		}		

		// 性別
		if (queryStudentCondition.getSex() == null || queryStudentCondition.getSex().trim().length() <= 0) {			
			sb2.append("性別:[無]  ");
		} else {
			sb2.append("性別:" + queryCodeDesc("001", queryStudentCondition.getSex().trim()) + "  ");						
		}		

		// 學校
		if (queryStudentCondition.getSchool() == null || queryStudentCondition.getSchool().trim().length() <= 0) {			
			sb2.append("學校:[無]  ");
		} else {
			sb2.append("學校:" + queryStudentCondition.getSchool() + "  ");			
		}

		// 學齡
		if (queryStudentCondition.getSchoolLevel() <= 0) {			
			sb2.append("學齡:[無]  ");
		} else {
			sb2.append("學齡:" + querySchoolLevel(queryStudentCondition.getSchoolLevel()) + "  ");			
		}

		// 列印條件第2列
		conditionDesc.add(sb2.toString());
		
		StringBuffer sb3 = new StringBuffer();
				
		// 入隊日
		if (queryStudentCondition.getJoinDateStr() == null || queryStudentCondition.getJoinDateStr().trim().length() <= 0 ||
			queryStudentCondition.getJoinDateEnd() == null || queryStudentCondition.getJoinDateEnd().trim().length() <= 0 ) {			
			sb3.append("　　　　　　　　　　　　　　　入隊日:[無]  ");			
		} else {
			sb3.append("　　　　　　　　　　　　　　　入隊日:" + queryStudentCondition.getJoinDateStr() + "～" + queryStudentCondition.getJoinDateEnd() + "  ");
		}

		// 成員所屬
		if (queryStudentCondition.getMemberBelong() == null || queryStudentCondition.getMemberBelong().trim().length() <= 0) {			
			sb3.append("成員所屬:[無]  ");
		} else {
			boolean hasBlong = false;
			sb3.append("成員所屬:");
			
			if (queryStudentCondition.getMemberBelong().substring(0,1).equalsIgnoreCase("1")) {
				sb3.append("[冬夏令營]");
				hasBlong = true;
			}
			if (queryStudentCondition.getMemberBelong().substring(1,2).equalsIgnoreCase("1")) {
				sb3.append("[學校社團]");
				hasBlong = true;
			}
			if (queryStudentCondition.getMemberBelong().substring(2,3).equalsIgnoreCase("1")) {
				sb3.append("[俱樂部]");
				hasBlong = true;
			}
			
			if (!hasBlong) {
				sb3.append("[無]");				
			}
			sb3.append("  ");	
		}
		// 列印第3列
		conditionDesc.add(sb3.toString());
		
		/* 
		 * 註：暫不採用自動斷行機制，因為切不準，有點醜。
		 * 每60個字斷行一次，目前最多三行
		 * 下面的if判斷是有「順序」的，一定要大到小做else if判斷，不然會有問題
		 */
		/*
		if (sb.length() >= 180) {
			conditionDesc.add(sb.substring(0, 80));
			conditionDesc.add("                      " + sb.substring(80, 120));
			conditionDesc.add("                      " + sb.substring(120));
		} else if (sb.length() >= 120) {
			conditionDesc.add(sb.substring(0, 80));
			conditionDesc.add("                      " + sb.substring(80));			
		} else {
			conditionDesc.add(sb.toString());
		}
		*/
		return conditionDesc;		
	}
	
	/*
	 * 查詢學齡中文
	 */
	private String querySchoolLevel(int schoolLevel) {
		String desc = "？"; //若找不到代碼時，先給「？」
		DBConnectionFactory dbf = new DBConnectionFactory();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "Select StudyDesc From StudyAge Where StudyAgeCode = ?";
		try {		
			conn = dbf.getSQLiteCon("", "Club.dll");
			pstmt = conn.prepareStatement(sql);
			pstmt.clearParameters();
			pstmt.setInt(1, schoolLevel); 
			rs = pstmt.executeQuery();
			while (rs.next()) {
				desc = rs.getString("StudyDesc");
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
		return desc;		
	}
	
	/*
	 * 查詢條件代碼的中文
	 */
	private String queryCodeDesc(String mainCode, String detailCode) {
		String desc = "？"; //若找不到代碼時，先給「？」
		DBConnectionFactory dbf = new DBConnectionFactory();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "Select Desc From CodeDetail Where MainCode = ? and DetailCode = ?";
		try {		
			conn = dbf.getSQLiteCon("", "Club.dll");
			pstmt = conn.prepareStatement(sql);
			pstmt.clearParameters();
			pstmt.setString(1, mainCode); 
			pstmt.setString(2, detailCode); 
			rs = pstmt.executeQuery();
			while (rs.next()) {
				desc = rs.getString("Desc");
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
		return desc;
	}
	
	/*
	 * 執行PDF產出主程序
	 * @return 會回傳 File類別(即該PDF物件)，可以透過該File取得檔案的絕對路徑
	 */
	public File run() {
		// 依條件查出符合的學員資料
		if (queryStudent()) {
		} else {	
			logger.info("查無學員資料。");
			return null;
		}
		
		Paragraph emptyLine = new Paragraph(new Chunk("XXXX", fontBlackSmallCN)); //10號字體空白列

		int currentPageCount = 1;
		int totalPageCount = alStudents.size() / 15 + 1;
		
		createPDF();		
		document.open(); // 開啟文件
		printCondition(currentPageCount, totalPageCount); // 列印表頭(Logo、檔名、條件)	
		/*
		 * 建立QRCode表格列與學員編號、姓名表格列
		 */
		Image qrCode = null;
		PdfPTable picTable = null; // 建立 QRCode 用 Table 列
		PdfPTable textTable = null; // 建立 學員編號_姓名 用 Table 列
		
		int cntPrintQRCode = 0; // 用來計算目前已經印到第幾個學員的 QRCode
		int cntPrintText = 0; // 用來計算目前已經印節第幾個學員的 員編_姓名
		int cntRow = 0; // 用來計算列 QRCode + 員編姓名 計為1列

		while(true) {
			cntRow++;
			picTable = new PdfPTable(5);
		    picTable.setWidthPercentage(100); // 設定表格寬度為100%
		    picTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
		    picTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
		    picTable.getDefaultCell().setFixedHeight(85);
		    
			textTable = new PdfPTable(5); // 建立文字用Table
		    textTable.setWidthPercentage(100); // 設定表格寬度為100%
			textTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
		    textTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
		    textTable.getDefaultCell().setFixedHeight(40);
		    
		    try {
				picTable.setWidths(new float[] {0.20f, 0.20f, 0.20f, 0.20f, 0.20f});
				textTable.setWidths(new float[] {0.20f, 0.20f, 0.20f, 0.20f, 0.20f});
		    } catch (Exception e) {
				logger.info(e.getMessage(), e);		
			}
		    
		    int cntColumn = 0; // 用來記錄本列已印了幾個學員的資料
			int cntData = 0; // 用來記錄下面迴讀到第幾筆 QRCode
			int cntLast = 5; // 預設一列只能印5個格子
			for (String data : alStudents) {
				cntData++;
				// 當讀取到的筆數，已超過上次列印最後1筆的QRCode資料時，才可以列印，但同一列最多印3個學員資料
				if (cntData > cntPrintQRCode && cntColumn < 3) {
					try {
						if (cntColumn < 2) {
							qrCode = Image.getInstance("Backup/QRCode/" + data + ".png");
		
							qrCode.scaleToFit(100, 100);
						    picTable.addCell(qrCode);
						    picTable.addCell(" "); // 中間空一格
						    cntLast = cntLast - 2; // 減掉2格
						} else {
						    picTable.addCell(qrCode); 
						    cntLast = cntLast - 1; // 減掉1格
						}
						cntColumn++;
						cntPrintQRCode++;
					} catch (Exception e) {
						logger.info(e.getMessage(), e);		
					}
				} 
			}
			
			for (int i = 1; i <= cntLast; i++) {
			    picTable.addCell(" "); // 中間空一格				
			}
			
		    cntColumn = 0; // 用來記錄本列已印了幾個學員的資料
			cntData = 0; // 用來記錄下面迴讀到第幾筆 QRCode
			cntLast = 5; //
			for (String data : alStudents) {
				cntData++;
				// 當讀取到的筆數，已超過上次列印最後1筆的 員編_姓名 資料時，才可以列印，但同一列最多印3個學員資料
				if (cntData > cntPrintText && cntColumn < 3) {
					try {
						if (cntColumn < 2) {
						    textTable.addCell(new Paragraph(new Chunk(data , fontBlackMidCN)));
						    textTable.addCell(" "); // 中間空一格
						    cntLast = cntLast - 2; // 減掉2格
						} else {
						    textTable.addCell(new Paragraph(new Chunk(data , fontBlackMidCN)));
						    cntLast = cntLast - 1; // 減掉1格
						}
						cntColumn++;
						cntPrintText++;
					} catch (Exception e) {
						logger.info(e.getMessage(), e);		
					}
				} 
			}
			
			for (int i = 1; i <= cntLast; i++) {
				textTable.addCell(" ");  // 中間空一格				
			}
			
		    try {
				document.add(picTable);
				document.add(textTable);
	
				
				// 因為一頁最多印5列，若除5可以整除 且 已列印的資料小於 alStudents 筆數時，增加一頁
				int remainder = cntRow % 5; // 餘數
				if (remainder == 0 && cntPrintQRCode < alStudents.size() ) {
					currentPageCount++;	
					document.newPage();
					printCondition(currentPageCount, totalPageCount); // 列印表頭(Logo、檔名、條件)	
				}
				
				// 若資料已印完，則離開迴圈
				if (cntPrintQRCode >= alStudents.size()) {
					break;
				}
				
			} catch (Exception e) {
				logger.info(e.getMessage(), e);		
			}			
		}
		
		if (cntPrintQRCode > 0) {
			document.close(); //結束文件
		}
		return file;
	}
	
	public static void main(String[] args) {
		/*
		 * QRCodeListPDF QRCodePDF使用範例
		 * 1.先透過建立 條件類別 QueryStudentCondition
		 * 2.再把 條件類別的實例 傳給 QRCodeListPDF 即可產出 QRCodePDF 報表
		 * 3.產出成功的話，會回傳一個File類別物件，可以透過File類別物件取得檔案位置，秀給前端使用者看
		 *   若回傳null，則代表該條件抓不到資料，產出失敗，請於前端介面回覆查無資料。
		 */
		
		QueryStudentCondition qsc = new QueryStudentCondition();
		/*
		 * qsc.setStatus("N"); // 狀態
		 * qsc.setCourseKind("01"); // 課程類別
		 * qsc.setDepartment("02"); // 上課分部
		 * qsc.setJoinDateStr("20210331"); // 入隊日(起)
		 * qsc.setJoinDateEnd("20210401"); // 入隊日(迄) 
		 * qsc.setLevel("01"); // 程度
		 * qsc.setMemberBelong("1100000000"); // 成員所屬
		 * qsc.setName("黃小明"); // 姓名
		 * qsc.setSchool("台中市光復國小"); // 就讀學校
		 * qsc.setSchoolLevel(906); // 學齡
		 * qsc.setSex("M"); // 性別
		 * qsc.setStudentNo("A000001"); // 學員編號
		 */
		qsc.setStatus("N");
		qsc.setJoinDateStr("19000331"); // 入隊日(起)
		qsc.setJoinDateEnd("20211231"); // 入隊日(迄) 

		//qsc.setStudentNo("A000001"); // 學員編號
		
		QRCodeListPDF qlPDF = new QRCodeListPDF();
		if (qlPDF.setCondition(qsc)) {
			qlPDF.translationOfCondition();
			System.out.println(qlPDF.run().getAbsolutePath());			
		}
	}
}
