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

public class Utility {
	
	public static List<String> readFile(String filePath, String[] colNames) throws IOException {
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
	                colNames[4] = Constants.COLUMN_5;
	                colNames[5] = Constants.COLUMN_6;
	        	}
	        	if(!line.startsWith("H"))
	        		lines.add(line);
	        }
	    }
	    return lines;
	}
	
	//Sorting input file by 1st then 2nd column  
	public static List<String> sortPsvFile(List<String> lines, int dateTimeColumnIndex) {
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT);
        List<String> sortedData = lines.stream()
                .sorted(Comparator.<String, String>comparing(s -> s.split("\\|")[0])
                        .thenComparing(s -> LocalDateTime.parse(s.split("\\|")[dateTimeColumnIndex], formatter))).toList();

        System.out.println("Input file >>> Sorting completed");
        //sortedData.forEach(System.out::println);
        return sortedData;
    }
	
	public static String getEndDateTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        
        // Set time to end of day
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);
        return sdf.format(calendar.getTime());
    }
	
	
}
