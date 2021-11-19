package com.test;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.github.sarxos.webcam.Webcam;

public class Main {

    public Main() throws IOException {

        /*
        Создаю окно, помещаю лэйбл, в котором буду отображать изображение
        */

        JFrame frame=new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(500,500);
        JLabel lbl=new JLabel();
        lbl.setSize(500,500);
        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /*
        Получаю объект вебкамеры, запускаю
        */

        Webcam webcam = Webcam.getDefault();
        webcam.open();

        /*
        В постоянном цикле получаю изображение с вебкамеры и кладу его в лэйбл
        */

        while(true) {
            BufferedImage image = flip(resize(webcam.getImage(), 500, 500));
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

    public static void main(String[] args) throws IOException {

        Main m = new Main();

    }

}
