package dados;

import java.math.BigDecimal;
import java.util.List;
import modelos.CategoriaProduto;
import modelos.Produto;
import utilitarios.ValidacaoException;

public class ServicoProduto {

    private static final BigDecimal PRECO_MIN = new BigDecimal("0.01");
    private static final BigDecimal PRECO_MAX = new BigDecimal("999999.99");
    private static final int ESTOQUE_MAX = 999_999;

    private final RepositorioProduto repositorioProduto;

    public ServicoProduto() {
        this.repositorioProduto = new RepositorioProduto();
    }

    public Produto cadastrar(String nome, BigDecimal preco, int estoque, String categoriaStr) {
        validarNome(nome);
        validarPreco(preco);
        validarEstoque(estoque);
        CategoriaProduto categoria = parseCategoria(categoriaStr);
        Produto novo = new Produto(nome.trim(), preco, estoque, categoria);
        return repositorioProduto.salvar(novo);
    }

    public Produto buscarPorId(int id) {
        return repositorioProduto.buscarPorId(id)
                .orElseThrow(() -> new ValidacaoException("Produto nao encontrado com id: " + id));
    }

    public List<Produto> listarTodos() {
        return repositorioProduto.listarTodos();
    }

    public List<Object[]> produtosMaisVendidos() {
        return repositorioProduto.produtosMaisVendidos();
    }

    public List<Object[]> totalVendidoPorCategoria() {
        return repositorioProduto.totalVendidoPorCategoria();
    }

    private void validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new ValidacaoException("Nome do produto e obrigatorio.");
        }
        if (nome.trim().length() > 100) {
            throw new ValidacaoException("Nome deve ter no maximo 100 caracteres.");
        }
    }

    private void validarPreco(BigDecimal preco) {
        if (preco == null) {
            throw new ValidacaoException("Preco e obrigatorio.");
        }
        if (preco.compareTo(PRECO_MIN) < 0) {
            throw new ValidacaoException("Preco deve ser maior que zero. Recebido: " + preco);
        }
        if (preco.compareTo(PRECO_MAX) > 0) {
            throw new ValidacaoException("Preco excede o limite maximo R$ 999.999,99. Recebido: " + preco);
        }
    }

    private void validarEstoque(int estoque) {
        if (estoque < 0) {
            throw new ValidacaoException("Estoque nao pode ser negativo. Recebido: " + estoque);
        }
        if (estoque > ESTOQUE_MAX) {
            throw new ValidacaoException("Estoque excede o limite maximo " + ESTOQUE_MAX + ".");
        }
    }

    private CategoriaProduto parseCategoria(String valor) {
        try {
            return CategoriaProduto.fromString(valor);
        } catch (IllegalArgumentException e) {
            throw new ValidacaoException(e.getMessage());
        }
    }
}