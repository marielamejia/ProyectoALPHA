package server;
import juego.*;
import java.net.*;
import java.io.*;

import java.net.ServerSocket;

public class TCPServer {

    public TCPServer(int idJuego){
        int serverPort = 49152;
        int client_count = 0;
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);
            System.out.println("Port 49152 is open");

            // Crear juego
            Juego juego = new Juego(idJuego);

            while (!Thread.currentThread().isInterrupted()){

                // aceptar la comunicación
                Socket clientSocket = serverSocket.accept();
                client_count++;

                TCPServerThread hilo = new TCPServerThread(clientSocket, client_count, juego);
                hilo.start();
            }

        } catch (IOException e) {
            if (Thread.currentThread().isInterrupted()) {
                System.out.println("Servidor detenido");
                return;
            }
            throw new RuntimeException(e);
        }
    }

    public static void main() {
        TCPServer tcpServer = new TCPServer(1);
    }
}
