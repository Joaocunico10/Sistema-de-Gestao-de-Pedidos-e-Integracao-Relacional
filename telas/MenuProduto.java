package telas;

import dados.ServicoProduto;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import modelos.CategoriaProduto;
import modelos.Produto;
import utilitarios.ValidacaoException;
import utilitarios.ValidadorEntrada;

public class MenuProduto {

    private final ServicoProduto servicoProduto;
    private final Scanner        scanner;

    public MenuProduto(Scanner scanner) {
        this.servicoProduto = new ServicoProduto();
        this.scanner        = scanner;
    }

    public void exibir() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n--- MENU PRODUTOS ---");
            System.out.println("1. Cadastrar produto");
            System.out.println("2. Listar produtos");
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
        System.out.println("\nNovo Produto:");
        String     nome       = ValidadorEntrada.lerStringObrigatoria(scanner, "Nome: ");
        BigDecimal preco      = ValidadorEntrada.lerPreco(scanner, "Preço (ex: 19.90): ");
        int        estoque    = ValidadorEntrada.lerEstoque(scanner, "Estoque: ");

        System.out.println("  Categorias disponíveis:");
        for (CategoriaProduto cat : CategoriaProduto.values()) {
            System.out.println("    " + cat.name());
        }
        String categoriaStr = ValidadorEntrada.lerStringObrigatoria(scanner, "Categoria: ");

        try {
            Produto produto = servicoProduto.cadastrar(nome, preco, estoque, categoriaStr);
            System.out.println("Produto cadastrado: " + produto);
        } catch (ValidacaoException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void listar() {
        List<Produto> lista = servicoProduto.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("  Nenhum produto cadastrado.");
            return;
        }
        System.out.println("\nProdutos:");
        System.out.printf("  %-4s %-25s %12s %8s %-15s%n",
            "ID", "Nome", "Preço", "Estoque", "Categoria");
        System.out.println("  " + "-".repeat(68));
        lista.forEach(p -> System.out.printf(
            "  %-4d %-25s R$%10.2f %8d %-15s%n",
            p.getId(), p.getNome(), p.getPreco(), p.getEstoque(), p.getCategoria()
        ));
    }

    private void buscarPorId() {
        int id = ValidadorEntrada.lerInteiroPositivo(scanner, "ID do produto: ");
        try {
            Produto p = servicoProduto.buscarPorId(id);
            System.out.println("  " + p);
        } catch (ValidacaoException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}