package com.uepb.parser;

import java.util.List;
import com.uepb.token.*;

public class Parser {
    private final List<Token> tokens;
    private int currentTokenIndex = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private Token lookahead() {
        if (currentTokenIndex < tokens.size()) {
            return tokens.get(currentTokenIndex);
        }
        return null; // Representa o fim do fluxo de tokens
    }

    private Token match(String tokenType) {
        Token token = lookahead();
        if (token != null && token.type.equals(tokenType)) {
            currentTokenIndex++;
            return token;
        }
        throw new RuntimeException("Erro sintático: esperado " + tokenType + " mas encontrado " + (token != null ? token.type : "EOF"));
    }

    public void program() {
        expr();
        while (lookahead() != null && lookahead().type.equals("SEMICOLON")) {
            match("SEMICOLON");
            if (lookahead() != null && !lookahead().type.equals("SEMICOLON")) {
                expr();
            } else if (lookahead() != null && lookahead().type.equals("SEMICOLON")) {
                throw new RuntimeException("Erro sintático: Expressão vazia não é permitida antes do ';'");
            }
        }
        if (lookahead() != null) {
            throw new RuntimeException("Erro sintático: Tokens não esperados após o fim da expressão.");
        }
    }

    private void expr() {
        term();
        while (lookahead() != null && (lookahead().type.equals("PLUS") || lookahead().type.equals("MINUS"))) {
            match(lookahead().type);
            term();
        }
    }

    private void term() {
        factor();
        while (lookahead() != null && (lookahead().type.equals("MULTIPLY") || lookahead().type.equals("DIVIDE"))) {
            match(lookahead().type);
            factor();
        }
    }

    private void factor() {
        // Verifica se há um sinal de menos antes do fator
        boolean negative = false; // flag para verificar se é negativo
        if (lookahead() != null && lookahead().type.equals("MINUS")) {
            match("MINUS");
            negative = true; // define a flag para indicar que o número será negativo
        }
    
        if (lookahead() != null) {
            if (lookahead().type.equals("INT") || lookahead().type.equals("FLOAT") || lookahead().type.equals("ID")) {
                match(lookahead().type); // Consume INT, FLOAT, or ID token
            } 
            // Verifica se é um parêntese para uma expressão interna
            else if (lookahead().type.equals("LPAREN")) { 
                match("LPAREN");
                expr(); 
                match("RPAREN");
            } 
            else {
                throw new RuntimeException("Erro sintático: esperado INT, FLOAT, ID ou LPAREN, mas encontrado " + lookahead().type);
            }
        } else {
            throw new RuntimeException("Erro sintático: esperado INT, FLOAT, ID ou LPAREN, mas não há mais tokens");
        }
    
        // Se o próximo token for um operador de exponenciação, analisa o próximo fator
        if (lookahead() != null && lookahead().type.equals("EXPONENTIAL")) {
            match("EXPONENTIAL");
            factor();  // Consome o fator que representa a potência
        }
    }
    

    private void base() {
        if (lookahead() == null) {
            throw new RuntimeException("Erro sintático: esperado INT, FLOAT, ID ou LPAREN, mas não há mais tokens");
        }

        // Verifique se é um número inteiro, número de ponto flutuante ou ID
        if (lookahead().type.equals("INT") || lookahead().type.equals("FLOAT") || lookahead().type.equals("ID")) {
            match(lookahead().type); // Consome INT, FLOAT ou ID
        } 
        // Verifica se é um parêntese para uma expressão interna
        else if (lookahead().type.equals("LPAREN")) { 
            match("LPAREN");
            expr(); 
            match("RPAREN");
        } 
        else {
            throw new RuntimeException("Erro sintático: esperado INT, FLOAT, ID ou LPAREN, mas encontrado " + lookahead().type);
        }
    }

    // Simplificação para lidar apenas com valores simples ou comparações
    private void ref() {
        if (lookahead().type.equals("INT") || lookahead().type.equals("FLOAT") || lookahead().type.equals("ID")) {
            match(lookahead().type); // Consome INT, FLOAT ou ID
        } else {
            throw new RuntimeException("Erro sintático em ref: esperado INT, FLOAT ou ID, mas encontrado " + lookahead().type);
        }
    }
}
