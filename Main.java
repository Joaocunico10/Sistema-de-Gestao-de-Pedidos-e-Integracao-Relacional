import telas.MenuPrincipal;
import utilitarios.ProcessadorPedidos;

public class Main {

    public static void main(String[] args) {
        ProcessadorPedidos processador = new ProcessadorPedidos();
        processador.start(); 
        MenuPrincipal menu = new MenuPrincipal();
        menu.iniciar();
        processador.encerrar();
        System.out.println("Threads encerradas. Até logo!");
    }
}
