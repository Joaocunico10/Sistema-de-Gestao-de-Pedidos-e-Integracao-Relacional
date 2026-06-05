package utilitarios;

import dados.RepositorioPedido;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import modelos.Pedido;
import modelos.StatusPedido;

public class ProcessadorPedidos extends Thread {

        // Quanto tempo a thread espera entre cada ciclo de processamento (5 segundos).
    private static final long INTERVALO_CICLO_MS     = 5_000;
    // Simula o tempo de "processar" um pedido (2 segundos).
    private static final long TEMPO_PROCESSAMENTO_MS = 2_000;

    // "volatile" garante que, quando Main mudar rodando=false,
    // a thread veja a mudança imediatamente (sem cache de CPU).
    private volatile boolean rodando = true;
    private final RepositorioPedido repositorioPedido;

    public ProcessadorPedidos() {
        this.repositorioPedido = new RepositorioPedido();
        // setDaemon(true): thread "daemon" é secundária — quando o programa
        // principal termina, ela é encerrada automaticamente pela JVM.
        setDaemon(true);
        setName("Thread-ProcessadorPedidos"); // nome aparece nos logs e depuração
    }

    /**
     * Chamado pelo Main quando o usuário digita 0 (sair).
     * interrupt() acorda a thread caso esteja dormindo no Thread.sleep().
     */
    public void encerrar() {
        this.rodando = false;
        this.interrupt();
    }

    /**
     * Método principal da thread — executado ao chamar start().
     * Não chame run() diretamente! Use start() para rodar em paralelo.
     */
    @Override
    public void run() {
        System.out.println("[THREAD] Processador de pedidos iniciado.");

        // Fica rodando até encerrar() ser chamado ou a thread ser interrompida.
        while (rodando && !isInterrupted()) {
            processarCiclo(); // busca e processa pedidos em FILA
            if (!dormirEntreCiclos()) break; // retorna false se foi interrompida
        }

        System.out.println("[THREAD] Processador de pedidos encerrado.");
    }

    private void processarCiclo() {
        try (Connection conn = Conexao.obterConexao()) {

            List<Pedido> pedidosNaFila = repositorioPedido.buscarPorStatus(conn, StatusPedido.FILA);

            // Se não houver pedidos na fila, não faz nada neste ciclo.
            if (pedidosNaFila.isEmpty()) {
                return;
            }

            System.out.printf("[THREAD] %d pedido(s) encontrado(s) na fila.%n", pedidosNaFila.size());

            // Processa cada pedido encontrado, um por um.
            for (Pedido pedido : pedidosNaFila) {
                processarPedido(conn, pedido);
            }

        } catch (SQLException e) {
            System.err.println("[THREAD] Erro de conexão no ciclo: " + e.getMessage());
        }
    }

    private void processarPedido(Connection conn, Pedido pedido) {
        try {
            // Atualiza o status de FILA para PROCESSANDO de forma condicional.
            // "WHERE status = FILA" garante que só esta thread pegue o pedido.
            // Se outra thread já pegou antes, reservado = false e pulamos.
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

    /**
     * Pausa a thread entre ciclos de processamento.
     * Extraído do loop principal para evitar o aviso "Thread.sleep called in loop".
     *
     * @return false se a thread foi interrompida durante o sono (sinal de encerramento)
     */
    private boolean dormirEntreCiclos() {
        try {
            // Dorme entre ciclos. Se encerrar() for chamado durante o sono,
            // InterruptedException é lançada e retornamos false para sair do loop.
            Thread.sleep(INTERVALO_CICLO_MS);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // restaura o flag de interrupção
            return false;
        }
    }

}