package org.example;

import com.github.javaparser.*;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.ForStmt;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.HashMap;
import java.util.Map;

public class CodeParser {

    public static void main(String[] args) {
//        String code = "public class Test {\n" +
//                "    public static final short ACC_ENUM = 0x400;\n" +
//                "    public static final int VALUE_ONE = 10;\n" +
//                "    int x = VALUE_ONE;\n" +
//                "    public Test() {\n" +
//                "       for (int i = 0; i < 10; i++) {\n" +
//                "           System.out.println(i);\n" +
//                "       }\n" +
//                "    }\n" +
//                "}";

        try {
            System.out.println(args[0] + ' ' +  args[1]);
//            FileInputStream in = new FileInputStream(
//                    "E:/exp_data/warning_line/pool_warnings\\warning_61\\temp_warning_method.txt E:/exp_data/warning_line/pool_warnings\\warning_61\\warning_abstract_method.txt");
            FileInputStream in = new FileInputStream(args[0]);

            // 创建一个 JavaParser 实例
            JavaParser javaParser = new JavaParser();
            // 解析 Java 源文件
            ParseResult<CompilationUnit> parseResult = javaParser.parse(in);
            if (parseResult.isSuccessful() && parseResult.getResult().isPresent()) {
                CompilationUnit cu = parseResult.getResult().get();
                Map<String, String> varMapping = new HashMap<>();
                Map<String, String> literalMapping = new HashMap<>();
                int[] counters = {1, 1}; // 0: for var, 1: for literal

                // Process variable declarations
                cu.findAll(VariableDeclarator.class).forEach(declarator -> {
                    processVariableDeclarator(declarator, varMapping, literalMapping, counters);
                });

                // Process for loop control expressions
                cu.findAll(ForStmt.class).forEach(forStmt -> {
                    processForStmt(forStmt, varMapping, literalMapping, counters);
                });

                // Replace other variable usages
                cu.findAll(NameExpr.class).forEach(nameExpr -> {
                    String name = nameExpr.getNameAsString();
                    if (varMapping.containsKey(name)) {
                        nameExpr.setName(varMapping.get(name));
                    }
                });

                System.out.println(cu.toString());

                // 将修改后的源代码保存到文件中
                String newCode = cu.toString();
                Files.write(Paths.get(args[1]), newCode.getBytes(StandardCharsets.UTF_8));
            } else {
                System.out.println("Failed to parse Java file.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static void processVariableDeclarator(VariableDeclarator declarator, Map<String, String> varMapping,
                                                  Map<String, String> literalMapping, int[] counters) {
        String varName = declarator.getName().asString();
        String varValue = declarator.getInitializer().isPresent() ? declarator.getInitializer().get().toString() : null;

        if (!varMapping.containsKey(varName)) {
            String var= declarator.getType().asString().toLowerCase();
            String newVarName;
            if (var.length() >= 2 && var.charAt(var.length()-2) == '[' && var.charAt(var.length()-1) == ']') {
                newVarName = declarator.getType().asString().toLowerCase().split("\\[\\]")[0] + "ArrayVar" + counters[0]++;
            } else {
                newVarName = declarator.getType().asString().toLowerCase() + "Var" + counters[0]++;
            }

            varMapping.put(varName, newVarName);
        }

        if (varValue != null && !literalMapping.containsKey(varValue)) {
            String literal = declarator.getType().asString().toLowerCase();
            String newLiteralValue;
            if (literal.length() >= 2 && literal.charAt(literal.length()-2) == '[' && literal.charAt(literal.length()-1) == ']') {
                newLiteralValue = declarator.getType().asString().toLowerCase().split("\\[\\]")[0] + "ArrayLiteral" + counters[0]++;
            } else {
                newLiteralValue = declarator.getType().asString().toLowerCase() + "Literal" + counters[0]++;
            }

            literalMapping.put(varValue, newLiteralValue);
        }

        declarator.setName(varMapping.get(varName));
        if (varValue != null) {
            declarator.getInitializer().ifPresent(init -> init.replace(new NameExpr(literalMapping.get(varValue))));
        }
    }

    private static void processForStmt(ForStmt forStmt, Map<String, String> varMapping,
                                       Map<String, String> literalMapping, int[] counters) {
        forStmt.getInitialization().forEach(initExpr -> {
            if (initExpr instanceof com.github.javaparser.ast.expr.VariableDeclarationExpr) {
                com.github.javaparser.ast.expr.VariableDeclarationExpr declarationExpr =
                        (com.github.javaparser.ast.expr.VariableDeclarationExpr) initExpr;
                declarationExpr.getVariables().forEach(declarator -> {
                    processVariableDeclarator(declarator, varMapping, literalMapping, counters);
                });
            }
        });

        forStmt.getCompare().ifPresent(compareExpr -> {
            if (compareExpr instanceof BinaryExpr) {
                BinaryExpr binaryExpr = (BinaryExpr) compareExpr;
                String rightValue = binaryExpr.getRight().toString();
                if (!literalMapping.containsKey(rightValue)) {
                    String newLiteralValue = "intLiteral" + counters[1]++;
                    literalMapping.put(rightValue, newLiteralValue);
                    binaryExpr.getRight().replace(new NameExpr(newLiteralValue));
                }
            }
        });
    }




}
