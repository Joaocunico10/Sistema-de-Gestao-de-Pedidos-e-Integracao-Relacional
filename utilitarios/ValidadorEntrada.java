package utilitarios;

import java.math.BigDecimal;
import java.util.Scanner;

public final class ValidadorEntrada {

    private static final String REGEX_EMAIL = "^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$";

    public static final BigDecimal PRECO_MAX     = new BigDecimal("999999.99");
    public static final BigDecimal PRECO_MIN     = new BigDecimal("0.01");
    public static final int        ESTOQUE_MAX   = 999_999;
    public static final int        QUANTIDADE_MAX = 999_999;

    private ValidadorEntrada() {}

    public static String lerStringObrigatoria(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String valor = scanner.nextLine().trim();
            if (!valor.isEmpty()) {
                return valor; 
            }
            System.out.println("  ✖ Campo obrigatório. Digite um valor válido.");
        }
    }

    public static String lerEmail(Scanner scanner, String prompt) {
        while (true) {
            String valor = lerStringObrigatoria(scanner, prompt);
            if (valor.matches(REGEX_EMAIL)) {
                return valor;
            }
            System.out.println("  ✖ E-mail inválido. Formato esperado: usuario@dominio.com");
        }
    }


    public static int lerInteiroIntervalo(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String linha = scanner.nextLine().trim();
            try {
                long valor = Long.parseLong(linha);
                if (valor < min || valor > max) {
                    System.out.printf("  ✖ Valor deve estar entre %d e %d.%n", min, max);
                    continue;
                }
                return (int) valor;
            } catch (NumberFormatException e) {
                System.out.println("  ✖ Digite um número inteiro válido.");
            }
        }
    }


    public static int lerInteiroPositivo(Scanner scanner, String prompt) {
        return lerInteiroIntervalo(scanner, prompt, 1, QUANTIDADE_MAX);
    }

    public static int lerEstoque(Scanner scanner, String prompt) {
        return lerInteiroIntervalo(scanner, prompt, 0, ESTOQUE_MAX);
    }

    public static BigDecimal lerPreco(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String linha = scanner.nextLine().trim().replace(",", ".");
            try {
                BigDecimal valor = new BigDecimal(linha);
                if (valor.compareTo(PRECO_MIN) < 0) {
                    System.out.printf("  ✖ Preço mínimo é R$ %.2f.%n", PRECO_MIN);
                    continue;
                }
                if (valor.compareTo(PRECO_MAX) > 0) {
                    System.out.printf("  ✖ Preço máximo é R$ %.2f.%n", PRECO_MAX);
                    continue;
                }
                return valor.setScale(2, java.math.RoundingMode.HALF_UP);
            } catch (NumberFormatException e) {
                System.out.println("  ✖ Digite um valor monetário válido. Ex: 29.90");
            }
        }
    }

    public static boolean emailValido(String email) {
        return email != null && !email.isBlank() && email.trim().matches(REGEX_EMAIL);
    }
}