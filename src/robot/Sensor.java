package robot;

import map.Map;
import robot.RobotConstants.DIRECTION;

/**
 * A sensor mounted on the robot.
 *
 */


public class Sensor {
    private final int minRange;
    private final int maxRange;
    private int sensorRow;
    private int sensorCol;
    private DIRECTION sensorDir;
    private final String id;

    public Sensor(int minRange, int maxRange, int row, int col, DIRECTION dir, String id) {
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.sensorRow = row;
        this.sensorCol = col;
        this.sensorDir = dir;
        this.id = id;
    }

    public void setSensor(int row, int col, DIRECTION dir) {
        this.sensorRow = row;
        this.sensorCol = col;
        this.sensorDir = dir;
    }

    /**
     * If no obstacle is detected, return -1
     * Else, return the number of tiles to the nearest detected osbtacle
     * Use during the simulation
     */

    public int senseVirtual(Map map) {
        switch (sensorDir) {
            case NORTH:
                return getSensorVal(map, 1, 0);
            case EAST:
                return getSensorVal(map, 0, 1);
            case SOUTH:
                return getSensorVal(map, -1, 0);
            case WEST:
                return getSensorVal(map, 0, -1);
        }
        return -1;
    }

    /**
     * Sets the appropriate obstacle tile in the map, returns the row or column value of the obstacle tile. 
     * Returns -1 if no obstacle is detected.
     * Use during the simulation.
     */

    private int getSensorVal(Map map, int rowInc, int colInc) {
        // Check if starting point is valid for sensors with minRange > 1.
        if (minRange > 1) {
            for (int i = 1; i < this.minRange; i++) {
                int row = this.sensorRow + (rowInc * i);
                int col = this.sensorCol + (colInc * i);

                if (!map.isValid(row, col)) return i;
                if (map.getTile(row, col).getState() == 1) return i;
            }
        }

        // Check if anything is detected by the sensor and return that value.
        for (int i = this.minRange; i <= this.maxRange; i++) {
            int row = this.sensorRow + (rowInc * i);
            int col = this.sensorCol + (colInc * i);

            if (!map.isValid(row, col)) return i;

            map.getTile(row, col).setExplored(true);
            //System.out.println("Set explored " + row + " " + col);

            if (map.getTile(row, col).getState() == 1) {
                return i;
            }
            /*
            if (realMap.getCell(row, col).getIsObstacle()) {
                exploredMap.setObstacleCell(row, col, true);
                return i;
            }*/
        }

        return -1;
    }

    /**
     * Uses the sensor direction and sensor value from RPI.
     * Use during real run.
     */

    public void senseReal(Map map, int sensorVal) {
        switch (sensorDir) {
            case NORTH:
                processSensorVal(map, sensorVal, 1, 0);
                break;
            case EAST:
                processSensorVal(map, sensorVal, 0, 1);
                break;
            case SOUTH:
                processSensorVal(map, sensorVal, -1, 0);
                break;
            case WEST:
                processSensorVal(map, sensorVal, 0, -1);
                break;
        }
    }

    /**
     * Sets the correct tile to explored and/or obstacle according to the actual sensor value.
     */

    private void processSensorVal(Map map, int sensorVal, int rowInc, int colInc) {
        if (sensorVal == 0) return;  // return value for LR sensor if obstacle before minRange

        // If above fails, check if starting point is valid for sensors with minRange > 1.
        for (int i = 1; i < this.minRange; i++) {
            int row = this.sensorRow + (rowInc * i);
            int col = this.sensorCol + (colInc * i);

            if (!map.isValid(row, col)) return;
            if (map.getTile(row, col).getState() == 1) return;
        }

        // Update map according to sensor's value.
        for (int i = this.minRange; i <= this.maxRange; i++) {
            int row = this.sensorRow + (rowInc * i);
            int col = this.sensorCol + (colInc * i);

            if (!map.isValid(row, col)) continue;

            map.getTile(row, col).setExplored(true);

            if (sensorVal == i) {
                map.setObstacle(row, col, true);
                break;
            }

            // Override previous obstacle value if front sensors detect no obstacle.
            if (map.getTile(row, col).getState() == 1) {
                if (id.equals("SRFL") || id.equals("SRFC") || id.equals("SRFR")) {
                    map.setObstacle(row, col, false);
                } else {
                    break;
                }
            }
        }
    }
}



