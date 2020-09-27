package robot;

public class Constant {
    // range of short range sensor (tiles)
    public static final int SENSOR_SHORT_RANGE_MIN = 1;
    // range of short range sensor (tiles)
    public static final int SENSOR_SHORT_RANGE_MAX = 2;
    // range of long range sensor (tiles)
    public static final int SENSOR_LONG_RANGE_MIN = 3;
    // range of long range sensor (tiles)
    public static final int SENSOR_LONG_RANGE_MAX = 4;

    public static final int SPEED = 50;
    public static final DIRECTION START_DIR = DIRECTION.NORTH;

    public static final int GOAL_ROW = 18;
    public static final int GOAL_COL = 13;
    public static final int START_ROW = 1;
    public static final int START_COL = 1;


    public static final int MOVE_COST = 50;
    public static final int TURN_COST = 100;
    public static final int INFINITE_COST = 99999;

    public static final String MOVE_FORWARD = "W|\n";
    public static final String MOVE_BACK = "S|\n";
    public static final String TURN_LEFT = "A|\n";
    public static final String TURN_RIGHT = "D|\n";
    public static final String CALIBRATE_SENSOR = "C|\n";
    public static final String SENSE_DATA = "R|\n";
    /*
    public static final String SHORT_CUT_1 = "F1|";
    public static final String SHORT_CUT_2 = "F2|";
    */

    public static final String START_EXPLORATION = "E|\n";
    public static final String START_FASTEST_PATH = "F|\n";
    public static final String SEND_ARENA = "SendArena\n";
    public static final String MDF_STRING = "M\n";

    public static final String TAKE_PICTURE = "TP\n";
    public static final String EXPLORATION_DONE = "ED\n";


    

    public enum MOVEMENT {
        FORWARD, BACKWARD, RIGHT, LEFT, CALIBRATE, ERROR;
    }

    public enum DIRECTION {
        NORTH, EAST, SOUTH, WEST;

        public static DIRECTION next(DIRECTION cur) {
            return values()[(cur.ordinal() + 1) % values().length];
        }

        public static DIRECTION prev(DIRECTION cur) {
            return values()[(cur.ordinal() + values().length - 1) % values().length];
        }

    }
}