package estresador;

import server.TCPServer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Estresador {

    static final boolean SIMULAR_GOLPES = true;
    static final int     NUM_GOLPES     = 5;
    static final int   NUM_CLIENTES     = 500;
    static final int     REPETICIONES   = 10;


    public static void main(String[] args) throws InterruptedException {

        Thread servidorThread = new Thread(() -> new TCPServer(1));
        servidorThread.start();
        Thread.sleep(500);


        ArrayList<Double> promedios = new ArrayList<Double>();
        ArrayList<Double> desviaciones = new ArrayList<Double>();
        int fallidos = 0;
        int totalJugadores = 0;
        double sumaProm = 0;

        for (int i = 0; i < REPETICIONES; i++){

            double[] resultado = correrConfiguracion(NUM_CLIENTES);
            promedios.add(resultado[0]);
            desviaciones.add(resultado[1]);
            fallidos += resultado[2];
            totalJugadores += NUM_CLIENTES;
            Thread.sleep(500);

        }

        servidorThread.interrupt();
        Thread.sleep(2000);

        for (double p : promedios) {
            sumaProm += p;
        }
        double promFinal = sumaProm / promedios.size();

        double sumaDesv = 0;
        for (double d : desviaciones) {
            sumaDesv += d;
        }
        double desvFinal = sumaDesv / desviaciones.size();

        double pctExito = 100.0 * (totalJugadores - fallidos) / totalJugadores;


        System.out.printf("%-10s %-10s %-15s %-15s %-10s %-10s%n",
                "Clientes", "TotalCl", "Prom. (ms)", "Desv. Est. (ms)", "Fallidos", "Exitosos%");
        System.out.println("-".repeat(80));
        System.out.printf("%-10d %-10d %-15.2f %-15.2f %-10d %-10.1f%n",
                NUM_CLIENTES, NUM_CLIENTES * REPETICIONES, promFinal, desvFinal, fallidos, pctExito);

        System.exit(0);

    }

    private static double[] correrConfiguracion(int n) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        List<EstresadorTCPClientThread> clientes = new ArrayList<>();


        for (int i = 0; i < n; i++){
            EstresadorTCPClientThread cliente = new EstresadorTCPClientThread(i, SIMULAR_GOLPES, NUM_GOLPES, latch);
            clientes.add(cliente);
            cliente.start();
        }

        latch.countDown();

        for (EstresadorTCPClientThread cliente : clientes) {
            cliente.join();
        }

        List<Long> tiempos = new ArrayList<>();
        int fallidos = 0;
        for (EstresadorTCPClientThread cliente : clientes) {
            if (!cliente.fallo()) tiempos.add(cliente.getDeltaT());
            else fallidos++;
        }

        double suma = 0;
        for (long t : tiempos) {
            suma += t;
        }

        double promedio = suma / tiempos.size();

        double sumaCuadrados = 0;
        for (long t : tiempos) {
            sumaCuadrados += Math.pow(t - promedio, 2);
        }

        double desviacion = Math.sqrt(sumaCuadrados / tiempos.size());

        double[] resultado = {promedio, desviacion, fallidos};
        return resultado;

    }



}
