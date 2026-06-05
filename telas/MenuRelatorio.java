package telas;

import dados.ServicoProduto;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class MenuRelatorio {

    private final ServicoProduto servicoProduto;
    private final Scanner        scanner;

    public MenuRelatorio(Scanner scanner) {
        this.servicoProduto = new ServicoProduto();
        this.scanner        = scanner;
    }

    public void exibir() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n--- MENU RELATORIOS ---");
            System.out.println("1. Produtos mais vendidos");
            System.out.println("2. Vendas por categoria");
            System.out.println("0. Voltar");
            System.out.print("Opção: ");

            String opcao = scanner.nextLine().trim();

            switch (opcao) {
                case "1" -> relatorioMaisVendidos();
                case "2" -> relatorioPorCategoria();
                case "0" -> voltar = true;
                default  -> System.out.println("Opcao invalida.");
            }
        }
    }

    private void relatorioMaisVendidos() {
        List<Object[]> dados = servicoProduto.produtosMaisVendidos();
        System.out.println("\nProdutos mais vendidos:");
        if (dados.isEmpty()) {
            System.out.println("  Nenhum dado disponível. Finalize alguns pedidos primeiro.");
            return;
        }
        System.out.printf("  %-30s %s%n", "Produto", "Qtd. Vendida");
        System.out.println("  " + "-".repeat(45));
        for (Object[] linha : dados) {
            System.out.printf("  %-30s %d%n", linha[0], linha[1]);
        }
    }

    private void relatorioPorCategoria() {
        List<Object[]> dados = servicoProduto.totalVendidoPorCategoria();
        System.out.println("\nVendas por categoria:");
        if (dados.isEmpty()) {
            System.out.println("  Nenhum dado disponível. Finalize alguns pedidos primeiro.");
            return;
        }
        System.out.printf("  %-20s %s%n", "Categoria", "Total Vendido (R$)");
        System.out.println("  " + "-".repeat(45));
        for (Object[] linha : dados) {
            System.out.printf("  %-20s R$%10.2f%n", linha[0], (BigDecimal) linha[1]);
        }
    }
}