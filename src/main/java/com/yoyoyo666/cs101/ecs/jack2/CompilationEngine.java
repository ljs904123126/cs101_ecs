package com.yoyoyo666.cs101.ecs.jack2;


import com.yoyoyo666.cs101.ecs.jack.JackConstant;
import com.yoyoyo666.cs101.ecs.jack.KeyWordType;
import com.yoyoyo666.cs101.ecs.jack.TokenType;
import com.yoyoyo666.cs101.ecs.vm.VMArithmeticType;
import com.yoyoyo666.cs101.ecs.vm.VMSegmentType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 结构 语句 表达式
 */
public class CompilationEngine {

    private JackTokenizer jackTokenizer;

    private final String typeExpect = "int | boolean | char | className";
    private final String statementExpect = "let | if | while | do | return";

    private StringBuffer out;

    private SymbolTabel mainSymbolTable;
    private SymbolTabel subRoutineSymbolTable;

    private String currentSubroutineType = null;
    private Map<String, String> subroutineTypeMap = new HashMap<>();

    private String className = null;


    private VMWriter vmWriter = null;
    private File outputFile;

    /**
     * @param inputFile
     * @param outputFile
     */

    public void constructor(File inputFile, File outputFile) {
        mainSymbolTable = SymbolTabel.getInstance();
        subRoutineSymbolTable = SymbolTabel.getInstance();
        this.outputFile = outputFile;
        vmWriter = new VMWriter(outputFile);
        jackTokenizer = new JackTokenizer();
        jackTokenizer.constructor(inputFile);
        out = new StringBuffer();
        compileClass();
    }


    private JackTokenizer.Tokenizer advanceAndGet() {
        jackTokenizer.advance();
        return jackTokenizer.getCurrentToken();
    }

    /*
        类型
        type int | boolean | char | className
        表达式
        expression: term (op term)*
        term: integerConstant | stringConstant | keywordConstant | varName | varName'['expression']' | subroutineCall
            | '('expression')' | unaryOp term
        subroutineCall:subroutineName'('expressionList')' | (className | varName).subroutineName'('expressionList')'
        expressionList:(expression (,expression)* )?
        op: + | - | * | / | & | `|` | < | > | =
        unaryOp:-|~
        keywordConstant: true | false | null | this
     */

    /**
     * class className { classVarDec* subroutineDec* }
     */
    public void compileClass() {
        JackTokenizer.Tokenizer tokenizer = this.advanceAndGet();
        if (KeyWordType.CLASS.equalsKey(tokenizer.getToken())) {
            append(getStartTag(KeyWordType.CLASS.getKey()));
            appendTerminals(tokenizer);
            tokenizer = this.advanceAndGet();
            if (tokenizer.getType() == TokenType.IDENTIFIER) {
                appendTerminals(tokenizer);
                className = tokenizer.getToken();
                tokenizer = this.advanceAndGet();
                if ("{".equals(tokenizer.getToken())) {
                    appendTerminals(tokenizer);
                    while (true) {
                        JackTokenizer.Tokenizer nextToken = jackTokenizer.getNextToken();
                        if ("}".equals(nextToken.getToken())) {
                            tokenizer = this.advanceAndGet();
                            appendTerminals(tokenizer);
                            break;
                        }
                        KeyWordType keyWordType = KeyWordType.get(nextToken.getToken());
                        switch (keyWordType) {
                            case FIELD:
                            case STATIC:
                                compileClassVarDec();
                                break;
                            case CONSTRUCTOR:
                            case FUNCTION:
                            case METHOD:
                                compileSubroutine();
                                break;
                            default:
                                throwError(nextToken, "static | field | constructor | method");
                        }

                    }
                } else {
                    throwError(tokenizer, "{");
                }
            } else {
                throwError(tokenizer, TokenType.IDENTIFIER.getKeyWord());
            }
            append(getEndTag(KeyWordType.CLASS.getKey()));
        } else {
            throwError(tokenizer, KeyWordType.CLASS.getKey());
        }
    }


