package server;

import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import juego.*;

public class GanadorPublisher {

    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    private static String subject_ganador= "ganador";
    private Juego juego;

    public GanadorPublisher(Juego juego){
        this.juego = juego;
    }

    public void publishMensajeGanador(){

        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false /*Transacter*/, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createTopic(subject_ganador);
            MessageProducer messageProducer = session.createProducer(destination);
            TextMessage textMessage = session.createTextMessage();

            System.out.println("GanadorPublisher iniciado, esperando que alguien gane...");
            while(!juego.isAlguienGano()){
                Thread.sleep(200);
            }

            String nombreGanador = juego.getNombreGanador();
            String mensaje = "Ganador:" + nombreGanador;
            textMessage.setText(mensaje);
            System.out.println("Publicando: " + textMessage.getText());
            messageProducer.send(textMessage);

            messageProducer.close();
            session.close();
            connection.close();

            while (juego.isMonstruosActivos()) {
                Thread.sleep(100);
            }

            juego.resetearJuego();
            System.out.println("Acabó el juego... Esperando nueva partida");


        } catch (JMSException | InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

}
