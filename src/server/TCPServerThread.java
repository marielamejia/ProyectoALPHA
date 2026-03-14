package server;

import juego.*;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import jakarta.jms.Connection;
import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.MessageProducer;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class TCPServerThread extends Thread {
    private Juego juego;
    private int clientId;
    private Jugador jugador;
    private DataInputStream in_socket;
    private DataOutputStream out_socket;
    private Socket clientSocket;


    public TCPServerThread(Socket aClientSocket, int id, Juego juego){
        this.clientSocket = aClientSocket;
        this.clientId = id;
        this.juego = juego;

        try {
            out_socket = new DataOutputStream(clientSocket.getOutputStream());
            in_socket = new DataInputStream(clientSocket.getInputStream());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        String bienvenida = "Hola! Introduce tu nombre";
        String respuestaRegistro = " - Quedaste registrado como el jugador: ";

        try {

            out_socket.writeUTF(bienvenida);
            System.out.println("Se envió la bienvenida... se espera una respuesta");
            String nombre = in_socket.readUTF();
            System.out.println("Se recibió mensaje de " + clientSocket.getRemoteSocketAddress());

            // Checamos si ya existe el jugador
            synchronized (juego) {
                Jugador existente = juego.buscarJugador(nombre);
                if (existente != null) {
                    // Jugador ya existente que se está reconectando
                    this.jugador = existente;
                    System.out.println("Reconexión: " + nombre + " (puntaje actual: " + existente.getPuntacion() + ")");
                } else {
                    // Jugador nuevo
                    this.jugador = new Jugador(clientId, nombre);
                    juego.agregarJugador(this.jugador);
                    System.out.println("Nuevo jugador: " + nombre + "  con id " + clientId + ")");
                }
            }


            out_socket.writeUTF("Confirmacion de registro - " + nombre + respuestaRegistro + jugador.getIdJugador());
            System.out.println("Confirmacion de registro: " + nombre + " quedo registrado como el jugador " + jugador.getIdJugador());

            // se manda el puntaje que el jugador tenía antes de desconectarse (si entra de neuvo a la misma partida)
            out_socket.writeUTF("Puntaje:" + jugador.getPuntacion());

            while(true){

                String mensaje_cliente = in_socket.readUTF();

                if (mensaje_cliente.equals("Golpe")) {
                    synchronized (juego) {
                        jugador.setPuntacion(jugador.getPuntacion() + 1);
                        out_socket.writeUTF("Puntaje:" + jugador.getPuntacion());

                        if (jugador.gane()) {
                            juego.setAlguienGano(true);
                        }
                    }
                }

                if (mensaje_cliente.equals("Iniciar")) {
                    synchronized (juego) {
                        if (!juego.isJugando()) {
                            juego.setJugando(true);
                            System.out.println("Partida iniciada por " + nombre);

                            new Thread(() -> new MonstruoPublisher(juego).publishMensajeMonstruo()).start();
                            new Thread(() -> new GanadorPublisher(juego).publishMensajeGanador()).start();

                        }
                    }
                }



            }

        } catch (EOFException e) {
            System.out.println("Cliente " + clientId + " desconectado");
        } catch (IOException e) {
            System.out.println("IO error cliente " + clientId + ": " + e.getMessage());
        } finally {
            try { clientSocket.close(); } catch (IOException ignored) {}
        }
    }


}
