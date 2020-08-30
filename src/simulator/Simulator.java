package simulator;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import java.nio.file.Paths;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;  
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import map.*;

public class Simulator extends Application {

    private static final int WIN_WIDTH = 375;
    private static final int WIN_HEIGHT = 574;
    private static final int rowSize = 15;
    private static final int colSize = 20;

    private Map screen;
    private FlowPane controls;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("MDP Group 21 Simulator");
        primaryStage.setResizable(false);

        // Window
        VBox window = new VBox();
        window.setSpacing(8);

        // Screen
        screen = new Map();
        

        // Controls
        controls = new FlowPane();
        controls.setVgap(8);
        controls.setHgap(4);
        Button exploration = new Button("Exploration");
        Button shortestPath = new Button("Shortest Path");
        Button timeLimited = new Button("Time-Limited");
        Button coverageLimited = new Button("Coverage-Limited");

        Button loadMap = new Button("Load Map");
        loadMap.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                loadMap();
            }
        });

        controls.getChildren().addAll(exploration, shortestPath, timeLimited, coverageLimited, loadMap);


        window.getChildren().addAll(screen, controls);
        Scene scene = new Scene(window, WIN_WIDTH, WIN_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public void loadMap() {
        int[][] map = new int[colSize][rowSize];
        int row = 0;
        int col = 0;
        FileChooser fc = new FileChooser();
        String currentPath = Paths.get(".").toAbsolutePath().normalize().toString()+"/templates/";
        fc.setInitialDirectory(new File(currentPath));
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"));
        File selectedFile = fc.showOpenDialog(new Stage());
        
        try {
            Scanner myReader = new Scanner(selectedFile);
            while (myReader.hasNextLine() && col < colSize) {
                String data = myReader.nextLine();
                while(row < rowSize){
                    int item = Character.getNumericValue(data.charAt(row));
                    map[col][row] = item;
                    row++;
                }
                col++;
                row = 0;
            }
            myReader.close();
        } catch (Exception e) {
            System.out.println("An error occurred.");
        }
        setStartEnd(map);
        screen.updateMap(map);
    }

    public void setStartEnd(int[][] map) {
        map[0][rowSize-1] = 3;
        map[0][rowSize-2] = 3;
        map[0][rowSize-3] = 3;
        map[1][rowSize-1] = 3;
        map[1][rowSize-2] = 3;
        map[1][rowSize-3] = 3;
        map[2][rowSize-1] = 3;
        map[2][rowSize-2] = 3;
        map[2][rowSize-3] = 3;
        map[colSize-1][0] = 2;
        map[colSize-2][0] = 2;
        map[colSize-3][0] = 2;
        map[colSize-1][1] = 2;
        map[colSize-2][1] = 2;
        map[colSize-3][1] = 2;
        map[colSize-1][2] = 2;
        map[colSize-2][2] = 2;
        map[colSize-3][2] = 2;
    }
}