package com.kantar.sessionsjob;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

public class MainProgramTest {

    @Test
    public void testFileComparison() throws IOException {
    	DataProcessor processor = new DataProcessor();
    	
        // Paths to your output and expected files
    	Path outputFile = Paths.get("C:\\eclipse-workspace\\KantarAudienceExerciseJava\\target\\actual-sessions.psv");
        Path expectedFile = Paths.get("C:\\eclipse-workspace\\KantarAudienceExerciseJava\\src\\test\\resources\\expected-sessions.psv");
        
    	String inputFilePath = "C:\\eclipse-workspace\\KantarAudienceExerciseJava\\src\\test\\resources\\input-statements.psv";
		String outputFilePath = "C:\\eclipse-workspace\\KantarAudienceExerciseJava\\target\\actual-sessions.psv";
				
		processor.processInputFile(inputFilePath, outputFilePath);
				
        // Read the content of both files
        String outputContent = new String(Files.readAllBytes(outputFile));
        String expectedContent = new String(Files.readAllBytes(expectedFile));
        // Compare the content of the two files
        assertEquals(outputContent, expectedContent, "The content of the files should be equal");
    }
}