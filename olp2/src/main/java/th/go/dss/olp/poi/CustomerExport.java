package th.go.dss.olp.poi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CustomerExport extends AbstractPOIExcelView {

	@Override
	protected Workbook createWorkbook() {
		// TODO Auto-generated method stub
		return new XSSFWorkbook();
	}

	@Override
	protected void buildExcelDocument(Map<String, Object> model,
			Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		String fiscalYear = (String) model.get("fiscalYear");
		
		String worksheetName = "ปี " + fiscalYear;
		logger.debug(worksheetName);
		
		@SuppressWarnings("unchecked")
		List<Map <String,Object>> list = (List<Map <String,Object>>) model.get("registrationList");
		
		@SuppressWarnings("unchecked")
		Integer activityId = (Integer) model.get("activityId");
		
		//create a wordsheet
		Sheet sheet = workbook.createSheet(worksheetName);
		DataFormat format = workbook.createDataFormat();
		
		int colCount=0;
		
		sheet.setColumnWidth(colCount++, 10*256);	// 1 A
		sheet.setColumnWidth(colCount++, 10*256);	// 2 B
		sheet.setColumnWidth(colCount++, 20*256);	// 3 C
		sheet.setColumnWidth(colCount++, 15*256);	// 4 D
		sheet.setColumnWidth(colCount++, 5*256);	// 4 D
		if(activityId == null) {
			sheet.setColumnWidth(colCount++, 30*256);	// 5 E	
		} else {
			sheet.setColumnWidth(colCount++, 5*256);	// 5 E
		}
		
		sheet.setColumnWidth(colCount++, 20*256);	// 6 F
		sheet.setColumnWidth(colCount++, 15*256);	// 7 G
		sheet.setColumnWidth(colCount++, 15*256);	// 8 H
		sheet.setColumnWidth(colCount++, 15*256);	// 9 I
		sheet.setColumnWidth(colCount++, 20*256);	// 10 J
		sheet.setColumnWidth(colCount++, 20*256);	// 11 K
		
		sheet.setColumnWidth(colCount++, 10*256);	// 12 L
		sheet.setColumnWidth(colCount++, 20*256);	// 12 L
		sheet.setColumnWidth(colCount++, 20*256);	// 13 M
		sheet.setColumnWidth(colCount++, 20*256);	// 14 N
		sheet.setColumnWidth(colCount++, 10*256);	// 15 O
		sheet.setColumnWidth(colCount++, 10*256);	// 16 P
		sheet.setColumnWidth(colCount++, 10*256);	// 17 Q
		sheet.setColumnWidth(colCount++, 10*256);	// 18 R
		sheet.setColumnWidth(colCount++, 20*256);	// 19 S
		sheet.setColumnWidth(colCount++, 20*256);	// 20 T
		sheet.setColumnWidth(colCount++, 20*256);	// 21 U
		sheet.setColumnWidth(colCount++, 20*256);	// 22 V
		sheet.setColumnWidth(colCount++, 20*256);	// 23 W
		sheet.setColumnWidth(colCount++, 20*256);	// 24 X
		sheet.setColumnWidth(colCount++, 20*256);	// 25 Y
		sheet.setColumnWidth(colCount++, 20*256);	// 26 Z
		sheet.setColumnWidth(colCount++, 20*256);	// 27 AA
		sheet.setColumnWidth(colCount++, 20*256);	// 28 AB
		sheet.setColumnWidth(colCount++, 20*256);	// 29 AC
		sheet.setColumnWidth(colCount++, 20*256);	// 30 AD
		sheet.setColumnWidth(colCount++, 20*256);	// 31 AF
		sheet.setColumnWidth(colCount++, 20*256);	// 32 AG
		sheet.setColumnWidth(colCount++, 20*256);	// 34 AH
		sheet.setColumnWidth(colCount++, 20*256);	// 35 AI
		sheet.setColumnWidth(colCount++, 20*256);	// 36 AJ
		sheet.setColumnWidth(colCount++, 20*256);	// 37 AK
		sheet.setColumnWidth(colCount++, 20*256);	// 38 AL
		sheet.setColumnWidth(colCount++, 20*256);	// 39 AM
		sheet.setColumnWidth(colCount++, 20*256);	// 40 AN
		sheet.setColumnWidth(colCount++, 20*256);	// 41 AO
 
		
		
		
		CellStyle style = workbook.createCellStyle();
		CellStyle numberStyle = workbook.createCellStyle();
		CellStyle digitStyle = workbook.createCellStyle();

		
		Font font = workbook.createFont();
		font.setFontHeightInPoints((short)8);
		font.setFontName("Tahoma");
		style.setFont(font);

		CellStyle headerStyle = workbook.createCellStyle();
		Font boldFont = workbook.createFont();
		boldFont.setFontHeightInPoints((short)8);
		boldFont.setFontName("Tahoma");
		boldFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		headerStyle.setFont(boldFont);
		headerStyle.setAlignment(HorizontalAlignment.CENTER);

		numberStyle.setFont(font);
		numberStyle.setDataFormat(format.getFormat("#,##0.00"));
		
		digitStyle.setFont(font);
				
		List<String> columnList = new ArrayList<String>();
		columnList.add("เลที่ลงทะเบียน");		// column 0
		columnList.add("รหัสลูกค้า");	// column 1
		columnList.add("ชื่อผู้สมัคร");		// column 2
		
		columnList.add("ชื่อหนังสือราชการ");		// column 2
		
		columnList.add("สถานะ");		// column 3
		if(activityId == null) {
			columnList.add("กิจกรรมที่สมัคร");		// column 4
		} else {
			columnList.add("รหัสลับ");		// column 4
		}
		columnList.add("ตำแหน่ง");		// column 5 
		columnList.add("เบอร์โทรศัพท์");		// column 6
		columnList.add("เบอร์มือถือ");		// column 7
		columnList.add("โทรสาร");		// column 8
		columnList.add("อีเมล์");		// column 9
		
		columnList.add("ประเภทหน่วยงาน");		// column 10
		columnList.add("หน่วยงาน (ไทย)");		// column 10
		columnList.add("หน่วยงาน (eng)");		// column 11 
		columnList.add("ที่อยู่");		// column 12
		columnList.add("ที่อยู่_1");		// column 13
		columnList.add("ตำบล");		// column 14
		columnList.add("อำเภอ");		// column 15
		columnList.add("จังหวัด");		// column 16
		columnList.add("รหัสไปรษณีย์");		// column 17
		
		columnList.add("หน่วยงาน (ไทย)");		// column 18
		columnList.add("หน่วยงาน (eng)");		// column 19 
		columnList.add("ที่อยู่");		// column 20
		columnList.add("ที่อยู่_1");		// column 21
		columnList.add("ตำบล");		// column 22
		columnList.add("อำเภอ");		// column 23
		columnList.add("จังหวัด");		// column 24
		columnList.add("รหัสไปรษณีย์");		// column 25
		
		columnList.add("หน่วยงาน (ไทย)");		// column 26
		columnList.add("หน่วยงาน (eng)");		// column 27 
		columnList.add("ที่อยู่");		// column 28
		columnList.add("ที่อยู่_1");		// column 29
		columnList.add("ตำบล");		// column 30
		columnList.add("อำเภอ");		// column 31
		columnList.add("จังหวัด");		// column 32
		columnList.add("รหัสไปรษณีย์");		// column 33
		
		columnList.add("หน่วยงาน (ไทย)");		// column 34
		columnList.add("หน่วยงาน (eng)");		// column 35 
		columnList.add("ที่อยู่");		// column 36
		columnList.add("ที่อยู่_1");		// column 37
		columnList.add("ตำบล");		// column 38
		columnList.add("อำเภอ");		// column 39
		columnList.add("จังหวัด");		// column 40
		columnList.add("รหัสไปรษณีย์");		// column 41
		
		
		Row firstRow = sheet.createRow(0);
		Cell firstCell = firstRow.createCell(0);
		firstCell.setCellStyle(style);
		
		logger.debug(list.get(0).keySet().toString());
		
		String activity = "";
		if(activityId != null) {
			activity = list.get(0).get("ACTIVITY_CODE").toString() + " " + list.get(0).get("ACTIVITY_NAME").toString();
		} 
		
		firstCell.setCellValue("รายชื่อผู้สมัครเข้าร่วมกิจกรรม " + activity + " ปี " + fiscalYear);
		
		Row mergeRow = sheet.createRow(1);
		Cell title = mergeRow.createCell(12);
		title.setCellStyle(headerStyle);
		title.setCellValue("ชื่อหน่วยงานในใบสมัคร");
		sheet.addMergedRegion(new CellRangeAddress(
	            1, //first row (0-based)
	            1, //last row  (0-based)
	            12, //first column (0-based)
	            19  //last column  (0-based)
		));
		
		title = mergeRow.createCell(20);
		title.setCellValue("ชื่อหน่วยงานในใบเสร็จรับเงิน");
		title.setCellStyle(headerStyle);
		sheet.addMergedRegion(new CellRangeAddress(
	            1, //first row (0-based)
	            1, //last row  (0-based)
	            20, //first column (0-based)
	            27  //last column  (0-based)
	    ));
		
		title = mergeRow.createCell(28);
		title.setCellValue("ชื่อหน่วยงานที่ส่งใบเสร็จรับเงิน");
		title.setCellStyle(headerStyle);
		sheet.addMergedRegion(new CellRangeAddress(
	            1, //first row (0-based)
	            1, //last row  (0-based)
	            28, //first column (0-based)
	            35  //last column  (0-based)
	    ));

		title = mergeRow.createCell(36);
		title.setCellValue("ชื่อหน่วยงานที่ออกในรายงาน");
		title.setCellStyle(headerStyle);
		sheet.addMergedRegion(new CellRangeAddress(
	            1, //first row (0-based)
	            1, //last row  (0-based)
	            36, //first column (0-based)
	            43  //last column  (0-based)
	    ));

		
		Row header = sheet.createRow(2);
		if(list.size() > 0) {
			int i=0;
			for(String columnName : columnList) {
				Cell cell = header.createCell(i++);
				cell.setCellStyle(headerStyle);
				cell.setCellValue(columnName);
				CellUtil.setAlignment(cell, workbook, CellStyle.ALIGN_CENTER);	
				
			}
		}
		 
		int rowNum = 3;
	
		for (Map<String, Object> rowData : list) {
			int col=0;
			//create the row data
			Row row = sheet.createRow(rowNum++);

			Cell cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("REGISTER_NUMBER")!=null?rowData.get("REGISTER_NUMBER").toString():"");
			
			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("CUSTOMER_CODE")!=null?rowData.get("CUSTOMER_CODE").toString():"");
			
			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("CUSTOMER_NAME_CANDIDATE")!=null?rowData.get("CUSTOMER_NAME_CANDIDATE").toString():"");
			
			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("SEND_MISSIVE_NAME")!=null?rowData.get("SEND_MISSIVE_NAME").toString():"");

			
			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("STATUS_REGIS_FORM")!=null?rowData.get("STATUS_REGIS_FORM").toString():"");
			
			cell = row.createCell(col++);
			cell.setCellStyle(style);
			
			if(activityId == null) {
				cell.setCellValue(rowData.get("ACTIVITIES")!=null?rowData.get("ACTIVITIES").toString():"");
			} else {
				cell.setCellValue(rowData.get("C_PASSWORD")!=null?rowData.get("C_PASSWORD").toString():"");
			}
			
			
			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("POSITION_CANDIDATE")!=null?rowData.get("POSITION_CANDIDATE").toString():"");
			
			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("TEL_NO")!=null?rowData.get("TEL_NO").toString():"");
			
			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("PHON_NO")!=null?rowData.get("PHON_NO").toString():"");

			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("FAX_NO")!=null?rowData.get("FAX_NO").toString():"");

			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("EMAIL")!=null?rowData.get("EMAIL").toString():"");

			String orgType = rowData.get("ORG_TYPE") != null ? rowData.get("ORG_TYPE").toString():"";
			if(orgType.equals("T1")) {
				orgType = "หน่วยงานราชการ";
			}  else if(orgType.equals("T2")) {
				orgType = "หน่วยงานรัฐวิสาหกิจ";
			} else if(orgType.equals("T3")) {
				orgType = "หน่วยงานเอกชน";
			} else if(orgType.equals("T3")) {
				orgType = "หน่วยงานในกำกับของรัฐ";
			} else {
				orgType = "ไม่ได้ระบุ";
			}
			
			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(orgType);
			
			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("COMPANY_TH_APPLICANT")!=null?rowData.get("COMPANY_TH_APPLICANT").toString():"");

			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("COMPANY_EN_APPLICANT")!=null?rowData.get("COMPANY_EN_APPLICANT").toString():"");

			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("ADD_APPLICANT")!=null?rowData.get("ADD_APPLICANT").toString():"");

			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("ADD_APPLICANT_1")!=null?rowData.get("ADD_APPLICANT_1").toString():"");

			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("TAMBON_NAME")!=null?rowData.get("TAMBON_NAME").toString():"");

			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("AMPHUR_NAME")!=null?rowData.get("AMPHUR_NAME").toString():"");

			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("PROVINCE_NAME")!=null?rowData.get("PROVINCE_NAME").toString():"");
		
			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("POSTCODE_APPLICANT")!=null?rowData.get("POSTCODE_APPLICANT").toString():"");
			
		
			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("COMPANY_TH_RECEIPT")!=null?rowData.get("COMPANY_TH_RECEIPT").toString():"");

			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("COMPANY_EN_RECEIPT")!=null?rowData.get("COMPANY_EN_RECEIPT").toString():"");

			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("ADD_RECEIPT")!=null?rowData.get("ADD_RECEIPT").toString():"");

			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("ADD_RECEIPT_1")!=null?rowData.get("ADD_RECEIPT_1").toString():"");

			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("TAMBON_NAME_1")!=null?rowData.get("TAMBON_NAME_1").toString():"");

			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("AMPHUR_NAME_1")!=null?rowData.get("AMPHUR_NAME_1").toString():"");

			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("PROVINCE_NAME_1")!=null?rowData.get("PROVINCE_NAME_1").toString():"");
		
			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("POSTCODE_RECEIPT")!=null?rowData.get("POSTCODE_RECEIPT").toString():"");
			
			

			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("COMPANY_TH_RECEIPT2")!=null?rowData.get("COMPANY_TH_RECEIPT2").toString():"");

			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("COMPANY_EN_RECEIPT2")!=null?rowData.get("COMPANY_EN_RECEIPT2").toString():"");

			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("ADD_RECEIPT2")!=null?rowData.get("ADD_RECEIPT2").toString():"");

			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("ADD_RECEIPT2_1")!=null?rowData.get("ADD_RECEIPT2_1").toString():"");

			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("TAMBON_NAME_2")!=null?rowData.get("TAMBON_NAME_2").toString():"");

			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("AMPHUR_NAME_2")!=null?rowData.get("AMPHUR_NAME_2").toString():"");

			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("PROVINCE_NAME_2")!=null?rowData.get("PROVINCE_NAME_2").toString():"");
		
			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("POSTCODE_RECEIPT2")!=null?rowData.get("POSTCODE_RECEIPT2").toString():"");
			

			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("COMPANY_TH_CERTIFICATE")!=null?rowData.get("COMPANY_TH_CERTIFICATE").toString():"");

			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("COMPANY_EN_CERTIFICATE")!=null?rowData.get("COMPANY_EN_CERTIFICATE").toString():"");

			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("ADD_CERTIFICATE")!=null?rowData.get("ADD_CERTIFICATE").toString():"");

			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("ADD_CERTIFICATE_1")!=null?rowData.get("ADD_CERTIFICATE_1").toString():"");

			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("TAMBON_NAME_3")!=null?rowData.get("TAMBON_NAME_3").toString():"");

			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("AMPHUR_NAME_3")!=null?rowData.get("AMPHUR_NAME_3").toString():"");

			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("PROVINCE_NAME_3")!=null?rowData.get("PROVINCE_NAME_3").toString():"");
		
			cell = row.createCell(col++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("POSTCODE_CERTIFICATE")!=null?rowData.get("POSTCODE_CERTIFICATE").toString():"");
			
						
			
		}
		
	}

}

