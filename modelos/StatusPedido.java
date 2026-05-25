package modelos;


public enum StatusPedido {

    ABERTO,
    FILA,
    PROCESSANDO,
    FINALIZADO;

    public static StatusPedido fromString(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Status não pode ser vazio.");
        }
        for (StatusPedido s : values()) {
            if (s.name().equalsIgnoreCase(valor.trim())) {
                return s;
            }
        }
        throw new IllegalArgumentException(
            "Status inválido: '" + valor + "'. Válidos: ABERTO, FILA, PROCESSANDO, FINALIZADO"
        );
    }
}