    /**
     * ---  type int | boolean | char | className
     * (static|field) type varName (, varName)* ;
     */
    public void compileClassVarDec() {
        append(getStartTag("classVarDec"));

        JackTokenizer.Tokenizer tokenizer = advanceAndGet();
        KeyWordType keyWordType = KeyWordType.get(tokenizer.getToken());
        if (KeyWordType.STATIC != keyWordType && KeyWordType.FIELD != keyWordType) {
            throwError(tokenizer, "static | field");
        }

        FieldType fieldType = FieldType.FIELD;
        //  2022/9/13 add static field to main symbol table
        if (keyWordType == KeyWordType.STATIC) {
            fieldType = FieldType.STATIC;
        }
        // kind
        appendTerminals(tokenizer);

        tokenizer = advanceAndGet();
        if (!isTypeToken(tokenizer)) {
            throwError(tokenizer, typeExpect);
        }
        String fieldTypeName = tokenizer.getToken();
        // type
        appendTerminals(tokenizer);

        tokenizer = advanceAndGet();
        // name
        String fieldName = tokenizer.getToken();
        mainSymbolTable.define(fieldName, fieldTypeName, fieldType);
        appendIdentifier(tokenizer);

        while (true) {
            tokenizer = advanceAndGet();
            String token = tokenizer.getToken();
            if (";".equals(token)) {
                append(getTerminals(TokenType.SYMBOL.getKeyWord(), ";"));
                break;
            }
            if (!",".equals(token)) {
                throwError(tokenizer, ",");
            }
            append(getTerminals(TokenType.SYMBOL.getKeyWord(), ","));

            tokenizer = advanceAndGet();
            // name more var name
            mainSymbolTable.define(tokenizer.getToken(), fieldTypeName, fieldType);
            appendIdentifier(tokenizer);
        }
        append(getEndTag("classVarDec"));
    }

    /**
     * (constructor|function|method) (type|void) subroutineName '(' parameterList ')' { varDec* statements }
     * 如果是method 则参数要增加一个 第一个参数为this(对象的地址)
     */
    public void compileSubroutine() {
        append(getStartTag("subroutineDec"));
        //(constructor|function|method)
        JackTokenizer.Tokenizer tokenizer = advanceAndGet();
        if (!isMethodToken(tokenizer)) {
            throwError(tokenizer, "method | constructor | function");
        }


        currentSubroutineType = tokenizer.getToken();
        subRoutineSymbolTable.startSubroutine();
        appendTerminals(tokenizer);

        // (type|void)
        tokenizer = advanceAndGet();
        if (!isTypeToken(tokenizer) && !tokenizer.getToken().equals("void")) {
            throwError(tokenizer, typeExpect + " | void");
        }
        appendTerminals(tokenizer);

        //subroutineName
        tokenizer = advanceAndGet();
        String subroutineName = tokenizer.getToken();
        subroutineTypeMap.put(subroutineName, currentSubroutineType);
        appendIdentifier(tokenizer);

        //'('
        tokenizer = advanceAndGet();
        appendSymbol(tokenizer, "(");

        //parameterList
        int argCnt = compileParameterList();

        //')'
        tokenizer = advanceAndGet();
        appendSymbol(tokenizer, ")");


        append(getStartTag("subroutineBody"));
        //varDec* statements
        //'{'
        tokenizer = advanceAndGet();
        appendSymbol(tokenizer, "{");

        int localCnt = 0;
        boolean varOver = false;
        // TODO: 2022/9/14  split var dec and statements ,and count var num, vm write function
        while (true) {
            tokenizer = jackTokenizer.getNextToken();
            if (KeyWordType.VAR.equalsKey(tokenizer.getToken())) {
                if (varOver) {
                    throw new RuntimeException("Variable declarations must precede statement declarations");
                }
                compileVarDec();
                localCnt++;
                continue;
            }
            if (!varOver) {
                varOver = true;
                if ("method".equals(currentSubroutineType)) {
                    localCnt++;
                }
                vmWriter.writeFunction(subroutineName, localCnt);
            }

            if ("}".equals(tokenizer.getToken())) {
                tokenizer = advanceAndGet();
                appendSymbol(tokenizer, "}");
                break;
            }
            compileStatements();
        }

        //}
        append(getEndTag("subroutineBody"));

        append(getEndTag("subroutineDec"));
    }


