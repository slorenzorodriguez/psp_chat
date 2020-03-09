package chatcliente;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 *
 * @author slorenzorodriguez
 */
public class ClienteChat extends JFrame {

    private JTextArea mensajesChat;
    private Socket socket;

    private int puerto;
    private String host;
    private String usuario;

    public ClienteChat() throws IOException {
        super("Cliente Chat");

        // Elementos de la ventana
        mensajesChat = new JTextArea();
        mensajesChat.setEnabled(false); // El area de mensajes del chat no se debe de poder editar
        mensajesChat.setLineWrap(true); // Las lineas se parten al llegar al ancho del textArea
        mensajesChat.setWrapStyleWord(true); // Las lineas se parten entre palabras (por los espacios blancos)
        JScrollPane scrollMensajesChat = new JScrollPane(mensajesChat);
        JTextField tfMensaje = new JTextField("");
        JButton btEnviar = new JButton("Enviar");

        // Colocacion de los componentes en la ventana
        Container c = this.getContentPane();
        c.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(20, 20, 0, 20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        c.add(scrollMensajesChat, gbc);
        // Restaura valores por defecto
        gbc.gridwidth = 1;
        gbc.weighty = 0;

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 20, 20, 20);

        gbc.gridx = 0;
        gbc.gridy = 1;
        c.add(tfMensaje, gbc);
        // Restaura valores por defecto
        gbc.weightx = 0;

        gbc.gridx = 1;
        gbc.gridy = 1;
        c.add(btEnviar, gbc);

        this.setBounds(200, 200, 500, 400);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Ventana de configuracion inicial
        VentanaConfiguracion vc = new VentanaConfiguracion(this);
        host = vc.getHost();
        puerto = vc.getPuerto();
        usuario = vc.getUsuario();

        System.out.println("Conectandose a " + host + " en el puerto " + puerto + ". Bienvenid@ " + usuario + ".");
        // Se crea el socket para conectar con el Servidor del Chat
        try {
            socket = new Socket(host, puerto);
            System.out.println("Conectado a sala de chat");

            DataOutputStream salidaDatos = null;
            salidaDatos = new DataOutputStream(socket.getOutputStream());

            salidaDatos.writeUTF(usuario + " / " + host + " / " + puerto);
        } catch (UnknownHostException ex) {
            System.out.println("Error al conectar con el servidor (" + ex.getMessage() + ").");
        } catch (IOException ex) {
            System.out.println("Error al conectar con el servidor (" + ex.getMessage() + ").");
        }

        // Accion para el boton enviar
        btEnviar.addActionListener(new ConexionServidor(socket, tfMensaje, usuario));

    }

    /**
     * Recibe los mensajes del chat reenviados por el servidor
     */
    public void recibirMensajesServidor() throws IOException {
        // Obtiene el flujo de entrada del socket
        DataInputStream entradaDatos = null;
        String mensaje;
        try {
            entradaDatos = new DataInputStream(socket.getInputStream());
        } catch (IOException ex) {
            System.out.println("Error al crear el stream de entrada: " + ex.getMessage());
        } catch (NullPointerException ex) {
            System.out.println("El socket no se creo correctamente. ");
        }

        // Bucle infinito que recibe mensajes del servidor
        boolean conectado = true;
        while (conectado) {
            try {
                mensaje = entradaDatos.readUTF();
                mensajesChat.append(mensaje + System.lineSeparator());

            } catch (IOException ex) {
                try {
                    //System.out.println("Error al leer del stream de entrada: " + ex.getMessage());
                    System.out.println("El servidor se desconectó");
                    Thread.sleep(1000);
                    System.exit(0);
                    conectado = false;
                } catch (InterruptedException ex2) {
                    Logger.getLogger(ClienteChat.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (NullPointerException ex) {
                System.out.println("El socket no se creo correctamente. ");
                conectado = false;
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        ClienteChat c = new ClienteChat();
        c.recibirMensajesServidor();
    }

}
