package modelos;


public enum CategoriaProduto {

    ALIMENTOS,   
    ELETRONICOS, 
    LIVROS;      

 
    public static CategoriaProduto fromString(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Categoria não pode ser vazia.");
        }
        // Percorre todos os valores do enum procurando um que bata com a entrada.
        for (CategoriaProduto c : values()) {
            if (c.name().equalsIgnoreCase(valor.trim())) {
                return c;
            }
        }
        throw new IllegalArgumentException(
            "Categoria inválida: '" + valor + "'. Válidas: ALIMENTOS, ELETRONICOS, LIVROS"
        );
    }
}
