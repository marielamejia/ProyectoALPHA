package client;

import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.swing.*;


public class GanadorSubscriber {

    private GUIJuego gui;
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    private static String subject_ganador = "ganador";
    private TCPClientThread cliente;

    public GanadorSubscriber(TCPClientThread cliente){
        this.cliente = cliente;
    }

    public void setGui(GUIJuego gui) {
        this.gui = gui;
    }

    public void escucharGanador(){

        boolean acaboJuego = false;

        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false /*Transacter*/, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createTopic(subject_ganador);
            MessageConsumer messageConsumer = session.createConsumer(destination);

            while (true){
                TextMessage textMessage = (TextMessage) messageConsumer.receive();
                if(textMessage != null && textMessage.getText().startsWith("Ganador:")){
                    String ganador = textMessage.getText().substring(8);
                    System.out.println("Ganador: " + ganador);

                    if (gui != null){
                        SwingUtilities.invokeLater(() -> {
                            gui.actualizarEstado("¡Ganó " + ganador + "! Presiona Iniciar para otra partida");
                            gui.limpiarTablero();
                            gui.actualizarGolpeados(0);
                        });
                    }
                }
            }

        } catch (JMSException e) {
            throw new RuntimeException(e);
        }


    }
}
