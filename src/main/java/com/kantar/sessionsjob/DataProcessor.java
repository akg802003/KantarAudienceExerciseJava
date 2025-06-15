package com.kantar.sessionsjob;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DataProcessor {
	
	public void processInputFile(String inputFile, String outputFile) {
    	System.out.println("Processing input file");
    	//String sortedFile1 = "C:\\eclipse-workspace\\KantarAudienceExerciseJava\\src\\test\\resources\\sorted-statements.psv";  
        String[] colNames = new String[6];
        List<String> lines = null;
		try {
			lines = Utility.readFile(inputFile, colNames);
		} catch (IOException e) {
			e.printStackTrace();
		}
                     
        List<String> sortedFile = Utility.sortPsvFile(lines, Constants.DATE_TIME_COLUMN_INDEX);
        /*Write sortedFile in temp file for verification
        writeSortedFile(sortedFile, sortedFile1);*/
        
        //Process and Generate output file
        generateOutput(sortedFile, colNames, outputFile);
    }
	
	public static void generateOutput(List<String> lines, String[] colNames, String outputFile) {
		System.out.println("Processing output file");
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT);
    	SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);
    	
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
        	String line = String.join("|", colNames); // returned as: "col1|col2|col3|col4"
        	writer.write(line);
        	writer.newLine();
        	        	
        	//Manipulation started
        	for (int i=0; i<=lines.size();i++) {
        		writer.write(lines.get(i));
        		
        		String currentRecord = "";
    			String nextRecord = "";
        		
        		if(i!=lines.size()-1) {
        			currentRecord = lines.get(i);
        			nextRecord = lines.get(i+1);
        		}
        		//System.out.println("Current Line = "+currentRecord+"  and Next Line = "+nextRecord);
        		if(i==lines.size()-1) {
        			// Get the current date and time
        			try {
        				//System.out.println("Last line");
        				currentRecord = lines.get(i);
						Date currDateTime = sdf.parse(currentRecord.split("\\|")[2]);
						String endDateTime = Utility.getEndDateTime(currDateTime);
						//System.out.println(endDateTime);
						Date currEndDateTime = sdf.parse(endDateTime);
						long finalDuration = TimeUnit.MILLISECONDS.toSeconds(currEndDateTime.getTime() - currDateTime.getTime())+1;
						writer.write("|");
        	            writer.write(endDateTime);
        	            writer.write("|");
        	            writer.write(Long.toString(finalDuration));
        	            writer.newLine();
						break;
					} catch (ParseException e) {
						e.printStackTrace();
					}
        		}
        		else if(currentRecord.split("\\|")[0].equals(nextRecord.split("\\|")[0])) {//If same HomeNo found
        			writer.write("|");
        			try {
        				Date date1 = sdf.parse(currentRecord.split("\\|")[2]);
        				Date date2 = sdf.parse(nextRecord.split("\\|")[2]);
        				//System.out.println("Date 1 time  = "+currentRecord.split("\\|")[2]+" and Date2 time = "+nextRecord.split("\\|")[2]);
        				long duration = TimeUnit.MILLISECONDS.toSeconds(date2.getTime() - date1.getTime());
        				//System.out.println("Time difference = "+duration);
        				        				
        				//End time calculation
        				// Parse the string into a LocalDateTime object
        		        LocalDateTime dateTime = (LocalDateTime.parse(nextRecord.split("\\|")[2], formatter)).minus(1, ChronoUnit.SECONDS);
        		        // Format the new date back to the desired string format
        		        String endTime = dateTime.format(formatter);
        	            //System.out.println("End Time = "+endTime);
        	            writer.write(endTime);
        	            writer.write("|");
        	            writer.write(Long.toString(duration));
        			}
        			catch (ParseException e) {
        				System.err.println("Error parsing date string: " + e.getMessage());
					}
        		}
        		else {
        			
        			// Get the current date and time
        			try {
						Date currDateTime = sdf.parse(currentRecord.split("\\|")[2]);
						String endDateTime = Utility.getEndDateTime(currDateTime);
						//System.out.println(endDateTime);
						Date currEndDateTime = sdf.parse(endDateTime);
						long finalDuration = TimeUnit.MILLISECONDS.toSeconds(currEndDateTime.getTime() - currDateTime.getTime())+1;
						writer.write("|");
        	            writer.write(endDateTime);
        	            writer.write("|");
        	            writer.write(Long.toString(finalDuration));
						
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		}
        		       		
                writer.newLine(); // Add a new line after each string
                //if(i==1) break;
            }
        	
        	
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
        }
    }

		/*public static void writeSortedFile(List<String> lines, String sortedFile1) {
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(sortedFile1))) {
		    for (String line : lines) {
		        writer.write(line);
		        writer.newLine(); // Add a new line after each string
		    }
		}
		catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		}*/
}
