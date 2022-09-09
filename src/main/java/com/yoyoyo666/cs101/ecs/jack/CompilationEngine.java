package com.yoyoyo666.cs101.ecs.jack;


import java.io.File;
import java.util.stream.Collectors;

/**
 * 结构 语句 表达式
 */
public class CompilationEngine {

    private JackTokenizer jackTokenizer;

    private final String typeExpect = "int | boolean | char | className";
    private final String statementExpect = "let | if | while | do | return";

    private StringBuffer out;

    /**
     * @param inputFile
     * @param outputFile
     */

    public void constructor(File inputFile, File outputFile) {
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
        appendTerminals(tokenizer);

        tokenizer = advanceAndGet();
        if (!isTypeToken(tokenizer)) {
            throwError(tokenizer, typeExpect);
        }
        appendTerminals(tokenizer);

        tokenizer = advanceAndGet();
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
            appendIdentifier(tokenizer);
        }
        append(getEndTag("classVarDec"));
    }

    /**
     * (constructor|function|method) (type|void) subroutineName '(' parameterList ')' { varDec* statements }
     */

    public void compileSubroutine() {
        append(getStartTag("subroutineDec"));

        //(constructor|function|method)
        JackTokenizer.Tokenizer tokenizer = advanceAndGet();
        if (!isMethodToken(tokenizer)) {
            throwError(tokenizer, "method | constructor | function");
        }
        appendTerminals(tokenizer);

        // (type|void)
        tokenizer = advanceAndGet();
        if (!isTypeToken(tokenizer) && !tokenizer.getToken().equals("void")) {
            throwError(tokenizer, typeExpect + " | void");
        }
        appendTerminals(tokenizer);

        //subroutineName
        tokenizer = advanceAndGet();
        appendIdentifier(tokenizer);

        //'('
        tokenizer = advanceAndGet();
        appendSymbol(tokenizer, "(");

        //parameterList
        compileParameterList();

        //')'
        tokenizer = advanceAndGet();
        appendSymbol(tokenizer, ")");


        append(getStartTag("subroutineBody"));
        //varDec* statements
        //'{'
        tokenizer = advanceAndGet();
        appendSymbol(tokenizer, "{");

        while (true) {
            tokenizer = jackTokenizer.getNextToken();
            if (KeyWordType.VAR.equalsKey(tokenizer.getToken())) {
                compileVarDec();
                continue;
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
     */
    public void compileParameterList() {
        append(getStartTag("parameterList"));
        JackTokenizer.Tokenizer tokenizer = advanceAndGet();
        if (")".equals(tokenizer.getToken())) {
            jackTokenizer.back();
        } else {
            if (!isTypeToken(tokenizer)) {
                throwError(tokenizer, typeExpect);
            }
            appendTerminals(tokenizer);

            tokenizer = advanceAndGet();
            appendIdentifier(tokenizer);
            while (true) {
                if (",".equals(jackTokenizer.getNextToken().getToken())) {
                    tokenizer = advanceAndGet();
                    appendTerminals(tokenizer);

                    tokenizer = advanceAndGet();
                    if (!isTypeToken(tokenizer)) {
                        throwError(tokenizer, typeExpect);
                    }
                    appendTerminals(tokenizer);

                    tokenizer = advanceAndGet();
                    appendIdentifier(tokenizer);
                } else {
                    break;
                }
            }
        }
        append(getEndTag("parameterList"));
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
        appendType(tokenizer);

        // varName
        tokenizer = advanceAndGet();
        appendIdentifier(tokenizer);

        // (, varName)
        while (",".equals(jackTokenizer.getNextToken().getToken())) {
            tokenizer = advanceAndGet();
            appendTerminals(tokenizer);
            tokenizer = advanceAndGet();
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

        tokenizer = advanceAndGet();
        if ("(".equals(tokenizer.getToken())) {
            appendSymbol(tokenizer, "(");
            //expressionList
            compileExpressionList();
            tokenizer = advanceAndGet();
            appendSymbol(tokenizer, ")");
        } else if (".".equals(tokenizer.getToken())) {
            appendSymbol(tokenizer, ".");
            tokenizer = advanceAndGet();
            //subroutineName
            appendIdentifier(tokenizer);
            tokenizer = advanceAndGet();
            appendSymbol(tokenizer, "(");
            //expressionList
            compileExpressionList();
            tokenizer = advanceAndGet();
            appendSymbol(tokenizer, ")");
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
        appendIdentifier(tokenizer);

        tokenizer = advanceAndGet();
        if (!"[".equals(tokenizer.getToken()) && !"=".equals(tokenizer.getToken())) {
            throwError(tokenizer, "[ | =");
        }
        if ("[".equals(tokenizer.getToken())) {
            appendSymbol(tokenizer, "[");
            compileExpression();
            tokenizer = advanceAndGet();
            appendSymbol(tokenizer, "]");
            tokenizer = advanceAndGet();
        }

//        tokenizer = advanceAndGet();
        appendSymbol(tokenizer, "=");

        compileExpression();

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
     */
    public void compileExpressionList() {
        append(getStartTag("expressionList"));
        if (!")".equals(jackTokenizer.getNextToken().getToken())) {
            compileExpression();
            while (true) {
                if (",".equals(jackTokenizer.getNextToken().getToken())) {
                    JackTokenizer.Tokenizer tokenizer = advanceAndGet();
                    appendSymbol(tokenizer, ",");
                    compileExpression();
                } else {
                    break;
                }
            }
        }
        append(getEndTag("expressionList"));
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

    public void writeResult() {

    }
}
