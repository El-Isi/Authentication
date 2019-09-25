import javax.crypto.Cipher;
import java.io.*;
import java.net.*;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.logging.*;

public class ServerHilo extends Thread {

    private Socket socket;
    private DataOutputStream dos;
    private DataInputStream dis;
    private static Cipher rsa;


    public ServerHilo(Socket socket) {

        this.socket = socket;

        try {

            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());

        } catch (IOException ex) {
            Logger.getLogger(ServerHilo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void desconnectar() {

        try {

            socket.close();

        } catch (IOException ex) {
            Logger.getLogger(ServerHilo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        byte[] nombre_usuario;
        byte[] pasword_usuario;
        int lenName;
        int lenpasw;

        PrivateKey privateKey = null;

        try {
            privateKey = loadPrivateKey("privatekey.dat");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

            rsa = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            rsa.init(Cipher.DECRYPT_MODE, privateKey);

            lenName = Integer.parseInt(dis.readUTF());
            nombre_usuario = new byte[lenName];
            for (int i = 0; i < lenName; i++){
                nombre_usuario[i] = dis.readByte();
            }

            lenpasw = Integer.parseInt(dis.readUTF());
            pasword_usuario = new byte[lenpasw];
            for (int i = 0; i < lenName; i++){
                pasword_usuario[i] = dis.readByte();
            }

            for (byte b : nombre_usuario) {
                System.out.print(Integer.toHexString(0xFF & b));
            }
            System.out.println();

            for (byte b : pasword_usuario) {
                System.out.print(Integer.toHexString(0xFF & b));
            }
            System.out.println();

            byte[] bytesDesencriptadosName = rsa.doFinal(nombre_usuario);
            byte[] bytesDesencriptadosPassword = rsa.doFinal(pasword_usuario);

            String textoDesencripadoName = new String(bytesDesencriptadosName);
            String textoDesencripadoPassword = new String(bytesDesencriptadosPassword);

            if ((textoDesencripadoName.equals("Isidro")) && (textoDesencripadoPassword.equals("Contraseña"))) {

                System.out.println("El cliente con Nombre " + textoDesencripadoName + " a ingresado");
                    dos.writeBoolean(true);

            } else {

                    System.out.println("El cliente con Nombre " + textoDesencripadoName + " ha intentado ingresar sin exito");
                    dos.writeBoolean(false);
            }

        } catch (IOException ex) {
            Logger.getLogger(ServerHilo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        desconnectar();
    }
    private static PrivateKey loadPrivateKey(String fileName) throws Exception {
        FileInputStream fis = new FileInputStream(fileName);
        int numBtyes = fis.available();
        byte[] bytes = new byte[numBtyes];
        fis.read(bytes);
        fis.close();

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        KeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
        PrivateKey keyFromBytes = keyFactory.generatePrivate(keySpec);
        return keyFromBytes;
    }

}