    /**
     * ((type varName) (, type varName)*)?
     * return 参数数量
     */
    public int compileParameterList() {
        append(getStartTag("parameterList"));
        int cnt = 0;
        JackTokenizer.Tokenizer tokenizer = advanceAndGet();
        if (")".equals(tokenizer.getToken())) {
            jackTokenizer.back();
        } else {
            if (!isTypeToken(tokenizer)) {
                throwError(tokenizer, typeExpect);
            }
            appendTerminals(tokenizer);

            String fieldTypeName = tokenizer.getToken();
            tokenizer = advanceAndGet();
            String fieldName = tokenizer.getToken();
            appendIdentifier(tokenizer);
            cnt++;
            subRoutineSymbolTable.define(fieldName, fieldTypeName, FieldType.ARG);
            while (true) {
                if (",".equals(jackTokenizer.getNextToken().getToken())) {
                    tokenizer = advanceAndGet();
                    appendTerminals(tokenizer);

                    tokenizer = advanceAndGet();
                    if (!isTypeToken(tokenizer)) {
                        throwError(tokenizer, typeExpect);
                    }
                    fieldTypeName = tokenizer.getToken();
                    appendTerminals(tokenizer);

                    tokenizer = advanceAndGet();
                    fieldName = tokenizer.getToken();
                    appendIdentifier(tokenizer);
                    subRoutineSymbolTable.define(fieldName, fieldTypeName, FieldType.ARG);
                    cnt++;
                } else {
                    break;
                }
            }
        }
        append(getEndTag("parameterList"));
        return cnt;
    }

    /**
     * var type varName (, varName)* ;
     */
    public void compileVarDec() {
        append(getStartTag("varDec"));
        // var
        JackTokenizer.Tokenizer tokenizer = advanceAndGet();
        appendKeyword(tokenizer, KeyWordType.VAR);

        // type
        tokenizer = advanceAndGet();
        String fieldTypeName = tokenizer.getToken();
        appendType(tokenizer);

        // varName
        tokenizer = advanceAndGet();
        String fieldName = tokenizer.getToken();
        appendIdentifier(tokenizer);
        subRoutineSymbolTable.define(fieldName, fieldTypeName, FieldType.VAR);

        // (, varName)
        while (",".equals(jackTokenizer.getNextToken().getToken())) {
            tokenizer = advanceAndGet();
            appendTerminals(tokenizer);
            tokenizer = advanceAndGet();
            subRoutineSymbolTable.define(tokenizer.getToken(), fieldTypeName, FieldType.VAR);
            appendIdentifier(tokenizer);
        }

        //;
        tokenizer = advanceAndGet();
        appendSymbol(tokenizer, ";");
        append(getEndTag("varDec"));
    }

    /**
     * (letStatement|ifStatement|whileStatement|doStatement|returnStatement) *
     */
    public void compileStatements() {
        append(getStartTag("statements"));
        while (true) {
            JackTokenizer.Tokenizer tokenizer = jackTokenizer.getNextToken();
            KeyWordType keyWordType = KeyWordType.get(tokenizer.getToken());
            if ("}".equals(tokenizer.getToken())) {
                break;
            }

            if (null == keyWordType) {
                throwError(tokenizer, statementExpect);
            }

            switch (keyWordType) {
                case LET:
                    compileLet();
                    break;
                case IF:
                    compileIf();
                    break;
                case WHILE:
                    compileWhile();
                    break;
                case DO:
                    compileDo();
                    break;
                case RETURN:
                    compileReturn();
                    break;
                default:
                    throwError(tokenizer, statementExpect);
            }
        }
        append(getEndTag("statements"));
    }

