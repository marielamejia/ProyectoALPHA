package client;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GUIJuego {
    private JPanel mainPanel;
    private JPanel panelTitulo;
    private JPanel panelBotones;
    private JPanel panelJuego;
    private JPanel panelEstado;
    private JPanel panelTablero;
    private JPanel panelJugadores;
    private JScrollPane scrollPaneJugadores;
    private JLabel labelTitulo;
    private JLabel labelJugador;
    private JLabel labelMonstGolpeados;
    private JButton button2;
    private JButton button3;
    private JButton button4;
    private JButton button6;
    private JButton button7;
    private JButton button8;
    private JButton button10;
    private JButton button11;
    private JButton button12;
    private JButton button9;
    private JButton button5;
    private JButton button1;
    private JList listaJugadores;
    private JButton buttonIniciarPartida;
    private JButton buttonSalirPartida;
    private JLabel labelEstado;
    private final JFrame frame;

    // Conectar con el hilo del jugador
    private final TCPClientThread cliente;

    private JButton[] casillas;

    private Color buttonColorMons = new Color(131,50,168);

    public GUIJuego(TCPClientThread cliente, JFrame frame){
        this.cliente = cliente;
        this.frame = frame;
        casillas = new JButton[]{button1, button2, button3, button4, button5, button6, button7, button8, button9, button10, button11, button12};
        inicializarVista();
        inicializarEventos();

    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void inicializarEventos(){
        for (JButton casilla : casillas) {
            casilla.addActionListener(e -> {
                if (casilla.getText().equals("Monstruo")){
                    new Thread(() -> cliente.sendMensajePegoAMonstruo()).start();
                }
            });
        }
        buttonIniciarPartida.addActionListener(e -> new Thread(() -> cliente.sendMensajeInicioPartida()).start());
        buttonSalirPartida.addActionListener(e -> {
            cliente.cerrarConexion();
            SwingUtilities.invokeLater(() -> {
                GUILogin login = new GUILogin(frame);
                frame.setContentPane(login.getPanelLogin());
                frame.setMinimumSize(new java.awt.Dimension(400, 200));
                frame.revalidate();
                frame.repaint();
                frame.pack();
                frame.setLocationRelativeTo(null);
            });
        });
    }

    private void inicializarVista(){
        labelJugador.setText("Jugador: " + cliente.getNomJugador());
        labelEstado.setText("Estado: Sin empezar partida");
        labelMonstGolpeados.setText("Monstruos golpeados: " + cliente.getPuntaje());

        for (JButton casilla : casillas){
            casilla.setPreferredSize(new java.awt.Dimension(100, 60));
        }

        DefaultListModel<String> modelo = new DefaultListModel<>();
        modelo.addElement(cliente.getNomJugador());
        listaJugadores.setModel(modelo);

        limpiarTablero();
    }

    public void limpiarTablero() {
        for (JButton casilla : casillas) {
            casilla.setText("");
            casilla.setEnabled(true);
        }
    }

    public void actualizarEstado(String estado) {
        System.out.println(">>> actualizarEstado llamado con: " + estado);
        labelEstado.setText("Estado: " + estado);
    }

    public void actualizarGolpeados(int golpeados) {
        labelMonstGolpeados.setText("Monstruos golpeados: " + golpeados);
    }

    public void actualizarListaJugadores(ArrayList<String> jugadores) {
        DefaultListModel<String> modelo = new DefaultListModel<>();
        for (String jugador : jugadores) {
            modelo.addElement(jugador);
        }
        listaJugadores.setModel(modelo);
    }

    public void mostrarMonstEn(int casilla) {
        limpiarTablero();
        if (casilla >= 0 && casilla < casillas.length) {
            casillas[casilla].setText("Monstruo");
        }
    }

}
