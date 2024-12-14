package org.example;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.utils.SourceRoot;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoveCommentsWithMapping {

    public static void main(String[] args) throws FileNotFoundException {
        String code = "public class Test {\n" +
                "    // This is a comment\n\n" +
                "    String badFilePrefix = \"file://\";  // This is not a comment\n" +
                "    /* Another comment */\n" +
                "}";
        FileInputStream in = new FileInputStream(
                "E:\\exp_data\\all_label_data_file\\configuration_warnings\\warning_1206\\b42fc94949b276c377da3aa95b1b3946f49edcc4-CatalogResolver.txt");


        Map<Integer, Integer> lineMapping = new HashMap<>();
        String cleanedCode = removeCommentsAndEmptyLines(in, lineMapping);

        System.out.println(cleanedCode);

        // Print the mapping
        lineMapping.forEach((srcLine, destLine) -> {
            System.out.println("Source line " + srcLine + " => Dest line " + destLine);
        });
    }

    private static String removeCommentsAndEmptyLines(FileInputStream code, Map<Integer, Integer> lineMapping) {
        JavaParser javaParser = new JavaParser();
        // 解析 Java 源文件
        ParseResult<CompilationUnit> parseResult = javaParser.parse(code);
        if (parseResult.isSuccessful() && parseResult.getResult().isPresent()) {
            CompilationUnit cu = parseResult.getResult().get();

// Remove all comments
            cu.getAllContainedComments().forEach(Comment::remove);

            String[] lines = cu.toString().split("\n");
            List<String> cleanedLines = new ArrayList<>();
            int destLine = 0;

            for (int srcLine = 0; srcLine < lines.length; srcLine++) {
                String line = lines[srcLine].trim();
                if (!line.isEmpty()) {
                    destLine++;
                    cleanedLines.add(lines[srcLine]);  // Preserve original indentation
                    lineMapping.put(srcLine + 1, destLine);  // 1-indexed lines
                }
            }

            return String.join("\n", cleanedLines);
        }



        return "";
    }
}

