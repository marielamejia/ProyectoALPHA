# ProyectoALPHA

Autoras: Mariela Mejía Gutiérrez (201373) y Elena Sofía Luna Palacio (201041)

## Sobre el proyecto 

#### ¿De qué trata?

PegaleAlMonstruo es un juego multijugador inspirado en el juego Whac a Mole, implementado com Sockets TCP y tópicos JMS (Active MQ). Los jugadores compiten para golpear a un mostruo que va cambiando de posición dentro de un tablero. El primero en pegarle a 5 monstruos gana la partida.



# Organización del proyecto

## Estructura del proyecto 
```bash
PegaleAlMonstruo/
├── src/
│   ├── client/
│   │   ├── ClientLauncher.java
│   │   ├── GUILogin.java
│   │   ├── GUIJuego.java
│   │   ├── TCPClientThread.java
│   │   ├── MonstruoSubscriber.java
│   │   └── GanadorSubscriber.java
│   ├── server/
│   │   ├── TCPServer.java
│   │   ├── TCPServerThread.java
│   │   ├── MonstruoPublisher.java
│   │   └── GanadorPublisher.java
│   ├── juego/
│   │   ├── Juego.java
│   │   └── Jugador.java
│   └── estresador/
│       ├── Estresador.java
│       └── EstresadorTCPClientThread.java
```
## Descripción del sistema
Para el desarrollo del juego se utilizaron las siguientes tecnologías:
- Sockets TCP: para la comunicación cliente-servidor para el registro de jugadores y reporte de los goles.
- JMS (Active MQ 6.2.0): utilizando tópicos (publisher-subscriber) se mandaba la posición de los monstruos a todos los jugadores y el anuncio de que alguien ganó.
- Java Swing: como primera interfaz gráfica
- Java Threads: para implementar la concurrencia de los hilos de los clientes y el servidor.

### Componentes principales
* Servidor: Acepta las conexiones TCP de los clientes, maneja el estado del juego y publica a través de JMS los tópics de Monstruos y Ganadores.
* Cliente: Se conecta al servidor TCP, se suscribe a los tópicos de Monstruo y Ganadores y es muestra la GUI al jugador.
* Message Broker: Quien distribue los mensajes de los tópicos a los suscriptores
*   Protocolo TCP:
    -   Los mensajes de login (y de puntaje en caso de haber) se mandan a través de DataStreams
    -   El cliente manda un mensaje en caso de haberle pegado a un monstruo y es resondido con su puntaje actulizado.
*   Protocolo JMS:
    -   Tópico Monstruo:
          -   MonstruoPublisher: manda `<posición>` un número de 0 a 11 para representar las casillas. Si se acabó el juego, manda `"Fin del juego"`
          -   MonstruoSubscriber
    -   Tópico Ganador:
          -   GanadorPublisher: manda `"Ganador: <nombre>"`
          -   GanadorSubscriber

### Concurrencia

#### Hilos del servidor
*    `TCPServerThread`: Se crea uno por cliente. Sirve para poder atender a clientes de forma simultánea sin bloquear al servidor.
*    `MonstruoPublisher`: Se crea uno por partida. Se publican las posiciones de los monstruos cada 100 ms sin bloquear al servidor.
*    `GanadorPublisher`: Se crea uno por partida. Espera a que haa un ganador para poderlo publicar, sin bloquear al servidor.

#### Hilos del cliente
*    `MonstruoSubscriber`: Uno por cliente. Permite utilizar `messageConsumer.receive()`, que es una acción bloqueante, sin bloquear la interfaz (el hilo que maneja la interfaz nunca puede estar bloqueado).
*    `GanadorSubscriber`:  Uno por cliente. Permite utilizar `messageConsumer.receive()` sin bloquear la interfaz.
*    Hilos en `GUILogin`:
        - Se crea un `TCPClientThread` y, con un hilo nuevo, se manda a llamar la función de `conectar()`.
        - Un segundo hilo manda a llamar `listenerGolpes()` para evitar bloquear la interfaz, ya que esta función utiliza `in_socket.readUTF` el cual es bloqueante.
 *    Hilos en `GUIJuego`
        - Un hilo para `sendMensajePegoMonstruo()` y otro para `sendMensajeIniciaPartida()` para no bloquear a la interfaz. 

