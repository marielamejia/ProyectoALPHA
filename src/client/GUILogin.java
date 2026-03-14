package client;

import javax.swing.*;

public class GUILogin {
    private JPanel panelLogin;
    private JLabel labelName;
    private JTextField textFieldNombre;
    private JButton buttonEntrar;
    private JFrame frame;

    public GUILogin(JFrame frame){
        this.frame = frame;
        conectarEventos();
    }

    public JPanel getPanelLogin(){
        return panelLogin;
    }

    private void conectarEventos() {
        buttonEntrar.addActionListener(e -> intentarEntrar());
    }

    private void intentarEntrar(){
        String nombre = textFieldNombre.getText().strip();

        new Thread(() -> {
            TCPClientThread cliente = new TCPClientThread();
            boolean conectado = cliente.conectar(nombre);

            if (conectado){
                new Thread(() -> cliente.listenerGolpes()).start();
                SwingUtilities.invokeLater(() -> ClientLauncher.iniciarJuego(cliente, frame));
            } else {
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(frame, "No se pudo conectar al servidor")
                );
            }
        }).start();
    }

}
