package ac;

import javax.media.opengl.*;

public class Ghost {

    Maze maze;
    int row, col;
    int dir = 1;
    int moveCounter = 0;
    private static final int MOVE_DELAY = 40;

    private final int startRow, startCol;

    float cr, cg, cb;       // normal color
    float normalCr, normalCg, normalCb;

    public Ghost(Maze m, int r, int c, float rr, float gg, float bb){
        maze = m;
        row = startRow = r;
        col = startCol = c;

        cr = normalCr = rr; 
        cg = normalCg = gg; 
        cb = normalCb = bb;

        pickRandomDirection();
    }

    private void pickRandomDirection(){
        dir = 1 + (int)(Math.random()*4);
    }

    public void update(){
        moveCounter++;
        if(moveCounter % MOVE_DELAY !=0) return;

        int nr=row, nc=col;
        if(dir==1) nr--; if(dir==2) nr++; if(dir==3) nc--; if(dir==4) nc++;

        if(maze.isWall(nr,nc)){
            pickRandomDirection();
            return;
        }

        row=nr; col=nc;

        if(maze.map[row][col]==4){
            for(int[] t: maze.getTeleports()){
                if(t[0]!=row || t[1]!=col){
                    row=t[0]; col=t[1];
                    pickRandomDirection();
                    break;
                }
            }
        }
    }

    public void respawn(){
        row=startRow; col=startCol;
        moveCounter=0;
        pickRandomDirection();
    }

    // Pass power mode as parameter
    public void draw(GL2 gl, boolean pacmanPower) {
        int tileSize = 50;
        int offsetX = maze.getCols() * tileSize / 2;
        int offsetY = maze.getRows() * tileSize / 2;

        gl.glPushMatrix();
        gl.glTranslated(
            col * tileSize - offsetX + tileSize / 2,
            offsetY - row * tileSize - tileSize / 2,
            0
        );

        if(pacmanPower){
            gl.glColor3f(0,0,0.5f); // dark blue during power-up
        } else {
            gl.glColor3f(cr, cg, cb); // normal color
        }

        gl.glRecti(-20, -20, 20, 20);
        gl.glPopMatrix();
    }

}
