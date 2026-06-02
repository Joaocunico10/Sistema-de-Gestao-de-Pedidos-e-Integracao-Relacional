package telas;

import dados.ServicoCliente;
import dados.ServicoPedido;
import dados.ServicoProduto;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import modelos.Cliente;
import modelos.ItemPedido;
import modelos.Pedido;
import modelos.Produto;
import utilitarios.EstoqueInsuficienteException;
import utilitarios.ValidacaoException;
import utilitarios.ValidadorEntrada;

public class MenuPedido {

    private final ServicoPedido  servicoPedido;
    private final ServicoCliente servicoCliente;
    private final ServicoProduto servicoProduto;
    private final Scanner        scanner;

    public MenuPedido(Scanner scanner) {
        this.servicoPedido  = new ServicoPedido();
        this.servicoCliente = new ServicoCliente();
        this.servicoProduto = new ServicoProduto();
        this.scanner        = scanner;
    }

    public void exibir() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n--- MENU PEDIDOS ---");
            System.out.println("1. Criar pedido");
            System.out.println("2. Listar pedidos");
            System.out.println("3. Detalhar pedido");
            System.out.println("0. Voltar");
            System.out.print("Opção: ");

            String opcao = scanner.nextLine().trim();

            switch (opcao) {
                case "1" -> criarPedido();
                case "2" -> listar();
                case "3" -> detalhar();
                case "0" -> voltar = true;
                default  -> System.out.println("Opcao invalida.");
            }
        }
    }

    private void criarPedido() {
        System.out.println("\nNovo Pedido:");

        int clienteId = ValidadorEntrada.lerInteiroPositivo(scanner, "ID do cliente: ");
        Cliente cliente;
        try {
            cliente = servicoCliente.buscarPorId(clienteId);
        } catch (ValidacaoException e) {
            System.out.println("Erro: " + e.getMessage());
            return;
        }
        System.out.println("  Cliente: " + cliente.getNome());

        List<ItemPedido> itens = new ArrayList<>();
        boolean continuarAdicionando = true;

        while (continuarAdicionando) {
            System.out.println("\n  Adicionar item:");

            // Exibe todos os produtos para o usuário escolher.
            servicoProduto.listarTodos().forEach(p ->
                System.out.printf("    [%d] %-20s  R$%.2f  Estoque: %d%n",
                    p.getId(), p.getNome(), p.getPreco(), p.getEstoque())
            );

            int produtoId = ValidadorEntrada.lerInteiroPositivo(scanner,
                "  ID do produto (0 para finalizar): ");
            if (produtoId == 0) {
                break;
            }

            Produto produto;
            try {
                produto = servicoProduto.buscarPorId(produtoId);
            } catch (ValidacaoException e) {
                System.out.println("Erro: " + e.getMessage());
                continue;
            }

            int quantidade = ValidadorEntrada.lerInteiroPositivo(scanner,
                "  Quantidade (estoque disponível: " + produto.getEstoque() + "): ");

            // Cria o item com snapshot do preço atual e adiciona no carrinho.
            itens.add(new ItemPedido(produto, quantidade));
            System.out.printf("Item adicionado: %s x %d = R$%.2f%n",
                produto.getNome(), quantidade,
                produto.getPreco().multiply(java.math.BigDecimal.valueOf(quantidade)));

            System.out.print("  Adicionar mais itens? (s/n): ");
            continuarAdicionando = scanner.nextLine().trim().equalsIgnoreCase("s");
        }

        if (itens.isEmpty()) {
            System.out.println("Pedido cancelado: nenhum item adicionado.");
            return;
        }

        System.out.printf("%n  Total: R$%.2f%n",
            itens.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
        System.out.print("  Confirmar pedido? (s/n): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("s")) {
            System.out.println("  Pedido cancelado pelo usuário.");
            return;
        }

        try {
            Pedido pedido = servicoPedido.criarPedido(cliente, itens);
            System.out.printf("Pedido #%d criado com sucesso! Status: %s%n",
                pedido.getId(), pedido.getStatus());
        } catch (EstoqueInsuficienteException | ValidacaoException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void listar() {
        List<Pedido> lista = servicoPedido.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("  Nenhum pedido cadastrado.");
            return;
        }
        System.out.println("\nPedidos:");
        System.out.printf("  %-4s %-20s %-14s %-20s %10s%n",
            "ID", "Cliente", "Status", "Data", "Total");
        System.out.println("  " + "-".repeat(74));
        for (Pedido p : lista) {
            System.out.printf("  %-4d %-20s %-14s %-20s R$%8.2f%n",
                p.getId(),
                p.getCliente().getNome(),
                p.getStatus(),
                p.getDataCriacao().toString().substring(0, 19),
                p.getTotal());
        }
    }

    private void detalhar() {
        int id = ValidadorEntrada.lerInteiroPositivo(scanner, "ID do pedido: ");
        try {
            Pedido p = servicoPedido.buscarPorId(id);
            System.out.println("\n  " + p);
            System.out.println("  Itens:");
            p.getItens().forEach(i -> System.out.println("    " + i));
        } catch (ValidacaoException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}