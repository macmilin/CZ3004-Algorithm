package algorithms;

import map.*;
import robot.*;
import robot.Constant;
import robot.Constant.DIRECTION;
import robot.Constant.MOVEMENT;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;


public class FastestPath {
    private Robot robot;
    private Map map;
    private int count;

    private double[][] g;

    private Tile curNode;
    private DIRECTION curDir;
    private Tile[] adjacentNodes;

    private ArrayList<Tile> frontier;
    private boolean[][] visited;
    private HashMap<Tile, Tile> previous;
    private Stack<Tile> path;

    public FastestPath(Robot robot, Map map) {
        this.robot = robot;
        this.map = map;
        count = 0;
        
        // Initialize data structures
        g = new double[map.getRowSize()][map.getColSize()];

        curNode = map.getTile(robot.getRow(), robot.getCol());
        curDir = robot.getDir();
        adjacentNodes = new Tile[4];

        frontier = new ArrayList<>();
        frontier.add(curNode);
        visited = new boolean[map.getRowSize()][map.getColSize()];
        previous = new HashMap<>();

        for (int row = 0; row < map.getRowSize(); row++){
            for(int col = 0; col < map.getColSize(); col++){
                visited[row][col] = false;
                if(canVisit(map.getTile(row, col))){
                    g[row][col] = 0;
                }else{
                    g[row][col] = Constant.INFINITE_COST;
                }
            }
        }
    }

    public void run(int targetRow, int targetCol) {
        System.out.println("Finding fastest path from row " + curNode.getRow() + " col " + curNode.getCol());

        do {
            if(visited[targetRow][targetCol]) {
                path = getPath(targetRow, targetCol);
                System.out.println("Found fastest path to goal");
                navigate(path);
                return;
            }

            curNode = getMinCost(targetRow, targetCol);
            if (previous.containsKey(curNode)) {
                curDir = getDir(curNode, previous.get(curNode), curDir);
            }
            visited[curNode.getRow()][curNode.getCol()] = true;
            frontier.remove(curNode);

            //System.out.println(frontier.size());

            if (map.isValid(curNode.getRow() + 1, curNode.getCol())) {
                Tile temp = map.getTile(curNode.getRow() + 1, curNode.getCol());
                if (canVisit(temp)) {
                    adjacentNodes[0] = temp;
                }else{
                    adjacentNodes[0] = null;
                }
            }

            if (map.isValid(curNode.getRow(), curNode.getCol() + 1)) {
                Tile temp = map.getTile(curNode.getRow(), curNode.getCol() + 1);
                if (canVisit(temp)) {
                    adjacentNodes[1] = temp;
                }else{
                    adjacentNodes[1] = null;
                }
            }

            if (map.isValid(curNode.getRow() - 1, curNode.getCol())) {
                Tile temp = map.getTile(curNode.getRow() - 1, curNode.getCol());
                if (canVisit(temp)) {
                    adjacentNodes[2] = temp;
                }else{
                    adjacentNodes[2] = null;
                }
            }

            if (map.isValid(curNode.getRow(), curNode.getCol() - 1)) {
                Tile temp = map.getTile(curNode.getRow(), curNode.getCol() - 1);
                if (canVisit(temp)) {
                    adjacentNodes[3] = temp;
                }else{
                    adjacentNodes[3] = null;
                }
            }

            for (int i = 0; i < adjacentNodes.length; i++){
                Tile temp = adjacentNodes[i];

                if(temp != null && !visited[temp.getRow()][temp.getCol()]){
                    if (!frontier.contains(temp)) {
                        g[temp.getRow()][temp.getCol()] = g[curNode.getRow()][curNode.getCol()] + getGn(curNode, temp, curDir);
                        frontier.add(temp);
                        previous.put(temp, curNode);
                    }else {
                        double newScore = g[curNode.getRow()][curNode.getCol()] + getGn(curNode, temp, curDir);
                        if (newScore < g[temp.getRow()][temp.getCol()]){
                            g[temp.getRow()][temp.getCol()] = newScore;
                            previous.put(temp, curNode);
                        }
                    }
                }
            }

        }while(!frontier.isEmpty());
        System.out.println("No path found");
    }

    public boolean canVisit(Tile t) {
        return !(t.getState() == 1) && !t.getVirtual() && t.getExplored();
    }

