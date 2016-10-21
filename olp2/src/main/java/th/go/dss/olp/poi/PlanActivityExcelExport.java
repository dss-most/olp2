package th.go.dss.olp.poi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class PlanActivityExcelExport extends AbstractPOIExcelView {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	private static final SimpleDateFormat sdf2 = new SimpleDateFormat("d MMM yyyy", new Locale("th", "TH"));
	
	
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
		List<Map <String,Object>> list = (List<Map <String,Object>>) model.get("planActivityList");
		
		//create a wordsheet
		Sheet sheet = workbook.createSheet(worksheetName);
		DataFormat format = workbook.createDataFormat();
		
		
		
		sheet.setColumnWidth(0, 10*256);
		sheet.setColumnWidth(1, 15*256);
		sheet.setColumnWidth(2, 15*256);
		sheet.setColumnWidth(3, 45*256);
		sheet.setColumnWidth(4, 5*256);
		sheet.setColumnWidth(5, 10*256);
		sheet.setColumnWidth(6, 10*256);
		sheet.setColumnWidth(7, 10*256);
		sheet.setColumnWidth(8, 20*256);
		
		
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
		headerStyle.setWrapText(true);

		numberStyle.setFont(font);
		numberStyle.setDataFormat(format.getFormat("#,##0.00"));
		
		digitStyle.setFont(font);
				
		List<String> columnList = new ArrayList<String>();
		columnList.add("สาขา");		// column 0
		columnList.add("ตัวอย่าง");	// column 1
		columnList.add("รหัสกิจกรรม");		// column 2
		columnList.add("รายการ");		// column 3
		columnList.add("ราคา");		// column 4
		columnList.add("จำนวน\nห้องปฏิบัติการ");		// column 5
		columnList.add("วันที่ปิดรับสมัคร");		// column 6 
		columnList.add("เริ่มกิจกรรม");		// column 7
		columnList.add("ผู้ประสานงาน");		// column 8
		
		
		Row firstRow = sheet.createRow(0);
		Cell firstCell = firstRow.createCell(0);
		firstCell.setCellStyle(style);
		firstCell.setCellValue("แผนกิจกรรมทดสอบความชำนาญห้องปฏิบัติการ ประจำปีงบประมาณ " + fiscalYear);
		
		Row header = sheet.createRow(1);
		header.setHeightInPoints((2*sheet.getDefaultRowHeightInPoints()));
		if(list.size() > 0) {
			int i=0;
			for(String columnName : columnList) {
				Cell cell = header.createCell(i++);
				cell.setCellStyle(headerStyle);
				cell.setCellValue(columnName);
				CellUtil.setAlignment(cell, workbook, CellStyle.ALIGN_CENTER);	
				
			}
		}
		 
		int rowNum = 2;
	
		for (Map<String, Object> rowData : list) {
			//create the row data
			int i = 0;
			Row row = sheet.createRow(rowNum++);

			Cell cell = row.createCell(i++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("BRANCH_NAME")!=null?rowData.get("BRANCH_NAME").toString():"");
			
			cell = row.createCell(i++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("EXAMPLE_NAME")!=null?rowData.get("EXAMPLE_NAME").toString():"");
			
			cell = row.createCell(i++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("ACTIVITY_CODE")!=null?rowData.get("ACTIVITY_CODE").toString():"");
			
			
			cell = row.createCell(i++);
			cell.setCellStyle(style);
			String roundTxt="";
			if(rowData.get("ROUND")!=null) {
				roundTxt = " (Round "+rowData.get("ROUND").toString()+ ")";
			}
			
			cell.setCellValue(rowData.get("ACTIVITY_NAME")!=null?rowData.get("ACTIVITY_NAME").toString()+roundTxt:"");
			
			cell = row.createCell(i++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("PRICE")!=null?rowData.get("PRICE").toString():"");
			
			cell = row.createCell(i++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("ROOM_NUMBER")!=null?rowData.get("ROOM_NUMBER").toString():"");
			
			cell = row.createCell(i++);
			cell.setCellStyle(style);
			if(rowData.get("CLOSE_APPLICANT_DATE")!=null) {
				Date d1 = sdf.parse(rowData.get("CLOSE_APPLICANT_DATE").toString());
				cell.setCellValue(sdf2.format(d1));
			} 
			
			
			cell = row.createCell(i++);
			cell.setCellStyle(style);
			if(rowData.get("START_ACTIVITY_DATE")!=null) {
				Date d1 = sdf.parse(rowData.get("START_ACTIVITY_DATE").toString());
				cell.setCellValue(sdf2.format(d1));
			}
			
			cell = row.createCell(i++);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("EMP_NAME")!=null?rowData.get("EMP_NAME").toString():"");
			
			
		}
		
	}

}

