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
import algorithms.*;
import communication.*;
import robot.Constant;
import java.util.Scanner;

public class Simulator {

    private static final int WIN_WIDTH = 530;
    private static final int WIN_HEIGHT = 870;
    private static final int ROW_SIZE = 20;
    private static final int COL_SIZE = 15;
    private static Map map;
    //private static Map realRunMap;
    private static int coverageLimit;
    private static int timeLimit;
    //private Map screen;
    //private FlowPane controls;

    private static JFrame win;
    private static JPanel screen;
    private static JPanel controls;

    private static Robot bot;
    //private static Robot realRunRobot;

    private static Communication comms = Communication.getComms();

    public static void main(String[] args) {
        bot = new Robot(1, 1, false);
        //realRunRobot = new Robot(1, 1, true);

        displaySimulator();
        //testComms();
        //manualInput();
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
        //realRunMap = new Map(realRunRobot);

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

        // Multi-Threading
        class MTExploration extends SwingWorker<Integer, String> {
            protected Integer doInBackground() throws Exception {
                int row, col;

                row = 1;
                col = 1;

                bot.setPos(row, col);
                map.paintComponent(map.getGraphics());

                Exploration exploration;
                exploration = new Exploration(18000, 300, bot, map, false);

                /*
                if (realRun) {
                    CommMgr.getCommMgr().sendMsg(null, CommMgr.BOT_START);
                }*/
                exploration.run();
                    
                // Test Map Descriptor Generator
                System.out.println(map.generateMapDescriptorPartOne());
                System.out.println(map.generateMapDescriptorPartTwo());

                /*
                if (realRun) {
                    new FastestPath().execute();
                }*/

                return 111;
            }
        }

        class MTCoverageExploration extends SwingWorker<Integer, String> {
            protected Integer doInBackground() throws Exception {
                int row, col;

                row = 1;
                col = 1;

                bot.setPos(row, col);
                map.paintComponent(map.getGraphics());

                Exploration exploration;
                exploration = new Exploration(18000, coverageLimit, bot, map, true);
                exploration.run();
                System.out.println(map.generateMapDescriptorPartOne());
                System.out.println(map.generateMapDescriptorPartTwo());

                return 444;
            }
        }

        class MTTimeExploration extends SwingWorker<Integer, String> {
            protected Integer doInBackground() throws Exception {
                int row, col;

                row = 1;
                col = 1;

                bot.setPos(row, col);
                map.paintComponent(map.getGraphics());

                Exploration exploration;
                exploration = new Exploration(timeLimit, 300, bot, map, true);
                exploration.run();
                System.out.println(map.generateMapDescriptorPartOne());
                System.out.println(map.generateMapDescriptorPartTwo());

                return 444;
            }
        }

        class MTFastestPath extends SwingWorker<Integer, String> {
            protected Integer doInBackground() throws Exception {
                bot.reset();
                map.paintComponent(map.getGraphics());

                /*
                if (realRun) {
                    while (true) {
                        System.out.println("Waiting for FP_START...");
                        String msg = comm.recvMsg();
                        if (msg.equals(CommMgr.FP_START)) break;
                    }
                }*/

                FastestPath fastestPath = new FastestPath(bot, map);
                fastestPath.run(18, 13);

                return 222;
            }
        }

        class MTRealRun extends SwingWorker<Integer, String> {
            protected Integer doInBackground() throws Exception {
                bot.setRealRun(true);
                comms.openSocket();
                Exploration exploration = new Exploration(18000, 300, bot, map, false);
                exploration.setImageRecRun(true);
                exploration.run();

                return 222;
            }
        }


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
                new MTExploration().execute();
            }
        });

        // Fastest Path Button
       JButton fastestPathButton = new JButton("Fastest Path");
       
        fastestPathButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                // Start fastest path
                new MTFastestPath().execute();
            }
        });

        // Time-Limited Exploration
       JButton timeLimitedButton = new JButton("Time Limited Exploration");
       
        timeLimitedButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                // Start time limited exploration
                JDialog inputDialog = new JDialog(win, "Time-Limited Exploration", true);
                inputDialog.setSize(400, 100);
                inputDialog.setLayout(new FlowLayout());
                final JTextField inputField = new JTextField(5);
                JButton timeButton = new JButton("Run");

                timeButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        inputDialog.setVisible(false);
                        String time = inputField.getText();
                        String[] time2 = time.split(":");
                        timeLimit = (Integer.parseInt(time2[0]) * 60) + Integer.parseInt(time2[1]);
                        new MTTimeExploration().execute();
                    }
                });
                
                inputDialog.add(new JLabel("Time Limit (minutes:seconds): "));
                inputDialog.add(inputField);
                inputDialog.add(timeButton);
                inputDialog.setVisible(true);
            }
        });

        // Coverage-Limited Exploration
       JButton coverageLimitedButton = new JButton("Coverage Limited Exploration");
       
        coverageLimitedButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                // Start coverage limited exploration
                JDialog inputDialog = new JDialog(win, "Coverage-Limited Exploration", true);
                inputDialog.setSize(400, 100);
                inputDialog.setLayout(new FlowLayout());
                final JTextField inputField = new JTextField(5);
                JButton coverageButton = new JButton("Run");

                coverageButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        inputDialog.setVisible(false);
                        coverageLimit = (int) ((Integer.parseInt(inputField.getText())) * 300 / 100.0);
                        new MTCoverageExploration().execute();
                    }
                });
                
                inputDialog.add(new JLabel("Coverage Limit (%): "));
                inputDialog.add(inputField);
                inputDialog.add(coverageButton);
                inputDialog.setVisible(true);
            }

        });
        
        // Real Run
        JButton realRunButton = new JButton("Real Run");
        realRunButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                new MTRealRun().execute();
            }
        });

        // Real Run
        JButton setSpeedButton = new JButton("Change Speed");
        setSpeedButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                JDialog inputDialog = new JDialog(win, "Set Speed", true);
                inputDialog.setSize(400, 100);
                inputDialog.setLayout(new FlowLayout());
                final JTextField inputField = new JTextField(5);
                JButton speedButton = new JButton("Set");

                speedButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        inputDialog.setVisible(false);
                        int speed = (int) (Integer.parseInt(inputField.getText()));
                        speed = (int)(1000 / speed);
                        bot.setSpeed(speed);
                    }
                });
                
                inputDialog.add(new JLabel("Set Speed X per Second: "));
                inputDialog.add(inputField);
                inputDialog.add(speedButton);
                inputDialog.setVisible(true);
            }
        });

        controls.add(loadMapButton);
        controls.add(explorationButton);
        controls.add(fastestPathButton);
        controls.add(timeLimitedButton);
        controls.add(coverageLimitedButton);
        controls.add(realRunButton);
        controls.add(setSpeedButton);
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

    public static void testComms() {
        Communication comms = new Communication();
        comms = comms.getComms();
        comms.openSocket();
        while(true){
            comms.sendMessage(Constant.TAKE_PICTURE);
        }

    }

    public static void manualInput() {
        Communication comms = new Communication();
        comms = comms.getComms();
        comms.openSocket();
        while(true){
            Scanner s = new Scanner(System.in);
            String message = s.nextLine();
            //System.out.println(message);
            comms.sendMessage(message);
        }
    }

    
    
}