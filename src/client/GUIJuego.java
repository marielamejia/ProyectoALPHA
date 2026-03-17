package client;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.*;

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
    private JList<String> listaJugadores;
    private JButton buttonIniciarPartida;
    private JButton buttonSalirPartida;
    private JLabel labelEstado;
    private final JFrame frame;
    private final TCPClientThread cliente;
    private JButton[] casillas;

    public GUIJuego(TCPClientThread cliente, JFrame frame){
        this.cliente = cliente;
        this.frame = frame;

        crearComponentes();

        casillas = new JButton[]{
                button1, button2, button3,
                button4, button5, button6,
                button7, button8, button9,
                button10, button11, button12
        };

        inicializarVista();
        inicializarEventos();
        estiloJuego();
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private void crearComponentes() {
        mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Título
        panelTitulo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        labelTitulo = new JLabel("¡Pégale al Monstruo!");
        panelTitulo.add(labelTitulo);

        // Centro
        panelJuego = new JPanel(new BorderLayout(15, 15));

        // Estado
        panelEstado = new JPanel();
        panelEstado.setLayout(new BoxLayout(panelEstado, BoxLayout.Y_AXIS));
        labelJugador = new JLabel();
        labelMonstGolpeados = new JLabel();
        labelEstado = new JLabel();

        labelJugador.setAlignmentX(Component.LEFT_ALIGNMENT);
        labelMonstGolpeados.setAlignmentX(Component.LEFT_ALIGNMENT);
        labelEstado.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelEstado.add(labelJugador);
        panelEstado.add(Box.createVerticalStrut(10));
        panelEstado.add(labelMonstGolpeados);
        panelEstado.add(Box.createVerticalStrut(10));
        panelEstado.add(labelEstado);

        // Tablero
        panelTablero = new JPanel(new GridLayout(4, 3, 12, 12));

        button1 = new JButton();
        button2 = new JButton();
        button3 = new JButton();
        button4 = new JButton();
        button5 = new JButton();
        button6 = new JButton();
        button7 = new JButton();
        button8 = new JButton();
        button9 = new JButton();
        button10 = new JButton();
        button11 = new JButton();
        button12 = new JButton();

        panelTablero.add(button1);
        panelTablero.add(button2);
        panelTablero.add(button3);
        panelTablero.add(button4);
        panelTablero.add(button5);
        panelTablero.add(button6);
        panelTablero.add(button7);
        panelTablero.add(button8);
        panelTablero.add(button9);
        panelTablero.add(button10);
        panelTablero.add(button11);
        panelTablero.add(button12);

        // Botones abajo
        panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonIniciarPartida = new JButton("Iniciar partida");
        buttonSalirPartida = new JButton("Salir del juego");
        panelBotones.add(buttonIniciarPartida);
        panelBotones.add(buttonSalirPartida);

        JPanel centro = new JPanel(new BorderLayout(10, 10));
        centro.add(panelEstado, BorderLayout.NORTH);
        centro.add(panelTablero, BorderLayout.CENTER);
        centro.add(panelBotones, BorderLayout.SOUTH);

        panelJuego.add(centro, BorderLayout.CENTER);

        // Jugadores a la derecha
        /*panelJugadores = new JPanel(new BorderLayout(10, 10));
        JLabel tituloJugadores = new JLabel("Jugadores");
        listaJugadores = new JList<>();
        scrollPaneJugadores = new JScrollPane(listaJugadores);

        panelJugadores.add(tituloJugadores, BorderLayout.NORTH);
        panelJugadores.add(scrollPaneJugadores, BorderLayout.CENTER);
        panelJugadores.setPreferredSize(new Dimension(220, 0));*/

        mainPanel.add(panelTitulo, BorderLayout.NORTH);
        mainPanel.add(panelJuego, BorderLayout.CENTER);
        //mainPanel.add(panelJugadores, BorderLayout.EAST);
    }

    public void inicializarEventos(){
        for (JButton casilla : casillas) {
            casilla.addActionListener(e -> {
                if (casilla.getText().equals("👹")){
                    new Thread(cliente::sendMensajePegoAMonstruo).start();
                }
            });
        }

        buttonIniciarPartida.addActionListener(e ->
                new Thread(cliente::sendMensajeInicioPartida).start()
        );

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
            casilla.setPreferredSize(new java.awt.Dimension(100, 70));
        }

        /*DefaultListModel<String> modelo = new DefaultListModel<>();
        modelo.addElement(cliente.getNomJugador());
        listaJugadores.setModel(modelo);*/

        limpiarTablero();
    }

    public void limpiarTablero() {
        for (JButton casilla : casillas) {
            casilla.setText("");
            casilla.setBackground(new Color(238, 241, 251));
            casilla.setForeground(new Color(44, 45, 91));
            casilla.setEnabled(true);
        }
    }

    public void actualizarEstado(String estado) {
        labelEstado.setText("Estado: " + estado);

        String estadoMin = estado.toLowerCase();
        if (estadoMin.contains("gan")) {
            labelEstado.setForeground(new Color(178, 60, 60));
        } else if (estadoMin.contains("inici")) {
            labelEstado.setForeground(new Color(79, 142, 42));
        } else {
            labelEstado.setForeground(new Color(44, 45, 91));
        }
    }

    public void actualizarGolpeados(int golpeados) {
        labelMonstGolpeados.setText("Monstruos golpeados: " + golpeados);
    }

    public void actualizarListaJugadores(ArrayList<String> jugadores) {
        /*DefaultListModel<String> modelo = new DefaultListModel<>();
        for (String jugador : jugadores) {
            modelo.addElement(jugador);
        }
        listaJugadores.setModel(modelo);*/
    }

    public void mostrarMonstEn(int casilla) {
        limpiarTablero();
        if (casilla >= 0 && casilla < casillas.length) {
            casillas[casilla].setText("👹");
            casillas[casilla].setBackground(new Color(131, 50, 168));
            casillas[casilla].setForeground(Color.WHITE);
        }
    }

    private void estiloJuego() {
        mainPanel.setBackground(new Color(247, 248, 255));

        labelTitulo.setFont(new Font("Trebuchet MS", Font.BOLD, 24));
        labelTitulo.setForeground(Color.WHITE);

        panelTitulo.setBackground(new Color(57, 73, 143));
        panelTitulo.setBorder(new CompoundBorder(
                new LineBorder(new Color(117, 132, 209), 3, true),
                new EmptyBorder(12, 18, 12, 18)
        ));

        panelEstado.setBackground(Color.WHITE);
        panelEstado.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 206, 232), 2, true),
                new EmptyBorder(12, 14, 12, 14)
        ));

        /*panelJugadores.setBackground(Color.WHITE);
        panelJugadores.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 206, 232), 2, true),
                new EmptyBorder(12, 14, 12, 14)
        ));*/

        panelJuego.setBackground(new Color(247, 248, 255));
        panelBotones.setBackground(new Color(247, 248, 255));
        panelTablero.setBackground(new Color(247, 248, 255));

        labelJugador.setFont(new Font("Trebuchet MS", Font.BOLD, 18));
        labelMonstGolpeados.setFont(new Font("Trebuchet MS", Font.BOLD, 18));
        labelEstado.setFont(new Font("Trebuchet MS", Font.BOLD, 18));

        buttonIniciarPartida.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
        buttonIniciarPartida.setBackground(new Color(57, 73, 143));
        buttonIniciarPartida.setForeground(Color.WHITE);
        buttonIniciarPartida.setFocusPainted(false);

        buttonSalirPartida.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
        buttonSalirPartida.setBackground(new Color(230, 233, 248));
        buttonSalirPartida.setForeground(new Color(57, 73, 143));
        buttonSalirPartida.setFocusPainted(false);

        /*listaJugadores.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));
        listaJugadores.setBackground(Color.WHITE);
        listaJugadores.setForeground(new Color(44, 45, 91));
        scrollPaneJugadores.setBorder(new LineBorder(new Color(200, 206, 232), 1, true));*/

        for (JButton casilla : casillas) {
            casilla.setFont(new Font("Segoe UI Emoji", Font.BOLD, 24));
            casilla.setBackground(new Color(238, 241, 251));
            casilla.setBorder(new LineBorder(new Color(154, 164, 201), 2, true));
            casilla.setFocusPainted(false);
            casilla.setText("");
            casilla.setHorizontalAlignment(SwingConstants.CENTER);
        }
    }
}