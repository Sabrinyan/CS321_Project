import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelRW {
	private static Workbook wb;
	private static org.apache.poi.ss.usermodel.Sheet sh;
	private static FileInputStream fis;
	private static FileOutputStream fos;
	private String filename;
	private String startUrl;
	private List<EventLines> events;
	private boolean readFromFile;
	private static String[] columns = {"XPath", "Action", "Input"};
	
	
	public ExcelRW(String filename, List<EventLines> events, boolean readFromFile, String startUrl) {
		this.filename = filename;
		this.events = events;
		this.readFromFile = readFromFile;
		this.startUrl = startUrl;
	}
	
	public void write() throws Exception {
		Cell action;
		Cell input;
		Cell xpath;
		if(readFromFile) {
			fis = new FileInputStream(filename + ".xlsx");
			wb = WorkbookFactory.create(fis);
			sh = wb.getSheet("Sheet0");
			int start = sh.getLastRowNum() + 1;
			setNavigate(start);
			for(int i = 0; i < events.size(); i++) {
				Row row = sh.createRow(i+start +1);
				
				xpath = row.createCell(0);
				xpath.setCellValue(events.get(i).getXPath());
				
				action = row.createCell(1);
				action.setCellValue(events.get(i).getAction());
				
				input = row.createCell(2);
				input.setCellValue(events.get(i).getInput());
			}
		}
		else {
			wb = new XSSFWorkbook();
			sh = wb.createSheet();
			setColumnNames();
			setNavigate(1);
			
			for(int i = 0; i < events.size(); i++) {
				Row row = sh.createRow(i+2);
				
				xpath = row.createCell(0);
				xpath.setCellValue(events.get(i).getXPath());
				
				action = row.createCell(1);
				action.setCellValue(events.get(i).getAction());
				
				input = row.createCell(2);
				input.setCellValue(events.get(i).getInput());
			}
		
			
		}
		resizeColumns();
		fos = new FileOutputStream(filename + ".xlsx");
		wb.write(fos);
		fos.flush();
		fos.close();
		wb.close();
	
	}
	public void setNavigate(int row) {
		Row r = sh.createRow(row);
		Cell action = r.createCell(1);
		action.setCellValue("get");
		Cell input = r.createCell(2);
		input.setCellValue(startUrl);
	}
	public void resizeColumns() {
		for(int i = 0; i < columns.length; i++) {
			sh.autoSizeColumn(i);
		}
	}
	
	public void setColumnNames() {
		Row header = sh.createRow(0);
		for(int i = 0; i < columns.length; i++) {
			Cell cell = header.createCell(i);
			cell.setCellValue(columns[i]);
		}
		
	}
	
}
