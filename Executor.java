import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

public class Executor {

	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String failedEvents = "";
		File folder = new File("./");
		File[] files = folder.listFiles();
		List<File> sheets = onlyExcels(files);
		int numFiles = sheets.size();
		if(numFiles == 0) {
			JOptionPane.showMessageDialog(null, "No Excel files in directory to execute\nExiting....");
			System.exit(0);
		}
		FileInputStream fis = new FileInputStream(chooseFile(sheets));
		Workbook wb = WorkbookFactory.create(fis);
		Sheet sh = wb.getSheet("Sheet0");

		List<EventLines> events = getEventsFromExcel(sh);
		System.setProperty("webdriver.ie.driver","IEDriverServer.exe");
		DesiredCapabilities dc=new DesiredCapabilities();
		dc.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
		dc.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR,UnexpectedAlertBehaviour.ACCEPT);
		WebDriver dr = new InternetExplorerDriver(dc);
		dr.get(events.get(0).getInput());
		for(int i = 1; i < events.size(); i++) {
//			try {
				executeStep(dr, events.get(i));
//			}catch(Exception e) {
				//failedEvents += String.format("STEP %d:\t\t%s\n",i, events.get(i).toString());
			//}
		}
//		if(failedEvents.equals("")) {
//			JOptionPane.showMessageDialog(null, "ALL STEPS COMPLETED SUCCESSFULLY\nExiting...");
//			System.exit(0);
//		}else {
//			JOptionPane.showMessageDialog(null, "FAILED STEPS:\n" + failedEvents);
//		}
		System.out.println("Test Completed");
	}
	

	public static void executeStep(WebDriver dr, EventLines event) throws Exception {
		if(event.getXPath().equals("N/A")) {
			dr.get(event.getInput());
			return;
		}
		WebElement el = dr.findElement(By.xpath(event.getXPath()));
		if(el == null) {
			throw new Exception("Element Not Found");
		}
		String action = event.getAction();
		String input = event.getInput();
		if(action.equals("Sendkeys")) {
			el.sendKeys(input);
		}
		if(action.equals("click")) {
			el.click();
		}
	
		
		
	}
	public static List<EventLines> getEventsFromExcel(Sheet sh){
		DataFormatter formatter = new DataFormatter();
		List<EventLines> events = new ArrayList<EventLines>();
		int lastRow = sh.getLastRowNum();
		Row row; String xpath, action, input;
		for(int i = 0; i < lastRow-2; i++) {
			row = sh.getRow(i+1);
			xpath = formatter.formatCellValue(row.getCell(0));
			action = formatter.formatCellValue(row.getCell(1));
			input = formatter.formatCellValue(row.getCell(2));
			events.add(new EventLines(xpath, action, input));
		}
		
		
		return events;
	}
	
	public static String chooseFile(List<File> files) {
		int chosen = 0;
		boolean loop = true;
		String allFiles = "";
		int x = 0;
		for(int i = 0; i < files.size(); i++) {
			allFiles += String.format("%d)  %s\n",i+1, files.get(i).getName());
		}
		
		while(loop) {
			try {
				chosen = Integer.parseInt(JOptionPane.showInputDialog("Select Excel Script Sheet:\n0)  Exit\n" + allFiles));
				if(chosen == 0) {
					System.exit(0);
				}
				if((chosen >= 1) && (chosen <= files.size())){
					loop = false;
				}else {
					throw new Exception("");
				}
			}catch(Exception e) {
				JOptionPane.showMessageDialog(null, "ERROR: Please select appropriate number");
			}
		}
		
		
		
		return files.get(chosen -1).getName();
	}
	public static List<File> onlyExcels(File[] files) {
		List<File> sheets = new ArrayList<File>();
		for(File file: files) {
			if(file.isFile()) {
				if(file.getName().contains(".xlsx")) {
					sheets.add(file);
				}
			}
		}
		return sheets;
	}
}
