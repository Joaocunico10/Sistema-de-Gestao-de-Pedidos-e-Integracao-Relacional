package dados;

import java.util.List;
import modelos.Cliente;
import utilitarios.ValidacaoException;
import utilitarios.ValidadorEntrada;

/**
 * Serviço de clientes.
 *
 * O que é uma camada de Serviço?
 * O Repositório sabe falar com o banco.
 * O Serviço sabe as REGRAS DE NEGÓCIO \u2014 o que pode e o que não pode.
 *
 * Exemplo: qualquer String tecnicamente pode ser inserida no banco,
 * mas a regra diz que o e-mail precisa ser válido e único.
 * Essa verificação fica aqui, não na tela nem no repositório.
 *
 * Por que separar em camadas (tela → serviço → repositório)?
 * Cada camada tem uma responsabilidade clara.
 * - Tela: mostrar menus e ler entradas do usuário.
 * - Serviço: aplicar regras de negócio e validar.
 * - Repositório: salvar e buscar no banco.
 * Se amanhã trocares o banco, só muda o repositório.
 * Se mudares as regras, só muda o serviço.
 * A tela não precisa ser tocada.
 */
public class ServicoCliente {

    private final RepositorioCliente repositorioCliente;

    public ServicoCliente() {
        this.repositorioCliente = new RepositorioCliente();
    }

    public Cliente cadastrar(String nome, String email) {
        validarNome(nome);
        validarEmail(email);
        verificarEmailUnico(email);
        Cliente novo = new Cliente(nome.trim(), email.trim().toLowerCase());
        return repositorioCliente.salvar(novo);
    }

    public Cliente buscarPorId(int id) {
        return repositorioCliente.buscarPorId(id)
                .orElseThrow(() -> new ValidacaoException("Cliente nao encontrado com id: " + id));
    }

    public List<Cliente> listarTodos() {
        return repositorioCliente.listarTodos();
    }

    private void validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new ValidacaoException("Nome do cliente e obrigatorio.");
        }
        if (nome.trim().length() > 100) {
            throw new ValidacaoException("Nome do cliente deve ter no maximo 100 caracteres.");
        }
    }

    private void validarEmail(String email) {
        // Delega a verificação do formato (regex) para o ValidadorEntrada.
        if (!ValidadorEntrada.emailValido(email)) {
            throw new ValidacaoException("E-mail invalido: " + email);
        }
        if (email.trim().length() > 150) {
            throw new ValidacaoException("E-mail deve ter no maximo 150 caracteres.");
        }
    }

    private void verificarEmailUnico(String email) {
        repositorioCliente.buscarPorEmail(email.trim().toLowerCase())
                .ifPresent(c -> {
                    throw new ValidacaoException("E-mail ja cadastrado: " + email);
                });
    }
}