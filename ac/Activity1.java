package ac;

import javax.media.opengl.*;
import javax.media.opengl.awt.*;
import com.jogamp.opengl.util.Animator;
import javax.media.opengl.glu.*;
import java.awt.event.*;
import javax.swing.*;

public class Activity1 extends JFrame implements GLEventListener, KeyListener {

    private static final int TILE_SIZE = 50;

    private GLCanvas canvas;
    private Animator animator;

    private Pacman pacman;
    private Ghost[] ghosts;
    private Maze maze;

    private int mapPixelWidth;
    private int mapPixelHeight;

    private final GLU glu = new GLU();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Activity1());
    }

    public Activity1() {
        super("Simple Pac-Man");

        maze = new Maze();
        pacman = new Pacman(maze);

        ghosts = new Ghost[] {
              new Ghost(maze, 10, 9, 1f, 0f, 0f),       // Red
              new Ghost(maze, 10, 9, 1f, 0.75f, 0.8f), // Pink
              new Ghost(maze, 10, 9, 1f, 0.5f, 0f),    // Orange
              new Ghost(maze, 10, 9, 0f, 1f, 1f)      // Cyan
        };

        canvas = new GLCanvas();
        canvas.addGLEventListener(this);
        canvas.addKeyListener(this);
        canvas.setFocusable(true);
        canvas.requestFocusInWindow();
        getContentPane().add(canvas);

        setSize(1200, 1000);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        animator = new Animator(canvas);
        animator.start();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        mapPixelWidth = maze.getCols() * TILE_SIZE;
        mapPixelHeight = maze.getRows() * TILE_SIZE;

        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0,0,0,1);

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluOrtho2D(-mapPixelWidth/2.0, mapPixelWidth/2.0,
                       -mapPixelHeight/2.0, mapPixelHeight/2.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        gl.glEnable(GL2.GL_POINT_SMOOTH);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x,int y,int width,int height) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glViewport(0,0,width,height);

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluOrtho2D(-mapPixelWidth/2.0, mapPixelWidth/2.0,
                       -mapPixelHeight/2.0, mapPixelHeight/2.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);

        maze.draw(gl);

        pacman.update();
        pacman.draw(gl);

        // Update + draw ghosts
        for (Ghost g : ghosts) {
            g.update();
            g.draw(gl, pacman.powerUpActive); // pass power-up state for color change
        }

        // Collision check
        pacman.checkGhostCollision(ghosts);

        // Draw lives and score
        pacman.drawLives(gl);
        pacman.drawScore(gl);

        // WIN condition
        if(maze.allDotsEaten() && pacman.lives>0){
            String msg = "You Win!\nScore: " + pacman.score;
            int choice = JOptionPane.showConfirmDialog(null, msg + "\nPlay again?", "Victory", JOptionPane.YES_NO_OPTION);
            if(choice==JOptionPane.YES_OPTION){
                pacman.resetGame();
                for(Ghost g: ghosts) g.respawn();
            } else {
                System.exit(0);
            }
        }
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        if(animator!=null && animator.isStarted()) animator.stop();
    }

    @Override public void keyPressed(KeyEvent e) { pacman.keyPressed(e); }
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
