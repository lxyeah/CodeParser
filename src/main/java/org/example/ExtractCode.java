package org.example;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ExtractCode {
    public static void main(String[] args) {
        String sourceCodePath = args[0]; // 替换为你的Java源代码文件路径
        String searchName = args[1]; // 替换为要搜索的字符串
//        int len = Integer.parseInt(args[2]);

        String s = ExtractCode.searchMethodByName(sourceCodePath, searchName);
        System.out.println(s);
    }

    public static String searchMemberVariable(String sourceCodePath, String searchName) {
        try {

            FileInputStream in = new FileInputStream(sourceCodePath);


            // 创建一个 JavaParser 实例
            JavaParser javaParser = new JavaParser();
            // 解析 Java 源文件
            ParseResult<CompilationUnit> parseResult = javaParser.parse(in);
            if (parseResult.isSuccessful() && parseResult.getResult().isPresent()) {
                CompilationUnit cu = parseResult.getResult().get();

                String result = searchForMemberVariable(cu, searchName).orElse("");


                // 将修改后的源代码保存到文件中
                return result;
            } else {
                System.out.println("Failed to parse Java file.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String searchMethod(String sourceCodePath, String searchName, int line) {
        try {

            FileInputStream in = new FileInputStream(sourceCodePath);


            // 创建一个 JavaParser 实例
            JavaParser javaParser = new JavaParser();
            // 解析 Java 源文件
            ParseResult<CompilationUnit> parseResult = javaParser.parse(in);
            if (parseResult.isSuccessful() && parseResult.getResult().isPresent()) {
                CompilationUnit cu = parseResult.getResult().get();

                String result = findMethodByLineAndName(cu, line, searchName).orElse("");


                // 将修改后的源代码保存到文件中
                return result;
            } else {
                System.out.println("Failed to parse Java file.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String searchConstructorByLine(String sourceCodePath, String searchName, int line) {
        try {

            FileInputStream in = new FileInputStream(sourceCodePath);


            // 创建一个 JavaParser 实例
            JavaParser javaParser = new JavaParser();
            // 解析 Java 源文件
            ParseResult<CompilationUnit> parseResult = javaParser.parse(in);
            if (parseResult.isSuccessful() && parseResult.getResult().isPresent()) {
                CompilationUnit cu = parseResult.getResult().get();

                String result = searchForConstructorByLineAndName(cu, searchName, line).orElse("");


                // 将修改后的源代码保存到文件中
                return result;
            } else {
                System.out.println("Failed to parse Java file.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }


    public static String searchMethodByLine(String sourceCodePath, int line) {
        try {

            FileInputStream in = new FileInputStream(sourceCodePath);


            // 创建一个 JavaParser 实例
            JavaParser javaParser = new JavaParser();
            // 解析 Java 源文件
            ParseResult<CompilationUnit> parseResult = javaParser.parse(in);
            if (parseResult.isSuccessful() && parseResult.getResult().isPresent()) {
                CompilationUnit cu = parseResult.getResult().get();

                String result = findMethodByLine(cu, line).orElse("");


                // 将修改后的源代码保存到文件中
                return result;
            } else {
                System.out.println("Failed to parse Java file.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String searchMethodByName(String sourceCodePath, String searchName) {
        try {

            FileInputStream in = new FileInputStream(sourceCodePath);


            // 创建一个 JavaParser 实例
            JavaParser javaParser = new JavaParser();
            // 解析 Java 源文件
            ParseResult<CompilationUnit> parseResult = javaParser.parse(in);
            if (parseResult.isSuccessful() && parseResult.getResult().isPresent()) {
                CompilationUnit cu = parseResult.getResult().get();

                String result = findMethodByName(cu, searchName).orElse("");


                // 将修改后的源代码保存到文件中
                return result;
            } else {
                System.out.println("Failed to parse Java file.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static Optional<String> searchForConstructorByLineAndName(CompilationUnit cu, String constructorName, int line) {
        final Optional<String>[] result = new Optional[]{Optional.empty()};

        new VoidVisitorAdapter<Object>() {
            @Override
            public void visit(ConstructorDeclaration constructorDeclaration, Object arg) {
                super.visit(constructorDeclaration, arg);
                if (constructorDeclaration.getNameAsString().equals(constructorName) &&
                        constructorDeclaration.getBegin().isPresent() &&
                        constructorDeclaration.getEnd().isPresent() &&
                        line >= constructorDeclaration.getBegin().get().line &&
                        line <= constructorDeclaration.getEnd().get().line) {
                    result[0] = Optional.of(constructorDeclaration.toString());
                }
            }
        }.visit(cu, null);

        return result[0];
    }

    public static Optional<String> searchForMemberVariable(CompilationUnit cu, String searchName) {
        final Optional<String>[] result = new Optional[]{Optional.empty()};
        new VoidVisitorAdapter<Object>() {
            @Override
            public void visit(FieldDeclaration fieldDeclaration, Object arg) {
                super.visit(fieldDeclaration, arg);
                fieldDeclaration.getVariables().forEach(variable -> {
                    if (variable.getNameAsString().equals(searchName)) {
                        result[0] = Optional.of(fieldDeclaration.toString());
                    }
                });
            }
        }.visit(cu, null);
        return result[0];
    }

    public  static  Optional<String> findMethodByName(CompilationUnit cu, String methodName) {
        final Optional<String>[] result = new Optional[]{Optional.empty()};
        new VoidVisitorAdapter<Object>() {
            @Override
            public void visit(MethodDeclaration methodDeclaration, Object arg) {
                super.visit(methodDeclaration, arg);

                    if (methodDeclaration.getNameAsString().equals(methodName)) {
                        result[0] = Optional.of(methodDeclaration.toString());
                    }


            }
        }.visit(cu, null);
        return result[0];
    }

    public  static  Optional<String> findMethodByLine(CompilationUnit cu, int line) {
        final Optional<String>[] result = new Optional[]{Optional.empty()};
        new VoidVisitorAdapter<Object>() {
            @Override
            public void visit(MethodDeclaration methodDeclaration, Object arg) {
                super.visit(methodDeclaration, arg);

                if (methodDeclaration.getBegin().isPresent() &&
                        methodDeclaration.getEnd().isPresent() &&
                        line >= methodDeclaration.getBegin().get().line &&
                        line <= methodDeclaration.getEnd().get().line) {
                    result[0] = Optional.of(methodDeclaration.toString());
                }


            }
        }.visit(cu, null);
        return result[0];
    }

    public static Optional<String> findMethodByLineAndName(CompilationUnit cu, int line, String methodName) {
        final Optional<String>[] result = new Optional[]{Optional.empty()};
        new VoidVisitorAdapter<Object>() {
            @Override
            public void visit(MethodDeclaration methodDeclaration, Object arg) {
                super.visit(methodDeclaration, arg);
                if (methodDeclaration.getNameAsString().equals(methodName) &&
                        methodDeclaration.getBegin().isPresent() &&
                        methodDeclaration.getEnd().isPresent() &&
                        line >= methodDeclaration.getBegin().get().line &&
                        line <= methodDeclaration.getEnd().get().line) {
                    result[0] = Optional.of(methodDeclaration.toString());
                }
            }
        }.visit(cu, null);
        return result[0];
    }

}

