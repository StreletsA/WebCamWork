package com.client;

import com.github.sarxos.webcam.Webcam;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private static int PORT = 8888;
    private static String ADDRESS = "localhost";
    private static Webcam webcam;

    public Client() throws IOException {

        webcam = Webcam.getDefault();
        webcam.open();

    }

    public static void main(String[] args) throws IOException {

        new Client();

        if(args.length >= 2) {
            ADDRESS = args[0];
            PORT = Integer.parseInt(args[1]);
        }

        Socket client_socket = new Socket(ADDRESS, PORT);

        DataOutputStream out = new DataOutputStream(client_socket.getOutputStream());

        new Thread(()->{

            Scanner scan = new Scanner(System.in);

            while(true){

                if(scan.nextLine().equals("quit")){
                    try {
                        client_socket.close();
                        out.close();
                        webcam.close();
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

        }).start();

        try {

            out.writeUTF(client_socket.getInetAddress().toString());

            while(!client_socket.isClosed()) {

                BufferedImage bi = webcam.getImage();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bi, "jpg", baos);
                baos.flush();

                byte[] data = baos.toByteArray();
                baos.flush();

                out.writeInt(data.length);
                for(int i = 0; i < data.length; i++){
                    out.writeByte(data[i]);
                }
            }

            client_socket.close();

        } catch (Exception e) {

        }

    }

}
