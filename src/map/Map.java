package map;

import javafx.scene.layout.GridPane;

public class Map extends GridPane {
    private static final int rowSize = 15;
    private static final int colSize = 20;
    private static final int HEIGHT = 500;
    private static final int WIDTH = 375;
    // Initialize Map
    //1 = wall , 0 = path
    private int[][] map;
    private Tile[][] mapTile;


    public Map() {
        this.setMinHeight(HEIGHT);
        this.setMinWidth(WIDTH);
        mapTile = new Tile[colSize][rowSize];

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
                mapTile[i][j] = new Tile(i, j, map[i][j]);
            }
        }

        // Render Tiles
        for(int i = 0; i < mapTile.length; i++){
            for(int j = 0; j < mapTile[0].length; j++){
                this.add(mapTile[i][j], j, i);
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
                mapTile[i][j].reset(map[i][j]);
            }
        }

    }

}