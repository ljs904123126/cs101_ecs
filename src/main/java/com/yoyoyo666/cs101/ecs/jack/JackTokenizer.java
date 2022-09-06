package com.yoyoyo666.cs101.ecs.jack;

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
    private LinkedList<Tokenizer> tokens;
    private int currentLinePointer = 0;
    private Tokenizer preToken;
    private Tokenizer currentToken;
    private File inputFile;

    // 单行注释 注释形式 1:// 2:/* */ 3:/** */
    private static Pattern lineCommentPattern = Pattern.compile("(^\\s*//)|(^\\s*/\\*.*\\*/$\\s*)");
    private static Pattern multilineStartPattern = Pattern.compile("^\\s*/\\*.*");
    private static Pattern multiLineEndPattern = Pattern.compile("\\*/\\s*$");

    private static Pattern integerConstPattern = Pattern.compile("^\\d+$");
    private static Pattern stringConstPattern = Pattern.compile("^\".+\"$");

    public void constructor(File inputFile) {
        this.inputFile = inputFile;
        init();
    }

    protected void reset() {
        lines = new ArrayList<>();
        tokens = new LinkedList<>();
        currentLinePointer = 0;
        preToken = null;
        currentToken = null;

    }


    private void init() {
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
        tokens.add(Tokenizer.getInstance(token, currentLinePointer));
//        System.out.println(token);
    }

    private void lexicalAnalysis(String line) {
        String[] split = line.split("");
        StringBuffer token = new StringBuffer();
        for (int i = 0; i < split.length; i++) {
            String s = split[i];
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
                    if (i >= split.length) {
                        throw new RuntimeException("string const error:" + currentLinePointer);
                    }
                    String _s = split[i];
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
        return !tokens.isEmpty();
    }

    public void advance() {
        if (!this.hasMoreTokens()) {
            throw new RuntimeException("is not have token");
        }
        preToken = currentToken;
        currentToken = tokens.poll();
    }

    public TokenTypeEnum tokenType() {
        return currentToken.getType();
    }

    public KeyWordTypeEnum keyWord() {
        return (KeyWordTypeEnum) currentToken.getValue();
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


    @Override
    public String toString() {

        init();
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
        init();
        return bf.toString();
    }

    public static class Tokenizer {

        private String token;
        private TokenTypeEnum type;
        private Object value;
        private Integer orgLine;

        public static Tokenizer getInstance(String token, Integer line) {
            return new Tokenizer(token, line);
        }

        private Tokenizer(String token, Integer line) {
            this.token = token;
            this.orgLine = line;
            KeyWordTypeEnum keyWordTypeEnum = KeyWordTypeEnum.get(token);
            if (Objects.nonNull(keyWordTypeEnum)) {
                type = TokenTypeEnum.KEYWORD;
                value = keyWordTypeEnum;
                return;
            }
            if (JackConstant.SYMBOL.contains(token)) {
                type = TokenTypeEnum.SYMBOL;
                value = token;
                return;
            }
            if (integerConstPattern.matcher(token).matches()) {
                type = TokenTypeEnum.INT_CONST;
                value = Integer.parseInt(token);
                return;
            }
            if (stringConstPattern.matcher(token).matches()) {
                type = TokenTypeEnum.STRING_CONST;
                value = token.substring(1, token.length() - 1);
                return;
            }
            type = TokenTypeEnum.IDENTIFIER;
            value = token;
        }

        public String getToken() {
            return token;
        }

        public TokenTypeEnum getType() {
            return type;
        }

        public Object getValue() {
            return value;
        }

        public Integer getOrgLine() {
            return orgLine;
        }
    }

    public static void main(String[] args) {

        System.out.println(stringConstPattern.matcher("\"00000\"").matches());

    }

}
