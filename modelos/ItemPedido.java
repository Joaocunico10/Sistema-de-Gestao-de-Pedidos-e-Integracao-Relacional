package modelos;

import java.math.BigDecimal;

public class ItemPedido {

    private static final BigDecimal PRECO_MAX = new BigDecimal("999999.99");
    private static final BigDecimal PRECO_MIN = new BigDecimal("0.01");
    private static final int QTD_MAX = 999_999;

    private final int id;
    private final int pedidoId;
    private final Produto produto;
    private final int quantidade;
    private final BigDecimal precoUnitario; 

   
    public ItemPedido(int id, int pedidoId, Produto produto, int quantidade, BigDecimal precoUnitario) {
        validarProduto(produto);
        validarQuantidade(quantidade);
        validarPrecoUnitario(precoUnitario);
        this.id = id;
        this.pedidoId = pedidoId;
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public ItemPedido(int pedidoId, Produto produto, int quantidade, BigDecimal precoUnitario) {
        this(0, pedidoId, produto, quantidade, precoUnitario);
    }

 
    public ItemPedido(Produto produto, int quantidade) {
        this(0, 0, produto, quantidade, produto.getPreco());
    }

    public int getId(){ return id;}
    public int getPedidoId(){ return pedidoId;}
    public Produto getProduto(){ return produto;}
    public int getQuantidade(){ return quantidade;}
    public BigDecimal getPrecoUnitario(){ return precoUnitario; }


    public BigDecimal getSubtotal() {
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }


    private static void validarProduto(Produto produto) {
        if (produto == null) {
            throw new utilitarios.ValidacaoException("Produto do item é obrigatório.");
        }
    }

    private static void validarQuantidade(int quantidade) {
        if (quantidade <= 0) {
            throw new utilitarios.ValidacaoException(
                "Quantidade deve ser maior que zero. Recebido: " + quantidade
            );
        }
        if (quantidade > QTD_MAX) {
            throw new utilitarios.ValidacaoException(
                "Quantidade excede o limite máximo de " + QTD_MAX + ". Recebido: " + quantidade
            );
        }
    }

    private static void validarPrecoUnitario(BigDecimal preco) {
        if (preco == null || preco.compareTo(PRECO_MIN) < 0) {
            throw new utilitarios.ValidacaoException("Preço unitário deve ser maior que zero.");
        }
        if (preco.compareTo(PRECO_MAX) > 0) {
            throw new utilitarios.ValidacaoException("Preço unitário excede o limite máximo.");
        }
    }

    @Override
    public String toString() {
        return String.format(
            "Item[produto='%s', qtd=%d, precoUnit=R$%.2f, subtotal=R$%.2f]",
            produto.getNome(), quantidade, precoUnitario, getSubtotal()
        );
    }
}