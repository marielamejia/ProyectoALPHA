package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.*;
import java.net.*;
import java.util.Scanner;

import org.apache.activemq.ActiveMQConnection;

import javax.swing.*;

public class TCPClientThread  {

    private DataInputStream in_socket;
    private DataOutputStream out_socket;
    private Socket socket = null;

    private String nomJugador;
    private int idJugador;
    private int puntaje = 0;
    private GUIJuego gui;
    private boolean acaboJuego = false;


    public TCPClientThread(){}

    public boolean conectar(String nombre){

        try {

            socket = new Socket("localhost", 49152);
            out_socket = new DataOutputStream(socket.getOutputStream());
            in_socket = new DataInputStream(socket.getInputStream());

            // Petición de nombre
            String mensaje = in_socket.readUTF();
            System.out.println("Mensaje recibido: " + mensaje);

            // Manda nombre
            out_socket.writeUTF(nombre);

            // Confirmación de que fue registrado
            String confirmacion = in_socket.readUTF();
            System.out.println("Mensaje recibido - " + mensaje);

            if (confirmacion.startsWith("Confirmacion de registro - ")){
                this.nomJugador = nombre;
                this.idJugador = Integer.parseInt(confirmacion.split(":")[1].strip());

                // leer puntaje, por si ya estaba conectado antes
                String puntajeMsg = in_socket.readUTF();
                this.puntaje = Integer.parseInt(puntajeMsg.split(":")[1]);

                return true;
            }

        } catch (IOException e) {
            System.out.println("Error conectando: " + e.getMessage());
        }

        return false;
    }



    public String getNomJugador() {
        return nomJugador;
    }


    public void sendMensajeInicioPartida(){
        try {
            out_socket.writeUTF("Iniciar");
            if (gui != null){
                SwingUtilities.invokeLater(() -> {
                    gui.actualizarEstado("Inició partida");
                });
            }
        } catch (IOException e) {
            System.out.println("Error enviando Iniciar: " + e.getMessage());
        }

    }

    public void sendMensajePegoAMonstruo() {
        try {
            out_socket.writeUTF("Golpe");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getPuntaje() {
        return puntaje;
    }

    public void acabarJuegoEnGUI(){
        if (gui != null) {
            System.out.println(">>> Actualizando estado en EDT...");
            SwingUtilities.invokeLater(() -> {
                gui.actualizarEstado("Fin del juego");
                gui.limpiarTablero();
            });
        }
    }

    public void cerrarConexion(){
        try {
            acaboJuego = true;
            in_socket.close();
            out_socket.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Error cerrando socket: " + e.getMessage());
        }
    }

    public void listenerGolpes(){
        try {
            while(!acaboJuego){
                String mensaje = in_socket.readUTF();
                System.out.println("Listener golpes: " + mensaje);

                if (mensaje.startsWith("Puntaje:")){
                    this.puntaje = Integer.parseInt(mensaje.substring(8));
                    if (gui != null){
                        SwingUtilities.invokeLater(() -> gui.actualizarGolpeados(this.puntaje));
                    }
                }
            }

        } catch (IOException e) {
            if (!acaboJuego) System.out.println("Listener cerrado: " + e.getMessage());

        }

    }

    public void setGUI(GUIJuego gui){
        this.gui = gui;
    }
    public static void main() {

    }
}
