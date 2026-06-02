package dados;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import modelos.CategoriaProduto;
import modelos.Produto;
import utilitarios.BancoDeDadosException;
import utilitarios.Conexao;

public class RepositorioProduto {

    public Produto salvar(Produto produto) {
        final String sql = "INSERT INTO produtos (nome, preco, estoque, categoria) VALUES (?, ?, ?, ?)";

        try (Connection conn = Conexao.obterConexao();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, produto.getNome());
            ps.setBigDecimal(2, produto.getPreco());
            ps.setInt(3, produto.getEstoque());
            ps.setString(4, produto.getCategoria().name());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return new Produto(id, produto.getNome(), produto.getPreco(),
                            produto.getEstoque(), produto.getCategoria());
                }
            }
            throw new BancoDeDadosException("Nao foi possivel obter o id gerado para o produto.", null);

        } catch (SQLException e) {
            throw new BancoDeDadosException("Erro ao salvar produto.", e);
        }
    }

    public Optional<Produto> buscarPorId(int id) {
        final String sql = "SELECT id, nome, preco, estoque, categoria FROM produtos WHERE id = ?";

        try (Connection conn = Conexao.obterConexao();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearProduto(rs));
                }
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new BancoDeDadosException("Erro ao buscar produto por id.", e);
        }
    }

    public Optional<Produto> buscarPorIdComLock(Connection conn, int id) throws SQLException {
        final String sql = "SELECT id, nome, preco, estoque, categoria FROM produtos WHERE id = ? FOR UPDATE";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearProduto(rs));
                }
            }
            return Optional.empty();
        }
    }

    public List<Produto> listarTodos() {
        final String sql = "SELECT id, nome, preco, estoque, categoria FROM produtos ORDER BY nome";
        List<Produto> lista = new ArrayList<>();

        try (Connection conn = Conexao.obterConexao();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearProduto(rs));
            }
            return lista;

        } catch (SQLException e) {
            throw new BancoDeDadosException("Erro ao listar produtos.", e);
        }
    }

    public boolean deduzirEstoque(Connection conn, int produtoId, int quantidade) throws SQLException {
        final String sql = "UPDATE produtos SET estoque = estoque - ? WHERE id = ? AND estoque >= ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantidade);
            ps.setInt(2, produtoId);
            ps.setInt(3, quantidade);
            return ps.executeUpdate() == 1;
        }
    }

    public List<Object[]> produtosMaisVendidos() {
        final String sql = "SELECT p.nome, SUM(i.quantidade) AS total_vendido " +
                "FROM itens_pedido i " +
                "JOIN produtos p ON p.id = i.produto_id " +
                "JOIN pedidos ped ON ped.id = i.pedido_id " +
                "WHERE ped.status = 'FINALIZADO' " +
                "GROUP BY p.id, p.nome " +
                "ORDER BY total_vendido DESC";

        List<Object[]> resultado = new ArrayList<>();

        try (Connection conn = Conexao.obterConexao();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                resultado.add(new Object[] { rs.getString("nome"), rs.getLong("total_vendido") });
            }
            return resultado;

        } catch (SQLException e) {
            throw new BancoDeDadosException("Erro ao gerar relatorio de produtos mais vendidos.", e);
        }
    }

    public List<Object[]> totalVendidoPorCategoria() {
        final String sql = "SELECT p.categoria, SUM(i.quantidade * i.preco_unitario) AS valor_total " +
                "FROM itens_pedido i " +
                "JOIN produtos p ON p.id = i.produto_id " +
                "JOIN pedidos ped ON ped.id = i.pedido_id " +
                "WHERE ped.status = 'FINALIZADO' " +
                "GROUP BY p.categoria " +
                "ORDER BY valor_total DESC";

        List<Object[]> resultado = new ArrayList<>();

        try (Connection conn = Conexao.obterConexao();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                resultado.add(new Object[] { rs.getString("categoria"), rs.getBigDecimal("valor_total") });
            }
            return resultado;

        } catch (SQLException e) {
            throw new BancoDeDadosException("Erro ao gerar relatorio por categoria.", e);
        }
    }

    private Produto mapearProduto(ResultSet rs) throws SQLException {
        return new Produto(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getBigDecimal("preco"),
                rs.getInt("estoque"),
                CategoriaProduto.fromString(rs.getString("categoria")));
    }
}