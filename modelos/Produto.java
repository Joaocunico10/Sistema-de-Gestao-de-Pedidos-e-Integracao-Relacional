package modelos;

import java.math.BigDecimal;


public class Produto {

    private static final BigDecimal PRECO_MAX = new BigDecimal("999999.99");
    private static final BigDecimal PRECO_MIN = new BigDecimal("0.01");
    private static final int ESTOQUE_MAX = 999999; 

    private final int id;
    private final String nome;
    private final BigDecimal preco;
    private final int estoque;
    private final CategoriaProduto categoria;


    public Produto(int id, String nome, BigDecimal preco, int estoque, CategoriaProduto categoria) {
        validarNome(nome);
        validarPreco(preco);
        validarEstoque(estoque);
        if (categoria == null) {
            throw new utilitarios.ValidacaoException("Categoria é obrigatória.");
        }
        this.id = id;
        this.nome = nome.trim();
  
        this.preco = preco.setScale(2, java.math.RoundingMode.HALF_UP);
        this.estoque = estoque;
        this.categoria = categoria;
    }

    public Produto(String nome, BigDecimal preco, int estoque, CategoriaProduto categoria) {
        this(0, nome, preco, estoque, categoria);
    }


    public int getId() {return id;}
    public String getNome() {return nome;}
    public BigDecimal getPreco() {return preco;}
    public int getEstoque() {return estoque;}
    public CategoriaProduto getCategoria() {return categoria;}


    private static void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new utilitarios.ValidacaoException("Nome do produto é obrigatório.");
        }
        if (nome.trim().length() > 100) {
            throw new utilitarios.ValidacaoException("Nome do produto deve ter no máximo 100 caracteres.");
        }
    }

    private static void validarPreco(BigDecimal preco) {
        if (preco == null) {
            throw new utilitarios.ValidacaoException("Preço é obrigatório.");
        }
        if (preco.compareTo(PRECO_MIN) < 0) {
            throw new utilitarios.ValidacaoException(
                "Preço deve ser maior que zero. Recebido: " + preco
            );
        }
        if (preco.compareTo(PRECO_MAX) > 0) {
            throw new utilitarios.ValidacaoException(
                "Preço excede o limite máximo de R$ 999.999,99. Recebido: " + preco
            );
        }
    }

    private static void validarEstoque(int estoque) {
        if (estoque < 0) {
            throw new utilitarios.ValidacaoException(
                "Estoque não pode ser negativo. Recebido: " + estoque
            );
        }
        if (estoque > ESTOQUE_MAX) {
            throw new utilitarios.ValidacaoException(
                "Estoque excede o limite máximo de " + ESTOQUE_MAX + ". Recebido: " + estoque
            );
        }
    }

    @Override
    public String toString() {
        return String.format(
            "Produto[id=%d, nome='%s', preco=R$%.2f, estoque=%d, categoria=%s]",
            id, nome, preco, estoque, categoria
        );
    }
}