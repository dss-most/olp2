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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class EMSExport extends AbstractPOIExcelView {

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
		
		//create a wordsheet
		Sheet sheet = workbook.createSheet(worksheetName);
		DataFormat format = workbook.createDataFormat();
		
		
		
		sheet.setColumnWidth(0, 20*256);
		sheet.setColumnWidth(1, 20*128);
		sheet.setColumnWidth(2, 20*256);
		sheet.setColumnWidth(3, 20*256);
		sheet.setColumnWidth(4, 20*256);
		sheet.setColumnWidth(5, 20*256);
		
		
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

		numberStyle.setFont(font);
		numberStyle.setDataFormat(format.getFormat("#,##0.00"));
		
		digitStyle.setFont(font);
				
		List<String> columnList = new ArrayList<String>();
		columnList.add("หมายเลขลงทะเบียน");		// column 0
		columnList.add("รหัสลูกค้า");	// column 1
		columnList.add("ชื่อบริษัท");		// column 2
		columnList.add("นามผู้รับ");		// column 3
		columnList.add("ปลายทาง");		// column 4
		columnList.add("กิจกรรมที่สมัคร");		// column 5 
		
		
		Row firstRow = sheet.createRow(0);
		Cell firstCell = firstRow.createCell(0);
		firstCell.setCellStyle(style);
		firstCell.setCellValue("รายชื่อบริษัทสำหรับ EMS ปี " + fiscalYear);
		
		Row header = sheet.createRow(1);
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
			Row row = sheet.createRow(rowNum++);

			Cell cell = row.createCell(0);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("REGISTER_NUMBER")!=null?rowData.get("REGISTER_NUMBER").toString():"");
			
			cell = row.createCell(1);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("CUSTOMER_CODE")!=null?rowData.get("CUSTOMER_CODE").toString():"");
			
			cell = row.createCell(2);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("COMPANY_TH_APPLICANT")!=null?rowData.get("COMPANY_TH_APPLICANT").toString():"");
			
			cell = row.createCell(3);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("CUSTOMER_NAME_CANDIDATE")!=null?rowData.get("CUSTOMER_NAME_CANDIDATE").toString():"");
			
			cell = row.createCell(4);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("PROVINCE_NAME")!=null?rowData.get("PROVINCE_NAME").toString():"");
			
			cell = row.createCell(5);
			cell.setCellStyle(style);
			cell.setCellValue(rowData.get("ACTIVITIES")!=null?rowData.get("ACTIVITIES").toString():"");
			
			
			
		}
		
	}

}

