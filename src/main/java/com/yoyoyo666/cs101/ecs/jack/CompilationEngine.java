package com.yoyoyo666.cs101.ecs.jack;


import java.io.File;

/**
 * 结构 语句 表达式
 */
public class CompilationEngine {

    private JackTokenizer jackTokenizer;

    private StringBuffer out;

    /**
     * @param inputFile
     * @param outputFile
     */

    public void constructor(File inputFile, File outputFile) {
        jackTokenizer = new JackTokenizer();
        jackTokenizer.constructor(inputFile);
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
        if (KeyWordTypeEnum.CLASS.equalsKey(tokenizer.getToken())) {
            append(getStartTag(KeyWordTypeEnum.CLASS.getKey()));
            append(getTerminals(tokenizer.getType().getKeyWord(), tokenizer.getToken()));
            tokenizer = this.advanceAndGet();
            if (tokenizer.getType() == TokenTypeEnum.IDENTIFIER) {
                append(getTerminals(tokenizer.getType().getKeyWord(), tokenizer.getToken()));
                tokenizer = this.advanceAndGet();
                if ("{".equals(tokenizer.getToken())) {

                } else {
                    throwError(tokenizer, "{");
                }
            } else {
                throwError(tokenizer, TokenTypeEnum.IDENTIFIER.getKeyWord());
            }
            append(getEndTag(KeyWordTypeEnum.CLASS.getKey()));
        } else {
            throwError(tokenizer, KeyWordTypeEnum.CLASS.getKey());
        }
    }


    /**
     * (static|field) type varName (, varName)* ;
     */
    public void compileClassVarDec() {
    }

    /**
     * (constructor|function|method) (type|void) subroutineName '(' parameterList ')' { varDec* statements }
     */
    public void compileSubroutine() {
    }

    /**
     * ((type varName) (, type varName)*)?
     */
    public void compileParameterList() {
    }

    /**
     * var type varName (, varName)* ;
     */
    public void compileVarDec() {
    }

    /**
     * (letStatement|ifStatement|whileStatement|doStatement|returnStatement) *
     */
    public void compileStatements() {
    }

    /**
     * do subroutineCall;
     */
    public void compileDo() {
    }

    /**
     * let varName('['expression']')? = expression;
     */
    public void compileLet() {
    }

    /**
     * while '(' expression ')' { statements }
     */
    public void compileWhile() {
    }

    /**
     * return expression? ;
     */
    public void compileReturn() {
    }

    /**
     * if'('expression')'{statements}(else { statements })?
     */
    public void compileIf() {
    }

    /**
     * term (op term)*
     */
    public void compileExpression() {
    }

    /**
     * integerConstant | stringConstant | keywordConstant | varName | varName'['expression']' | subroutineCall
     * | '('expression')' | unaryOp term
     */
    public void compileTerm() {
    }

    /**
     * (expression (,expression)* )?
     */
    public void compileExpressionList() {
    }

    //_error(key, val, type) {
    //        let error = 'line:' + this.tokens[this.i].line + ' syntax error: {' + key + ': ' + val
    //                  + '}\r\nExpect the type of key to be ' + type + '\r\nat ' + this.rawFile
    //        throw error
    //    }
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
}
