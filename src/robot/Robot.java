package robot;


import java.util.concurrent.TimeUnit;
import map.Map;
import robot.Constant.DIRECTION;
import robot.Constant.MOVEMENT;
import communication.Communication;
import java.awt.*;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;


public class Robot {
    
    private boolean reachedGoal;
    private boolean realRun;
    private int row; 
    private int col; 
    private DIRECTION dir;
    private int speed;
    private static int TILE_SIZE = 35;
    private static int ROW_SIZE = 20;
    private int countForward = 0;
    
    // SR on front left facing north
    private final Sensor srFrontL;
    // SR on front center facing north
    private final Sensor srFrontC;
    // SR on front right facing north
    private final Sensor srFrontR;
    // SR on left facing west
    private final Sensor srRightB;
    // SR on right facing east
    private final Sensor srRightT;
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
        srRightB = new Sensor(Constant.SENSOR_SHORT_RANGE_MIN, Constant.SENSOR_SHORT_RANGE_MAX, row - 1, col + 1, newDir(MOVEMENT.RIGHT), "SHORT_RANGE_RIGHT_BOTTOM");
        srRightT = new Sensor(Constant.SENSOR_SHORT_RANGE_MIN, Constant.SENSOR_SHORT_RANGE_MAX, row + 1, col + 1, newDir(MOVEMENT.RIGHT), "SHORT_RANGE_RIGHT_TOP");
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

    public void faceNorth(){
        switch (dir) {
            case NORTH:
                break;
            case EAST:
                System.out.println("End exploration. Robot is facing East");
                sendMovement(MOVEMENT.CALIBRATE);
                System.out.println("Robot is calibrating");
                dir = newDir(MOVEMENT.RIGHT);
                sendMovement(MOVEMENT.RIGHT);
                System.out.println("Robot turn right");
                sendMovement(MOVEMENT.CALIBRATE);
                System.out.println("Robot is calibrating");
                dir = newDir(MOVEMENT.RIGHT);
                sendMovement(MOVEMENT.RIGHT);
                System.out.println("Robot turn right");
                sendMovement(MOVEMENT.CALIBRATE);
                System.out.println("Robot is calibrating");
                dir = newDir(MOVEMENT.RIGHT);
                sendMovement(MOVEMENT.RIGHT);
                System.out.println("Robot turn right");
                break;
            case SOUTH:
                System.out.println("End exploration. Robot is facing South");
                sendMovement(MOVEMENT.CALIBRATE);
                System.out.println("Robot is calibrating");
                dir = newDir(MOVEMENT.RIGHT);
                sendMovement(MOVEMENT.RIGHT);
                System.out.println("Robot turn right");
                sendMovement(MOVEMENT.CALIBRATE_FRONT);
                System.out.println("Robot is calibrating front");
                dir = newDir(MOVEMENT.RIGHT);
                sendMovement(MOVEMENT.RIGHT);
                System.out.println("Robot turn right");
                break;
            case WEST:
                sendMovement(MOVEMENT.CALIBRATE_FRONT);
                System.out.println("Robot is calibrating front");
                dir = newDir(MOVEMENT.RIGHT);
                sendMovement(MOVEMENT.RIGHT);
                System.out.println("Robot turn right");
                break;
            default:
                System.out.println("Error in Robot.face()!");
                break;
            }
    }

    
    public void move(MOVEMENT m, boolean updateAndroid, Map map) {
        if (!realRun) {
            try {
                TimeUnit.MILLISECONDS.sleep(speed);
            } catch (InterruptedException e) {
                System.out.println("Something went wrong in Robot.move()!");
            }
        }
        System.out.println("Robot move from " + row + ", " + col);
        System.out.println("Robot is facing " + dir);
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
                // U-turn
                dir = newDir(MOVEMENT.LEFT);
                dir = newDir(MOVEMENT.LEFT);
                break;
                /*
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
                break;*/
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

        if (realRun) {
            sendMovement(m);
        }
        /*
        if (realRun && canAlignFront(map)){
            sendMovement(MOVEMENT.CALIBRATE_FRONT);
        }*/
        
        if (realRun && countForward >= 0 && canCalibrate(map)) {
            sendMovement(MOVEMENT.CALIBRATE);
            Communication comms = Communication.getComms();
            String result = comms.receiveMessage();
            if (result.equals(Constant.CALIBRATE_SENSOR_PASS)){
                System.out.println("Calibration Successful");
                countForward = 0;
            }else {
                System.out.println("Calibration Fail");
            }
                    
        }else {
            countForward++;
        }

        if (updateAndroid) {
            sendRobotData();
            sendMDFToAndroid(map);
        }
        updateReachedGoal();
    }

