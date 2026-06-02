package dados;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import modelos.Cliente;
import modelos.ItemPedido;
import modelos.Pedido;
import modelos.Produto;
import modelos.StatusPedido;
import utilitarios.BancoDeDadosException;
import utilitarios.Conexao;
import utilitarios.EstoqueInsuficienteException;
import utilitarios.ValidacaoException;

public class ServicoPedido {

    private final RepositorioPedido repositorioPedido;
    private final RepositorioProduto repositorioProduto;

    public ServicoPedido() {
        this.repositorioPedido = new RepositorioPedido();
        this.repositorioProduto = new RepositorioProduto();
    }

    public Pedido criarPedido(Cliente cliente, List<ItemPedido> itens) {
        validarCliente(cliente);
        validarItens(itens);

        try (Connection conn = Conexao.obterConexao()) {
            conn.setAutoCommit(false);

            try {
                for (ItemPedido item : itens) {
                    int produtoId = item.getProduto().getId();
                    int quantidade = item.getQuantidade();

                    Produto produtoBanco = repositorioProduto
                            .buscarPorIdComLock(conn, produtoId)
                            .orElseThrow(() -> new ValidacaoException(
                                    "Produto nao encontrado: id=" + produtoId));

                    if (produtoBanco.getEstoque() < quantidade) {
                        throw new EstoqueInsuficienteException(
                                produtoBanco.getNome(), produtoBanco.getEstoque(), quantidade);
                    }

                    boolean deduzido = repositorioProduto.deduzirEstoque(conn, produtoId, quantidade);
                    if (!deduzido) {
                        throw new EstoqueInsuficienteException(
                                produtoBanco.getNome(), produtoBanco.getEstoque(), quantidade);
                    }
                }

                Pedido pedido = new Pedido(cliente, StatusPedido.FILA, LocalDateTime.now(), itens);
                int pedidoId = repositorioPedido.inserirPedido(conn, pedido);

                for (ItemPedido item : itens) {
                    repositorioPedido.inserirItem(conn, pedidoId, item);
                }

                // ── Etapa 7: commit \u2014 confirma tudo ──────────────────────────
                conn.commit();
                return new Pedido(pedidoId, cliente, StatusPedido.FILA, pedido.getDataCriacao(), itens);

            } catch (EstoqueInsuficienteException | ValidacaoException e) {
                rollbackSilencioso(conn);
                throw e;
            } catch (SQLException e) {
                rollbackSilencioso(conn);
                throw new BancoDeDadosException("Erro ao criar pedido.", e);
            }

        } catch (SQLException e) {
            throw new BancoDeDadosException("Erro ao abrir conexao para criar pedido.", e);
        }
    }

    public List<Pedido> listarTodos() {
        return repositorioPedido.listarTodos();
    }

    public Pedido buscarPorId(int id) {
        return repositorioPedido.buscarPorId(id)
                .orElseThrow(() -> new ValidacaoException("Pedido nao encontrado com id: " + id));
    }

    private void rollbackSilencioso(Connection conn) {
        try {
            conn.rollback();
        } catch (SQLException ex) {
            System.err.println("[WARN] Falha ao executar rollback: " + ex.getMessage());
        }
    }

    private void validarCliente(Cliente cliente) {
        if (cliente == null || cliente.getId() <= 0) {
            throw new ValidacaoException("Pedido deve ter um cliente valido.");
        }
    }

    private void validarItens(List<ItemPedido> itens) {
        if (itens == null || itens.isEmpty()) {
            throw new ValidacaoException("Pedido deve conter pelo menos um item.");
        }
        for (ItemPedido item : itens) {
            if (item.getQuantidade() <= 0) {
                throw new ValidacaoException(
                        "Quantidade invalida no item: " + item.getProduto().getNome());
            }
        }
    }
}