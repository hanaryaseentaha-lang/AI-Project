package ac;

import javax.media.opengl.*;
import javax.swing.JOptionPane;
import java.awt.event.KeyEvent;
import com.jogamp.opengl.util.gl2.GLUT;

public class Pacman {

    Maze maze;
    int row, col;
    int dir = 0;      
    int nextDir = 0;  
    int score = 0;
    int startRow, startCol;
    int lives = 3;     

    boolean powerUpActive = false;
    int powerUpCounter = 0;
    private static final int POWER_UP_TIME = 200; 

    private int moveCounter = 0;
    private static final int MOVE_DELAY = 20;

    private final GLUT glut = new GLUT(); // For text rendering

    public Pacman(Maze m){
        maze = m;

        for(int r=0; r<m.getRows(); r++){
            for(int c=0; c<m.getCols(); c++){
                if(m.map[r][c]==7){
                    row = startRow = r;
                    col = startCol = c;
                }
            }
        }
    }

    public void update(){
        moveCounter++;
        if(moveCounter % MOVE_DELAY !=0) return;

        int nr=row, nc=col;
        if(nextDir==1) nr--; if(nextDir==2) nr++; if(nextDir==3) nc--; if(nextDir==4) nc++;
        if(!maze.isWall(nr,nc)) dir = nextDir;

        nr=row; nc=col;
        if(dir==1) nr--; if(dir==2) nr++; if(dir==3) nc--; if(dir==4) nc++;
        if(!maze.isWall(nr,nc)){
            row=nr; col=nc;
            int eaten = maze.eat(row,col);
            if(eaten==2) score+=10;
            if(eaten==3){ 
                score+=50; 
                powerUpActive=true; 
                powerUpCounter=POWER_UP_TIME; 
            }
        }

        if(maze.map[row][col]==4){
            for(int[] t: maze.getTeleports()){
                if(t[0]!=row || t[1]!=col){ row=t[0]; col=t[1]; break; }
            }
        }

        if(powerUpActive){
            powerUpCounter--;
            if(powerUpCounter<=0) powerUpActive=false;
        }
    }

    public void draw(GL2 gl){
        int tileSize=50;
        int offsetX = maze.getCols() * tileSize / 2;
        int offsetY = maze.getRows() * tileSize / 2;

        gl.glPushMatrix();
        gl.glTranslated(col*tileSize - offsetX + tileSize/2, offsetY - row*tileSize - tileSize/2,0);
        gl.glColor3f(1,1,0);
        gl.glRectf(-20,-20,20,20);
        gl.glPopMatrix();
    }

    public void keyPressed(KeyEvent e){
        if(e.getKeyCode()==KeyEvent.VK_UP) nextDir=1;
        if(e.getKeyCode()==KeyEvent.VK_DOWN) nextDir=2;
        if(e.getKeyCode()==KeyEvent.VK_LEFT) nextDir=3;
        if(e.getKeyCode()==KeyEvent.VK_RIGHT) nextDir=4;
    }

    public void checkGhostCollision(Ghost[] ghosts){
        for(Ghost g: ghosts){
            if(row==g.row && col==g.col){
                if(powerUpActive){
                    score+=200;
                    g.respawn();
                } else {
                    lives--;
                    if(lives<=0) endGame();
                    respawn();
                    for(Ghost gg: ghosts) gg.respawn();
                }
            }
        }
    }

    public void respawn(){
        row=startRow; col=startCol;
        dir=0; nextDir=0;
    }

    private void endGame(){
        int choice = JOptionPane.showConfirmDialog(
                null,
                "You lost all lives!\nRestart game?",
                "Game Over",
                JOptionPane.YES_NO_OPTION
        );
        if(choice == JOptionPane.YES_OPTION){
            resetGame();
        } else {
            System.exit(0);
        }
    }

    public void resetGame(){
        lives = 3;
        score = 0;
        powerUpActive = false;
        powerUpCounter = 0;
        respawn();
        maze.reset(); // restore all dots and powers
    }

    public void drawLives(GL2 gl){
        int size = 10;
        for(int i=0;i<lives;i++){
            gl.glPushMatrix();
            gl.glTranslated(-maze.getCols()*25 + 20 + i*20, maze.getRows()*25 - 20, 0);
            gl.glColor3f(1,0,0);
            gl.glBegin(GL2.GL_TRIANGLES);
            gl.glVertex2f(0,0);
            gl.glVertex2f(size,0);
            gl.glVertex2f(size/2,size);
            gl.glEnd();
            gl.glPopMatrix();
        }
    }

    // NEW: Draw the score
    public void drawScore(GL2 gl){
        int offsetX = -maze.getCols()*25 + 100; // right of lives
        int offsetY = maze.getRows()*25 - 25;

        gl.glColor3f(1,1,1); // white color
        gl.glRasterPos2f(offsetX, offsetY);

        String scoreText = "Score: " + score;
        for(int i=0;i<scoreText.length();i++){
            glut.glutBitmapCharacter(GLUT.BITMAP_HELVETICA_18, scoreText.charAt(i));
        }
    }
}
