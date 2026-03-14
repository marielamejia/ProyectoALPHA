package estresador;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

public class EstresadorTCPClientThread extends Thread {

    private  int id;
    private boolean simularGolpes;
    private int numGolpes;
    private CountDownLatch latch;
    private long deltaT = -1;

    public EstresadorTCPClientThread(int id, boolean simularGolpes, int numGolpes, CountDownLatch latch){
        this.id = id;
        this.simularGolpes = simularGolpes;
        this.numGolpes = numGolpes;
        this.latch = latch;

    }


    @Override
    public void run() {
        try {
            latch.await();

            long inicio = System.currentTimeMillis();

            Socket socket = new Socket("localhost", 49152);

            DataOutputStream out_socket = new DataOutputStream(socket.getOutputStream());
            DataInputStream in_socket = new DataInputStream(socket.getInputStream());

            in_socket.readUTF(); // Hola
            out_socket.writeUTF("JUgador de estresador: " + id);
            in_socket.readUTF(); // confirmación
            in_socket.readUTF(); // puntuación

            if (simularGolpes) {
                for (int i = 0; i < numGolpes; i++) {
                    out_socket.writeUTF("Golpe");
                    in_socket.readUTF();                // "Puntaje:X"
                }
            }

            deltaT = System.currentTimeMillis() - inicio;
            socket.close();


        } catch (InterruptedException | IOException e) {
            System.out.println("Error cliente " + id + ": " + e.getMessage());
            deltaT = -1;
        }
    }

    public long getDeltaT() {
        return deltaT;
    }

    public boolean fallo(){
        return deltaT == -1;
    }
}