    public void sendMovement(MOVEMENT m) {
        Communication comms = Communication.getComms();
        switch(m){
            case FORWARD:
                comms.sendMessage(Constant.MOVE_FORWARD);
                break;
            case BACKWARD:
                //comms.sendMessage(Constant.MOVE_BACK);
                comms.sendMessage(Constant.TURN_RIGHT);
                comms.sendMessage(Constant.TURN_RIGHT);
                break;
            case RIGHT:
                comms.sendMessage(Constant.TURN_RIGHT);
                break;
            case LEFT:
                comms.sendMessage(Constant.TURN_LEFT);
                break;
            case CALIBRATE:
                comms.sendMessage(Constant.CALIBRATE_SENSOR);
                break;
            case CALIBRATE_FRONT:
                comms.sendMessage(Constant.CALIBRATE_SENSOR_FRONT);
                break;
            default:
                System.out.println("Error in sending movement");
        }
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
            result[3] = srRightB.senseSimulator(map);
            result[4] = srRightT.senseSimulator(map);
            result[5] = lrLeftC.senseSimulator(map);
        } else {
            Communication comms = Communication.getComms();

            //comms.sendMessage(Constant.SENSE_DATA_RAW);
            //comms.receiveMessage();
            
            comms.sendMessage(Constant.SENSE_DATA);
            
            String message = comms.receiveMessage();
            //System.out.println(message);


            //comms.sendMessage(Constant.MOVE_FORWARD);
            //comms.sendMessage(Constant.MOVE_FORWARD);
            //String message = comms.receiveMessage();
            //System.out.println(message);
            String[] messageArr = message.split("\\|");
            System.out.println(Arrays.toString(messageArr));

            
            result[0] = Integer.parseInt(messageArr[0]);
            result[1] = Integer.parseInt(messageArr[1]);
            result[2] = Integer.parseInt(messageArr[2]);
            result[3] = Integer.parseInt(messageArr[3]);
            result[4] = Integer.parseInt(messageArr[4]);
            result[5] = Integer.parseInt(messageArr[5]);
            
            srFrontL.senseReal(map, result[0]);
            srFrontC.senseReal(map, result[1]);
            srFrontR.senseReal(map, result[2]);
            srRightB.senseReal(map, result[3]);
            srRightT.senseReal(map, result[4]);
            lrLeftC.senseReal(map, result[5]);
        }

