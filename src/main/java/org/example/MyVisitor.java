package org.example;

import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class MyVisitor extends VoidVisitorAdapter<Void> {
    @Override
    public void visit(VariableDeclarator vd, Void arg) {
        super.visit(vd, arg);

        // 获取变量的初始化表达式
        if (vd.getInitializer().isPresent()) {
            Expression init = vd.getInitializer().get();

            // 使用 replace() 方法来替换表达式
            if (init instanceof IntegerLiteralExpr) {
                init.replace(new NameExpr("NumLiteral"));
            } else if (init instanceof StringLiteralExpr) {
                init.replace(new NameExpr("StringLiteral"));
            } else if (init instanceof BooleanLiteralExpr) {
                init.replace(new NameExpr("BooleanLiteral"));
            } else if (init instanceof CharLiteralExpr) {
                init.replace(new NameExpr("CharLiteral"));
            } else if (init instanceof DoubleLiteralExpr) {
                init.replace(new NameExpr("DoubleLiteral"));
            } else if (init instanceof LongLiteralExpr) {
                init.replace(new NameExpr("LongLiteral"));
            }
        }

        // 根据变量类型修改变量名
        switch (vd.getType().asString()) {
            case "byte":
                vd.setName("byteVar");
                break;
            case "short":
                vd.setName("shortVar");
                break;
            case "int":
                vd.setName("intVar");
                break;
            case "long":
                vd.setName("longVar");
                break;
            case "float":
                vd.setName("floatVar");
                break;
            case "double":
                vd.setName("doubleVar");
                break;
            case "char":
                vd.setName("charVar");
                break;
            case "boolean":
                vd.setName("booleanVar");
                break;
            case "String":
                vd.setName("stringVar");
                break;
            default:
                vd.setName(vd.getType().asString() + "Var");
            // 可以为其他类型增加更多的条件
        }
    }

}
