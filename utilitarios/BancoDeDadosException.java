package utilitarios;

public class BancoDeDadosException extends RuntimeException {

    public BancoDeDadosException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