    public Tile getMinCost(int targetRow, int targetCol) {
        double min = Constant.INFINITE_COST;
        Tile answer = null;

        for (int i = frontier.size() - 1; i > -1; i--){
            double gn = g[frontier.get(i).getRow()][frontier.get(i).getCol()];
            double fn = gn + getHn(frontier.get(i), targetRow, targetCol);
            if (fn < min){
                min = fn;
                answer = frontier.get(i);
            }
        }
        return answer;
    }

    public DIRECTION getDir(Tile next, Tile prev, DIRECTION curDir) {
        if (prev.getRow() - next.getRow() > 0) {
            return DIRECTION.SOUTH;
        } else if (next.getRow() - prev.getRow() > 0) {
            return DIRECTION.NORTH;
        } else if (prev.getCol() - next.getCol() > 0) {
            return DIRECTION.WEST;
        } else if (next.getCol() - prev.getCol() > 0) {
            return DIRECTION.EAST;
        } else {
            return curDir;
        }
    }

    public double getGn(Tile cur, Tile target, DIRECTION dir) {
        DIRECTION nextDir = getDir(target, cur, dir);

        int numberOfTurns = Math.abs(dir.ordinal() - nextDir.ordinal());
        if (numberOfTurns > 2) {
            numberOfTurns = numberOfTurns % 2;
        }
        return Constant.MOVE_COST + numberOfTurns*Constant.TURN_COST;
    }

    public double getHn(Tile cur, int targetRow, int targetCol) {
        double moveCost = (Math.abs(targetCol - cur.getCol()) + Math.abs(targetRow - cur.getRow())) * Constant.MOVE_COST;
        if (moveCost != 0) {
            double turnCost = 0;
            if (targetRow - cur.getRow() != 0 || targetCol - cur.getCol() != 0) {
                turnCost = Constant.TURN_COST;
            }
            return moveCost + turnCost;
        }else {
            return 0;
        }
    }

    private Stack<Tile> getPath(int targetRow, int targetCol) {
        Stack<Tile> path = new Stack<>();
        Tile cur = map.getTile(targetRow, targetCol);

        while (true) {
            System.out.println(cur.getRow() + " " + cur.getCol());
            path.push(cur);
            cur = previous.get(cur);
            if (cur == null) {
                break;
            }
        }
        return path;
    }

    public void navigate(Stack<Tile> path) {
        StringBuilder instructions = new StringBuilder();
        Tile cur = path.pop();
        DIRECTION curDir = robot.getDir();
        DIRECTION nextDir;

        while(!path.empty()){
            Tile prev = cur;
            cur = path.pop();
            nextDir = getDir(cur, prev, curDir);
            //System.out.println(cur.getRow() + " " + cur.getCol());
            if (nextDir == curDir){
                //System.out.println("Forward");
                robot.moveFast(MOVEMENT.FORWARD, false, map);
            }else {
                //System.out.println("Turn");
                robot.moveFast(getTurnDirection(curDir, nextDir), false, map);
                robot.moveFast(MOVEMENT.FORWARD, false, map);
                curDir = nextDir;
            }
            map.paintComponent(map.getGraphics());
        }
    }

    public MOVEMENT getTurnDirection(DIRECTION curDir, DIRECTION nextDir) {
        switch (curDir) {
            case NORTH:
                switch (nextDir) {
                    case NORTH:
                        return MOVEMENT.FORWARD;
                    case SOUTH:
                        return MOVEMENT.BACKWARD;
                    case WEST:
                        return MOVEMENT.LEFT;
                    case EAST:
                        return MOVEMENT.RIGHT;
                }
                break;

            case SOUTH:
                switch (nextDir) {
                    case NORTH:
                        return MOVEMENT.BACKWARD;
                    case SOUTH:
                        return MOVEMENT.FORWARD;
                    case WEST:
                        return MOVEMENT.RIGHT;
                    case EAST:
                        return MOVEMENT.LEFT;
                }
                break;

            case WEST:
                switch (nextDir) {
                    case NORTH:
                        return MOVEMENT.RIGHT;
                    case SOUTH:
                        return MOVEMENT.LEFT;
                    case WEST:
                        return MOVEMENT.FORWARD;
                    case EAST:
                        return MOVEMENT.BACKWARD;
                }
                break;

            case EAST:
                switch (nextDir) {
                    case NORTH:
                        return MOVEMENT.LEFT;
                    case SOUTH:
                        return MOVEMENT.RIGHT;
                    case WEST:
                        return MOVEMENT.BACKWARD;
                    case EAST:
                        return MOVEMENT.FORWARD;
                }
        }
        return MOVEMENT.ERROR;
    }

}