    /**
     * do subroutineCall;
     */
    public void compileDo() {
        append(getStartTag("doStatement"));
        JackTokenizer.Tokenizer tokenizer = advanceAndGet();
        appendKeyword(tokenizer, KeyWordType.DO);
        compileSubroutineCall();
        //Pop return value and discard
        vmWriter.writePop(VMSegmentType.S_TEMP, 0);
        tokenizer = advanceAndGet();
        appendSymbol(tokenizer, ";");
        append(getEndTag("doStatement"));
    }

    /**
     * subroutineCall:subroutineName'('expressionList')' | (className | varName).subroutineName'('expressionList')'
     */
    public void compileSubroutineCall() {
        // subroutineName | className | varName
        JackTokenizer.Tokenizer tokenizer = advanceAndGet();
        appendIdentifier(tokenizer);

        String subroutineName = tokenizer.getToken();
        int argCnt = 0;

        tokenizer = advanceAndGet();
        if ("(".equals(tokenizer.getToken())) {
            appendSymbol(tokenizer, "(");
            vmWriter.writePush(VMSegmentType.S_PTR, 0);
            //expressionList
            argCnt = compileExpressionList();
            argCnt++;
            vmWriter.writeCall(getFunName(className, subroutineName), argCnt);
            tokenizer = advanceAndGet();
            appendSymbol(tokenizer, ")");
        } else if (".".equals(tokenizer.getToken())) {
            appendSymbol(tokenizer, ".");
            tokenizer = advanceAndGet();
            String classNameOrVarName = subroutineName;
            //subroutineName
            subroutineName = tokenizer.getToken();
            appendIdentifier(tokenizer);
            tokenizer = advanceAndGet();

            SymbolTabel.SymbolEntity subEntity = subRoutineSymbolTable.get(classNameOrVarName);
            SymbolTabel.SymbolEntity mainEntity = mainSymbolTable.get(classNameOrVarName);
            appendSymbol(tokenizer, "(");

            //expressionList
            if (subEntity == null && mainEntity == null) {
                //if don't have the classNameOrVarName is className;
                argCnt = compileExpressionList();
                vmWriter.writeCall(getFunName(classNameOrVarName, subroutineName), argCnt);
            } else {
                if (subEntity != null) {
                    //push local index(target this)
                    vmWriter.writePush(VMSegmentType.S_LCL, subEntity.getIndex());
                    argCnt = compileExpressionList();
                    argCnt++;
                    vmWriter.writeCall(getFunName(subEntity.getTypeName(), subroutineName), argCnt);
                } else {
                    //push this index
                    vmWriter.writePush(VMSegmentType.S_THIS, mainEntity.getIndex());
                    argCnt = compileExpressionList();
                    argCnt++;
                    vmWriter.writeCall(getFunName(mainEntity.getTypeName(), subroutineName), argCnt);
                }
            }

            tokenizer = advanceAndGet();
            appendSymbol(tokenizer, ")");
        }


    }

    private void writeVariable(String val, boolean isPop) {
        SymbolTabel.SymbolEntity symbolEntity = subRoutineSymbolTable.get(val);
        if (null == symbolEntity) {
            symbolEntity = mainSymbolTable.get(val);
            if (null == symbolEntity) {
                throw new RuntimeException("unkown the variable");
            }
            FieldType fieldType = symbolEntity.getFieldType();
            if (isPop) {
                vmWriter.writePop(fieldType.getVmSegmentType(), symbolEntity.getIndex());
            } else {
                vmWriter.writePush(fieldType.getVmSegmentType(), symbolEntity.getIndex());
            }
        } else {
            FieldType fieldType = symbolEntity.getFieldType();
            if (isPop) {
                vmWriter.writePop(fieldType.getVmSegmentType(), symbolEntity.getIndex());
            } else {
                vmWriter.writePush(fieldType.getVmSegmentType(), symbolEntity.getIndex());
            }
        }

    }

