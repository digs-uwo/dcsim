package edu.uwo.csd.dcsim.logging;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.ErrorCode;

public class SimulationFileAppender extends FileAppender {
	
	private String dateFormatString;
	private String simName = "";
	
	public SimulationFileAppender() {
	}

	public SimulationFileAppender(Layout layout, String filename, boolean append, boolean bufferedIO, int bufferSize) throws IOException {
		super(layout, filename, append, bufferedIO, bufferSize);
	}

	public SimulationFileAppender(Layout layout, String filename, boolean append) throws IOException {
		super(layout, filename, append);
	}

	public SimulationFileAppender(Layout layout, String filename) throws IOException {
		super(layout, filename);
	}
	
	public String getSimName() {
		return simName;
	}
	
	public void setSimName(String simName) {
		this.simName = simName;
	}
	
	public String getDateFormat() {
		return dateFormatString;
	}
	
	public void setDateFormat(String dateFormat) {
		this.dateFormatString = dateFormat;
	}

	public void activateOptions() {
		if (fileName != null) {
			try {
				fileName = getNewLogFileName();
				setFile(fileName, fileAppend, bufferedIO, bufferSize);
			} catch (Exception e) {
				errorHandler.error("Error while activating log options", e, ErrorCode.FILE_OPEN_FAILURE);
			}
		}
	}

	private String getNewLogFileName() {
		if (fileName != null) {
			File logFile = new File(fileName);
			String fileName = logFile.getName();
			SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatString);
					
			String timeStamp = dateFormat.format(new Date());
			
			int dateMarker = fileName.indexOf("%d");
			
			if (dateMarker != -1) {
				fileName = fileName.substring(0, dateMarker) + timeStamp + fileName.substring(dateMarker + 2);
			}
			
			int simNameMarker = fileName.indexOf("%n");
			
			if (simNameMarker != -1) {
				fileName = fileName.substring(0, simNameMarker) + simName + fileName.substring(simNameMarker + 2);
			}
			
			return logFile.getParent() + File.separator + fileName;
		}
		return null;
	}

}
