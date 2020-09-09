package algorithms;

import map.*;
import robot.Robot;
import robot.RobotConstants;
import robot.RobotConstants.DIRECTION;
import robot.RobotConstants.MOVEMENT;
import utils.*;

public class Exploration {
    private int timeLimit;
    private int coverageLimit;
    private Robot robot;
    private long startTime;
    private long endTime;
    private Map map;
    private int areaExplored;

    public Exploration(int timeLimit, int coverageLimit, Robot robot, Map map) {
        this.timeLimit = timeLimit;
        this.coverageLimit = coverageLimit;
        this.robot = robot;
        this.map = map;
    }

    public void run() {
        // Calibrate Robot During the Real Exploration
        if (robot.getRealBot()) {
            System.out.println("Calibrating...");

            CommMgr.getCommMgr().recvMsg();
            
            robot.move(MOVEMENT.LEFT, false);
            CommMgr.getCommMgr().recvMsg();
            robot.move(MOVEMENT.CALIBRATE, false);
            CommMgr.getCommMgr().recvMsg();
            robot.move(MOVEMENT.LEFT, false);
            CommMgr.getCommMgr().recvMsg();
            robot.move(MOVEMENT.CALIBRATE, false);
            CommMgr.getCommMgr().recvMsg();
            robot.move(MOVEMENT.RIGHT, false);
            CommMgr.getCommMgr().recvMsg();
            robot.move(MOVEMENT.CALIBRATE, false);
            CommMgr.getCommMgr().recvMsg();
            robot.move(MOVEMENT.RIGHT, false);
            

            while (true) {
                System.out.println("Waiting for Start Command");
                String msg = CommMgr.getCommMgr().recvMsg();
                String[] msgArr = msg.split(";");
                if (msgArr[0].equals(CommMgr.EX_START)){
                    break;
                }
            }
        }

        System.out.println("Starting exploration...");
        startTime = System.currentTimeMillis();
        endTime = startTime + (timeLimit * 1000);

        // During real run, send comms indicating start
        if (robot.getRealBot()) {
            CommMgr.getCommMgr().sendMsg(null, CommMgr.BOT_START);
        }

        sense();

        areaExplored = getAreaExplored();
        System.out.println("Explored Area: " + areaExplored);

        explore(robot.getRow(), robot.getCol());
    }

    public void sense() {
        robot.setSensors();
        robot.sense(map);
        map.paintComponent(map.getGraphics());
    }

    /**
     * Continuously explore the map using the right wall following algorithm until
     * 1. Robot is back at start zone with everything explored.
     * 2. Area Explored is greater than the coverage limit.
     * 3. Time taken is greater than time limit.
     */
    public void explore(int startR, int startC) {
        do {
            nextStep();
            areaExplored = getAreaExplored();
            System.out.println("Area explored: " + areaExplored);

            if (robot.getRow() == startR && robot.getCol() == startC){
                break;
            }
        } while (areaExplored <= coverageLimit && System.currentTimeMillis() <= endTime);

        returnHome();
    }

    /** 
     * Right Wall Following Algorithm
     */

     public void nextStep() {
        if (checkRight()) {
            move(MOVEMENT.RIGHT);
            System.out.println("Right empty. Rotate right");
            if (checkFront()) {
                move(MOVEMENT.FORWARD);
                System.out.println("Forward empty after right rotation. Move forward");
            }
        } else if (checkFront()) {
            move(MOVEMENT.FORWARD);
            System.out.println("Forward empty. Move forward");
        } else if (checkLeft()) {
            move(MOVEMENT.LEFT);
            System.out.println("Left empty. Rotate left");
            if (checkFront()) {
                move(MOVEMENT.FORWARD);
                System.out.println("Forward empty after left rotation. Move forward");
            }
        } else {
            move(MOVEMENT.RIGHT);
            move(MOVEMENT.RIGHT);
            System.out.println("Deadend. U-turn");
        }
     }

    
    private boolean checkRight() {
        switch (robot.getDir()) {
            case NORTH:
                return eastFree();
            case EAST:
                return southFree();
            case SOUTH:
                return westFree();
            case WEST:
                return northFree();
        }
        return false;
    }

    private boolean checkFront() {
        switch (robot.getDir()) {
            case NORTH:
                return northFree();
            case EAST:
                return eastFree();
            case SOUTH:
                return southFree();
            case WEST:
                return westFree();
        }
        return false;
    }

