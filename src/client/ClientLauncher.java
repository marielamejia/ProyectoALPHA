package client;

import javax.swing.*;

public class ClientLauncher {

    public static void iniciarJuego(TCPClientThread cliente, JFrame frame){
        MonstruoSubscriber ms = new MonstruoSubscriber();
        GanadorSubscriber gs = new GanadorSubscriber(cliente);
        GUIJuego gui = new GUIJuego(cliente, frame);

        cliente.setGUI(gui);
        ms.setGui(gui);
        gs.setGui(gui);


        frame.setContentPane(gui.getMainPanel());
        frame.setMinimumSize(new java.awt.Dimension(800, 600)); // ← tamaño para el juego
        frame.revalidate();
        frame.repaint();
        frame.pack();

        new Thread(() -> ms.escucharMonstruo()).start();
        new Thread(() -> gs.escucharGanador()).start();


    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Pégale al Monstruo");

            GUILogin login = new GUILogin(frame);

            frame.setContentPane(login.getPanelLogin());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setMinimumSize(new java.awt.Dimension(400, 200));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

    }
}
