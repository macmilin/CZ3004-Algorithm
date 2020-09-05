package robot;

/**
 * Constants for the robot.
 *
 */

public class RobotConstants {
    public static final int GOAL_ROW = 18;
    public static final int GOAL_COL = 13;
    public static final int START_ROW = 1;
    public static final int START_COL = 1;
    public static final int MOVE_COST = 10;
    public static final int TURN_COST = 20;
    public static final int SPEED = 100;
    public static final DIRECTION START_DIR = DIRECTION.NORTH;
    // range of short range sensor (tiles)
    public static final int SENSOR_SHORT_RANGE_L = 1;
    // range of short range sensor (tiles)
    public static final int SENSOR_SHORT_RANGE_H = 2;
    // range of long range sensor (tiles)
    public static final int SENSOR_LONG_RANGE_L = 3;
    // range of long range sensor (tiles)
    public static final int SENSOR_LONG_RANGE_H = 4;

    public static final int INFINITE_COST = 9999;

    public enum DIRECTION {
        NORTH, EAST, SOUTH, WEST;

        public static DIRECTION getNext(DIRECTION curDirection) {
            return values()[(curDirection.ordinal() + 1) % values().length];
        }

        public static DIRECTION getPrevious(DIRECTION curDirection) {
            return values()[(curDirection.ordinal() + values().length - 1) % values().length];
        }

        public static char print(DIRECTION d) {
            switch (d) {
                case NORTH:
                    return 'N';
                case EAST:
                    return 'E';
                case SOUTH:
                    return 'S';
                case WEST:
                    return 'W';
                default:
                    return 'X';
            }
        }
    }

    public enum MOVEMENT {
        FORWARD, BACKWARD, RIGHT, LEFT, CALIBRATE, ERROR;

        public static char print(MOVEMENT m) {
            switch (m) {
                case FORWARD:
                    return 'F';
                case BACKWARD:
                    return 'B';
                case RIGHT:
                    return 'R';
                case LEFT:
                    return 'L';
                case CALIBRATE:
                    return 'C';
                case ERROR:
                default:
                    return 'E';
            }
        }
    }
}