package utilitarios;

public class EstoqueInsuficienteException extends RuntimeException {

    public EstoqueInsuficienteException(String nomeProduto, int estoqueAtual, int quantidadeSolicitada) {
        super(String.format(
            "Estoque insuficiente para o produto '%s'. Disponível: %d | Solicitado: %d",
            nomeProduto, estoqueAtual, quantidadeSolicitada
        ));
    }
}