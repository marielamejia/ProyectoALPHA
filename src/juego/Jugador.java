package juego;

public class Jugador {

    private int idJugador;
    private String nombre;
    private int puntacion;
    public static final int META = 5;

    public Jugador(int idJugador, String nombre){

        this.idJugador = idJugador;
        this.nombre = nombre;
        this.puntacion = 0;
    }

    public int getIdJugador() {
        return idJugador;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPuntacion() {
        return puntacion;
    }

    public void setPuntacion(int puntacion) {
        this.puntacion = puntacion;
    }

    public boolean gane(){
        if (this.puntacion >= META)
            return true;
        else
            return false;

    }

}
