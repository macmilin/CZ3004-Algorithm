package robot;

import map.Map;
import map.MapConstants;
import robot.RobotConstants.DIRECTION;
import robot.RobotConstants.MOVEMENT;
import utils.CommMgr;
import java.awt.*;


import java.util.concurrent.TimeUnit;

// @formatter:off
/**
 * Represents the robot moving in the arena.
 *
 * The robot is represented by a 3 x 3 cell space as below:
 *
 *          ^   ^   ^
 *         SR  SR  SR
 *       < SR
 *        [X] [X] [X]
 *   < LR [X] [X] [X] SR >
 *        [X] [X] [X]
 *
 * SR = Short Range Sensor, LR = Long Range Sensor
 *
 */
// @formatter:on

public class Robot {
    
    private int row; 
    private int col; 
    private DIRECTION robotDir;
    private int speed;
    private static int TILE_SIZE = 35;
    private static int ROW_SIZE = 20;
    
    // SR on front left facing north
    private final Sensor srFrontL;
    // SR on front center facing north
    private final Sensor srFrontC;
    // SR on front right facing north
    private final Sensor srFrontR;
    // SR on left facing west
    private final Sensor srLeft;
    // SR on right facing east
    private final Sensor srRight;
    // LR on left facing west
    private final Sensor lrLeft;
    
    private boolean reachedGoal;
    private final boolean realBot;

    public Robot(int row, int col, boolean realBot) {
        this.row = row;
        this.col = col;

        robotDir = RobotConstants.START_DIR;
        speed = RobotConstants.SPEED;

        this.realBot = realBot;
        
        srFrontL = new Sensor(RobotConstants.SENSOR_SHORT_RANGE_L, RobotConstants.SENSOR_SHORT_RANGE_H, this.row + 1, this.col - 1, this.robotDir, "SRFL");
        srFrontC = new Sensor(RobotConstants.SENSOR_SHORT_RANGE_L, RobotConstants.SENSOR_SHORT_RANGE_H, this.row + 1, this.col, this.robotDir, "SRFC");
        srFrontR = new Sensor(RobotConstants.SENSOR_SHORT_RANGE_L, RobotConstants.SENSOR_SHORT_RANGE_H, this.row + 1, this.col + 1, this.robotDir, "SRFR");
        srLeft = new Sensor(RobotConstants.SENSOR_SHORT_RANGE_L, RobotConstants.SENSOR_SHORT_RANGE_H, this.row + 1, this.col - 1, findNewDirection(MOVEMENT.LEFT), "SRL");
        srRight = new Sensor(RobotConstants.SENSOR_SHORT_RANGE_L, RobotConstants.SENSOR_SHORT_RANGE_H, this.row + 1, this.col + 1, findNewDirection(MOVEMENT.RIGHT), "SRR");
        lrLeft = new Sensor(RobotConstants.SENSOR_LONG_RANGE_L, RobotConstants.SENSOR_LONG_RANGE_H, this.row, this.col - 1, findNewDirection(MOVEMENT.LEFT), "LRL" );
        
    }

    public void reset() {
        this.row = 1;
        this.col = 1;

        robotDir = RobotConstants.START_DIR;
        speed = RobotConstants.SPEED;
        setSensors();
    }

