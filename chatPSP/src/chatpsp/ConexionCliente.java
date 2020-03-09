package chatpsp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConexionCliente extends Thread implements Observer {

    private Socket socket;
    private MensajesChat mensajes;
    private DataInputStream entradaDatos;
    private DataOutputStream salidaDatos;
    private int cont;
    private String[] msj;


    public ConexionCliente(Socket socket, MensajesChat mensajes) {
        this.socket = socket;
        this.mensajes = mensajes;

        try {
            entradaDatos = new DataInputStream(socket.getInputStream());
            salidaDatos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void run() {
        String mensajeRecibido = null;
        boolean conectado = true;
        // se apunta a la lista de observadores de mensajes
        mensajes.addObserver(this);

        while (conectado) {
            try {
                // lee el mensaje del cliente
                mensajeRecibido = entradaDatos.readUTF();
                // notifica el nuevo mensaje
                mensajes.setMensaje(mensajeRecibido);

                
            } catch (IOException ex) {
                try {
                    System.out.println("Cliente con la IP " + socket.getInetAddress() + " desconectado");

                    ChatPSP cp = new ChatPSP();
                    msj = mensajeRecibido.split(":");
                    mensajes.setMensaje(msj[0] + " abandonó el chat.");

                    cont = cp.getCont() - 1;
                    cp.setCont(cont);

                    if (cont == 0) {
                        System.out.println("Ningún cliente conectado");
                    } else {
                        System.out.println("Actualmente hay " + cont + " usuario(s) conectado(s)");
                    }

                    conectado = false;
                    // si se ha producido un error al recibir datos del cliente se cierra la conexión con el
                    entradaDatos.close();
                    salidaDatos.close();
                } catch (Exception ex1) {
                    Logger.getLogger(ConexionCliente.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        try {
            //envia el mensaje al cliente
            salidaDatos.writeUTF(arg.toString());

        } catch (IOException ex) {
            //System.out.println("Error al enviar mensaje al cliente (" + ex.getMessage() + ").");
        }
    }
}
