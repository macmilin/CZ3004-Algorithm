package map;

import javafx.scene.layout.Pane;

public class Tile extends Pane {

    private final int row;
    private final int col;
    // 1 = wall, 0 = path, 2 = start, 3 = goal
    private int state;
    private int explored;
   
    public Tile(int col, int row, int state) {
        this.row = row;
        this.col = col;
        this.state = state;

        // Default explored = 0
        this.explored = 1;

        this.setMinHeight(25);
        this.setMinWidth(25);

        this.setColorByState(state);
    }

    public int getExplored() {
        return this.explored;
    }

    public void explore() {
        this.explored = 1;  
    }

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }

    public void changeState(int state) {
        this.state = state;
    }

    public int getState() {
        return this.state;
    }

    public void setColorByState(int state) {
        if(explored == 1){
            switch(state){
                case 0:
                    this.setStyle("-fx-border-color: black; -fx-background-color: gray;");
                    break;
                case 1:
                    this.setStyle("-fx-border-color: black; -fx-background-color: black;");
                    break;
                case 2:
                    this.setStyle("-fx-border-color: black; -fx-background-color: yellow;");
                    break;
                case 3:
                    this.setStyle("-fx-border-color: black; -fx-background-color: green;");
                    break;
            }
            
        }else{
            this.setStyle("-fx-border-color: black; -fx-background-color: white;");
        }
        
    }

    public void reset(int state) {
        this.state = state;
        // Default explored = 0
        this.explored = 1;
        this.setColorByState(state);
    }


}