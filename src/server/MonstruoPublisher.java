package server;

import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import java.util.Random;
import juego.*;

public class MonstruoPublisher {
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    private static String subject_monstruo = "monstruo";
    private Juego juego;

    public MonstruoPublisher(Juego juego){
        this.juego = juego;
    }

    public void publishMensajeMonstruo(){
        MessageProducer messageProducer = null;
        TextMessage textMessage;
        String randButton;
        Connection connection = null;
        Session session = null;

        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
            connection = connectionFactory.createConnection();
            connection.start();

            session = connection.createSession(false /*Transacter*/, Session.AUTO_ACKNOWLEDGE);

            // Destino para mandar monstruos
            Destination destination = session.createTopic(subject_monstruo);
            messageProducer = session.createProducer(destination);
            textMessage = session.createTextMessage();

            juego.setMonstruosActivos(true);
            // Mandar coordenadas
            while (!juego.isAlguienGano()){
                randButton = Integer.toString(genRandNums());
                textMessage.setText(randButton);
                System.out.println("Sending the following message: " + textMessage.getText());
                messageProducer.send(textMessage);
                for (int i = 0; i < 10; i++) {
                    if (juego.isAlguienGano()) break;
                    Thread.sleep(100); // 10 × 100ms = 1 segundo total
                }
            }

            // Acabar el juego porque alguien gano
            if (juego.isAlguienGano()){
                textMessage.setText("Fin del juego");
                System.out.println("Sending the following message: " + textMessage.getText());
                messageProducer.send(textMessage);
            }

        } catch (JMSException e) {
            throw new RuntimeException(e);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            try {

                juego.setMonstruosActivos(false);
                messageProducer.close();
                session.close();
                connection.close();

            } catch (JMSException e) {
                System.out.println("Error cerrando JMS publisher: " + e.getMessage());
            }
        }

    }

    public int genRandNums(){
        Random random = new Random();
        int randButton = random.nextInt(12);
        return randButton;
    }


}
