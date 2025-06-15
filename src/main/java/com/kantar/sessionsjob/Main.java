package com.kantar.sessionsjob;

import java.io.IOException;

public class Main {

    // See README.txt for usage example
	public static void main(String[] args) throws IOException {

		String inputFilePath = "";
    	String outputFilePath = "";
    	DataProcessor processor = new DataProcessor();
    	
    	try {
    		
	    	//System.out.println("Arguments length = "+args.length);
	    	if (args.length == 0) {
	    		inputFilePath = "C:\\eclipse-workspace\\KantarAudienceExerciseJava\\src\\test\\resources\\input-statements.psv";
	    		outputFilePath = "C:\\eclipse-workspace\\KantarAudienceExerciseJava\\target\\actual-sessions.psv";
	    		processor.processInputFile(inputFilePath, outputFilePath);
	    	}
	    	else if(args.length == 2) {
	    		inputFilePath = args[0];
	    		outputFilePath = args[1];
	    		processor.processInputFile(inputFilePath, outputFilePath);
	        }
	    	else {
	    		System.err.println("Missing valid arguments: <input-statements-file> <output-sessions-file>");
	    	}
	    	System.out.println("Final output file generated");
    	}
    	catch (Exception e) {
			e.printStackTrace();
		}
	}
}
