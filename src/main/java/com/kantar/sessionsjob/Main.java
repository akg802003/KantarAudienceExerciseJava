package com.kantar.sessionsjob;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {

    // See README.txt for usage example
		
	public static final String COLUMN_5 = "EndTime";
	public static final String COLUMN_6 = "Duration";
	
    public static void main(String[] args) throws IOException {

    	String inputFilePath = "";
    	String outputFilePath = "";
    	//System.out.println("Arguments length = "+args.length);
    	if (args.length == 0) {
    		inputFilePath = "C:\\eclipse-workspace\\KantarAudienceExerciseJava\\src\\test\\resources\\input-statements.psv";
    		outputFilePath = "C:\\eclipse-workspace\\KantarAudienceExerciseJava\\target\\actual-sessions.psv";
    		processInputFile(inputFilePath, outputFilePath);
        //}
    	}
    	else if(args.length == 2) {
    		inputFilePath = args[0];
    		outputFilePath = args[1];
    		processInputFile(inputFilePath, outputFilePath);
        }
    	else {
    		System.err.println("Missing valid arguments: <input-statements-file> <output-sessions-file>");
    	}
    }
    
    private static void processInputFile(String inputFile, String outputFile) {
    	
    	//String sortedFile1 = "C:\\eclipse-workspace\\KantarAudienceExerciseJava\\src\\test\\resources\\sorted-statements.psv";
        int dateTimeColumnIndex = 2; // Index of the datetime column
        String dateTimeFormat = "yyyyMMddHHmmss"; // Format of the date-time string
                
        String[] colNames = new String[6];
        List<String> lines = null;
		try {
			lines = readFile(inputFile, colNames);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //Sorting input file by 1st then 2nd column               
        List<String> sortedFile = sortPsvFile(lines, dateTimeColumnIndex, dateTimeFormat);
        /*Write sortedFile in temp file for verification
        writeSortedFile(sortedFile, sortedFile1);*/
        //Prepare output file
        generateOutput(sortedFile, colNames, outputFile, dateTimeFormat);
    }
    
    private static List<String> readFile(String filePath, String[] colNames) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
            	if(line.startsWith("H")) {
            		//System.out.println(line);
            		String[] columns = line.split("\\|");
            		colNames[0] = columns[0];
                    colNames[1] = columns[1];
                    colNames[2] = columns[2];
                    colNames[3] = columns[3];
                    colNames[4] = COLUMN_5;
                    colNames[5] = COLUMN_6;
            	}
            	if(!line.startsWith("H"))
            		lines.add(line);
            }
        }
        return lines;
    }
    
    public static List<String> sortPsvFile(List<String> lines, int dateTimeColumnIndex, String dateTimeFormat) {
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);
        List<String> sortedData = lines.stream()
                .sorted(Comparator.<String, String>comparing(s -> s.split("\\|")[0])
                        .thenComparing(s -> LocalDateTime.parse(s.split("\\|")[dateTimeColumnIndex], formatter))).toList();

        System.out.println("Input file >>> Sorting completed");
        //sortedData.forEach(System.out::println);
        return sortedData;
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

    public static String getEndDateTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        
        // Set time to end of day
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(calendar.getTime());
    }
    
    public static void generateOutput(List<String> lines, String[] colNames, String outputFile, String dateTimeFormat) {
        
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    	
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
        	String line = String.join("|", colNames);
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
						String endDateTime = getEndDateTime(currDateTime);
						//System.out.println(endDateTime);
						Date currEndDateTime = sdf.parse(endDateTime);
						long finalDuration = TimeUnit.MILLISECONDS.toSeconds(currEndDateTime.getTime() - currDateTime.getTime())+1;
						writer.write("|");
        	            writer.write(endDateTime);
        	            writer.write("|");
        	            writer.write(Long.toString(finalDuration));
        	            writer.newLine();
        	            System.out.println("Final output file generated");
						break;
					} catch (ParseException e) {
						// TODO Auto-generated catch block
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
						String endDateTime = getEndDateTime(currDateTime);
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
}
