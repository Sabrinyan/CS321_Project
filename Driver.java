import java.io.BufferedReader;
import javax.swing.JOptionPane;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.gargoylesoftware.htmlunit.javascript.background.JavaScriptExecutor;
public class Driver {
	
	
	private static String script;
	private static WebDriver dr;
	private static JavascriptExecutor js;
	private static File scriptFile = new File("script.js");
	private static String startUrl;
	
	public static void main(String[] args) throws Exception {
		setScript();
		launchBrowser();	
		//dr.get("http://toolsqa.com/automation-practice-form/");
		
		List<String> events;
		String allEvents = "";
		String total = allEvents;
		String keep = "";
		String current = dr.getCurrentUrl();
		try {
			while(true) {
		        js.executeScript(script);
//		        js.executeScript("document.addEventListener('DOMSubtreeModified', function(){"
//		        		+ "applyEvents();"
//	        	+ "});");
				try {
					while(dr.getCurrentUrl().equals(current)) {
						total = (String)js.executeScript("return localStorage[0];");
					}
				}catch(Exception e) {
					
				}
				current = dr.getCurrentUrl();
				allEvents+= total;
				
				total = "";
			}
		}catch(Exception e) {
			System.out.println("Test Ended");
			if(allEvents.equals("")) {
				events = separate(total);
			}else {
				events = separate(allEvents);
			}
		}
		
		ExcelRW scriptSheet = getExcelSheet(stringToEvents(events));
		scriptSheet.write();
		
		
		
		
	} 
	public static List<EventLines> stringToEvents(List<String> events){
		List<EventLines> newEvents = new ArrayList<EventLines>();
		for(int i = 0; i < events.size(); i++) {
			newEvents.add(new EventLines(events.get(i)));
		}
		return newEvents;
		
	}
	public static ExcelRW getExcelSheet(List<EventLines> events) {
		String filename = "";
		int decision = -1;
		boolean loop = true;
		boolean readFromFile = true;
		while(loop) {
			try {
				decision = Integer.parseInt(JOptionPane.showInputDialog("Would you like to:\n1) Create new Script Sheet\n2) Append to existing Script Sheet"));
				if((decision == 1)|| (decision ==2)){
					loop = false;
				}else {
					throw new Exception("");
				}
			}catch(Exception e) {
				JOptionPane.showMessageDialog(null, "ERROR: Please enter a 1 or 2");
			}
		}
			
		switch(decision) {
			case 1:
				filename = JOptionPane.showInputDialog("Enter filename");
				
				readFromFile = false;
				break;
			case 2:
				filename = JOptionPane.showInputDialog("Enter filename");
				while(!inFolder(filename)) {
					filename = JOptionPane.showInputDialog("File not found in folder\nEnter filename");
				}
				break;
			default:
				decision = Integer.parseInt(JOptionPane.showInputDialog("ERROR: Please enter a 1 or 2\n1) Create new Script Sheet\n2) Append to existing Script Sheet"));
		}
		
		
		if(filename.contains(".xlsx"))
			filename.replace(".xlsx", " ");
		
		return new ExcelRW(filename, events, readFromFile, startUrl);
	}
	public static boolean inFolder(String filename) {
		File folder = new File("./");
		File[] files = folder.listFiles();
		for(int i = 0; i < files.length; i++) {
			if(files[i].isFile()) {
				if(files[i].getName().contains(filename)) {
					return true;
				}
			}
		}
		
		return false;
	}
	public static List<String> separate(String keep){
		Stack<String> temp = new Stack<String>();
		List<String> finalList = new ArrayList<String>();
		String[] newKeep = keep.split("\n");
		
		
		for(int i = newKeep.length -1; i >=0; i--) {
			if(i == newKeep.length -1) {
				temp.push(newKeep[i]);
			}else {
				String prev = temp.peek();
				if(!prev.contains(newKeep[i])) {
					temp.push(newKeep[i]);
				}
			}
		}
		for(int i = temp.size() -1; i >=0; i--) {
			finalList.add(temp.get(i));
		}
		
		return finalList;
	}
	public static void launchBrowser() {
		startUrl = JOptionPane.showInputDialog("Enter the URL of the site you would like to test");
		//System.setProperty("webdriver.chrome.driver","chromedriver.exe");
		System.setProperty("webdriver.ie.driver","IEDriverServer.exe");
		DesiredCapabilities dc=new DesiredCapabilities();
		dc.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
		dc.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR,UnexpectedAlertBehaviour.ACCEPT);
		//dr = new ChromeDriver(dc);
		dr = new InternetExplorerDriver(dc);
		js = (JavascriptExecutor)dr;
		//dr.get("http://toolsqa.com/automation-practice-form/");
		try {
			dr.get(startUrl);
		}catch(Exception e) {
			JOptionPane.showMessageDialog(null, "Error launching browser\nExiting...");
			System.exit(0);
		}
	}
	public static void setScript() {
		Scanner file = null;
		try {
			file = new Scanner(scriptFile);
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
		script = readScript(file);
	}
	public static String readScript(Scanner file) {
		String script = "";
		while(file.hasNextLine()) {
			script += file.nextLine();
			script += "\n";
		}
		return script;
	}
		
}
