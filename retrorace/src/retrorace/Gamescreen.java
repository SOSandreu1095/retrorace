/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package retrorace;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author sosan
 */
public class Gamescreen extends Canvas implements Runnable, KeyListener {

    private Partida partida;
    private GUI gui;
    private final Set<Character> pressed = new HashSet<Character>();
    private Dimension offDimension;
    private Image offImage;
    private Graphics offGraphics;

    public Gamescreen(GUI gui, Partida partida) {
        this.addKeyListener(this);
        this.gui = gui;
        this.partida = partida;
        this.partida.getMapa().iniciarMapa();
    }

    public void setPartida(Partida partida) {

        this.partida = partida;
        this.partida.getMapa().iniciarMapa();
    }

    @Override
    public void run() {
//        long lastTime = System.nanoTime();
//        double amountOfTicks = 60.0;
//        double ns = 1000000000 / amountOfTicks;
//        double delta = 0;
//        long timer = System.currentTimeMillis();
//        int updates = 0;
//        int frames = 0;
        while (partida.isActiva()) {
//            long now = System.nanoTime();
//            delta += (now - lastTime) / ns;
//            lastTime = now;
//            while (delta >= 1) {
//                updates++;
//                delta--;
//            }
            preDraw();
//            frames++;
//
//            if (System.currentTimeMillis() - timer > 1000) {
//                timer += 1000;
//                //System.out.println("FPS: " + frames + " TICKS: " + updates);
//                frames = 0;
//                updates = 0;
//            }
        }
    }

    private void preDraw() { //Method which prepares the screen for drawing
//        BufferStrategy bs = this.getBufferStrategy(); //Gets the buffer strategy our canvas is currently using
//        if (bs == null) { //True if our canvas has no buffer strategy (should only happen once when we first start the game)
//            this.createBufferStrategy(2); //Create a buffer strategy using two buffers (double buffer the canvas)
//            return; //Break out of the preDraw method instead of continuing on, this way we have to check again if bs == null instead of just assuming createBufferStrategy(2) worked
//        }
//
//        Graphics g = this.getGraphics(); //Get the graphics from our buffer strategy (which is connected to our canvas)
//        
//        g.setColor(getBackground());
//        g.fillRect(0, 0, WIDTH, HEIGHT); //Fill the screen with the canvas' background color
//        g.setColor(getForeground());
//
//        //repaint();
//        paint(g); //Call our draw method, passing in the graphics object which we just got from our buffer strategy
//
//        g.dispose(); //Dispose of our graphics object because it is no longer needed, and unnecessarily taking up memory
//        bs.show(); //Show the buffer strategy, flip it if necessary (make back buffer the visible buffer and vice versa) 
//        try {
//            Thread.sleep(10); // always a good idea to let is breath a bit
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        Graphics g = this.getGraphics();
        paint(g);
    }

    public void paint(Graphics g) {
        if ((offGraphics == null)
                || (this.getWidth() != offDimension.width)
                || (this.getHeight() != offDimension.height)) {
            offDimension = this.getSize();
            offImage = createImage(this.getWidth(), this.getHeight());
            offGraphics = offImage.getGraphics();
        }
        offGraphics.setColor(getBackground());
        offGraphics.fillRect(0, 0, this.getWidth(), this.getHeight());

//        this.partida.getMapa().mover(g, ((int)this.partida.getPersonaje(0).getX()));
        this.partida.getMapa().paint(offGraphics);
        for (int i = 0; i < this.partida.totalPersonajes(); i++) {
            this.partida.getPersonaje(i).pintar(offGraphics);
        }
        g.drawImage(offImage, 0, 0, this);
    }

    @Override
    public void keyTyped(KeyEvent ke) {

    }

    @Override
    public void keyPressed(KeyEvent ke) {
        int key = ke.getKeyCode();
        System.out.println("keyPressed=" + KeyEvent.getKeyText(ke.getKeyCode()));

        switch (key) {
            case 37:    //Left
                this.partida.getPersonaje(0).setMovingLeft(true);
                break;
            case 38:    //Up
            case 32:
                this.partida.getPersonaje(0).saltar();
                break;
            case 39:    //Right
                this.partida.getPersonaje(0).setMovingRight(true);
                break;
            case 82:    //R restart
                this.partida.getPersonaje(0).reset(this.partida.getLastCheckPoint());
                break;
            default:    //Other keys
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        int key = ke.getKeyCode();
        System.out.println("keyReleased=" + KeyEvent.getKeyText(ke.getKeyCode()));

        switch (key) {
            case 37:    //Left
                this.partida.getPersonaje(0).setMovingLeft(false);
                break;
            case 39:    //Right
                this.partida.getPersonaje(0).setMovingRight(false);
                break;
            default:    //Other keys
        }
    }
}
