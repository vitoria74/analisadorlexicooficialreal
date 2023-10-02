public class Token {
    public static int TIPO_INTEIRO = 0;
    public static int TIPO_DECIMAL = 1;
    public static int TIPO_CHAR = 2;
    public static int TIPO_IDENTIFICADOR = 3;
    public static int TIPO_OPERADOR= 4;
    public static int TIPO_PALAVRA_RESERVADA = 5;
    public static int TIPO_TEXTO = 6;
    public static int TIPO_COMENTARIO = 7;
    public static int TIPO_SIMBOLO_ESPECIAL = 8;
    public static final int TIPO_FIM_CODIGO = -1;


    private int tipo; 
    private String lexema; 

    public Token(String lexema, int tipo){
        this.lexema = lexema;
        this.tipo = tipo;
    }

    public String getLexema(){
        return this.lexema;
    }

    public int getTipo(){
        return this.tipo;
    }

    @Override
    public String toString()
    {
        switch(this.tipo){
            case 0:
                return this.lexema + " -> NUM_INT" ;
            case 1:
                return this.lexema + " -> NUM_DEC";
            case 2:
                return this.lexema + " -> CHAR";
            case 3:
                return this.lexema + " -> ID";
            case 4:
                return this.lexema + " -> OPERADOR";
            case 5:
                return this.lexema + " -> PALAVRA_RESERVADA";
            case 6:
                return this.lexema + " -> TEXTO";
            case 7:
                return this.lexema + " -> COMENTARIO";
            case 8:
                return this.lexema + " -> SIMBOLO_ESPECIAL";
            case -1:
                return this.lexema + " -> FIM_CODIGO";
        }
        return "";
    }

    public static String toString(int tipo)
    {
        switch(tipo){
          case 0:
                return " -> NUM_INT" ;
            case 1:
                return " -> NUM_DEC";
            case 2:
                return " -> CHAR";
            case 3:
                return " -> ID";
            case 4:
                return " -> OPERADOR";
            case 5:
                return " -> OPERADOR_COMPARACAO";
            case 6:
                return " -> PALAVRA_RESERVADA";
            case 7:
                return " -> TEXTO";
            case 8:
                return " -> COMENTARIO";
            case 9:
                return " -> SIMBOLO_ESPECIAL";
            case -1:
                return  " -> FIM_CODIGO";
            }  
        return "";
    }
}

