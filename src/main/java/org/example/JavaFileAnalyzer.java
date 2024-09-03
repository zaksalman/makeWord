package org.example;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.stmt.BlockStmt;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class JavaFileAnalyzer {

    public static ParseDto analyze(String filePath, int docStartNo) {

        ParseDto parseDto = new ParseDto();
        try (FileInputStream in = new FileInputStream(filePath)) {
            CompilationUnit cu = StaticJavaParser.parse(in);
            cu.getPackageDeclaration().ifPresent(packageDeclaration -> {
                String packageName = packageDeclaration.getNameAsString();
                //System.out.println("Package Name: " + packageName);
                parseDto.setPackageName(packageName);  // 예시로 패키지 이름을 progName에 설정
                String[] workDiv = getWorkDiv(packageName);
                parseDto.setProgNo("PG-" + workDiv[0] + "-" + docStartNo);
                parseDto.setProgName(workDiv[1]);
            });

            // Extract class information
            cu.findAll(ClassOrInterfaceDeclaration.class).forEach(clazz -> {
                //System.out.println("Class Name: " + clazz.getName());
                parseDto.setClassName(clazz.getName().toString());
                clazz.getAnnotations().forEach(annotation -> {
                    String annotationName = annotation.getNameAsString();
                    //System.out.println("Class Annotation: " + annotationName);
                    annotation.getChildNodesByType(MemberValuePair.class).forEach(pair -> {
                        //System.out.println(pair.getName() + ": " + pair.getValue());

                        if(annotationName.equals("Tag")){
                            if(pair.getName().toString().equals("name")) {
                                //parseDto.setProgName(pair.getValue().toString());
                            }
                            if(pair.getName().toString().equals("description")) {
                                parseDto.setClassDescription(pair.getValue().toString().replaceAll("\"","").replaceAll("/\\*\\*\\s*|\\s*\\*/", "").replaceAll("\\s*\\*\\s*", "\n"));
                            }
                        }
                    });
                });

                // Extract class comments
                if (clazz.getComment().isPresent() && clazz.getComment().get() instanceof JavadocComment) {
                    System.out.println("Class Comment: " + clazz.getComment().get().toString());
                    if( parseDto.getClassDescription() == null || parseDto.getClassDescription().equals("")) {
                        parseDto.setClassDescription(clazz.getComment().get().toString().replaceAll("\"","").replaceAll("/\\*\\*\\s*|\\s*\\*/", "").replaceAll("\\s*\\*\\s*", "\n"));
                    }
                }

                List<ParseDto.Method> methods = new ArrayList<>();

                // Extract method information
                clazz.findAll(MethodDeclaration.class).forEach(method -> {
                    ParseDto.Method methodx = parseDto.new Method();
                    methodx.setMethodVisible(method.getAccessSpecifier().toString());
                    System.out.println("  Method Name: " + method.getName());
                    methodx.setMethodName(method.getName().toString());
                    // Extract method annotations
                    method.getAnnotations().forEach(annotation -> {
                        String annotationName = annotation.getNameAsString();
                        System.out.println("  Method Annotation: " + annotationName);
                        annotation.getChildNodesByType(MemberValuePair.class).forEach(pair -> {
                            System.out.println("    " + pair.getName() + ": " + pair.getValue());
                            if(annotationName.toString().equals("Operation") && pair.getName().toString().equals("description")) {
                                methodx.setMethodDesc(pair.getValue().toString().replaceAll("\"","").replaceAll("/\\*\\*\\s*|\\s*\\*/", "").replaceAll("\\s*\\*\\s*", "\n"));
                            }
                            if(annotationName.toString().equals("Operation") && pair.getName().toString().equals("summary") && (methodx.getMethodDesc() == null || methodx.getMethodDesc().equals(""))) {
                                methodx.setMethodDesc(pair.getValue().toString().replaceAll("\"","").replaceAll("/\\*\\*\\s*|\\s*\\*/", "").replaceAll("\\s*\\*\\s*", "\n"));
                            }

                        });
                    });

                    // Extract method comments
                    if (method.getComment().isPresent() && method.getComment().get() instanceof JavadocComment) {
                        System.out.println("  Method Comment: " + method.getComment().get().toString());
                        if((methodx.getMethodDesc() == null || methodx.getMethodDesc().equals(""))) {
                            methodx.setMethodDesc(method.getComment().get().toString().replaceAll("\"","").replaceAll("/\\*\\*\\s*|\\s*\\*/", "").replaceAll("\\s*\\*\\s*", "\n"));
                        }

                    }

                    methods.add((methodx));
                });
                parseDto.setMethods(methods);
            });

        } catch (IOException | ParseProblemException e) {
            e.printStackTrace();
        }
        return parseDto;
    }


    public static String[] getWorkDiv(String packageName) {
        //인수 : tra ^ transfer_accession
        //등록 : reg ^ Registration
        //수집 : acq ^ Acquisition
        //디지털화 : dig ^ Digitization
        //복원 : pre ^ Restoration
        //서고 : rep ^ Repository
        //평가 : eva ^ Evaluation
        //열람 : red ^ Reading
        //민원 : civ ^ Civil
        //검색 : ser ^ Search
        //통계 : sta ^ Statistics
        //분류체계 : cls ^ Classification
        //시스템 : sys ^ System
        //공통 : com ^ Common
        //연계 : inf ^ Interface
        //연계 : usr ^ User

        String[] workDiv = null;
        if(packageName.contains("mn.gov.archives.web.accession")) { workDiv = new String[]{"TRA","Transfer_Accession"}; }
        if(packageName.contains("mn.gov.archives.web.acquisition")) { workDiv = new String[]{"ACQ","Acquisition"}; }
        if(packageName.contains("mn.gov.archives.web.civil")) { workDiv = new String[]{"CIV","Civil"};  }
        if(packageName.contains("mn.gov.archives.web.classification")) { workDiv = new String[]{"CLS","Classification"}; }
        if(packageName.contains("mn.gov.archives.web.common")) { workDiv = new String[]{"COM","Common"};  }
        if(packageName.contains("mn.gov.archives.web.digitization")) { workDiv = new String[]{"DIG","Digitization"};  }
        if(packageName.contains("mn.gov.archives.web.evaluation")) { workDiv = new String[]{"EVA","Evaluation"};  }
        if(packageName.contains("mn.gov.archives.web.interfaceApi")) { workDiv = new String[]{"INF","Interface API"}; }
        if(packageName.contains("mn.gov.archives.web.registration")) { workDiv = new String[]{"REG","Registration"}; }
        if(packageName.contains("mn.gov.archives.web.repository")) { workDiv = new String[]{"REP","Repository"}; }
        if(packageName.contains("mn.gov.archives.web.restoration")) { workDiv = new String[]{"PRE","Restoration"}; }
        if(packageName.contains("mn.gov.archives.web.search")) { workDiv = new String[]{"SER","Search"}; }
        if(packageName.contains("mn.gov.archives.web.statistics")) { workDiv = new String[]{"STA","Statistics"}; }
        if(packageName.contains("mn.gov.archives.web.system")) { workDiv = new String[]{"SYS","System"};  }
        if(packageName.contains("mn.gov.archives.web.user")) { workDiv = new String[]{"USR","User"}; }

        if(packageName.contains("mn.gov.archives.accession")) { workDiv = new String[]{"TRA","Transfer_Accession"}; }
        if(packageName.contains("mn.gov.archives.acquisition")) { workDiv = new String[]{"ACQ","Acquisition"}; }
        if(packageName.contains("mn.gov.archives.archive")) { workDiv = new String[]{"ARC","Archive"};  }
        if(packageName.contains("mn.gov.archives.civil")) { workDiv = new String[]{"CIV","Civil"}; }
        if(packageName.contains("mn.gov.archives.classification")) { workDiv = new String[]{"CLS","Classification"}; }
        if(packageName.contains("mn.gov.archives.common")) { workDiv = new String[]{"COM","Common"}; }
        if(packageName.contains("mn.gov.archives.config")) { workDiv = new String[]{"CFG","Config"};  }
        if(packageName.contains("mn.gov.archives.digitization")) { workDiv = new String[]{"DIG","Digitization"};  }
        if(packageName.contains("mn.gov.archives.evaluation")) { workDiv = new String[]{"EVA","Evaluation"}; }
        if(packageName.contains("mn.gov.archives.exception")) { workDiv = new String[]{"EXP","Exception"};  }
        if(packageName.contains("mn.gov.archives.interfaceApi")) { workDiv = new String[]{"INF","Interface API"}; }
        if(packageName.contains("mn.gov.archives.registration")) { workDiv = new String[]{"REG","Registration"}; }
        if(packageName.contains("mn.gov.archives.repository")) { workDiv = new String[]{"REP","Repository"}; }
        if(packageName.contains("mn.gov.archives.restoration")) { workDiv = new String[]{"PRE","Restoration"}; }
        if(packageName.contains("mn.gov.archives.search")) { workDiv = new String[]{"SER","Search"}; }
        if(packageName.contains("mn.gov.archives.statistics")) { workDiv = new String[]{"STA","Statistics"}; }
        if(packageName.contains("mn.gov.archives.system")) { workDiv = new String[]{"SYS","System"}; }
        if(packageName.contains("mn.gov.archives.user")) { workDiv = new String[]{"USR","User"}; }
        if(packageName.contains("mn.gov.archives.utils")) { workDiv = new String[]{"UTL","Util"}; }

        return workDiv;
    }
}
