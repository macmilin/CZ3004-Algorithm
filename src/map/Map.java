package map;

import javax.swing.*;
import java.awt.*;
import robot.VirtualRobot;

public class Map extends JPanel {
    private static final int ROW_SIZE = 20;
    private static final int COL_SIZE = 15;
    private static final int HEIGHT = 700;
    private static final int WIDTH = 530;

    // Initialize Map
    //1 = wall , 0 = path
    private int[][] map;
    private Tile[][] mapTile;


    public Map() {
        this.setSize(WIDTH, HEIGHT);
        mapTile = new Tile[ROW_SIZE][COL_SIZE];

        // Test Map 
        map = new int[][]  {{0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 3, 3, 3},
                            {0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 3, 3, 3},
                            {0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 3, 3, 3},
                            {1, 1, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0},
                            {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
                            {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
                            {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
                            {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
                            {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
                            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                            {1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0},
                            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0},
                            {2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0},
                            {2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                            {2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};

        // Convert Map(int) to Map(Tile)
        for(int i = 0; i < map.length; i++){
            for(int j = 0; j < map[0].length; j++){
                mapTile[i][j] = new Tile(i, j, map[ROW_SIZE-1-i][j]);
                // Set the virtual walls of the arena
                if (i == 0 || j == 0 || i == ROW_SIZE - 1 || j ==COL_SIZE - 1) {
                    mapTile[i][j].setVirtualWall(true);
                }
            }
        }

        // Render Robot
        //virtualRobot.renderRobot();
    }

    @Override
    public void paintComponent(Graphics g) {
        for(int i = 0; i < mapTile.length; i++){
            for(int j = 0; j < mapTile[0].length; j++){
                mapTile[i][j].renderTile(g);
            }
        }
    }


    public Tile[][] getMap() {
        return mapTile;
    }

    public void updateMap(int[][] map) {
        this.map = map;
        for(int i = 0; i < map.length; i++){
            for(int j = 0; j < map[0].length; j++){
                mapTile[i][j] = new Tile(i, j, map[ROW_SIZE-1-i][j]);
                // Set the virtual walls of the arena
                if (i == 0 || j == 0 || i == ROW_SIZE - 1 || j ==COL_SIZE - 1) {
                    mapTile[i][j].setVirtualWall(true);
                }
            }
        }
        paintComponent(this.getGraphics());
    
        // Test Map Descriptor Generator
        System.out.println(generateMapDescriptorPartOne());
        System.out.println(generateMapDescriptorPartTwo());

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

}