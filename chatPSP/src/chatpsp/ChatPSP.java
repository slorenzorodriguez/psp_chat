package chatpsp;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JOptionPane;

public class ChatPSP {

    private static int cont = 0;
    private static String msj;

    public int getCont() {
        return cont;
    }

    public void setCont(int cont) {
        this.cont = cont;
    }

    public static void main(String[] args) {

        int puerto = Integer.parseInt(JOptionPane.showInputDialog("Introducir puerto servidor:", 1234));
        int maximoConexiones = 2; //número máximo de conexiones simultáneas
        ServerSocket servidor = null;
        Socket socket = null;
        MensajesChat mensajes = new MensajesChat();
        DataInputStream entradaDatos = null;

        try {
            //se crea el serverSocket
            servidor = new ServerSocket(puerto, maximoConexiones);

            //bucle infinito para esperar conexiones
            while (true) {
                System.out.println("Servidor a la espera de conexiones");

                if (cont == 0) {
                    System.out.println("Ningún cliente conectado.");
                }

                socket = servidor.accept();
                ConexionCliente cc = new ConexionCliente(socket, mensajes);

                if (cont >= maximoConexiones) {
                    System.out.println("Servidor lleno, rechazando nuevos clientes...");
                    socket.close();
                } else {
                    entradaDatos = new DataInputStream(socket.getInputStream());
                    msj = entradaDatos.readUTF();
                    System.out.println("Nuevo cliente conectado: (" + msj + ")");

                    cont++;
                    System.out.println("Actualmente hay " + cont + " usuario(s) conectado(s).");

                    cc.start();
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {
                socket.close();
                servidor.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
