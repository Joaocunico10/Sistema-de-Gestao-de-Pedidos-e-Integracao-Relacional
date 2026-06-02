package telas;

import dados.ServicoCliente;
import java.util.List;
import java.util.Scanner;
import modelos.Cliente;
import utilitarios.ValidacaoException;
import utilitarios.ValidadorEntrada;

public class MenuCliente {

    private final ServicoCliente servicoCliente;
    private final Scanner        scanner;

    public MenuCliente(Scanner scanner) {
        this.servicoCliente = new ServicoCliente();
        this.scanner        = scanner;
    }

    public void exibir() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n--- MENU CLIENTES ---");
            System.out.println("1. Cadastrar cliente");
            System.out.println("2. Listar clientes");
            System.out.println("3. Buscar por ID");
            System.out.println("0. Voltar");
            System.out.print("Opção: ");

            String opcao = scanner.nextLine().trim();

            switch (opcao) {
                case "1" -> cadastrar();
                case "2" -> listar();
                case "3" -> buscarPorId();
                case "0" -> voltar = true;
                default  -> System.out.println("Opcao invalida.");
            }
        }
    }

    private void cadastrar() {
        System.out.println("\nNovo Cliente:");
        String nome  = ValidadorEntrada.lerStringObrigatoria(scanner, "Nome: ");
        String email = ValidadorEntrada.lerEmail(scanner, "E-mail: ");

        try {
            Cliente cliente = servicoCliente.cadastrar(nome, email);
            System.out.println("Cliente cadastrado: " + cliente);
        } catch (ValidacaoException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void listar() {
        List<Cliente> lista = servicoCliente.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("  Nenhum cliente cadastrado.");
            return;
        }
        System.out.println("\nClientes:");
        lista.forEach(c -> System.out.printf(
            "  [%d] %-30s  %s%n", c.getId(), c.getNome(), c.getEmail()
        ));
    }

    private void buscarPorId() {
        int id = ValidadorEntrada.lerInteiroPositivo(scanner, "ID do cliente: ");
        try {
            Cliente c = servicoCliente.buscarPorId(id);
            System.out.println("  " + c);
        } catch (ValidacaoException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}