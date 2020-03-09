package chatpsp;

import java.util.Observable;

public class MensajesChat extends Observable {

    private String mensaje;

    public MensajesChat() {
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
        //indica que el mensaje ha cambiado
        this.setChanged();
        //notifica a los observadores que el mensaje ha cambiado y se lo pasa
        //internamente notifyObservers llama al método update del observador
        this.notifyObservers(this.getMensaje());
    }
}