    /**
     * let varName('['expression']')? = expression;
     */
    public void compileLet() {
        append(getStartTag("letStatement"));
        JackTokenizer.Tokenizer tokenizer = advanceAndGet();
        appendKeyword(tokenizer, KeyWordType.LET);

        tokenizer = advanceAndGet();
        String varName = tokenizer.getToken();
        appendIdentifier(tokenizer);
        writeVariable(varName, false);

        tokenizer = advanceAndGet();
        if (!"[".equals(tokenizer.getToken()) && !"=".equals(tokenizer.getToken())) {
            throwError(tokenizer, "[ | =");
        }

        if ("[".equals(tokenizer.getToken())) {
            appendSymbol(tokenizer, "[");
            compileExpression();
            vmWriter.writeArithmetic(VMArithmeticType.ADD);
            // TODO: 2022/9/15
            tokenizer = advanceAndGet();
            appendSymbol(tokenizer, "]");
            tokenizer = advanceAndGet();
            appendSymbol(tokenizer, "=");
            compileExpression();
            //cache expression result
            vmWriter.writePop(VMSegmentType.S_TEMP, 1);
            vmWriter.writePop(VMSegmentType.S_PTR, 1);
            vmWriter.writePush(VMSegmentType.S_TEMP, 1);
            vmWriter.writePop(VMSegmentType.S_THAT, 0);
        } else {
            tokenizer = advanceAndGet();
            appendSymbol(tokenizer, "=");
            compileExpression();
            // TODO: 2022/9/16  
//            vmWriter.writePop(VMSegmentType.S_LCL, symbolEntity.getIndex());
        }

        // TODO: 2022/9/15 unComplete
        //field static var
        //对于变量的赋值 要区分是本地变量还是全局变量，
        //如果是全局变量，还需要区分是static还是field，
        if (subRoutineSymbolTable.has(varName)) {
            SymbolTabel.SymbolEntity symbolEntity = subRoutineSymbolTable.get(varName);
            if ("[".equals(jackTokenizer.getNextToken().getToken())) {
                vmWriter.writePush(VMSegmentType.S_LCL, symbolEntity.getIndex());
                appendSymbol(tokenizer, "[");
                compileExpression();
                vmWriter.writeArithmetic(VMArithmeticType.ADD);
                // TODO: 2022/9/15
                tokenizer = advanceAndGet();
                appendSymbol(tokenizer, "]");
                tokenizer = advanceAndGet();
                appendSymbol(tokenizer, "=");
                compileExpression();
                //cache expression result
                vmWriter.writePop(VMSegmentType.S_TEMP, 1);
                vmWriter.writePop(VMSegmentType.S_PTR, 1);
                vmWriter.writePush(VMSegmentType.S_TEMP, 1);
                vmWriter.writePop(VMSegmentType.S_THAT, 0);
            } else {
                tokenizer = advanceAndGet();
                appendSymbol(tokenizer, "=");
                compileExpression();
                vmWriter.writePop(VMSegmentType.S_LCL, symbolEntity.getIndex());
            }

        } else if (mainSymbolTable.has(varName)) {
            SymbolTabel.SymbolEntity symbolEntity = subRoutineSymbolTable.get(varName);
            if ("[".equals(jackTokenizer.getNextToken().getToken())) {
                vmWriter.writePush(VMSegmentType.S_LCL, symbolEntity.getIndex());
                appendSymbol(tokenizer, "[");
                compileExpression();
                vmWriter.writeArithmetic(VMArithmeticType.ADD);
                // TODO: 2022/9/15
                tokenizer = advanceAndGet();
                appendSymbol(tokenizer, "]");
                tokenizer = advanceAndGet();
                appendSymbol(tokenizer, "=");
                compileExpression();
                //cache expression result
                vmWriter.writePop(VMSegmentType.S_TEMP, 1);
                vmWriter.writePop(VMSegmentType.S_PTR, 1);
                vmWriter.writePush(VMSegmentType.S_TEMP, 1);
                vmWriter.writePop(VMSegmentType.S_THAT, 0);
            } else {
                tokenizer = advanceAndGet();
                appendSymbol(tokenizer, "=");
                compileExpression();
                vmWriter.writePop(VMSegmentType.S_LCL, symbolEntity.getIndex());
            }

        }
//        if ("[".equals(tokenizer.getToken())) {
//            appendSymbol(tokenizer, "[");
//            compileExpression();
//            tokenizer = advanceAndGet();
//            appendSymbol(tokenizer, "]");
//            tokenizer = advanceAndGet();
//        }
//
//        appendSymbol(tokenizer, "=");
//
//        compileExpression();

        tokenizer = advanceAndGet();
        appendSymbol(tokenizer, ";");

        append(getEndTag("letStatement"));
    }

