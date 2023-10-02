import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AnalisadorLexico {

    private char[] conteudo;
    private int indiceConteudo;

    public AnalisadorLexico(String codigo) {
        try {
            String conteudoStr = new String(Files.readAllBytes(Paths.get(codigo)), StandardCharsets.UTF_8);
            this.conteudo = conteudoStr.toCharArray();
            this.indiceConteudo = 0;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private char nextChar() {
        return this.conteudo[this.indiceConteudo++];
    }

    private boolean hasNextChar() {
        return indiceConteudo < this.conteudo.length    ;
    }
    

    private void back() {
        this.indiceConteudo--;
    }

    private boolean letra(char c) {
        return ((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z')) || Character.isLetter(c);
    }

    private boolean digito(char c) {
        return (c >= '0') && (c <= '9');
    }

    private boolean operadorSimples(char c) {
        char[] operadores = {'=', '+', '-', '*', '%', '/', '!', '>', '<'};
        for (char operador : operadores) {
            if (c == operador) {
                return true;
            }
        }
        return false;
    }

    private boolean operadorComposto(String op) {
        String[] operadores = {"||", "&&", ">=", "<=", "!=", "=="};
        for (String operador : operadores) {
            if (op.equals(operador)) {
                return true;
            }
        }
        return false;
    }

    private boolean simboloEspecial(char c) {
        return c == ';' || c == ',' || c == ')' || c == '(' || c == '}' || c == '{' || c == '[' || c == ']';
    }

    private boolean palavraReservada(String c) {
        String[] reservadas = {"if", "int", "float", "char", "boolean", "void", "else", "for", "while", "scanf", "println", "main", "return"};
        for (String reservada : reservadas) {
            if (c.equals(reservada)) {
                return true;
            }
        }
        return false;
    }

    private Token createOperatorToken(String operator) {
        if (operadorComposto(operator) || operadorSimples(operator.charAt(0))) {
            return enterOperador(operator);
        } else {
            throw new RuntimeException("ERRO: Operador desconhecido: " + operator);
        }
    }

    public Token nextToken() {
        while (hasNextChar()) {
            char c = nextChar();
    
            // RETIRAR ESPAÇOS E QUEBRAS DE LINHA
            while (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                if (!hasNextChar()) {
                    return new Token("FIM_DO_CODIGO", Token.TIPO_FIM_CODIGO);
                }
                c = nextChar();
            }
    
            // COMENTÁRIO DE LINHA OU BLOCO
            if (c == '/') {
                char nextChar = nextChar();
                if (nextChar == '/') {
                    return enterComentario("//");
                } else if (nextChar == '*') {
                    return enterComentario("/*");
                } else {
                    back();
                }
            }
    
            if (c == '\'') return enterChar(c);
            if (digito(c) || c == '.') return enterDigit(c);
    
            // Verificar operadores simples ou compostos
            String possibleOperator = String.valueOf(c);
            char next = hasNextChar() ? conteudo[indiceConteudo] : '\0';
            String possibleCompoundOperator = possibleOperator + next;
    
            if (operadorComposto(possibleCompoundOperator) || operadorSimples(c)) {
                nextChar(); 
                return createOperatorToken(possibleCompoundOperator);
            }
    
            if (simboloEspecial(c)) return entersimboloEspecial(c);
            if (letra(c)) return enterIdentificador(c);
            if (c == '"') return enterTexto(c);
    
            System.out.println("Caractere não reconhecido: " + c);
            throw new RuntimeException("ERRO: Caractere não reconhecido");
        }
    
        return new Token("FIM_DO_CODIGO", Token.TIPO_FIM_CODIGO);
    }
    

    private Token enterIdentificador(char c) {
        StringBuilder lexema = new StringBuilder();

        while (letra(c) || digito(c)) {
            lexema.append(c);
            c = nextChar();
        }

        back();
        if (palavraReservada(lexema.toString())) return new Token(lexema.toString(), Token.TIPO_PALAVRA_RESERVADA);
        return new Token(lexema.toString(), Token.TIPO_IDENTIFICADOR);
    }

    private Token entersimboloEspecial(char c) {
        String lexema = "" + c;
        return new Token(lexema, Token.TIPO_SIMBOLO_ESPECIAL);
    }

    private Token enterOperador(String op) {
        return new Token(op, Token.TIPO_OPERADOR);
    }


    private Token enterChar(char c) {
        String lexema = "" + c;
        c = nextChar();
        if (!letra(c) && !digito(c)) throw new RuntimeException("ERROR: Char inválido");
        lexema += c;
        c = nextChar();
        if (c != '\'') throw new RuntimeException("ERROR: Char inválido");
        lexema += c;

        return new Token(lexema, Token.TIPO_CHAR);
    }

    private Token enterDigit(char c) {
        StringBuilder lexema = new StringBuilder();

        do {
            lexema.append(c);
            c = nextChar();
        } while (digito(c) || c == '.');

        back();
        if (lexema.toString().contains(".")) return new Token(lexema.toString(), Token.TIPO_DECIMAL);
        return new Token(lexema.toString(), Token.TIPO_INTEIRO);
    }

    private Token enterTexto(char c) {
        StringBuilder lexema = new StringBuilder();
        lexema.append(c);
        c = nextChar();

        while (c != '"') {
            lexema.append(c);
            c = nextChar();
        }

        lexema.append(c);

        return new Token(lexema.toString(), Token.TIPO_TEXTO);
    }

    private Token enterComentario(String startDelimiter) {
        StringBuilder lexema = new StringBuilder();
        lexema.append(startDelimiter);

        while (hasNextChar()) {
            char c = nextChar();
            lexema.append(c);
            
            if (startDelimiter.equals("/*")) {
                if (c == '*' && hasNextChar() && conteudo[indiceConteudo] == '/') {
                    lexema.append(nextChar()); // Consume o próximo caractere '/'
                    return new Token(lexema.toString(), Token.TIPO_COMENTARIO);
                }
            } else if (c == '\n') {
                return new Token(lexema.toString(), Token.TIPO_COMENTARIO);
            }
        }

        return new Token(lexema.toString(), Token.TIPO_COMENTARIO);
    }

    public static void main(String[] args) {
        AnalisadorLexico analisador = new AnalisadorLexico("teste.txt");
        Token token;
        do {
            token = analisador.nextToken();
            if (token.getTipo() == Token.TIPO_FIM_CODIGO) {
                System.out.println("Fim do código alcançado.");
                break;
            }
            System.out.println(token);
        } while (true);
    }
}
