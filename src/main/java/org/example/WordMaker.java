package org.example;

import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class WordMaker {

    public static void workMake(List<ParseDto> results, String outputFileName) throws IOException {

        // 새 문서 생성
        XWPFDocument document = new XWPFDocument();

        // 번호 매기기 스타일 생성
        XWPFNumbering numbering = document.createNumbering();
        CTAbstractNum ctAbstractNum = CTAbstractNum.Factory.newInstance();
        ctAbstractNum.setAbstractNumId(BigInteger.valueOf(0));

        // 레벨 0 설정 (예: a, b, c, ...)
        CTLvl level0 = ctAbstractNum.addNewLvl();
        level0.setIlvl(BigInteger.valueOf(0));
        level0.addNewNumFmt().setVal(STNumberFormat.DECIMAL);  // 1, 2, 3, ...
        level0.addNewLvlText().setVal("%1.");  // "1.1.", "1.2.", ...
        level0.addNewStart().setVal(BigInteger.valueOf(1));

        // PPr 설정을 한 번에 처리
        CTPPrGeneral level0PPr = level0.addNewPPr();
        CTInd level0Ind = level0PPr.addNewInd();
        level0Ind.setLeft(BigInteger.valueOf(360));  // 전체 들여쓰기
        level0Ind.setHanging(BigInteger.valueOf(360));  // 번호와 텍스트 사이의 간격 설정

        // 레벨 1 설정 (예: a, b, c, ...)
        CTLvl level1 = ctAbstractNum.addNewLvl();
        level1.setIlvl(BigInteger.valueOf(1));
        level1.addNewNumFmt().setVal(STNumberFormat.DECIMAL);  // 1, 2, 3, ...
        level1.addNewLvlText().setVal("%1.%2.");  // "1.1.", "1.2.", ...
        level1.addNewStart().setVal(BigInteger.valueOf(1));

        // PPr 설정을 한 번에 처리
        CTPPrGeneral level1PPr = level1.addNewPPr();
        CTInd level1Ind = level1PPr.addNewInd();
        level1Ind.setLeft(BigInteger.valueOf(560));  // 전체 들여쓰기
        level1Ind.setHanging(BigInteger.valueOf(560));  // 번호와 텍스트 사이의 간격 설정

        // 번호 매기기 스타일 추가
        XWPFAbstractNum abstractNum = new XWPFAbstractNum(ctAbstractNum);
        BigInteger abstractNumID = numbering.addAbstractNum(abstractNum);
        BigInteger numID = numbering.addNum(abstractNumID);

        results.forEach((parseDto) -> {
            makeDocx(document, numID, parseDto);
        });

        // 문서 저장
        try (FileOutputStream out = new FileOutputStream(outputFileName)) {
            document.write(out);
        }
        document.close();
    }


    public static void makeDocx(XWPFDocument document, BigInteger numID, ParseDto parseDto) {

        // 다단계 목록 추가 (레벨 0)
        XWPFParagraph paragraph1 = document.createParagraph();
        paragraph1.setAlignment(ParagraphAlignment.LEFT);
        setParagraphLevel(paragraph1, numID, 0);

        XWPFRun run1 = paragraph1.createRun();
        run1.setText(" " + parseDto.getProgName() + " (" +  parseDto.getProgNo() +")");
        run1.setBold(true);
        run1.setFontSize(20);
        run1.setFontFamily("맑은 고딕");
        run1.setColor("000000"); // 검정색 텍스트

        // 다단계 목록 추가 (레벨 1)
        XWPFParagraph paragraph2 = document.createParagraph();
        paragraph2.setAlignment(ParagraphAlignment.LEFT);
        setParagraphLevel(paragraph2, numID, 1);
        XWPFRun run2 = paragraph2.createRun();
        run2.setText(" " + parseDto.getClassName());
        run2.setFontSize(14);
        run2.setFontFamily("맑은 고딕");
        run2.setColor("000000");
        run2.addBreak();

        if(parseDto.getMethods() == null) { parseDto.setMethods(new ArrayList<>());}

        // Create a table with 4 rows and 3 columns
        XWPFTable table = document.createTable(5 + parseDto.getMethods().size(), 3);
        table.setWidth("100%");
        // Set text and style for each cell
        setCellStyle(table.getRow(0).getCell(0), " Class Name", "F2F2F2", false, 10, null, "500");
        setCellStyle(table.getRow(0).getCell(1), " " + parseDto.getClassName(), "FFFFFF", false, 10, null);
        setCellStyle(table.getRow(0).getCell(2), "", "", true, 14, null);
        mergeCellsHorizontally(table, 0, 1, 2, 2);

        setCellStyle(table.getRow(1).getCell(0), " Package Name", "F2F2F2", false, 10, null);
        setCellStyle(table.getRow(1).getCell(1), " " + parseDto.getPackageName(), "FFFFFF", false, 10, null);
        setCellStyle(table.getRow(1).getCell(2), "", "", true, 14, null);
        mergeCellsHorizontally(table, 1, 1, 2, 2);

        setCellStyle(table.getRow(2).getCell(0), " Description", "F2F2F2", false, 10, null);
        setCellStyle(table.getRow(2).getCell(1), " " + parseDto.getClassDescription(), "FFFFFF", false, 10, null);
        setCellStyle(table.getRow(2).getCell(2), "", "", true, 14, null);
        mergeCellsHorizontally(table, 2, 1, 2, 2);


        setCellStyle(table.getRow(3).getCell(0), "Attribute", "F2F2F2", false, 10, "center");
        setCellStyle(table.getRow(3).getCell(1), "", "FFFFFF", false, 10, null);
        setCellStyle(table.getRow(3).getCell(2), "", "", true, 14, null);

        // Merge cells in the second row (index 1), from cell 0 to cell 2
        mergeCellsHorizontally(table, 3, 0, 2, 1);


        setCellStyle(table.getRow(4).getCell(0), "Name", "F2F2F2", false, 10, "center");
        setCellStyle(table.getRow(4).getCell(1), "Visibility", "F2F2F2", false, 10, "center");
        setCellStyle(table.getRow(4).getCell(2), "Description", "F2F2F2", false, 10, "center");

        for(int i = 0 ; i < parseDto.getMethods().size() ; i++) {

            setCellStyle(table.getRow(i + 5).getCell(0), parseDto.getMethods().get(i).getMethodName(), "FFFFFF", false, 10, null);
            setCellStyle(table.getRow(i + 5).getCell(1), parseDto.getMethods().get(i).getMethodVisible(), "FFFFFF", false, 10, null);
            setCellStyle(table.getRow(i + 5).getCell(2), parseDto.getMethods().get(i).getMethodDesc(), "FFFFFF", false, 10, null);
        }

        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.addBreak(); // 줄바꿈 추가

    }

    // 단락의 목록 레벨 설정
    private static void setParagraphLevel(XWPFParagraph paragraph, BigInteger numID, int level) {
        CTP ctp = paragraph.getCTP();
        CTPPr ppr = ctp.isSetPPr() ? ctp.getPPr() : ctp.addNewPPr();
        CTNumPr numPr = ppr.isSetNumPr() ? ppr.getNumPr() : ppr.addNewNumPr();

        CTDecimalNumber numId = numPr.isSetNumId() ? numPr.getNumId() : numPr.addNewNumId();
        numId.setVal(numID);

        CTDecimalNumber ilvl = numPr.isSetIlvl() ? numPr.getIlvl() : numPr.addNewIlvl();
        ilvl.setVal(BigInteger.valueOf(level));
    }

    //테이블 셀 스타일 지정
    private static void setCellStyle(XWPFTableCell cell, String text, String bgColor, boolean bold, int fontSize, String align) {
        setCellStyle(cell, text, bgColor, bold, fontSize, align, null);
    }

    //테이블 셀 슽타일 지정
    private static void setCellStyle(XWPFTableCell cell, String text, String bgColor, boolean bold, int fontSize, String align, String width) {
        cell.setText(text);
        if(width != null && !width.equals("")) {
            cell.setWidth(width);
        }

        // Set background color
        cell.getCTTc().addNewTcPr().addNewShd().setFill(bgColor);

        // Set font size and bold
        for (XWPFParagraph paragraph : cell.getParagraphs()) {
            CTP ctp = paragraph.getCTP();
            CTPPr pPr = ctp.isSetPPr() ? ctp.getPPr() : ctp.addNewPPr();
            CTInd ind = pPr.isSetInd() ? pPr.getInd() : pPr.addNewInd();
            ind.setLeft(BigInteger.valueOf(30));   // 왼쪽 들여쓰기 (1/20 포인트 단위, 예: 720은 36 포인트)
            ind.setRight(BigInteger.valueOf(30));

            if(align != null && align.equals("center")) {
                paragraph.setAlignment(ParagraphAlignment.CENTER);
            }
            for (XWPFRun run : paragraph.getRuns()) {
                run.setFontSize(fontSize);
                run.setBold(bold);
            }
        }
    }

    //테이블 셀 머지
    private static void mergeCellsHorizontally(XWPFTable table, int rowIndex, int fromCell, int toCell,int delCell) {
        XWPFTableRow row = table.getRow(rowIndex);
        // Set GridSpan for the first cell in the merged range
        XWPFTableCell firstCell = row.getCell(fromCell);
        firstCell.getCTTc().addNewTcPr().addNewGridSpan().setVal(BigInteger.valueOf(toCell - fromCell + 1));

        // Remove the other cells in the range
        for (int i = fromCell + 1; i <= toCell; i++) {
            XWPFTableCell cellToRemove = row.getCell(i);
            row.removeCell(delCell); // This will remove the cell
        }
    }
}
