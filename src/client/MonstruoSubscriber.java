package client;

import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.swing.*;

public class MonstruoSubscriber {
    private GUIJuego gui;
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    private static String subject_monstruo = "monstruo";

    public MonstruoSubscriber(){

    }

    public void setGui(GUIJuego gui) {
        this.gui = gui;
    }

    public void escucharMonstruo(){

        boolean acaboJuego = false;
        Connection connection = null;
        Session session = null;
        MessageConsumer messageConsumer = null;


        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
            connection = connectionFactory.createConnection();
            connection.start();

            session = connection.createSession(false /*Transacter*/, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createTopic(subject_monstruo);
            messageConsumer = session.createConsumer(destination);

            while(true){
                System.out.println("Esperando coordenadas... ");
                TextMessage textMessage = (TextMessage) messageConsumer.receive();
                if(textMessage != null){
                    System.out.println("Coordenada recibida: " + textMessage.getText());
                    System.out.println();

                }
                if (textMessage.getText() != null && textMessage.getText().equals("Fin del juego")) {
                    acaboJuego = true;
                    if (gui != null){
                        SwingUtilities.invokeLater(() -> gui.limpiarTablero());
                    }
                }
                else{
                    if (gui != null){
                        int posMonst = Integer.parseInt(textMessage.getText());
                        SwingUtilities.invokeLater(() -> gui.mostrarMonstEn(posMonst));
                    }
                }
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        } finally {

            try {
                messageConsumer.close();
                session.close();
                connection.close();
            } catch (JMSException e) {
                System.out.println("Error cerrando JMS: " + e.getMessage());
            }

        }

    }
}