    /**
     * while '(' expression ')' { statements }
     */
    public void compileWhile() {
        append(getStartTag("whileStatement"));
        JackTokenizer.Tokenizer tokenizer = advanceAndGet();
        appendKeyword(tokenizer, KeyWordType.WHILE);
        tokenizer = advanceAndGet();
        appendSymbol(tokenizer, "(");
        compileExpression();
        tokenizer = advanceAndGet();
        appendSymbol(tokenizer, ")");
        tokenizer = advanceAndGet();
        appendSymbol(tokenizer, "{");
        compileStatements();
        tokenizer = advanceAndGet();
        appendSymbol(tokenizer, "}");
        append(getEndTag("whileStatement"));
    }

    /**
     * if'('expression')'{statements} (else { statements })?
     */
    public void compileIf() {
        append(getStartTag("ifStatement"));
        JackTokenizer.Tokenizer tokenizer = advanceAndGet();
        appendKeyword(tokenizer, KeyWordType.IF);
        tokenizer = advanceAndGet();
        appendSymbol(tokenizer, "(");
        compileExpression();
        tokenizer = advanceAndGet();
        appendSymbol(tokenizer, ")");
        tokenizer = advanceAndGet();
        appendSymbol(tokenizer, "{");
        compileStatements();
        tokenizer = advanceAndGet();
        appendSymbol(tokenizer, "}");

        JackTokenizer.Tokenizer nextToken = jackTokenizer.getNextToken();
        if (KeyWordType.ELSE.equalsKey(nextToken.getToken())) {
            tokenizer = advanceAndGet();
            appendKeyword(tokenizer, KeyWordType.ELSE);
            tokenizer = advanceAndGet();
            appendSymbol(tokenizer, "{");
            compileStatements();
            tokenizer = advanceAndGet();
            appendSymbol(tokenizer, "}");
        }

        append(getEndTag("ifStatement"));
    }


    /**
     * return expression? ;
     */
    public void compileReturn() {
        append(getStartTag("returnStatement"));
        JackTokenizer.Tokenizer tokenizer = advanceAndGet();
        appendKeyword(tokenizer, KeyWordType.RETURN);

        if (";".equals(jackTokenizer.getNextToken().getToken())) {
            tokenizer = advanceAndGet();
            appendSymbol(tokenizer, ";");
        } else {
            compileExpression();
            tokenizer = advanceAndGet();
            appendSymbol(tokenizer, ";");
        }
        append(getEndTag("returnStatement"));
    }


    /**
     * term (op term)*
     * op: + | - | * | / | & | `|` | < | > | =
     */
    public void compileExpression() {
        append(getStartTag("expression"));
        compileTerm();
        while (true) {
            JackTokenizer.Tokenizer nextToken = jackTokenizer.getNextToken();
            if (JackConstant.OP_SYMBOL.contains(nextToken.getToken())) {
                JackTokenizer.Tokenizer tokenizer = advanceAndGet();
                appendOpSymbol(tokenizer);
                compileTerm();
            } else {
                break;
            }
        }
        append(getEndTag("expression"));
    }

