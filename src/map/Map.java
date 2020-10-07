package map;

import javax.swing.*;
import java.awt.*;
import robot.Robot;
import robot.Constant.MOVEMENT;

public class Map extends JPanel {
    private static final int ROW_SIZE = 20;
    private static final int COL_SIZE = 15;
    private static final int HEIGHT = 700;
    private static final int WIDTH = 530;

    private final Robot bot;

    // Initialize Map
    //1 = obstacle , 0 = path
    private int[][] map;
    private Tile[][] mapTile;


    public Map(Robot bot) {
        this.bot = bot;
        this.setSize(WIDTH, HEIGHT);
        mapTile = new Tile[ROW_SIZE][COL_SIZE];

        // Test Map 
        map = new int[][]  {{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3},
                            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3},
                            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3},
                            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                            {2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                            {2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                            {2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};

        // Convert Map(int) to Map(Tile)
        for(int i = 0; i < map.length; i++){
            for(int j = 0; j < map[0].length; j++){
                mapTile[i][j] = new Tile(i, j, map[ROW_SIZE-1-i][j]);
                // Set the virtual walls of the arena
                if (i == 0 || j == 0 || i == ROW_SIZE - 1 || j ==COL_SIZE - 1) {
                    mapTile[i][j].setVirtual(true);
                }
            }
        }
        //setVirtualObstacles();
    }

    @Override
    public void paintComponent(Graphics g) {
        //System.out.println(g);
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        for(int i = 0; i < mapTile.length; i++){
            for(int j = 0; j < mapTile[0].length; j++){
                //System.out.println("row:" + i + " col:" + j);
                mapTile[i][j].renderTile(g);
            }
        }

        bot.renderRobot(g);
    }


    public Tile[][] getMap() {
        return mapTile;
    }

    public void updateMap(int[][] map) {
        this.map = map;
        bot.setPos(1, 1);
        for(int i = 0; i < map.length; i++){
            for(int j = 0; j < map[0].length; j++){
                mapTile[i][j] = new Tile(i, j, map[ROW_SIZE-1-i][j]);
                // Set the virtual walls of the arena
                if (i == 0 || j == 0 || i == ROW_SIZE - 1 || j == COL_SIZE - 1) {
                    mapTile[i][j].setVirtual(true);
                }
            }
        }
        //setVirtualObstacles();
        paintComponent(this.getGraphics());
        bot.reset();

    }

    public String binToHex(String bin) {
        int dec = Integer.parseInt(bin, 2);
        return Integer.toHexString(dec);
    }

    public String generateMapDescriptorPartOne() {
        StringBuilder ans = new StringBuilder();
        StringBuilder bin = new StringBuilder();
        
        bin.append("11");
        
        for(int i = 0; i < mapTile.length; i++){
            for(int j = 0; j < mapTile[0].length; j++){
                if(mapTile[i][j].getExplored()){
                    bin.append("1");
                }else{
                    bin.append("0");
                }

                if(bin.length() == 4){
                    ans.append(binToHex(bin.toString()));
                    bin.setLength(0);
                }
            }
        }

        bin.append("11");
        ans.append(binToHex(bin.toString()));
        return ans.toString();
    }

    public String generateMapDescriptorPartTwo() {
        StringBuilder ans = new StringBuilder();
        StringBuilder bin = new StringBuilder();
        
        for(int i = 0; i < mapTile.length; i++){
            for(int j = 0; j < mapTile[0].length; j++){
                if(mapTile[i][j].getExplored()){
                    if(mapTile[i][j].getState() == 0 || mapTile[i][j].getState() == 2 || mapTile[i][j].getState() == 3) {
                        bin.append("0");
                    }else if(mapTile[i][j].getState() == 1 ){
                        bin.append("1");
                    }
                }

                if(bin.length() == 4){
                    ans.append(binToHex(bin.toString()));
                    bin.setLength(0);
                }
            }
        }

        if(bin.length() > 0) {
            ans.append(binToHex(bin.toString()));
        }
        return ans.toString();
    }

    public boolean isValid(int row, int col) {
        return row >= 0 && col >= 0 && row < ROW_SIZE && col < COL_SIZE;
    }

    public Tile getTile(int row, int col) {
        return mapTile[row][col];
    }


    public void setObstacle(int row, int col, boolean obstacle) {

        if(obstacle){
            mapTile[row][col].setState(1);
        }else{
            mapTile[row][col].setState(0);
        }

        if (row >= 1) {
            mapTile[row - 1][col].setVirtual(obstacle);     

            if (col < COL_SIZE - 1) {
                mapTile[row - 1][col + 1].setVirtual(obstacle);
            }

            if (col >= 1) {
                mapTile[row - 1][col - 1].setVirtual(obstacle);
            }
        }

        if (row < ROW_SIZE - 1) {
            mapTile[row + 1][col].setVirtual(obstacle);

            if (col < COL_SIZE - 1) {
                mapTile[row + 1][col + 1].setVirtual(obstacle);
            }
            if (col >= 1) {
                mapTile[row + 1][col - 1].setVirtual(obstacle);
            }
        }

        if (col >= 1) {
            mapTile[row][col - 1].setVirtual(obstacle);
        }

        if (col < COL_SIZE - 1) {
            mapTile[row][col + 1].setVirtual(obstacle);
        }
    }

    /*
    public void testMovement() {
        bot.move(MOVEMENT.FORWARD, false);
        paintComponent(this.getGraphics());
        bot.move(MOVEMENT.FORWARD, false);
        paintComponent(this.getGraphics());
        paintComponent(this.getGraphics());
        bot.move(MOVEMENT.RIGHT, false);
        paintComponent(this.getGraphics());
        bot.move(MOVEMENT.FORWARD, false);
        paintComponent(this.getGraphics());
        bot.move(MOVEMENT.FORWARD, false);
        bot.move(MOVEMENT.LEFT, false);
        paintComponent(this.getGraphics());
        bot.move(MOVEMENT.FORWARD, false);
        paintComponent(this.getGraphics());
        bot.move(MOVEMENT.FORWARD, false);
        bot.move(MOVEMENT.LEFT, false);
        paintComponent(this.getGraphics());
        bot.move(MOVEMENT.LEFT, false);
        paintComponent(this.getGraphics());
        bot.move(MOVEMENT.LEFT, false);
        paintComponent(this.getGraphics());
        bot.move(MOVEMENT.LEFT, false);
        paintComponent(this.getGraphics());
    }*/

    public int getRowSize() {
        return ROW_SIZE;
    }

    public int getColSize() {
        return COL_SIZE;
    }

    public void setVirtualObstacles() {
        for(int i = 0; i < mapTile.length; i++){
            for(int j = 0; j < mapTile[0].length; j++){
                if (mapTile[i][j].getState() == 1) {
                    setObstacle(i, j, true);
                }
            }
        }
    }

    public int[] getNextNearestUnexplored() {
        int[] ans;

        for(int i = 0; i < mapTile.length; i++){
            for(int j = 0; j < mapTile[0].length; j++){
                if (!mapTile[i][j].getExplored()) {
                    if (isValid(i-2, j) && mapTile[i-2][j].getExplored() 
                    && mapTile[i-2][j].getState() == 0 && !mapTile[i-2][j].getVirtual()){
                        ans = new int[]{i-2, j};
                        return ans;
                    }
                    
                    else if (isValid(i, j-2) && mapTile[i][j-2].getExplored() 
                    && mapTile[i][j-2].getState() == 0 && !mapTile[i][j-2].getVirtual()){
                        ans = new int[]{i, j-2};
                        return ans;
                    }

                    else if (isValid(i, j+2) && mapTile[i][j+2].getExplored() 
                    && mapTile[i][j+2].getState() == 0 && !mapTile[i][j+2].getVirtual()){
                        ans = new int[]{i, j+2};
                        return ans;
                    }
                    
                    else if (isValid(i+2, j) && mapTile[i+2][j].getExplored() 
                    && mapTile[i+2][j].getState() == 0 && !mapTile[i+2][j].getVirtual()){
                        ans = new int[]{i+2, j};
                        return ans;
                    }
                }
            }
        }

        ans = new int[]{-1, -1};
        return ans;
    }

    public boolean isWallOrObstacle(int row, int col) {
        //System.out.println("CHECKPOINT1");
        //System.out.println(mapTile[row][col]);
        if (!isValid(row, col)) {
        //System.out.println("CHECKPOINT2");
            return true;
        }
        //System.out.println(mapTile[row][col]);
        if (mapTile[row][col].getExplored() && mapTile[row][col].getState() == 1) {
            return true;
        }
        return false;
    }

}