import java.io.*;
import java.net.*;
import java.util.logging.*;

public class ServerAuth {

    public static void main(String args[]) throws IOException {

        ServerSocket ss; //Se crea el socket del servidor

        try {

            //Se inicializa el servidor
            ss = new ServerSocket(5000);
            System.out.println("Servidor inicializado...");


            while (true) {

                Socket socket;
                socket = ss.accept();
                System.out.println("Nueva conexión entrante: " + socket);
                new ServerHilo(socket).start(); //Se crea un hilo para cada cliente que ingresa

            }

        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
