package modelos;

import utilitarios.ValidacaoException;

public class Cliente {

    private final int id;
    private final String nome;
    private final String email;

    public Cliente(int id, String nome, String email) {
        validarNome(nome);
        validarEmail(email);

        this.id = id;
        this.nome = nome.trim();
        this.email = email.trim().toLowerCase();
    }

    public Cliente(String nome, String email) {
        this(0, nome, email);
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    private static void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new ValidacaoException("Nome do cliente é obrigatório.");
        }

        if (nome.trim().length() > 100) {
            throw new ValidacaoException("Nome do cliente deve ter no máximo 100 caracteres.");
        }
    }

    private static void validarEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ValidacaoException("E-mail do cliente é obrigatório.");
        }

        if (!email.trim().matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$")) {
            throw new ValidacaoException("E-mail inválido: " + email);
        }

        if (email.trim().length() > 150) {
            throw new ValidacaoException("E-mail deve ter no máximo 150 caracteres.");
        }
    }

    @Override
    public String toString() {
        return String.format(
                "Cliente[id=%d, nome='%s', email='%s']",
                id,
                nome,
                email);
    }
}
