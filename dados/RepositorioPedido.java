package dados;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import modelos.CategoriaProduto;
import modelos.Cliente;
import modelos.ItemPedido;
import modelos.Pedido;
import modelos.Produto;
import modelos.StatusPedido;
import utilitarios.BancoDeDadosException;
import utilitarios.Conexao;

public class RepositorioPedido {

    public int inserirPedido(Connection conn, Pedido pedido) throws SQLException {
        final String sql = "INSERT INTO pedidos (cliente_id, status, data_criacao) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, pedido.getCliente().getId());
            ps.setString(2, pedido.getStatus().name());
            ps.setTimestamp(3, Timestamp.valueOf(pedido.getDataCriacao()));
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            throw new BancoDeDadosException("Não foi possível obter o id do pedido.", null);
        }
    }

    public void inserirItem(Connection conn, int pedidoId, ItemPedido item) throws SQLException {
        final String sql = "INSERT INTO itens_pedido (pedido_id, produto_id, quantidade, preco_unitario) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pedidoId);
            ps.setInt(2, item.getProduto().getId());
            ps.setInt(3, item.getQuantidade());
            ps.setBigDecimal(4, item.getPrecoUnitario());
            ps.executeUpdate();
        }
    }

    public Optional<Pedido> buscarPorId(int id) {
        final String sqlPedido = "SELECT p.id, p.status, p.data_criacao, " +
                "       c.id AS cli_id, c.nome AS cli_nome, c.email AS cli_email " +
                "FROM pedidos p " +
                "JOIN clientes c ON c.id = p.cliente_id " +
                "WHERE p.id = ?";

        try (Connection conn = Conexao.obterConexao();
                PreparedStatement ps = conn.prepareStatement(sqlPedido)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearPedido(rs, buscarItensDoPedido(conn, id)));
                }
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new BancoDeDadosException("Erro ao buscar pedido por id.", e);
        }
    }

    public List<Pedido> listarTodos() {
        final String sql = "SELECT p.id, p.status, p.data_criacao, " +
                "       c.id AS cli_id, c.nome AS cli_nome, c.email AS cli_email " +
                "FROM pedidos p " +
                "JOIN clientes c ON c.id = p.cliente_id " +
                "ORDER BY p.id DESC";

        List<Pedido> lista = new ArrayList<>();

        try (Connection conn = Conexao.obterConexao();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int pedidoId = rs.getInt("id");
                lista.add(mapearPedido(rs, buscarItensDoPedido(conn, pedidoId)));
            }
            return lista;

        } catch (SQLException e) {
            throw new BancoDeDadosException("Erro ao listar pedidos.", e);
        }
    }

    public List<Pedido> buscarPorStatus(Connection conn, StatusPedido status) throws SQLException {
        final String sql = "SELECT p.id, p.status, p.data_criacao, " +
                "       c.id AS cli_id, c.nome AS cli_nome, c.email AS cli_email " +
                "FROM pedidos p " +
                "JOIN clientes c ON c.id = p.cliente_id " +
                "WHERE p.status = ? " +
                "ORDER BY p.id ASC";

        List<Pedido> lista = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int pedidoId = rs.getInt("id");
                    lista.add(mapearPedido(rs, buscarItensDoPedido(conn, pedidoId)));
                }
            }
        }
        return lista;
    }

    public boolean atualizarStatusCondicional(Connection conn,
            int pedidoId,
            StatusPedido statusAtual,
            StatusPedido novoStatus) throws SQLException {
        final String sql = "UPDATE pedidos SET status = ? WHERE id = ? AND status = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, novoStatus.name());
            ps.setInt(2, pedidoId);
            ps.setString(3, statusAtual.name());
            return ps.executeUpdate() == 1;
        }
    }

    private List<ItemPedido> buscarItensDoPedido(Connection conn, int pedidoId) throws SQLException {
        final String sql = "SELECT i.id, i.pedido_id, i.quantidade, i.preco_unitario, " +
                "       p.id AS prod_id, p.nome AS prod_nome, p.preco AS prod_preco, " +
                "       p.estoque AS prod_estoque, p.categoria AS prod_categoria " +
                "FROM itens_pedido i " +
                "JOIN produtos p ON p.id = i.produto_id " +
                "WHERE i.pedido_id = ?";

        List<ItemPedido> itens = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pedidoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Produto produto = new Produto(
                            rs.getInt("prod_id"),
                            rs.getString("prod_nome"),
                            rs.getBigDecimal("prod_preco"),
                            rs.getInt("prod_estoque"),
                            CategoriaProduto.fromString(rs.getString("prod_categoria")));
                    itens.add(new ItemPedido(
                            rs.getInt("id"),
                            rs.getInt("pedido_id"),
                            produto,
                            rs.getInt("quantidade"),
                            rs.getBigDecimal("preco_unitario")));
                }
            }
        }
        return itens;
    }

    private Pedido mapearPedido(ResultSet rs, List<ItemPedido> itens) throws SQLException {
        Cliente cliente = new Cliente(
                rs.getInt("cli_id"),
                rs.getString("cli_nome"),
                rs.getString("cli_email"));
        return new Pedido(
                rs.getInt("id"),
                cliente,
                StatusPedido.fromString(rs.getString("status")),
                rs.getTimestamp("data_criacao").toLocalDateTime(),
                itens);
    }
}