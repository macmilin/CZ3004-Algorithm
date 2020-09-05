package simulator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;  
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;
import java.nio.file.Paths;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;  
import map.*;
import robot.Robot;

public class Simulator {

    private static final int WIN_WIDTH = 530;
    private static final int WIN_HEIGHT = 870;
    private static final int ROW_SIZE = 20;
    private static final int COL_SIZE = 15;
    private static Map map;  
    //private Map screen;
    //private FlowPane controls;

    private static JFrame win;
    private static JPanel screen;
    private static JPanel controls;

    private static Robot bot;

    public static void main(String[] args) {
        bot = new Robot(1, 1, false);

        displaySimulator();
    }

    private static void displaySimulator() {
        // Initialize main window
        win = new JFrame();
        win.setTitle("MDP Group 21 Simulator");
        win.setSize(new Dimension(WIN_WIDTH, WIN_HEIGHT));
        win.setResizable(false);
        Container content = win.getContentPane();


        // Initialize map screen
        map = new Map(bot);
        screen = new JPanel(new CardLayout());
        content.add(screen, BorderLayout.CENTER);
        screen.add(map, "MAP");

        // Initialize control buttons
        controls = new JPanel(new GridLayout(5,1));
        content.add(controls, BorderLayout.PAGE_END);
        displayControls();

        // Display the application
        win.setVisible(true);
        win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private static void displayControls() {

        // Load Map Button
       JButton loadMapButton = new JButton("Load Map");
       
        loadMapButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                // Load Map
                loadMap();
            }
        });
        

         // Exploration Button
       JButton explorationButton = new JButton("Exploration");
       
        explorationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                // Start exploration
            }
        });

        // Fastest Path Button
       JButton fastestPathButton = new JButton("Fastest Path");
       
        fastestPathButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                // Start fastest path
            }
        });

        // Time-Limited Exploration
       JButton timeLimitedButton = new JButton("Time Limited Exploration");
       
        timeLimitedButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                // Start time limited exploration
            }
        });

        // Coverage-Limited Exploration
       JButton coverageLimitedButton = new JButton("Coverage Limited Exploration");
       
        coverageLimitedButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                // Start coverage limited exploration
            }
        });




        controls.add(loadMapButton);
        controls.add(explorationButton);
        controls.add(fastestPathButton);
        controls.add(timeLimitedButton);
        controls.add(coverageLimitedButton);
    }

    public static void loadMap() {
        int[][] map = new int[ROW_SIZE][COL_SIZE];
        int row = 0;
        int col = 0;

        String currentPath = Paths.get(".").toAbsolutePath().normalize().toString()+"/templates/";
        JFileChooser jfc = new JFileChooser(currentPath);

        int returnValue = jfc.showOpenDialog(null);
		// int returnValue = jfc.showSaveDialog(null);
        File selectedFile = null;
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			selectedFile = jfc.getSelectedFile();
			System.out.println(selectedFile.getAbsolutePath());
		}
        
        try {
            Scanner myReader = new Scanner(selectedFile);
            while (myReader.hasNextLine() && row < ROW_SIZE) {
                String data = myReader.nextLine();
                while(col < COL_SIZE){
                    int item = Character.getNumericValue(data.charAt(col));
                    map[row][col] = item;
                    col++;
                }
                row++;
                col = 0;
            }
            myReader.close();
        } catch (Exception e) {
            System.out.println("An error occurred.");
        }
        setStartEnd(map);
        Simulator.map.updateMap(map);
    }
    
    public static void setStartEnd(int[][] map) {
        map[0][COL_SIZE-1] = 3;
        map[0][COL_SIZE-2] = 3;
        map[0][COL_SIZE-3] = 3;
        map[1][COL_SIZE-1] = 3;
        map[1][COL_SIZE-2] = 3;
        map[1][COL_SIZE-3] = 3;
        map[2][COL_SIZE-1] = 3;
        map[2][COL_SIZE-2] = 3;
        map[2][COL_SIZE-3] = 3;
        map[ROW_SIZE-1][0] = 2;
        map[ROW_SIZE-2][0] = 2;
        map[ROW_SIZE-3][0] = 2;
        map[ROW_SIZE-1][1] = 2;
        map[ROW_SIZE-2][1] = 2;
        map[ROW_SIZE-3][1] = 2;
        map[ROW_SIZE-1][2] = 2;
        map[ROW_SIZE-2][2] = 2;
        map[ROW_SIZE-3][2] = 2;
    }
    
}