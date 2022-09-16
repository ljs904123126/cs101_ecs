package com.yoyoyo666.cs101.ecs.jack2;

import com.yoyoyo666.cs101.ecs.jack.JackConstant;
import com.yoyoyo666.cs101.ecs.jack.KeyWordType;
import com.yoyoyo666.cs101.ecs.jack.TokenType;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class JackTokenizer {

    private List<String> lines;
    private LinkedList<Tokenizer> tokensSq;
    private int sqPointer = 0;
    private int currentLinePointer = 0;
    private Tokenizer preToken;
    private Tokenizer currentToken;
    private Tokenizer nextToken;
    private File inputFile;

    // 单行注释 注释形式 1:// 2:/* */ 3:/** */
    private static Pattern lineCommentPattern = Pattern.compile("(^\\s*//)|(^\\s*/\\*.*\\*/$\\s*)");
    private static Pattern multilineStartPattern = Pattern.compile("^\\s*/\\*.*");
    private static Pattern multiLineEndPattern = Pattern.compile("\\*/\\s*$");

    private static Pattern integerConstPattern = Pattern.compile("^\\d+$");
    private static Pattern stringConstPattern = Pattern.compile("^\".+\"$");

    public String getInputFilePath() {
        return inputFile.getPath();
    }

    public void constructor(File inputFile) {
        this.inputFile = inputFile;
        init();
    }

    protected void reset() {
        currentLinePointer = 0;
        sqPointer = 0;
        preToken = null;
        currentToken = null;
    }


    private void init() {
        lines = new ArrayList<>();
        tokensSq = new LinkedList<>();
        reset();
        try (FileInputStream inputStream = new FileInputStream(inputFile);
             InputStreamReader is = new InputStreamReader(inputStream);
             BufferedReader bf = new BufferedReader(is)) {
            lines = bf.lines().collect(Collectors.toList());
            for (; currentLinePointer < lines.size(); currentLinePointer++) {
                String line = lines.get(currentLinePointer);
                if (this.isValid(line)) {
                    // TODO: 2022/9/5  replace inner line comments
                    line = line.replaceAll("//.*", "");
                    line = line.replaceAll("\\s*/\\*.*\\*/", "");
                    lexicalAnalysis(line);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //增加token
    private void pushToken(String token) {
        tokensSq.add(Tokenizer.getInstance(token, currentLinePointer));
//        System.out.println(token);
    }

    private void lexicalAnalysis(String line) {
        String[] chars = line.split("");
        StringBuffer token = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            String s = chars[i];
            if (StringUtils.isBlank(s)) {
                if (token.length() == 0) {
                    continue;
                } else {
                    pushToken(token.toString());
                    token.setLength(0);
                    continue;
                }
            }
            if (JackConstant.SYMBOL.contains(s)) {
                if (token.length() > 0) {
                    pushToken(token.toString());
                    token.setLength(0);
                }
                pushToken(s);
                continue;
            }
            //处理字符串
            if ("\"".equals(s)) {
                token.append(s);
                while (true) {
                    i++;
                    if (i >= chars.length) {
                        throw new RuntimeException("string const error:" + currentLinePointer);
                    }
                    String _s = chars[i];
                    token.append(_s);
                    if ("\"".equals(_s)) {
                        break;
                    }
                }
                continue;
            }
            token.append(s);
        }
        if (token.length() > 0) {
            throw new RuntimeException("error end line:" + currentLinePointer);
        }
    }

    private boolean isValid(String str) {
        if (lineCommentPattern.matcher(str).find()) {
            return false;
        } else if (multilineStartPattern.matcher(str).find()) {
            int startLine = currentLinePointer;
            while (true) {
                currentLinePointer++;
                if (currentLinePointer >= lines.size()) {
                    throw new RuntimeException("token lien error ,multiline comments error :" + startLine);
                }
                String inStr = lines.get(currentLinePointer);
                if (multiLineEndPattern.matcher(inStr).find()) {
                    return false;
                }
            }
        }
        return true;

    }

    public boolean hasMoreTokens() {
        return sqPointer < tokensSq.size();
    }

    public void advance() {
        if (!this.hasMoreTokens()) {
            throw new RuntimeException("is not have token");
        }
        preToken = currentToken;
        currentToken = tokensSq.get(sqPointer);
        nextToken = null;
        sqPointer++;
        if (sqPointer < tokensSq.size()) {
            nextToken = tokensSq.get(sqPointer);
        }
    }

    public void back() {
        if (sqPointer <= 0) {
            return;
        }
        sqPointer--;
        nextToken = currentToken;
        currentToken = tokensSq.get(sqPointer);
        preToken = null;
        if (sqPointer - 1 >= 0) {
            preToken = tokensSq.get(sqPointer - 1);
        }
    }

    public TokenType tokenType() {
        return currentToken.getType();
    }

    public KeyWordType keyWord() {
        return (KeyWordType) currentToken.getValue();
    }

    public String symbol() {
        return (String) currentToken.getValue();
    }

    public String identifier() {
        return (String) currentToken.getValue();
    }

    /**
     * return the integer value of the current token, should be called only when tokenTyoe is int_const
     *
     * @return
     */
    public Integer intVal() {
        return (Integer) currentToken.getValue();
    }

    public String stringVal() {
        return (String) currentToken.getValue();
    }

    public Tokenizer getCurrentToken() {
        return currentToken;
    }

    public Tokenizer getPreToken() {
        return preToken;
    }

    public Tokenizer getNextToken() {
        return nextToken;
    }

    public String getResult() {

        reset();
        StringBuffer bf = new StringBuffer("<tokens>\n");
        while (this.hasMoreTokens()) {
            this.advance();
            String type = this.tokenType().getKeyWord();
            bf.append("<" + type + "> ");
            bf.append(currentToken.getToken());
//            bf.append(currentToken.getOrgLine());
            bf.append(" </" + type + ">");
            bf.append("\n");
        }
        bf.append("</tokens>");
        reset();
        return bf.toString();
    }

    public static class Tokenizer {

        private String token;
        private TokenType type;
        private Object value;
        private Integer orgLine;

        public static Tokenizer getInstance(String token, Integer line) {
            return new Tokenizer(token, line);
        }

        private Tokenizer(String token, Integer line) {
            this.token = token;
            this.orgLine = line;
            KeyWordType keyWordType = KeyWordType.get(token);
            if (Objects.nonNull(keyWordType)) {
                type = TokenType.KEYWORD;
                value = keyWordType;
                return;
            }
            if (JackConstant.SYMBOL.contains(token)) {
                type = TokenType.SYMBOL;
                value = token;
                return;
            }
            if (integerConstPattern.matcher(token).matches()) {
                type = TokenType.INT_CONST;
                value = Integer.parseInt(token);
                return;
            }
            if (stringConstPattern.matcher(token).matches()) {
                type = TokenType.STRING_CONST;
                value = token.substring(1, token.length() - 1);
                return;
            }
            type = TokenType.IDENTIFIER;
            value = token;
        }

        public String getToken() {
            return token;
        }

        public TokenType getType() {
            return type;
        }

        public Object getValue() {
            return value;
        }

        public Integer getOrgLine() {
            return orgLine;
        }

        @Override
        public String toString() {
            return "Tokenizer{" +
                    "token='" + token + '\'' +
                    ", type=" + type +
                    ", value=" + value +
                    ", orgLine=" + orgLine +
                    '}';
        }
    }


}