    public void setPos(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setDir(DIRECTION dir) {
        robotDir = dir;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public DIRECTION getDir() {
        return robotDir;
    }

    
    public boolean getRealBot() {
        return realBot;
    }

    private void updateReachedGoal() {
        if (this.getRow() == MapConstants.GOAL_ROW && this.getCol() == MapConstants.GOAL_COL)
            this.reachedGoal = true;
    }

    public boolean getReachedGoal() {
        return this.reachedGoal;
    }

    /**
     * Takes in a MOVEMENT and moves the robot accordingly by changing its position and direction.
     * if this.realBot is set, then send the movement.
     */
     
    public void move(MOVEMENT m, boolean sendMoveToAndroid) {
        if (!realBot) {
            // Emulate real movement by pausing execution.
            //System.out.println("MOVE");
            try {
                TimeUnit.MILLISECONDS.sleep(speed);
            } catch (InterruptedException e) {
                System.out.println("Something went wrong in Robot.move()!");
            }
        }

        switch (m) {
            case FORWARD:
                switch (robotDir) {
                    case NORTH:
                        row++;
                        break;
                    case EAST:
                        col++;
                        break;
                    case SOUTH:
                        row--;
                        break;
                    case WEST:
                        col--;
                        break;
                }
                break;
            case BACKWARD:
                switch (robotDir) {
                    case NORTH:
                        row--;
                        break;
                    case EAST:
                        col--;
                        break;
                    case SOUTH:
                        row++;
                        break;
                    case WEST:
                        col++;
                        break;
                }
                break;
            case RIGHT:
            case LEFT:
                robotDir = findNewDirection(m);
                break;
            case CALIBRATE:
                break;
            default:
                System.out.println("Error in Robot.move()!");
                break;
        }

        /*
        if (realBot) sendMovement(m, sendMoveToAndroid);
        else System.out.println("Move: " + MOVEMENT.print(m));*/

        updateReachedGoal();
    }

    /**
     * Overloaded method that calls this.move(MOVEMENT m, boolean sendMoveToAndroid = true).
     */
     
    public void move(MOVEMENT m) {
        this.move(m, true);
    }

    /**
     * Sends a number instead of 'F' for multiple continuous forward movements.
     */
     /*
    public void moveForwardMultiple(int count) {
        if (count == 1) {
            move(MOVEMENT.FORWARD);
        } else {
            CommMgr comm = CommMgr.getCommMgr();
            if (count == 10) {
                comm.sendMsg("0", CommMgr.INSTRUCTIONS);
            } else if (count < 10) {
                comm.sendMsg(Integer.toString(count), CommMgr.INSTRUCTIONS);
            }

            switch (robotDir) {
                case NORTH:
                    row += count;
                    break;
                case EAST:
                    col += count;
                    break;
                case SOUTH:
                    row += count;
                    break;
                case WEST:
                    col += count;
                    break;
            }

            comm.sendMsg(this.getRow() + "," + this.getCol() + "," + DIRECTION.print(this.getDir()), CommMgr.BOT_POS);
        }
    }*/

    /**
     * Uses the CommMgr to send the next movement to the robot.
     */
     /*
    private void sendMovement(MOVEMENT m, boolean sendMoveToAndroid) {
        CommMgr comm = CommMgr.getCommMgr();
        comm.sendMsg(MOVEMENT.print(m) + "", CommMgr.INSTRUCTIONS);
        if (m != MOVEMENT.CALIBRATE && sendMoveToAndroid) {
            comm.sendMsg(this.getRow() + "," + this.getCol() + "," + DIRECTION.print(this.getDir()), CommMgr.BOT_POS);
        }
    }*/

    /**
     * Sets the sensors' position and direction values according to the robot's current position and direction.
     */
     
    public void setSensors() {
        switch (robotDir) {
            case NORTH:
                srFrontL.setSensor(this.row + 1, this.col - 1, this.robotDir);
                srFrontC.setSensor(this.row + 1, this.col, this.robotDir);
                srFrontR.setSensor(this.row + 1, this.col + 1, this.robotDir);
                srLeft.setSensor(this.row + 1, this.col - 1, findNewDirection(MOVEMENT.LEFT));
                lrLeft.setSensor(this.row, this.col - 1, findNewDirection(MOVEMENT.LEFT));
                srRight.setSensor(this.row + 1, this.col + 1, findNewDirection(MOVEMENT.RIGHT));
                break;
            case EAST:
                srFrontL.setSensor(this.row + 1, this.col + 1, this.robotDir);
                srFrontC.setSensor(this.row, this.col + 1, this.robotDir);
                srFrontR.setSensor(this.row - 1, this.col + 1, this.robotDir);
                srLeft.setSensor(this.row + 1, this.col + 1, findNewDirection(MOVEMENT.LEFT));
                lrLeft.setSensor(this.row + 1, this.col, findNewDirection(MOVEMENT.LEFT));
                srRight.setSensor(this.row - 1, this.col + 1, findNewDirection(MOVEMENT.RIGHT));
                break;
            case SOUTH:
                srFrontL.setSensor(this.row - 1, this.col + 1, this.robotDir);
                srFrontC.setSensor(this.row - 1, this.col, this.robotDir);
                srFrontR.setSensor(this.row - 1, this.col - 1, this.robotDir);
                srLeft.setSensor(this.row - 1, this.col + 1, findNewDirection(MOVEMENT.LEFT));
                lrLeft.setSensor(this.row, this.col + 1, findNewDirection(MOVEMENT.LEFT));
                srRight.setSensor(this.row - 1, this.col - 1, findNewDirection(MOVEMENT.RIGHT));
                break;
            case WEST:
                srFrontL.setSensor(this.row - 1, this.col - 1, this.robotDir);
                srFrontC.setSensor(this.row, this.col - 1, this.robotDir);
                srFrontR.setSensor(this.row + 1, this.col - 1, this.robotDir);
                srLeft.setSensor(this.row - 1, this.col - 1, findNewDirection(MOVEMENT.LEFT));
                lrLeft.setSensor(this.row - 1, this.col, findNewDirection(MOVEMENT.LEFT));
                srRight.setSensor(this.row + 1, this.col - 1, findNewDirection(MOVEMENT.RIGHT));
                break;
        }

    }

    /**
     * Uses the current direction of the robot and the given movement to find the new direction of the robot.
     */
    private DIRECTION findNewDirection(MOVEMENT m) {
        if (m == MOVEMENT.RIGHT) {
            return DIRECTION.getNext(robotDir);
        } else {
            return DIRECTION.getPrevious(robotDir);
        }
    }

    /**
     * Render Robot 
     */
    public void renderRobot(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        g.fillOval((col - 1) * TILE_SIZE, (ROW_SIZE - 2 - row) * TILE_SIZE, 3*TILE_SIZE, 3*TILE_SIZE);

        // Paint the robot's direction indicator on-screen.
        
        g.setColor(Color.BLACK);

        switch (robotDir) {
            case NORTH:
                g.fillOval((col) * TILE_SIZE, (ROW_SIZE - 2 - row) * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                break;
            case EAST:
                g.fillOval((col + 1) * TILE_SIZE, (ROW_SIZE - 1 - row) * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                break;
            case SOUTH:
                g.fillOval((col) * TILE_SIZE, (ROW_SIZE - row) * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                break;
            case WEST:
                g.fillOval((col - 1) * TILE_SIZE, (ROW_SIZE - 1 - row) * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                break;
        }
    }    

    /**
     * Calls the .sense() method of all the attached sensors and stores the received values in an integer array.
     *
     * @return [SRFrontLeft, SRFrontCenter, SRFrontRight, SRLeft, SRRight, LRLeft]
     */
    public int[] sense(Map map) {
        int[] result = new int[6];

        if (!realBot) {
            result[0] = srFrontL.senseVirtual(map);
            result[1] = srFrontC.senseVirtual(map);
            result[2] = srFrontR.senseVirtual(map);
            result[3] = srLeft.senseVirtual(map);
            result[4] = srRight.senseVirtual(map);
            result[5] = lrLeft.senseVirtual(map);
        } /*else {
            CommMgr comm = CommMgr.getCommMgr();
            String msg = comm.recvMsg();
            String[] msgArr = msg.split(";");

            if (msgArr[0].equals(CommMgr.SENSOR_DATA)) {
                result[0] = Integer.parseInt(msgArr[1].split("_")[1]);
                result[1] = Integer.parseInt(msgArr[2].split("_")[1]);
                result[2] = Integer.parseInt(msgArr[3].split("_")[1]);
                result[3] = Integer.parseInt(msgArr[4].split("_")[1]);
                result[4] = Integer.parseInt(msgArr[5].split("_")[1]);
                result[5] = Integer.parseInt(msgArr[6].split("_")[1]);
            }

            srFrontL.senseReal(map, result[0]);
            srFrontC.senseReal(map, result[1]);
            srFrontR.senseReal(map, result[2]);
            srLeft.senseReal(map, result[3]);
            srRight.senseReal(map, result[4]);
            srLeft.senseReal(map, result[5]);

            
            String[] mapStrings = MapDescriptor.generateMapDescriptor(map);
            comm.sendMsg(mapStrings[0] + " " + mapStrings[1], CommMgr.MAP_STRINGS);
        }*/

        return result;
    }
}