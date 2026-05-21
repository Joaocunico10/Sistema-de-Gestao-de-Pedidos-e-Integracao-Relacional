package modelos;


public class Cliente {
    private final Integer id; 
    private final String nome;
    private final String email;


    public Cliente(Integer id, String nome, String email) {
        validarCliente(nome, email);
        this.id = id;
        this.nome = nome;
        this.email = email;
    }

 
    public Cliente(String nome, String email) {
        this(null, nome, email);
    }

    private void validarCliente(String nome, String email) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new RegraNegocioException("O nome do cliente é obrigatório.");
        }
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            throw new RegraNegocioException("E-mail inválido ou vazio.");
        }
    }

    public Integer getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
}
