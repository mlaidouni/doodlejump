package gui;

import javax.swing.*;

import gameobjects.Joueur;
import gameobjects.Personnage;
import gameobjects.Plateforme;
import gameobjects.Terrain;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.image.*;
import javax.imageio.*;
import java.io.*;

public class Vue extends JPanel implements Runnable, KeyListener {

    public static boolean isRunning;
    Thread thread;
    BufferedImage view, terrainView, platformeView, persoView, settingView;

    Terrain terter;
    int lll;
    static boolean isMenu;

    public Vue(Terrain ter) {
        this.terter = ter;
        // lll = (int) (terter.getHeight() * terter.getAdvancement());
        lll = (int) (terter.getHeight() * 0.5);
        setPreferredSize(new Dimension((int) terter.getWidth(), (int) terter.getHeight()));
        retournMenu();
        addKeyListener(this);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (thread == null) {
            thread = new Thread(this);
            isRunning = true;
            thread.start();
        }
    }

    public void start() {
        String chemin = (new File("gui/images/")).getAbsolutePath();

        try {
            try {
                view = new BufferedImage((int) terter.getWidth(), (int) terter.getHeight(), BufferedImage.TYPE_INT_RGB);

                terrainView = ImageIO.read(new File(chemin + "/" + "background.png"));
                platformeView = ImageIO.read(new File(chemin + "/" + "plateformeBase.png"));
                persoView = ImageIO.read(new File(chemin + "/" + "doodleNinja.png"));
                settingView = ImageIO.read(new File(chemin + "/" + "setting.png"));

            } catch (Exception e) {
                terrainView = ImageIO.read(new File("src/gui/images/background.png"));
                platformeView = ImageIO.read(new File("src/gui/images/plateforme.png"));
                persoView = ImageIO.read(new File("src/gui/images/doodleNinja.png"));
                settingView = ImageIO.read(new File("src/gui/images/setting.png"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(getGraphics() == null);
    }

    public boolean endGame() {
        isRunning = false;
        if (terter.getJoueurA().getPerso().getY() + terter.getJoueurA().getPerso().getHeight() > this.getHeight()) {
            return true;
        } else {
            isRunning = true;
            return false;
        }
    }

    public void update() {
        Personnage p = terter.getJoueurA().getPerso();

        // Gère les boutons flèches
        if (p.isRight) {
            p.setX(p.getX() + 5);
        } else if (p.isLeft) {
            p.setX(p.getX() - 5);
        }
        if(terter.getJoueurA()!=null){
            p = terter.getJoueurB().getPerso();
            if (p.isRight) {
                p.setX(p.getX() + 5);
            } else if (p.isLeft) {
                p.setX(p.getX() - 5);
            }
        }

        terter.update();
    }

    public void draw() {
        Personnage pA = terter.getJoueurA().getPerso();
        Personnage pB = terter.getJoueurB()==null? null:terter.getJoueurB().getPerso();

        Graphics2D g2 = (Graphics2D) view.getGraphics();
        g2.drawImage(terrainView, 0, 0, (int) terter.getWidth(), (int) terter.getHeight(), null);

        for (Plateforme pf : terter.getPlateformesListe()) {
            g2.drawImage(
                    platformeView,
                    (int) pf.getX(),
                    (int) pf.getY(),
                    (int) pf.getWidth(),
                    (int) pf.getHeight(),
                    null);
        }
        g2.drawImage(settingView, 0, 0, 50, 50, null);
        g2.drawImage(persoView, (int) pA.getX(), (int) pA.getY(), (int) pA.getWidth(), (int) pA.getHeight(), null);
        if(pB!=null) g2.drawImage(persoView, (int) pB.getX(), (int) pB.getY(), (int) pB.getWidth(), (int) pB.getHeight(), null);

        Graphics g = getGraphics();
        g.drawImage(view, 0, 0, (int) terter.getWidth(), (int) terter.getHeight(), null);
        g.dispose();
    }

    @Override
    public void run() {
        try {
            requestFocusInWindow();
            start();
            while (isRunning) {
                update();
                draw();
                Thread.sleep(10);
            }
            if (isMenu) {
                JPanel j = new MenuPrincipal(this);
                // j.setSize(0, 0);
                this.add(j);
                // j.setLocation(this.getWidth() / 2, this.getHeight() / 2);
                j.setBounds(0, 0, 0, 0);

            }
            if (endGame()) {
                removeAll();
                repaint();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(terter.isHost || !terter.multiplayer){
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                terter.getJoueurA().getPerso().isRight = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                terter.getJoueurA().getPerso().isLeft = true;
            }
        }
        if(!terter.isHost && terter.multiplayer){
            if (e.getKeyCode() == KeyEvent.VK_D) {
                terter.getJoueurB().getPerso().isRight = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_Q) {
                terter.getJoueurA().getPerso().isLeft = true;
            }
        }
    }


    public void retournMenu() {
        if(terter.isHost||!terter.multiplayer){
            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    if (0 <= e.getX() && e.getX() <= 50 && 0 <= e.getY() && e.getY() <= 50) {
                        isRunning = false;
                        isMenu = true;
                    }
                }
            });
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub
        
    }
}