    /**
     * integerConstant | stringConstant | keywordConstant | varName | varName'['expression']' | subroutineCall
     * | '('expression')' | unaryOp term
     */
    public void compileTerm() {
        append(getStartTag("term"));

        JackTokenizer.Tokenizer tokenizer = advanceAndGet();

        TokenType type = tokenizer.getType();

        //integerConstant
        if (TokenType.INT_CONST == type) {
            appendTerminals(TokenType.INT_CONST.getKeyWord(), String.valueOf(tokenizer.getValue()));
            append(getEndTag("term"));
            return;
        }

        //stringConstant
        if (TokenType.STRING_CONST == type) {
            appendTerminals(TokenType.STRING_CONST.getKeyWord(), String.valueOf(tokenizer.getValue()));
            append(getEndTag("term"));
            return;
        }

        //keywordConstant
        if (TokenType.KEYWORD == type) {
            appendKeywordConstant(tokenizer);
            append(getEndTag("term"));
            return;
        }

        // varName | varName'['expression']' | subroutineCall
        if (TokenType.IDENTIFIER == type) {
            //subroutineCall
            if ("(".equals(jackTokenizer.getNextToken().getToken())
                    || ".".equals(jackTokenizer.getNextToken().getToken())) {
                jackTokenizer.back();
                compileSubroutineCall();
                append(getEndTag("term"));
                return;
            }
            //varName | varName'['expression']'
            appendIdentifier(tokenizer);
            if ("[".equals(jackTokenizer.getNextToken().getToken())) {
                tokenizer = advanceAndGet();
                appendSymbol(tokenizer, "[");
                compileExpression();
                tokenizer = advanceAndGet();
                appendSymbol(tokenizer, "]");
            }
            append(getEndTag("term"));
            return;
        }

        if (TokenType.SYMBOL == type) {
            //'('expression')'
            if ("(".equals(tokenizer.getToken())) {
                appendSymbol(tokenizer, "(");
                compileExpression();
                tokenizer = advanceAndGet();
                appendSymbol(tokenizer, ")");
                append(getEndTag("term"));
                return;
            }
            //unaryOp term
            if (JackConstant.UNARYOP_SYMBOL.contains(tokenizer.getToken())) {
                appendTerminals(tokenizer);
                compileTerm();
                append(getEndTag("term"));
                return;
            }
            throwError(tokenizer, "( | - | ~");
        }
        append(getEndTag("term"));
    }

    /**
     * (expression (,expression)* )?
     * return expression number
     */
    // TODO: 2022/9/15 push expression result
    public int compileExpressionList() {
        int eNum = 0;
        append(getStartTag("expressionList"));
        if (!")".equals(jackTokenizer.getNextToken().getToken())) {
            compileExpression();
            eNum++;
            while (true) {
                if (",".equals(jackTokenizer.getNextToken().getToken())) {
                    JackTokenizer.Tokenizer tokenizer = advanceAndGet();
                    appendSymbol(tokenizer, ",");
                    compileExpression();
                    eNum++;
                } else {
                    break;
                }
            }
        }
        append(getEndTag("expressionList"));
        return eNum;
    }


    private void throwError(JackTokenizer.Tokenizer tk, String expect) {
        StringBuffer sb = new StringBuffer();
        sb.append("line:").append(tk.getOrgLine()).append("syntax error: {").append(tk.getType().getKeyWord())
                .append(":").append(tk.getToken()).append("}\r\nExpect the type of str to be ").append(expect)
                .append(" in file").append(jackTokenizer.getInputFilePath());
        throw new RuntimeException(sb.toString());
    }

    private String getStartTag(String key) {
        return "<" + key + ">";
    }

    private String getEndTag(String key) {
        return "</" + key + ">";
    }

    private String getTerminals(String tag, String value) {
        return getStartTag(tag) + value + getEndTag(tag);
    }

    private void append(String ap) {
        out.append(ap);
    }

    private void appendTerminals(JackTokenizer.Tokenizer tokenizer) {
        append(getTerminals(tokenizer.getType().getKeyWord(), tokenizer.getToken()));
    }