    private boolean checkLeft() {
        switch (robot.getDir()) {
            case NORTH:
                return westFree();
            case EAST:
                return northFree();
            case SOUTH:
                return eastFree();
            case WEST:
                return southFree();
        }
        return false;
    }

    private boolean northFree() {
        int row = robot.getRow();
        int col = robot.getCol();
        return (isExploredAndNotObstacle(row + 1, col - 1) && isExploredAndFree(row + 1, col) && isExploredAndNotObstacle(row + 1, col + 1));
    }

    private boolean eastFree() {
        int row = robot.getRow();
        int col = robot.getCol();
        return (isExploredAndNotObstacle(row - 1, col + 1) && isExploredAndFree(row, col + 1) && isExploredAndNotObstacle(row + 1, col + 1));
    }

    private boolean southFree() {
        int row = robot.getRow();
        int col = robot.getCol();
        return (isExploredAndNotObstacle(row - 1, col - 1) && isExploredAndFree(row - 1, col) && isExploredAndNotObstacle(row - 1, col + 1));
    }

    private boolean westFree() {
        int row = robot.getRow();
        int col = robot.getCol();
        return (isExploredAndNotObstacle(row - 1, col - 1) && isExploredAndFree(row, col - 1) && isExploredAndNotObstacle(row + 1, col - 1));
    }

    

    private boolean isExploredAndNotObstacle(int r, int c) {
        if (map.isValid(r, c)) {
            Tile t = map.getTile(r, c);
            return (t.getExplored() && (t.getState() == 0 || t.getState() == 2 || t.getState() == 3));
        }
        return false;
    }

    private boolean isExploredAndFree(int r, int c) {
        if (map.isValid(r, c)) {
            Tile t = map.getTile(r, c);
            return (t.getExplored() && (t.getState() == 0 || t.getState() == 2 || t.getState() == 3) && !t.getVirtual());
        }
        return false;
    }

    private void move(MOVEMENT m) {
        robot.move(m);
        map.paintComponent(map.getGraphics());
        if (m != MOVEMENT.CALIBRATE) {
            sense();
        } else {
            CommMgr commMgr = CommMgr.getCommMgr();
            commMgr.recvMsg();
        }

        /*
        if (robot.getRealBot() && !calibrationMode) {
            calibrationMode = true;

            if (canCalibrateOnTheSpot(bot.getRobotCurDir())) {
                lastCalibrate = 0;
                moveBot(MOVEMENT.CALIBRATE);
            } else {
                lastCalibrate++;
                if (lastCalibrate >= 5) {
                    DIRECTION targetDir = getCalibrationDirection();
                    if (targetDir != null) {
                        lastCalibrate = 0;
                        calibrateBot(targetDir);
                    }
                }
            }

            calibrationMode = false;
        }*/
    }

    private int getAreaExplored() {
        int result = 0;
        for (int r = 0; r < map.getRowSize(); r++) {
            for (int c = 0; c < map.getColSize(); c++) {
                if (map.getTile(r, c).getExplored()) {
                    result++;
                }
            }
        }
        return result;
    }

    private void returnHome() {
        /*
        if (!robot.getReachedGoal() && coverageLimit == 300 && timeLimit == 3600) {
            FastestPathAlgo goToGoal = new FastestPathAlgo(exploredMap, bot, realMap);
            goToGoal.runFastestPath(RobotConstants.GOAL_ROW, RobotConstants.GOAL_COL);
        }

        FastestPathAlgo returnToStart = new FastestPathAlgo(exploredMap, bot, realMap);
        returnToStart.runFastestPath(RobotConstants.START_ROW, RobotConstants.START_COL);

        System.out.println("Exploration complete!");
        areaExplored = calculateAreaExplored();
        System.out.printf("%.2f%% Coverage", (areaExplored / 300.0) * 100.0);
        System.out.println(", " + areaExplored + " Cells");
        System.out.println((System.currentTimeMillis() - startTime) / 1000 + " Seconds");

        if (bot.getRealBot()) {
            turnBotDirection(DIRECTION.WEST);
            moveBot(MOVEMENT.CALIBRATE);
            turnBotDirection(DIRECTION.SOUTH);
            moveBot(MOVEMENT.CALIBRATE);
            turnBotDirection(DIRECTION.WEST);
            moveBot(MOVEMENT.CALIBRATE);
        }
        turnBotDirection(DIRECTION.NORTH);
        */
    }

}