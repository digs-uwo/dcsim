package edu.uwo.csd.dcsim.logging;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.ErrorCode;

public class LogPerRunFileAppender extends FileAppender {
	
	private String dateFormatString;
	
	public LogPerRunFileAppender() {
	}

	public LogPerRunFileAppender(Layout layout, String filename, boolean append, boolean bufferedIO, int bufferSize) throws IOException {
		super(layout, filename, append, bufferedIO, bufferSize);
	}

	public LogPerRunFileAppender(Layout layout, String filename, boolean append) throws IOException {
		super(layout, filename, append);
	}

	public LogPerRunFileAppender(Layout layout, String filename) throws IOException {
		super(layout, filename);
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
			String newFileName;
			SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatString);
					
			String timeStamp = dateFormat.format(new Date());
			
			int dateMarker = fileName.indexOf("%d");
			
			if (dateMarker != -1) {
				// insert the time stamp between the file name and the extension
				newFileName = fileName.substring(0, dateMarker) + timeStamp + fileName.substring(dateMarker + 2);
			} else {
				newFileName = "";
			}
			return logFile.getParent() + File.separator + newFileName;
		}
		return null;
	}

}
