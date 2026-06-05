package utilitarios;

import dados.RepositorioPedido;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import modelos.Pedido;
import modelos.StatusPedido;

public class ProcessadorPedidos extends Thread {
    private static final long INTERVALO_CICLO_MS     = 5_000;
    private static final long TEMPO_PROCESSAMENTO_MS = 2_000;
    private volatile boolean rodando = true;
    private final RepositorioPedido repositorioPedido;

    public ProcessadorPedidos() {
        this.repositorioPedido = new RepositorioPedido();

        setDaemon(true);
        setName("Thread-ProcessadorPedidos"); 
    }

    public void encerrar() {
        this.rodando = false;
        this.interrupt();
    }


    @Override
    public void run() {
        System.out.println("[THREAD] Processador de pedidos iniciado.");

       
        while (rodando && !isInterrupted()) {
            processarCiclo(); 
            if (!dormirEntreCiclos()) break; 
        }

        System.out.println("[THREAD] Processador de pedidos encerrado.");
    }

    private void processarCiclo() {
        try (Connection conn = Conexao.obterConexao()) {

            List<Pedido> pedidosNaFila = repositorioPedido.buscarPorStatus(conn, StatusPedido.FILA);

            if (pedidosNaFila.isEmpty()) {
                return;
            }

            System.out.printf("[THREAD] %d pedido(s) encontrado(s) na fila.%n", pedidosNaFila.size());

            for (Pedido pedido : pedidosNaFila) {
                processarPedido(conn, pedido);
            }

        } catch (SQLException e) {
            System.err.println("[THREAD] Erro de conexão no ciclo: " + e.getMessage());
        }
    }

    private void processarPedido(Connection conn, Pedido pedido) {
        try {
  
            boolean reservado = repositorioPedido.atualizarStatusCondicional(
                conn, pedido.getId(), StatusPedido.FILA, StatusPedido.PROCESSANDO
            );

            if (!reservado) {
                System.out.printf("[THREAD] Pedido #%d já foi reservado por outra instância.%n",
                    pedido.getId());
                return;
            }

            System.out.printf("[THREAD] Processando pedido #%d do cliente '%s'...%n",
                pedido.getId(), pedido.getCliente().getNome());

            Thread.sleep(TEMPO_PROCESSAMENTO_MS);

            repositorioPedido.atualizarStatusCondicional(
                conn, pedido.getId(), StatusPedido.PROCESSANDO, StatusPedido.FINALIZADO
            );

            System.out.printf("[THREAD] Pedido #%d FINALIZADO com sucesso.%n", pedido.getId());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("[THREAD] Interrompida durante processamento do pedido #" + pedido.getId());
        } catch (SQLException e) {
            System.err.println("[THREAD] Erro SQL ao processar pedido #" + pedido.getId()
                + ": " + e.getMessage());
        }
    }


    private boolean dormirEntreCiclos() {
        try {

            Thread.sleep(INTERVALO_CICLO_MS);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); 
            return false;
        }
    }

}