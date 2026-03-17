package client;
//agregamos imports para el diseño grafico
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class GUILogin {
    public static String ultimoNombre = "";

    private JPanel panelLogin;
    private JLabel labelName;
    private JTextField textFieldNombre;
    private JButton buttonEntrar;
    private JFrame frame;

    public GUILogin(JFrame frame){
        this.frame = frame;
        crearComponentes();
        conectarEventos();
        estiloLogin();
    }

    public JPanel getPanelLogin(){
        return panelLogin;
    }

    private void crearComponentes() {
        panelLogin = new JPanel(new GridBagLayout());

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(184, 193, 234), 2, true),
                new EmptyBorder(25, 25, 25, 25)
        ));
        card.setBackground(Color.WHITE);

        labelName = new JLabel("Ingresa tu nombre para entrar al juego");
        labelName.setAlignmentX(Component.CENTER_ALIGNMENT);

        textFieldNombre = new JTextField(18);
        if (!ultimoNombre.isEmpty()) {
            textFieldNombre.setText(ultimoNombre);
        }
        textFieldNombre.setMaximumSize(new Dimension(260, 40));


        buttonEntrar = new JButton("Entrar");
        buttonEntrar.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(labelName);
        card.add(Box.createVerticalStrut(20));
        card.add(textFieldNombre);
        card.add(Box.createVerticalStrut(20));
        card.add(buttonEntrar);

        panelLogin.add(card);
    }

    private void conectarEventos() {
        buttonEntrar.addActionListener(e -> intentarEntrar());
    }

    private void intentarEntrar(){
        String nombre = textFieldNombre.getText().strip();
        ultimoNombre = nombre;
        
        if (nombre.isBlank()) {
            JOptionPane.showMessageDialog(frame, "Escribe tu nombre para entrar");
            return;
        }

        buttonEntrar.setEnabled(false);

        new Thread(() -> {
            TCPClientThread cliente = new TCPClientThread();
            boolean conectado = cliente.conectar(nombre);

            if (conectado){
                new Thread(cliente::listenerGolpes).start();
                SwingUtilities.invokeLater(() -> ClientLauncher.iniciarJuego(cliente, frame));
            } else {
                SwingUtilities.invokeLater(() -> {
                    buttonEntrar.setEnabled(true);
                    JOptionPane.showMessageDialog(frame, "No se pudo conectar al servidor");
                });
            }
        }).start();
    }

    private void estiloLogin() {
        panelLogin.setBackground(new Color(236, 240, 255));

        labelName.setFont(new Font("Trebuchet MS", Font.BOLD, 18));
        labelName.setForeground(new Color(57, 73, 143));

        textFieldNombre.setFont(new Font("Trebuchet MS", Font.PLAIN, 18));
        textFieldNombre.setBorder(new CompoundBorder(
                new LineBorder(new Color(184, 193, 234), 2, true),
                new EmptyBorder(10, 14, 10, 14)
        ));

        buttonEntrar.setFont(new Font("Trebuchet MS", Font.BOLD, 18));
        buttonEntrar.setBackground(new Color(57, 73, 143));
        buttonEntrar.setForeground(Color.WHITE);
        buttonEntrar.setFocusPainted(false);
        buttonEntrar.setBorder(new EmptyBorder(12, 18, 12, 18));
    }
}