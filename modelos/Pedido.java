package modelos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pedido {

    private final int id;
    private final Cliente cliente;
    private final StatusPedido status;
    private final LocalDateTime dataCriacao; 
    private final List<ItemPedido> itens;

    
    public Pedido(int id, Cliente cliente, StatusPedido status,
                  LocalDateTime dataCriacao, List<ItemPedido> itens) {
        validarCliente(cliente);
        if (status == null) {
            throw new utilitarios.ValidacaoException("Status do pedido é obrigatório.");
        }
        if (dataCriacao == null) {
            throw new utilitarios.ValidacaoException("Data de criação é obrigatória.");
        }
        this.id = id;
        this.cliente = cliente;
        this.status = status;
        this.dataCriacao = dataCriacao;
        this.itens = Collections.unmodifiableList(new ArrayList<>(
            itens != null ? itens : new ArrayList<>()
        ));
    }

    public Pedido(Cliente cliente, StatusPedido status,
                  LocalDateTime dataCriacao, List<ItemPedido> itens) {
        this(0, cliente, status, dataCriacao, itens);
    }


    public int getId(){ return id;}
    public Cliente getCliente(){ return cliente;}
    public StatusPedido getStatus(){ return status;}
    public LocalDateTime getDataCriacao() { return dataCriacao; }

    public List<ItemPedido>  getItens(){ return itens;}

    public BigDecimal getTotal() {
        return itens.stream()
            .map(ItemPedido::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    private static void validarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new utilitarios.ValidacaoException("Pedido deve ter um cliente válido.");
        }
    }

    @Override
    public String toString() {
        return String.format(
            "Pedido[id=%d, cliente='%s', status=%s, criacao=%s, itens=%d, total=R$%.2f]",
            id, cliente.getNome(), status, dataCriacao, itens.size(), getTotal()
        );
    }
}