package ac;

import javax.media.opengl.*;
import java.util.ArrayList;
import java.util.List;

public class Maze {

    private final int[][] initialMap = {
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,2,2,2,2,2,2,2,2,1,2,2,2,2,2,2,2,2,1},
        {1,3,1,1,2,1,1,1,2,1,2,1,1,1,2,1,1,3,1},
        {1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,7,2,2,1},
        {1,2,1,1,2,1,2,1,1,1,1,1,2,1,2,1,1,2,1},
        {1,2,2,2,2,1,2,2,2,1,2,2,2,1,2,2,2,2,1},
        {1,1,1,1,2,1,1,1,2,1,2,1,1,1,2,1,1,1,1},
        {1,1,1,1,2,1,0,0,0,0,0,0,0,1,2,1,1,1,1},
        {1,1,1,1,2,1,0,1,1,5,1,1,0,1,2,1,1,1,1},
        {4,0,0,0,2,2,0,1,5,5,5,1,0,2,2,0,0,0,4},
        {1,1,1,1,2,1,0,1,1,1,1,1,0,1,2,1,1,1,1},
        {1,1,1,1,2,1,0,0,0,0,0,0,0,1,2,1,1,1,1},
        {1,1,1,1,2,1,0,1,1,1,1,1,0,1,2,1,1,1,1},
        {1,2,2,2,2,2,2,2,2,1,2,2,2,2,2,2,2,2,1},
        {1,2,1,1,2,1,1,1,2,1,2,1,1,1,2,1,1,2,1},
        {1,3,2,1,2,2,2,2,2,2,2,2,2,2,2,1,2,3,1},
        {1,1,2,1,2,1,2,1,1,1,1,1,2,1,2,1,2,1,1},
        {1,2,2,2,2,1,2,2,2,1,2,2,2,1,2,2,2,2,1},
        {1,2,1,1,1,1,1,1,2,1,2,1,1,1,1,1,1,2,1},
        {1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
    };

    public int[][] map;

    public Maze() {
        reset();
    }

    public void reset() {
        map = new int[initialMap.length][initialMap[0].length];
        for(int r=0; r<initialMap.length; r++){
            System.arraycopy(initialMap[r], 0, map[r], 0, initialMap[r].length);
        }
    }

    public int getRows() { return map.length; }
    public int getCols() { return map[0].length; }

    public List<int[]> getTeleports() {
        List<int[]> tele = new ArrayList<>();
        for(int r=0; r<getRows(); r++){
            for(int c=0; c<getCols(); c++){
                if(map[r][c]==4) tele.add(new int[]{r,c});
            }
        }
        return tele;
    }

    public void draw(GL2 gl){
        int tileSize = 50;
        int offsetX = getCols() * tileSize / 2;
        int offsetY = getRows() * tileSize / 2;

        for(int r=0; r<getRows(); r++){
            for(int c=0; c<getCols(); c++){
                gl.glPushMatrix();
                gl.glTranslated(c*tileSize - offsetX + tileSize/2, offsetY - r*tileSize - tileSize/2, 0);
                if(map[r][c]==1) drawWall(gl);
                if(map[r][c]==2) drawDot(gl);
                if(map[r][c]==3) drawPower(gl);
                gl.glPopMatrix();
            }
        }
    }

    private void drawWall(GL2 gl){ gl.glColor3f(0,0,1); gl.glRectf(-25,-25,25,25); }
    private void drawDot(GL2 gl){ gl.glColor3f(1,1,0); gl.glPointSize(8); gl.glBegin(GL2.GL_POINTS); gl.glVertex2f(0,0); gl.glEnd(); }
    private void drawPower(GL2 gl){ gl.glColor3f(1,1,1); gl.glPointSize(14); gl.glBegin(GL2.GL_POINTS); gl.glVertex2f(0,0); gl.glEnd(); }

    public boolean isWall(int r,int c){
        if(r<0 || c<0 || r>=getRows() || c>=getCols()) return true;
        return map[r][c]==1;
    }

    public int eat(int r,int c){
        int val = map[r][c];
        if(val==2 || val==3) map[r][c]=0;
        return val;
    }
    public boolean allDotsEaten() {
        for(int r=0; r<getRows(); r++){
            for(int c=0; c<getCols(); c++){
                if(map[r][c]==2 || map[r][c]==3) return false;
            }
        }
        return true;
    }

}
