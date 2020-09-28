package robot;

import map.Map;
import robot.Constant.DIRECTION;

public class Sensor {
    private DIRECTION dir;
    private String id;
    private int row;
    private int col;
    private int min;
    private int max;

    public Sensor(int min, int max, int row, int col, DIRECTION dir, String id) {
        this.row = row;
        this.col = col;
        this.min = min;
        this.max = max;
        this.dir = dir;
        this.id = id;
    }

    public int getSensorValueSimulator(int rowChange, int colChange, Map map) {
        if (min > 1) {
            for (int i = 1; i < this.min; i++) {
                int row = this.row + (rowChange * i);
                int col = this.col + (colChange * i);

                if (map.isValid(row, col)) {
                    if (map.getTile(row, col).getState() == 1) {
                        return i;
                    }
                }else {
                    return i;
                }
            }
        }

        for (int i = this.min; i <= this.max; i++) {
            int row = this.row + (rowChange * i);
            int col = this.col + (colChange * i);

            if (map.isValid(row, col)) {
                map.getTile(row, col).setExplored(true);
                if (map.getTile(row, col).getState() == 1) {
                    map.setObstacle(row, col, true);
                    return i;
                }
            }else{
                return i;
            }
        }

        return -1;
    }

    public int senseSimulator(Map map) {
        switch (dir) {
            case NORTH:
                return getSensorValueSimulator(1, 0, map);
            case EAST:
                return getSensorValueSimulator(0, 1, map);
            case SOUTH:
                return getSensorValueSimulator(-1, 0, map);
            case WEST:
                return getSensorValueSimulator(0, -1, map);
        }
        return -1;
    }

    public void setSensor(int row, int col, DIRECTION dir) {
        this.row = row;
        this.col = col;
        this.dir = dir;
    }

    public void senseReal(Map map, int sensorValue) {
        switch (dir) {
            case NORTH:
                useSenseValueReal(1, 0, map, sensorValue);
                break;
            case EAST:
                useSenseValueReal(0, 1, map, sensorValue);
                break;
            case SOUTH:
                useSenseValueReal(-1, 0, map, sensorValue);
                break;
            case WEST:
                useSenseValueReal(0, -1, map, sensorValue);
                break;
        }
    }


    private void useSenseValueReal(int rowChange, int colChange, Map map, int sensorVal) {
        /*
        if (sensorVal == 0) {
            return;
        }*/

        System.out.println("Sensor ID: " + id + " Value: " + sensorVal + " Pos: " + row + ", " + col);

        int sensorValue = sensorVal + 1;

        // Check if anything before the min range of the sensor is valid if min range is greater than 1
        for (int i = 1; i < this.min; i++) {
            int row = this.row + (rowChange * i);
            int col = this.col + (colChange * i);

            if (!map.isValid(row, col)) {
                return;
            }else {
                if (map.getTile(row, col).getState() == 1) {
                    return;
                }
            }
        }


        for (int i = this.min; i <= this.max; i++) {
            int row = this.row + (rowChange * i);
            int col = this.col + (colChange * i);

            if (map.isValid(row, col)) {
                map.getTile(row, col).setExplored(true);

                if (sensorValue == i) {
                    map.setObstacle(row, col, true);
                    System.out.println("Sensor ID: " + id + " Obstacle at " + row + " " + col);
                    break;
                }

                /*
                if (map.getTile(row, col).getState() == 1) {
                    if (id.equals("SHORT_RANGE_FRONT_LEFT") || id.equals("SHORT_RANGE_FRONT_CENTER") || id.equals("SHORT_RANGE_FRONT_RIGHT")) {
                        map.setObstacle(row, col, false);
                    } else {
                        break;
                    }
                }*/

            }
        }
    }
}



