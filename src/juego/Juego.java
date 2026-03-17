package juego;

import java.util.ArrayList;

public class Juego {

    private int juegoId;
    private boolean jugando;
    private boolean alguienGano;
    private ArrayList<Jugador> jugadores;
    private boolean monstruosActivos = false;

    public Juego(int juegoId){

        this.juegoId = juegoId;
        this.jugadores = new ArrayList<Jugador>();
        this.jugando = false;
        this.alguienGano = false;

    }

    public Juego(int juegoId, ArrayList<Jugador> jugadores){

        this.juegoId = juegoId;
        this.jugadores = jugadores;
        this.jugando = false;
        this.alguienGano = false;

    }

    public boolean agregarJugador(Jugador j){
        boolean agregar = jugadores.add(j);
        return agregar;
    }

    public void resetearJuego(){
        this.jugando = false;
        this.alguienGano = false;
        for (Jugador j : jugadores) {
            j.setPuntacion(0);
        }
    }

    public Jugador buscarJugador(String nombre) {
        for (Jugador j : jugadores) {
            if (j.getNombre().equals(nombre)) return j;
        }
        return null;
    }

    public String getNombreGanador() {
        for (Jugador j : jugadores) {
            if (j.getPuntacion() >= Jugador.META) return j.getNombre();
        }
        return "Desconocido";
    }

    public void setJugando(boolean jugando) {
        this.jugando = jugando;
    }

    public void setAlguienGano(boolean alguienGano) {
        this.alguienGano = alguienGano;
    }

    public int getJuegoId() {
        return juegoId;
    }

    public boolean isJugando() {
        return jugando;
    }

    public boolean isAlguienGano() {
        return alguienGano;
    }

    public ArrayList<Jugador> getJugadores() {
        return jugadores;
    }

    public boolean isMonstruosActivos() {
        return monstruosActivos;
    }

    public void setMonstruosActivos(boolean monstruosActivos) {
        this.monstruosActivos = monstruosActivos;
    }
}
