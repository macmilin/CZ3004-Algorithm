package robot;

public class Constant {
    // range of short range sensor (tiles)
    public static final int SENSOR_SHORT_RANGE_MIN = 1;
    // range of short range sensor (tiles)
    public static final int SENSOR_SHORT_RANGE_MAX = 2;
    // range of long range sensor (tiles)
    public static final int SENSOR_LONG_RANGE_MIN = 1;
    // range of long range sensor (tiles)
    public static final int SENSOR_LONG_RANGE_MAX = 5;

    public static final int SPEED = 100;
    public static final DIRECTION START_DIR = DIRECTION.NORTH;

    public static final int GOAL_ROW = 18;
    public static final int GOAL_COL = 13;
    public static final int START_ROW = 1;
    public static final int START_COL = 1;


    public static final int MOVE_COST = 50;
    public static final int TURN_COST = 100;
    public static final int INFINITE_COST = 99999;

    public static final String MOVE_FORWARD = "W|";
    public static final String MOVE_BACK = "S|";
    public static final String TURN_LEFT = "A|";
    public static final String TURN_RIGHT = "D|";
    public static final String CALIBRATE_SENSOR = "C|";
    public static final String CALIBRATE_SENSOR_FRONT = "V|";
    public static final String CALIBRATE_SENSOR_LEFT = "X|";
    public static final String CALIBRATE_SENSOR_PASS = "P|";
    public static final String CALIBRATE_SENSOR_FAIL = "O|";
    public static final String SENSE_DATA = "R|";
    public static final String SENSE_DATA_RAW = "T|";

    /*
    public static final String SHORT_CUT_1 = "F1|";
    public static final String SHORT_CUT_2 = "F2|";
    */

    public static final String START_EXPLORATION = "E|";
    public static final String START_FASTEST_PATH = "F|";
    public static final String SEND_ARENA = "UA|";
    public static final String MDF_STRING = "M";

    public static final String TAKE_PICTURE = "TP";
    public static final String EXPLORATION_DONE = "ED";


    

    public enum MOVEMENT {
        FORWARD, BACKWARD, RIGHT, LEFT, CALIBRATE, ERROR, CALIBRATE_FRONT, CALIBRATE_LEFT;
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