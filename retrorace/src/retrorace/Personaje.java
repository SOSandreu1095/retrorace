/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package retrorace;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author sosan
 */
public class Personaje implements Runnable {

    private double x, y;
    private double ancho, alto;
    private double velX = 3, velY = 0;
    private double buffMovimiento = 0;
    private boolean falling = true;
    private boolean jumping = false;
    private boolean muerto = false;
    private final double fuerzaSalto = 11.5;
    private boolean movingLeft = false;
    private boolean movingRight = false;
    private boolean estaSobreSuelo = false;
    private boolean estaMoviendo = true;
    private String lastDirection = "Right";
    private Partida partida;
    private int timeRefresh = 30;
    private boolean enMeta;
    private Timer animacion;
    private String color;

    private BufferedImage imgTransicionRight[];
    private BufferedImage imgTransicionLeft[];
    private BufferedImage imgSaltar;
    private BufferedImage imgMuerto;

    public Personaje(Partida partida) {
        this.x = 5;
        this.y = 0;
        this.partida = partida;
        this.enMeta = false;
        this.estaMoviendo = false;
        this.animacion = new Timer(40);
        this.color = "White";

        initImagenes();
    }

    @Override
    public void run() {
        new Thread(this.animacion).start();

        while (partida.isActiva()) {
            try {
                if (!muerto) {
                    y += velY;
                }
                if (falling || jumping) {
                    velY += this.partida.getGravedad();
                    //max vel
                }

                if (enMeta) {
                    saltar(this.fuerzaSalto);
                }

                Thread.sleep(timeRefresh);
            } catch (InterruptedException ex) {
                Logger.getLogger(Personaje.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.animacion.end();
    }

    public double getX() {
        return this.x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public double getY() {
        return this.y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public double getVelX() {
        return velX;
    }

    public void setVelX(float velX) {
        this.velX = velX;
    }

    public double getVelY() {
        return velY;
    }

    public void setVelY(float velY) {
        this.velY = velY;
    }

    public boolean isFalling() {
        return falling;
    }

    public void setFalling(boolean falling) {
        this.falling = falling;
    }

    public boolean isEstaSobreSuelo() {
        return estaSobreSuelo;
    }

    public void setEstaSobreSuelo(boolean estaSobreSuelo) {
        this.estaSobreSuelo = estaSobreSuelo;
        if (estaSobreSuelo) {
            velY = 0;
            falling = false;
            jumping = false;
        } else {
            falling = true;
        }

    }

    public boolean isJumping() {
        return jumping;
    }

    public void setJumping(boolean jumping) {
        this.jumping = jumping;
    }

    public boolean isMovingLeft() {
        return movingLeft;
    }

    public void setMovingLeft(boolean movingLeft) {
        if (!movingLeft) {
            this.animacion.restartContador();
        }
        this.movingLeft = estaMoviendo = movingLeft;
    }

    public boolean isMovingRight() {
        return movingRight;
    }

    public void setMovingRight(boolean movingRight) {
        if (!movingRight) {
            this.animacion.restartContador();
        }
        this.movingRight = estaMoviendo = movingRight;
    }

    public double getAncho() {
        return ancho;
    }

    public double getAlto() {
        return alto;
    }

    public boolean isEnMeta() {
        return enMeta;
    }

    public void setEnMeta(boolean enMeta) {
        this.enMeta = enMeta;
    }

    public double getBuffMovimiento() {
        return buffMovimiento;
    }

    public void setBuffMovimiento(double buffMovimiento) {
        this.buffMovimiento = buffMovimiento;
    }

    public double getFuerzaSalto() {
        return fuerzaSalto;
    }

    public Point getCoordenadas() {
        return new Point((int) x, (int) y);
    }

    private void moverPersonaje() {
        if (!muerto) {
            if (this.movingLeft) {
                lastDirection = "Left";
                moverIzquerda();
            }
            if (this.movingRight) {
                lastDirection = "Right";
                moverDerecha();
            }
        }
        //if (this.jumping) saltar();
    }

    public void pintar(Graphics g) {
        moverPersonaje();
        BufferedImage img = imgTransicionRight[0];

        if (muerto) {
            img = imgMuerto;
        } else if (jumping) {
            img = imgSaltar;
        } else if (estaMoviendo) {
            if (lastDirection.equals("Left")) {
                img = imgTransicionLeft[this.animacion.getContador() % imgTransicionLeft.length];
            } else {
                img = imgTransicionRight[this.animacion.getContador() % imgTransicionRight.length];
            }
        } else { //Parado
            if (lastDirection.equals("Left")) {
                img = imgTransicionLeft[0];
            } else {
                img = imgTransicionRight[0];
            }
        }

        this.alto = img.getHeight();
        this.ancho = img.getWidth();

        g.drawImage(img, (int) x, (int) y, null);
        //g.drawImage(img, 0, (int) y, null);
    }

    public void moverIzquerda() {
        if (!enMeta && partida.puedeMoverIzquierda((int) x, (int) y)) {
            this.x = x - velX + buffMovimiento;
        }
    }

    public void moverDerecha() {
        if (!enMeta && partida.puedeMoverDerecha((int) x, (int) y)) {
            this.x = x + velX + buffMovimiento;
        }
    }

    public void saltar(double fuerza) {
        if (!jumping && !falling) {
            velY = -11.5;
            this.y = y + velY;
            jumping = true;
        }
    }

    public void matar() {
        this.muerto = true;
        this.estaSobreSuelo = true;
        this.velY = 0;
        try {
            Thread.sleep(1200);
            reset(this.partida.getLastCheckPoint());
        } catch (InterruptedException ex) {
            Logger.getLogger(Personaje.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void reset(Point lastCheck) {
        if (!enMeta) {
            this.estaSobreSuelo = false;
            this.estaMoviendo = false;
            this.muerto = false;
            this.velY = 0;
            this.velX = 3;
            if (lastCheck != null) {
                this.x = lastCheck.x;
                this.y = lastCheck.y;
            } else {
                this.x = 0;
                this.y = 0;
            }
        }
    }

    private void initImagenes() {
        try {
            //Imagenes caminar
            this.imgTransicionRight = new BufferedImage[6];
            this.imgTransicionLeft = new BufferedImage[6];

            for (int i = 0; i < this.imgTransicionRight.length; i++) {
                imgTransicionRight[i] = ImageIO.read(new File("img/personaje30/pjRight" + i + color + ".png"));
                imgTransicionLeft[i] = ImageIO.read(new File("img/personaje30/pjLeft" + i + color + ".png"));
            }
            //Salto
            imgSaltar = ImageIO.read(new File("img/personaje30/pjJumping" + color + ".png"));
            //Muerto
            imgMuerto = ImageIO.read(new File("img/personaje30/pjDying" + color + ".png"));

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("REAED ALL SUCCESSFULLY");
    }

}
