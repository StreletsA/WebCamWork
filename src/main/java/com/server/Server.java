package com.server;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static int PORT = 8888;
    private static ServerSocket SERVER_SOCKET;
    private static ExecutorService executeIt;

    public static void startServer() throws java.io.IOException{
        SERVER_SOCKET = new ServerSocket(PORT);

        while(!SERVER_SOCKET.isClosed()){
            Socket client = SERVER_SOCKET.accept();
            executeIt.execute(new MultiThread(client));
        }

    }

    public static void main(String[] args) throws IOException {

        executeIt = Executors.newFixedThreadPool(1000);

        if(args.length >= 1) {
            PORT = Integer.parseInt(args[0]);
        }

        new Thread(()->{

            Scanner scan = new Scanner(System.in);

            while(true){

                if(scan.nextLine().equals("quit")){
                    try {
                        executeIt.shutdownNow();
                        SERVER_SOCKET.close();
                        break;
                    } catch (Exception e) {

                    }
                }

            }

        }).start();

        startServer();

    }

}

class MultiThread implements Runnable{

    private Socket sock;
    private DataInputStream in;
    private DataOutputStream out;
    private InetAddress ClientInetAddress;
    private Frame frame;

    public MultiThread(Socket client) throws IOException{

        sock = client;
        in = new DataInputStream(sock.getInputStream());
        out = new DataOutputStream(sock.getOutputStream());
        ClientInetAddress = sock.getInetAddress();

        String client_IP = "";

        try {
            client_IP = in.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }

        frame = new Frame(client_IP);

    }


    public void run() {

        while(!sock.isClosed()){

            try {

                int len = in.readInt();
                byte[] data = new byte[len];

                for(int i = 0; i < len; i++){
                    data[i] = in.readByte();
                }

                ByteArrayInputStream bais = new ByteArrayInputStream(data);
                BufferedImage b = ImageIO.read(bais);

                frame.setImg(b);

            } catch (IOException e) {
                try {
                    in.close();
                    out.close();
                    sock.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        }

        System.out.println(ClientInetAddress + " is disconnected");

        try {
            in.close();
            out.close();
            sock.close();
        }catch (IOException e){

        }

    }
}

class Frame extends JFrame{

    public static JLabel lbl;

    public Frame(String title){

        setLayout(new FlowLayout());
        setSize(500,500);
        setTitle(title);

        lbl = new JLabel();
        lbl.setSize(500,500);

        add(lbl);
        setVisible(true);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    }

    public void setImg(BufferedImage img){
        if(img != null) {
            BufferedImage image = flip(resize(img, 500, 500));
            ImageIcon icon = new ImageIcon(image);
            lbl.setIcon(icon);
        }
    }

    // Изменение размера изображения
    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    // Отражение изображения
    public static BufferedImage flip(BufferedImage img){

        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-img.getWidth(null), 0);

        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        img = op.filter(img, null);

        return img;
    }

}
