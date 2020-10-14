package map;

import java.awt.*;

public class Tile {

    private final int row;
    private final int MAX_ROW = 20;
    private final int col;
    // 1 = obstacle, 0 = path, 2 = start, 3 = goal
    private int state;
    private boolean virtual;
    private boolean explored;
    private int SIZE = 35;
    private int INNER_SIZE;
    private int BORDER = 2;
    private int X;
    private int Y;

   
    public Tile(int row, int col, int state) {
        this.row = row;
        this.col = col;
        this.state = state;

        // Default explored = false
        this.explored = false;
        if (state == 2 /*|| state == 3*/){
            this.explored = true;
        }

        this.virtual = false;

        // Set X,Y location of on screen
        X = col*SIZE + BORDER;
        Y = (MAX_ROW - 1 - row)*SIZE + BORDER;
        INNER_SIZE = SIZE - 2*BORDER;
    }

    public boolean getExplored() {
        return this.explored;
    }

    public void setExplored(boolean bool) {
        this.explored = bool;  
    }

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return this.state;
    }

    public void reset(int state) {
        this.state = state;
        // Default explored = false
        this.explored = false;
        if (state == 2 || state == 3){
            this.explored = true;
        }
        this.virtual = false;
    }

    public void setVirtualWall(boolean bool) {
        this.virtual = bool;
    }

    public void setVirtual(boolean bool) {
        if (row == 0 || row == 19 || col == 0 || col == 14){
            return;
        }
        this.virtual = bool;
    }

    public boolean getVirtual() {
        return this.virtual;
    }

    public void renderTile(Graphics g) {
        Color color = null;

        if(explored){
            switch(state){
                case 0:
                    color = Color.WHITE;
                    if (virtual) {
                        color = Color.CYAN;
                    }
                    break;
                case 1:
                    color = Color.BLACK;
                    break;
                case 2:
                    color = Color.YELLOW;
                    break;
                case 3:
                    color = Color.GREEN;
                    break;
            }
        }else{
            color = Color.GRAY;
        }
        g.setColor(color);
        g.fillRect(X, Y, INNER_SIZE, INNER_SIZE);
    }


}