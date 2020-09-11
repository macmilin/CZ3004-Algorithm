package robot;


import java.util.concurrent.TimeUnit;
import map.Map;
import robot.Constant.DIRECTION;
import robot.Constant.MOVEMENT;
import utils.CommMgr;
import java.awt.*;

public class Robot {
    
    private boolean reachedGoal;
    private final boolean realRun;

    private int row; 
    private int col; 
    private DIRECTION dir;
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
    private final Sensor srLeftT;
    // SR on right facing east
    private final Sensor srRightC;
    // LR on left facing west
    private final Sensor lrLeftC;
    

    public Robot(int row, int col, boolean realRun) {
        this.row = row;
        this.col = col;

        dir = Constant.START_DIR;
        speed = Constant.SPEED;

        this.realRun = realRun;
        
        srFrontL = new Sensor(Constant.SENSOR_SHORT_RANGE_MIN, Constant.SENSOR_SHORT_RANGE_MAX, row + 1, col - 1, dir, "SHORT_RANGE_FRONT_LEFT");
        srFrontC = new Sensor(Constant.SENSOR_SHORT_RANGE_MIN, Constant.SENSOR_SHORT_RANGE_MAX, row + 1, col, dir, "SHORT_RANGE_FRONT_CENTER");
        srFrontR = new Sensor(Constant.SENSOR_SHORT_RANGE_MIN, Constant.SENSOR_SHORT_RANGE_MAX, row + 1, col + 1, dir, "SHORT_RANGE_FRONT_RIGHT");
        srLeftT = new Sensor(Constant.SENSOR_SHORT_RANGE_MIN, Constant.SENSOR_SHORT_RANGE_MAX, row + 1, col - 1, newDir(MOVEMENT.LEFT), "SHORT_RANGE_LEFT_TOP");
        srRightC = new Sensor(Constant.SENSOR_SHORT_RANGE_MIN, Constant.SENSOR_SHORT_RANGE_MAX, row + 1, col + 1, newDir(MOVEMENT.RIGHT), "SHORT_RANGE_RIGHT_CENTER");
        lrLeftC = new Sensor(Constant.SENSOR_LONG_RANGE_MIN, Constant.SENSOR_LONG_RANGE_MAX, row, col - 1, newDir(MOVEMENT.LEFT), "LONG_RANGE_LEFT_CENTER" );
        
    }

    public void reset() {
        row = 1;
        col = 1;

        dir = Constant.START_DIR;
        speed = Constant.SPEED;
        setSensors();
    }

    public void renderRobot(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        g.fillOval((col - 1) * TILE_SIZE, (ROW_SIZE - 2 - row) * TILE_SIZE, 3*TILE_SIZE, 3*TILE_SIZE);

        // Paint the robot's direction indicator on-screen.
        
        g.setColor(Color.BLACK);

        switch (dir) {
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
        this.dir = dir;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public DIRECTION getDir() {
        return dir;
    }

    
    public boolean getRealRun() {
        return realRun;
    }

    private void updateReachedGoal() {
        if (getRow() == Constant.GOAL_ROW && getCol() == Constant.GOAL_COL){
            reachedGoal = true;
        }
    }

    public boolean getReachedGoal() {
        return this.reachedGoal;
    }

    
    public void move(MOVEMENT m, boolean sendMoveToAndroid) {
        if (!realRun) {
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
                switch (dir) {
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
                switch (dir) {
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
                dir = newDir(m);
                break;
            case CALIBRATE:
                break;
            default:
                System.out.println("Error in Robot.move()!");
                break;
        }

        /*
        if (realRun) sendMovement(m, sendMoveToAndroid);
        else System.out.println("Move: " + MOVEMENT.print(m));*/

        updateReachedGoal();
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

            switch (dir) {
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

    private DIRECTION newDir(MOVEMENT m) {
        if (m == MOVEMENT.RIGHT) {
            return DIRECTION.next(dir);
        } else {
            return DIRECTION.prev(dir);
        }
    }

        
    public int[] sense(Map map) {
        int[] result = new int[6];

        if (!realRun) {
            result[0] = srFrontL.senseSimulator(map);
            result[1] = srFrontC.senseSimulator(map);
            result[2] = srFrontR.senseSimulator(map);
            result[3] = srLeftT.senseSimulator(map);
            result[4] = srRightC.senseSimulator(map);
            result[5] = lrLeftC.senseSimulator(map);
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
            srLeftT.senseReal(map, result[3]);
            srRightC.senseReal(map, result[4]);
            srLeftT.senseReal(map, result[5]);

            
            String[] mapStrings = MapDescriptor.generateMapDescriptor(map);
            comm.sendMsg(mapStrings[0] + " " + mapStrings[1], CommMgr.MAP_STRINGS);
        }*/

        return result;
    }

    public void setSensors() {
        switch (dir) {
            case NORTH:
                srFrontL.setSensor(this.row + 1, this.col - 1, this.dir);
                srFrontC.setSensor(this.row + 1, this.col, this.dir);
                srFrontR.setSensor(this.row + 1, this.col + 1, this.dir);
                srLeftT.setSensor(this.row + 1, this.col - 1, newDir(MOVEMENT.LEFT));
                lrLeftC.setSensor(this.row, this.col - 1, newDir(MOVEMENT.LEFT));
                srRightC.setSensor(this.row + 1, this.col + 1, newDir(MOVEMENT.RIGHT));
                break;
            case EAST:
                srFrontL.setSensor(this.row + 1, this.col + 1, this.dir);
                srFrontC.setSensor(this.row, this.col + 1, this.dir);
                srFrontR.setSensor(this.row - 1, this.col + 1, this.dir);
                srLeftT.setSensor(this.row + 1, this.col + 1, newDir(MOVEMENT.LEFT));
                lrLeftC.setSensor(this.row + 1, this.col, newDir(MOVEMENT.LEFT));
                srRightC.setSensor(this.row - 1, this.col + 1, newDir(MOVEMENT.RIGHT));
                break;
            case SOUTH:
                srFrontL.setSensor(this.row - 1, this.col + 1, this.dir);
                srFrontC.setSensor(this.row - 1, this.col, this.dir);
                srFrontR.setSensor(this.row - 1, this.col - 1, this.dir);
                srLeftT.setSensor(this.row - 1, this.col + 1, newDir(MOVEMENT.LEFT));
                lrLeftC.setSensor(this.row, this.col + 1, newDir(MOVEMENT.LEFT));
                srRightC.setSensor(this.row - 1, this.col - 1, newDir(MOVEMENT.RIGHT));
                break;
            case WEST:
                srFrontL.setSensor(this.row - 1, this.col - 1, this.dir);
                srFrontC.setSensor(this.row, this.col - 1, this.dir);
                srFrontR.setSensor(this.row + 1, this.col - 1, this.dir);
                srLeftT.setSensor(this.row - 1, this.col - 1, newDir(MOVEMENT.LEFT));
                lrLeftC.setSensor(this.row - 1, this.col, newDir(MOVEMENT.LEFT));
                srRightC.setSensor(this.row + 1, this.col - 1, newDir(MOVEMENT.RIGHT));
                break;
        }

    }
}