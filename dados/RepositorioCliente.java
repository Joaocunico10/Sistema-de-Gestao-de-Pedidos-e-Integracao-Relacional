package dados;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import modelos.Cliente;
import utilitarios.BancoDeDadosException;
import utilitarios.Conexao;

/**
 * Repositório de clientes.
 *
 * O que é um repositório?
 * É a classe responsável por FALAR COM O BANCO DE DADOS.
 * Ela não sabe de regras de negócio \u2014 só sabe fazer SELECT, INSERT, etc.
 * As regras ficam no Serviço (ServicoCliente).
 *
 * O que é PreparedStatement?
 * É a forma segura de executar SQL com parâmetros variáveis.
 * Em vez de concatenar strings como:
 * "SELECT * FROM clientes WHERE email = '" + email + "'"
 * usamos:
 * "SELECT * FROM clientes WHERE email = ?"
 * e depois ps.setString(1, email);
 *
 * Por que isso é importante?
 * Concatenar strings permite SQL Injection \u2014 um ataque onde o usuário
 * digita código SQL malicioso no campo. PreparedStatement previne isso.
 *
 * try-with-resources:
 * A sintaxe "try (Connection conn = ...) { }" garante que a conexão
 * seja fechada AUTOMATICAMENTE ao final, mesmo se ocorrer um erro.
 * Sem isso, conexões ficam abertas e o banco esgota os recursos.
 *
 * Optional<Cliente>:
 * É um "container" que pode ou não conter um Cliente.
 * Evita retornar null \u2014 o chamador é obrigado a verificar se tem valor.
 * Optional.empty() = não encontrado. Optional.of(obj) = encontrado.
 */
public class RepositorioCliente {

    public Cliente salvar(Cliente cliente) {

        final String sql = "INSERT INTO clientes (nome, email) VALUES (?, ?)";

        try (Connection conn = Conexao.obterConexao();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, cliente.getNome());
            ps.setString(2, cliente.getEmail());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGerado = rs.getInt(1);
                    return new Cliente(idGerado, cliente.getNome(), cliente.getEmail());
                }
            }
            throw new BancoDeDadosException("Não foi possível obter o id gerado para o cliente.", null);

        } catch (SQLIntegrityConstraintViolationException e) {
            throw new utilitarios.ValidacaoException("E-mail já cadastrado: " + cliente.getEmail());
        } catch (SQLException e) {
            throw new BancoDeDadosException("Erro ao salvar cliente.", e);
        }
    }

    public Optional<Cliente> buscarPorId(int id) {
        final String sql = "SELECT id, nome, email FROM clientes WHERE id = ?";

        try (Connection conn = Conexao.obterConexao();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearCliente(rs));
                }
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new BancoDeDadosException("Erro ao buscar cliente por id.", e);
        }
    }

    public Optional<Cliente> buscarPorEmail(String email) {
        final String sql = "SELECT id, nome, email FROM clientes WHERE email = ?";

        try (Connection conn = Conexao.obterConexao();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearCliente(rs));
                }
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new BancoDeDadosException("Erro ao buscar cliente por e-mail.", e);
        }
    }

    public List<Cliente> listarTodos() {
        final String sql = "SELECT id, nome, email FROM clientes ORDER BY nome";
        List<Cliente> lista = new ArrayList<>();

        try (Connection conn = Conexao.obterConexao();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearCliente(rs));
            }
            return lista;

        } catch (SQLException e) {
            throw new BancoDeDadosException("Erro ao listar clientes.", e);
        }
    }

    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("email"));
    }
}