        return result;
    }

    public void setSensors() {
        switch (dir) {
            case NORTH:
                srFrontL.setSensor(this.row + 1, this.col - 1, this.dir);
                srFrontC.setSensor(this.row + 1, this.col, this.dir);
                srFrontR.setSensor(this.row + 1, this.col + 1, this.dir);
                srRightB.setSensor(this.row - 1, this.col + 1, newDir(MOVEMENT.RIGHT));
                lrLeftC.setSensor(this.row, this.col - 1, newDir(MOVEMENT.LEFT));
                srRightT.setSensor(this.row + 1, this.col + 1, newDir(MOVEMENT.RIGHT));
                break;
            case EAST:
                srFrontL.setSensor(this.row + 1, this.col + 1, this.dir);
                srFrontC.setSensor(this.row, this.col + 1, this.dir);
                srFrontR.setSensor(this.row - 1, this.col + 1, this.dir);
                srRightB.setSensor(this.row - 1, this.col - 1, newDir(MOVEMENT.RIGHT));
                lrLeftC.setSensor(this.row + 1, this.col, newDir(MOVEMENT.LEFT));
                srRightT.setSensor(this.row - 1, this.col + 1, newDir(MOVEMENT.RIGHT));
                break;
            case SOUTH:
                srFrontL.setSensor(this.row - 1, this.col + 1, this.dir);
                srFrontC.setSensor(this.row - 1, this.col, this.dir);
                srFrontR.setSensor(this.row - 1, this.col - 1, this.dir);
                srRightB.setSensor(this.row + 1, this.col - 1, newDir(MOVEMENT.RIGHT));
                lrLeftC.setSensor(this.row, this.col + 1, newDir(MOVEMENT.LEFT));
                srRightT.setSensor(this.row - 1, this.col - 1, newDir(MOVEMENT.RIGHT));
                break;
            case WEST:
                srFrontL.setSensor(this.row - 1, this.col - 1, this.dir);
                srFrontC.setSensor(this.row, this.col - 1, this.dir);
                srFrontR.setSensor(this.row + 1, this.col - 1, this.dir);
                srRightB.setSensor(this.row + 1, this.col + 1, newDir(MOVEMENT.RIGHT));
                lrLeftC.setSensor(this.row - 1, this.col, newDir(MOVEMENT.LEFT));
                srRightT.setSensor(this.row + 1, this.col - 1, newDir(MOVEMENT.RIGHT));
                break;
        }

    }

    public void setRealRun(boolean realRun) {
        this.realRun = realRun;
    }

    
    public boolean canCalibrate(Map map) {
        switch (dir) {
            case NORTH:
                if (map.isWallOrObstacle(row + 1, col + 2) && map.isWallOrObstacle(row, col + 2) && map.isWallOrObstacle(row - 1, col + 2)){
                    return true;
                }else if (map.isWallOrObstacle(row + 1, col + 2) && map.isWallOrObstacle(row, col + 2)){
                    return true;
                }else if (map.isWallOrObstacle(row, col + 2) && map.isWallOrObstacle(row - 1, col + 2)) {
                    return true;
                }else {
                    return false;
                }
                //return map.isWallOrObstacle(row + 1, col + 2) && map.isWallOrObstacle(row, col + 2) && map.isWallOrObstacle(row - 1, col + 2);
            case EAST:
                if (map.isWallOrObstacle(row - 2, col + 1) && map.isWallOrObstacle(row - 2, col) && map.isWallOrObstacle(row - 2, col - 1)){
                    return true;
                }else if (map.isWallOrObstacle(row - 2, col + 1) && map.isWallOrObstacle(row - 2, col)) {
                    return true;
                }else if (map.isWallOrObstacle(row - 2, col) && map.isWallOrObstacle(row - 2, col - 1)) {
                    return true;
                }else {
                    return false;
                }
                //return map.isWallOrObstacle(row - 2, col + 1) && map.isWallOrObstacle(row - 2, col) && map.isWallOrObstacle(row - 2, col - 1);
            case SOUTH:
                if (map.isWallOrObstacle(row - 1, col - 2) && map.isWallOrObstacle(row, col - 2) && map.isWallOrObstacle(row + 1, col - 2)){
                    return true;
                }else if (map.isWallOrObstacle(row - 1, col - 2) && map.isWallOrObstacle(row, col - 2)) {
                    return true;
                }else if (map.isWallOrObstacle(row, col - 2) && map.isWallOrObstacle(row + 1, col - 2)) {
                    return true;
                }else {
                    return false;
                }
                //return map.isWallOrObstacle(row - 1, col - 2) && map.isWallOrObstacle(row, col - 2) && map.isWallOrObstacle(row + 1, col - 2);
            case WEST:
                if (map.isWallOrObstacle(row + 2, col - 1) && map.isWallOrObstacle(row + 2, col) && map.isWallOrObstacle(row + 2, col + 1)){
                    return true;
                }else if (map.isWallOrObstacle(row + 2, col - 1) && map.isWallOrObstacle(row + 2, col)) {
                    return true;
                }else if (map.isWallOrObstacle(row + 2, col) && map.isWallOrObstacle(row + 2, col + 1)){
                    return true;
                }else {
                    return false;
                }
                //return map.isWallOrObstacle(row + 2, col - 1) && map.isWallOrObstacle(row + 2, col) && map.isWallOrObstacle(row + 2, col + 1);
        }

        return false;
    }

    public boolean canAlignFront(Map map) {
        switch (dir) {
            case NORTH:
                return map.isWallOrObstacle(row + 2, col - 1) && map.isWallOrObstacle(row + 2, col) && map.isWallOrObstacle(row + 2, col + 1);
            case EAST:
                return map.isWallOrObstacle(row - 1, col + 2) && map.isWallOrObstacle(row, col + 2) && map.isWallOrObstacle(row + 1, col + 2);
            case SOUTH:
                return map.isWallOrObstacle(row - 2, col - 1) && map.isWallOrObstacle(row - 2, col) && map.isWallOrObstacle(row - 2, col + 1);
            case WEST:
                return map.isWallOrObstacle(row - 1, col - 2) && map.isWallOrObstacle(row, col - 2) && map.isWallOrObstacle(row + 1, col - 2);
        }

        return false;

    }

    public int[] huggedObstacle(Map map) {
        //System.out.println("Check is hugged obstacle");
        //System.out.println(dir);
        // [1/0, row, col]
        int[] result = new int[7];
        switch (dir) {
            case NORTH:
                /*
                System.out.println("Check is hugged obstacle robot face north --> NO");
                System.out.println(map);
                System.out.println(map.isValid(row, col + 2));
                System.out.println(map.getTile(row, col + 2).getState());
                System.out.println("A");
                */
                if (map.isValid(row, col + 2) && map.getTile(row, col + 2).getState() == 1 ||
                map.isValid(row + 1, col + 2) && map.getTile(row + 1, col + 2).getState() == 1 ||
                map.isValid(row - 1, col + 2) && map.getTile(row - 1, col + 2).getState() == 1 ){
                    //System.out.println("Check is hugged obstacle robot face north --> YES");
                    result[0] = 1;
                    result[1] = row + 1;
                    result[2] = col + 2;
                    result[3] = row;
                    result[4] = col + 2;
                    result[5] = row - 1;
                    result[6] = col + 2;
                    return result;
                }
                System.out.println("Check is hugged obstacle robot face north --> AFTERR IF");
                break;
            case EAST:
                if (map.isValid(row - 2, col) && map.getTile(row - 2, col).getState() == 1 ||
                map.isValid(row - 2, col + 1) && map.getTile(row - 2, col + 1).getState() == 1 ||
                map.isValid(row - 2, col - 1) && map.getTile(row - 2, col - 1).getState() == 1 ){
                    System.out.println("Check is hugged obstacle robot face east --> YES");
                    result[0] = 1;
                    result[1] = row - 2;
                    result[2] = col + 1;
                    result[3] = row - 2;
                    result[4] = col;
                    result[5] = row - 2;
                    result[6] = col - 1;
                    return result;
                }
                break;
            case SOUTH:
                if (map.isValid(row, col - 2) && map.getTile(row, col - 2).getState() == 1 ||
                map.isValid(row - 1, col - 2) && map.getTile(row - 1, col - 2).getState() == 1 ||
                map.isValid(row + 1, col - 2) && map.getTile(row + 1, col - 2).getState() == 1){
                    System.out.println("Check is hugged obstacle robot face south --> YES");
                    result[0] = 1;
                    result[1] = row - 1;
                    result[2] = col - 2;
                    result[3] = row;
                    result[4] = col - 2;
                    result[5] = row + 1;
                    result[6] = col - 2;
                    return result;
                }
                break;
            case WEST:
                if (map.isValid(row + 2, col) && map.getTile(row + 2, col).getState() == 1 ||
                map.isValid(row + 2, col - 1) && map.getTile(row + 2, col - 1).getState() == 1 ||
                map.isValid(row + 2, col + 1) && map.getTile(row + 2, col + 1).getState() == 1 ){
                    System.out.println("Check is hugged obstacle robot face west --> YES");
                    result[0] = 1;
                    result[1] = row + 2;
                    result[2] = col - 1;
                    result[3] = row + 2;
                    result[4] = col;
                    result[5] = row + 2;
                    result[6] = col + 1;

                    return result;
                }
                break;
        }

        result[0] = 0;
        result[1] = row;
        result[2] = col;
        result[3] = row;
        result[4] = col;
        result[5] = row;
        result[6] = col;
        return result;
    }

    public void takePicture(boolean imageRecRun, Map map) {
        if (imageRecRun) {
            //Communication comms = Communication.getComms();
            //comms.sendMessage(Constant.TAKE_PICTURE);

            int[] result = huggedObstacle(map);
            
            if (result[0] == 1) {
                System.out.println("Yes hugged obstacle");
                Communication comms = Communication.getComms();
                String data = Constant.TAKE_PICTURE;
                
                data += "|";
                data += result[1];
                data += "|";
                data += result[2];
                data += "|";
                data += result[3];
                data += "|";
                data += result[4];
                data += "|";
                data += result[5];
                data += "|";
                data += result[6];
                
                comms.sendMessage(data);
            }
        }
    }

    public void sendMDFToAndroid(Map map) {
        String explored = map.generateMapDescriptorPartOne();
        String obstacle = map.generateMapDescriptorPartTwo();

        String data = "M{\"map\":[{\"explored\":\"";
        data += explored;
        data += "\",\"length\":";
        data += 300;
        data += ",\"obstacle\":\"";
        data += obstacle;
        data += "\"}]}\n";

        Communication.getComms().sendMessage(data);
    }

    public void sendRobotData(){
        String data = "RP{\"robotPosition\":[";
        data += col - 1;
        data += ',';
        data += 19 - row - 1;
        data += ',';
        int d = 0;
        switch (dir) {
            case NORTH:
                d = 0;
                break;
            case EAST:
                d = 90;
                break;
            case SOUTH:
                d = 180;
                break;
            case WEST:
                d = 270;
                break;
        }
        data += d;
        data += "]}\n";
        Communication.getComms().sendMessage(data);
    }

    public void moveFast(MOVEMENT m, boolean updateAndroid, Map map) {
        if (!realRun) {
            try {
                TimeUnit.MILLISECONDS.sleep(speed);
            } catch (InterruptedException e) {
                System.out.println("Something went wrong in Robot.move()!");
            }
        }
        System.out.println("Robot move from " + row + ", " + col);
        System.out.println("Robot is facing " + dir);
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
                // U-turn
                dir = newDir(MOVEMENT.LEFT);
                dir = newDir(MOVEMENT.LEFT);
                break;
                /*
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
                break;*/
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

        if (realRun) {
            sendMovement(m);
        }

        if (updateAndroid) {
            sendRobotData();
            sendMDFToAndroid(map);
        }
        updateReachedGoal();
    }
    



}