## Diseño
Se decidió hacer una interfaz que fuera interactiva para el usuario. Para el diseño de esta, se pidió a ChatGPT que generará una imagen sobre la cual nos basamos para elaborar el proyecto. De igual manera, se investigó de qué manera se puede vincular nuestra interfaz con la estructura TCP necesaria para el proyecto. 

A continuación se presenta la evidencia de las consultas que se hicieron:

https://chatgpt.com/share/69afa98a-a954-8001-93ad-90e01622f457 

El diseño en el cual nos basamos fue el siguiente: 
<div style="text-align:center;">
    <img src="diseño\imagenes\InspiracionInterfaz.jpg" alt="Interfaz base" width="200" height="350">
</div>

### HTML
En la clase index.html se crea la pantalla inicial de login donde el jugador ingresa su nombre, y una pantalla principal de juego que se activa después del registro. 

Pantalla login:

<div style="text-align:center;">
    <img src="diseño\imagenes\loginInterfaz.png" alt="Interfaz base" width="250" height="200">
</div>

Dentro del juego, como se muestra en la imagen de abajo, tenemos una barra superior con el título, un panel de información con el nombre del jugador, una barra de progreso, un contador de monstruos golpeados, un temporizador y el estado de la partida. La zona central contiene un tablero en forma de grid de 4x3 donde aparecen los monstruos y botones para iniciar o salir del juego. En la columna lateral se despliega un tablero de jugadores. 

<div style="text-align:center;">
    <img src="diseño\imagenes\vistaJuego.png" alt="Interfaz base" width="280" height="200">
</div>

Además, el código incorpora un fondo de estrellas para hacer la interfaz más llamativa ante el usuario. Se enlaza un archivo CSS para la apariencia visual y un archivo JavaScript para la lógica y funcionalidad del juego.

### CSS
En la clase styles.css se define el estilo visual para el juego. En ella se configura una base para toda la página, en este caso el fondo de estrellas degradado en tonos azules. Se incluyen estilo específicos para la pantalla de login, como los bordes redondos y las sombras para las tarjetas. 

También se establece la apariencia para el main (nuestra pantalla del juego), con un contenedor central, una barra superior con el título y un panel de información que muestra el nombre del jugador en turno. 

Define el diseño del tablero en forma de grid, con casillas que cambian de color según la acción (monstruo activo o golpeado), y botones principales y secundarios con degradados y efectos de sombra. Además, estiliza el tablero de jugadores, mostrando filas con puntuaciones, indicadores visuales y un banner para el ganador. 

Se incorporaron algunos estilos/reglas de responsividad para que el juego se pueda adaptar en pantallas más pequeñas a la de una computadora.

### Javascript
En esta clase se implementa la lógica del juego en JavaScript, coordinando tanto la interacción del usuario como la actualización dinámica de la interfaz. Primero se define el puntaje necesario para ganar, el tamaño del tablero de 4 x 3 y el intervalo de aparición de los monstruos. 

Después, mediante un objeto de estado global, se almacena toda la información relevante de la partida, incluyendo el jugador actual, los puntajes, la posición del monstruo, el tiempo transcurrido y el ganador. A partir de esto, se inicializa la interfaz, se gestiona el ingreso del jugador, se crean las casillas del tablero y se controla la lógica del juego: iniciar partida, mostrar monstruos en posiciones aleatorias, detectar clics correctos, sumar puntos, actualizar la barra de progreso y el scoreboard, y declarar al ganador cuando alcanza el puntaje establecido.


