package utilitarios;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public final class Conexao {

    private static final String URL     = "jdbc:mysql://localhost:3306/loja_vendas"
                                        + "?useSSL=false"
                                        + "&serverTimezone=America/Sao_Paulo"
                                        + "&allowPublicKeyRetrieval=true"
                                        + "&characterEncoding=UTF-8";
    private static final String USUARIO = "root";
    private static final String SENHA   = ""; 

 
    private Conexao() {}

    public static Connection obterConexao() {
        try {
            return DriverManager.getConnection(URL, USUARIO, SENHA);
        } catch (SQLException e) {
            throw new BancoDeDadosException(
                "Não foi possível conectar ao banco de dados: " + e.getMessage(), e
            );
        }
    }
}
