package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.regex.*;
import java.util.stream.Stream;

public class Main {


    public static void main(String[] args) {
        String directoryPath = "C:\\target"; // 텍스트 파일들이 있는 디렉토리 경로
        String outputFileName = "C:\\output\\result.docx";
        List<ParseDto> results = new ArrayList<>();
        try {
            // JAVA 파일 목록 가져오기
            List<Path> textFiles = Files.walk(Paths.get(directoryPath))
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .collect(Collectors.toList());

            int docStartNo = 0;

            for (Path textFile : textFiles) {
                docStartNo++;

                //Java Pasing 후 ParseDTO로 변환
                ParseDto parseDto = JavaFileAnalyzer.analyze(textFile.toString(), docStartNo);
                results.add(parseDto);
            }
            //ParseDTO를 Word로 변환
            WordMaker.workMake(results, outputFileName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}