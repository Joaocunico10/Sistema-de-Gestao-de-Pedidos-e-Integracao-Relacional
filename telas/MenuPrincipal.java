package telas;

import java.util.Scanner;

public class MenuPrincipal {

    public void iniciar() {
        System.out.println("SISTEMA DE GESTAO DE PEDIDOS");
        System.out.println("Versao 1.0 - Console Java + MySQL");

        try (Scanner scanner = new Scanner(System.in)) {
            MenuCliente   menuCliente   = new MenuCliente(scanner);
            MenuProduto   menuProduto   = new MenuProduto(scanner);
            MenuPedido    menuPedido    = new MenuPedido(scanner);
            MenuRelatorio menuRelatorio = new MenuRelatorio(scanner);

            boolean sair = false;
            while (!sair) {
                System.out.println("\n--- MENU PRINCIPAL ---");
                System.out.println("1. Clientes");
                System.out.println("2. Produtos");
                System.out.println("3. Pedidos");
                System.out.println("4. Relatorios");
                System.out.println("0. Sair");
                System.out.print("Opção: ");

                String opcao = scanner.nextLine().trim();

                switch (opcao) {
                    case "1" -> menuCliente.exibir();
                    case "2" -> menuProduto.exibir();
                    case "3" -> menuPedido.exibir();
                    case "4" -> menuRelatorio.exibir();
                    case "0" -> sair = true;
                    default  -> System.out.println("Opcao invalida.");
                }
            }

            System.out.println("\nSistema encerrado. Até logo!");
        }
    }
}