    private void appendTerminals(String key, String val) {
        append(getStartTag(key));
        append(val);
        append(getEndTag(key));
    }

    private void appendIdentifier(JackTokenizer.Tokenizer tokenizer) {
        if (tokenizer.getType() != TokenType.IDENTIFIER) {
            throwError(tokenizer, "identifier");
        }
        appendTerminals(tokenizer);
    }


    private void appendKeywordConstant(JackTokenizer.Tokenizer tokenizer) {
        if (!JackConstant.KEYWORD_CONSTANT.contains(tokenizer.getToken())) {
            throwError(tokenizer, JackConstant.KEYWORD_CONSTANT.stream().collect(Collectors.joining(" | ")));
        }
        appendTerminals(tokenizer);
    }

    private void appendOpSymbol(JackTokenizer.Tokenizer tokenizer) {
        if (!JackConstant.OP_SYMBOL.contains(tokenizer.getToken())) {
            throwError(tokenizer, JackConstant.OP_SYMBOL.stream().collect(Collectors.joining(" | ")));
        }
        if ("<".equals(tokenizer.getToken())) {
            appendTerminals(tokenizer.getType().getKeyWord(), "&lt;");
            return;
        }
        if (">".equals(tokenizer.getToken())) {
            appendTerminals(tokenizer.getType().getKeyWord(), "&gt;");
            return;
        }
        if ("&".equals(tokenizer.getToken())) {
            appendTerminals(tokenizer.getType().getKeyWord(), "&amp;");
            return;
        }
        appendTerminals(tokenizer);
    }

    private void appendSymbol(JackTokenizer.Tokenizer tokenizer, String expect) {
        if (!expect.equals(tokenizer.getToken())) {
            throwError(tokenizer, expect);
        }
        if ("<".equals(tokenizer.getToken())) {
            appendTerminals(tokenizer.getType().getKeyWord(), "&lt;");
            return;
        }
        if (">".equals(tokenizer.getToken())) {
            appendTerminals(tokenizer.getType().getKeyWord(), "&gt;");
            return;
        }
        if ("&".equals(tokenizer.getToken())) {
            appendTerminals(tokenizer.getType().getKeyWord(), "&amp;");
            return;
        }
        appendTerminals(tokenizer);
    }

    private void appendKeyword(JackTokenizer.Tokenizer tokenizer, KeyWordType expect) {
        if (!expect.equalsKey(tokenizer.getToken())) {
            throwError(tokenizer, expect.getKey());
        }
        appendTerminals(tokenizer);
    }

    private void appendType(JackTokenizer.Tokenizer tokenizer) {
        if (!isTypeToken(tokenizer)) {
            throwError(tokenizer, typeExpect);
        }
        appendTerminals(tokenizer);
    }

    //type int | boolean | char | className
    private boolean isTypeToken(JackTokenizer.Tokenizer tokenizer) {
        //className
        boolean isIdentifier = tokenizer.getType() == TokenType.IDENTIFIER;
        if (tokenizer.getType() != TokenType.KEYWORD) {
            return isIdentifier;
        }
        //int | boolean | char
        KeyWordType kt = KeyWordType.get(tokenizer.getToken());
        return kt == KeyWordType.INT
                || kt == KeyWordType.BOOLEAN
                || kt == KeyWordType.CHAR
                || isIdentifier;
    }

    //constructor|function|method
    private boolean isMethodToken(JackTokenizer.Tokenizer tokenizer) {
        if (tokenizer.getType() != TokenType.KEYWORD) {
            return false;
        }
        KeyWordType kt = KeyWordType.get(tokenizer.getToken());
        return kt == KeyWordType.FUNCTION
                || kt == KeyWordType.CONSTRUCTOR
                || kt == KeyWordType.METHOD;
    }


    public String getResult() {
        return out.toString();
    }


    public String getFunName(String className, String subroutineName) {
        return className + "." + subroutineName;
    }

    public void writeResult() {

    }
}
