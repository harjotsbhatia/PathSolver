package pathsolver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;

public class PathSolver {

    //initial data storage
    public int arrSize = 3;
    String path = "";
    int nodesCount = 0;

    int[] horizontalIdx = {1, 0, -1, 0};
    int[] verticalIdx = {0, -1, 0, 1};

    //this funciton loops over a node
    //find total paths this node can travel
    //finds total effort of travel
    //returns effort count
    public int findEffortOfNode(int[][] starterNodes, int[][] endingNodes) {
        int indexerNodes = 0;
        int totalNodes = starterNodes.length;
        for (int iter1 = 0; iter1 < totalNodes; iter1++) {
            for (int iter2 = 0; iter2 < totalNodes; iter2++) {
                if (starterNodes[iter1][iter2] != 0 && starterNodes[iter1][iter2] != endingNodes[iter1][iter2]) {
                    indexerNodes++;
                    this.nodesCount++;
                }
            }
        }
        return indexerNodes;
    }

    //this display all nodes
    public void displayNodes(int[][] nodesSet) {
        for (int iter1 = 0; iter1 < nodesSet.length; iter1++) {
            for (int iter2 = 0; iter2 < nodesSet.length; iter2++) {
                System.out.print(nodesSet[iter1][iter2] + " ");
            }
            System.out.println();
        }
    }
    
    //this check if a solution move is possible or not
    public boolean canMoveToNode(int nodePt1, int nodePt2) {
        return (nodePt1 >= 0 && nodePt1 < arrSize && nodePt2 >= 0 && nodePt2 < arrSize);
    }

    //this path calcualtes and reutrn total node ways
    public void showNodeWay(PathNode root) {
        if (root == null) {
            return;
        }
        showNodeWay(root.topNode);
        displayNodes(root.nodesSet);
        System.out.println();
    }

    //this method gets a node paths,
    //makes a list of path
    //chooses the bets path and returns true if possible
    public boolean canGetNodeResult(int[][] nodesSet) {
        int indexerNodes = 0;
        List<Integer> array = new ArrayList<Integer>();

        for (int iter1 = 0; iter1 < nodesSet.length; iter1++) {
            for (int iter2 = 0; iter2 < nodesSet.length; iter2++) {
                array.add(nodesSet[iter1][iter2]);
            }
        }

        Integer[] anotherArray = new Integer[array.size()];
        array.toArray(anotherArray);

        for (int iter1 = 0; iter1 < anotherArray.length - 1; iter1++) {
            for (int iter2 = iter1 + 1; iter2 < anotherArray.length; iter2++) {
                if (anotherArray[iter1] != 0 && anotherArray[iter2] != 0 && anotherArray[iter1] > anotherArray[iter2]) {
                    indexerNodes++;
                }
            }
        }

        return indexerNodes % 2 == 0;
    }

    //this method picks the starter node
    //using that node, it moves to every other node
    //makes a priority queue of their paths
    //using that key, this method dequeues one item at a  time
    //pushes that item to link list
    //and returns a total list of paths
    public void solve(int[][] starterNodes, int[][] endingNodes, int nodePt1, int nodePt2) {
        PriorityQueue<PathNode> nodeIdxr = new PriorityQueue<PathNode>(1000, (a, b) -> (a.cost + a.level) - (b.cost + b.level));
        PathNode root = new PathNode(starterNodes, nodePt1, nodePt2, nodePt1, nodePt2, 0, null);
        root.cost = findEffortOfNode(starterNodes, endingNodes);
        nodeIdxr.add(root);

        while (!nodeIdxr.isEmpty()) {
            PathNode leastNode = nodeIdxr.poll();
            if (leastNode.cost == 0) {
                showNodeWay(leastNode);
                return;
            }

            for (int iter1 = 0; iter1 < 4; iter1++) {
                if (canMoveToNode(leastNode.nodePt1 + horizontalIdx[iter1], leastNode.nodePt2 + verticalIdx[iter1])) {
                    if (horizontalIdx[iter1] == -1) {
                        path += "u";
                    } else if (horizontalIdx[iter1] == 1) {
                        path += "d";
                    } else if (verticalIdx[iter1] == -1) {
                        path += "l";
                    } else if (verticalIdx[iter1] == 1) {
                        path += "r";
                    }
                    PathNode child = new PathNode(leastNode.nodesSet, leastNode.nodePt1, leastNode.nodePt2, leastNode.nodePt1 + horizontalIdx[iter1], leastNode.nodePt2 + verticalIdx[iter1], leastNode.level + 1, leastNode);
                    child.cost = findEffortOfNode(child.nodesSet, endingNodes);
                    nodeIdxr.add(child);
                }
            }
        }
    }

    private int moves = 0;
    private SearchNode finalNode;
    private Stack<Board> boards;

    //thsi is overloaded cosntrucotr
    //this sets up initial empty board
    public PathSolver(Board initial) {
        if (initial!=null) {
            if (!initial.isSolvable()) {
                throw new IllegalArgumentException("Unsolvable puzzle");
            }
            PriorityQueue<SearchNode> minPQ = new PriorityQueue<SearchNode>(initial.size() + 10);

            Set<Board> previouses = new HashSet<Board>(50);
            Board dequeuedBoard = initial;
            Board previous = null;
            SearchNode dequeuedNode = new SearchNode(initial, 0, null);
            Iterable<Board> boards;

            while (!dequeuedBoard.isGoal()) {
                boards = dequeuedBoard.neighbors();
                moves++;

                for (Board board : boards) {
                    if (!board.equals(previous) && !previouses.contains(board)) {
                        minPQ.add(new SearchNode(board, moves, dequeuedNode));
                    }
                }

                previouses.add(previous);
                previous = dequeuedBoard;
                dequeuedNode = minPQ.poll();
                dequeuedBoard = dequeuedNode.current;
            }
            finalNode = dequeuedNode;
        }
    }

// min number of moves to solve initial board
    public int moves() {
        if (boards != null) {
            return boards.size() - 1;
        }
        solution();
        return boards.size() - 1;
    }

    public Iterable<Board> solution() {
        if (boards != null) {
            return boards;
        }
        boards = new Stack<Board>();
        SearchNode pointer = finalNode;
        while (pointer != null) {
            boards.push(pointer.current);
            pointer = pointer.previous;
        }
        return boards;
    }

    //this is search node method
    //it compares two nodes
    //choose best one to move on
    //moves with the selected node
    private class SearchNode implements Comparable<SearchNode> {

        private final int priority;
        private final SearchNode previous;
        private final Board current;

        public SearchNode(Board current, int moves, SearchNode previous) {
            this.current = current;
            this.previous = previous;
            this.priority = moves + current.manhattan();
        }

        @Override
        public int compareTo(SearchNode that) {
            int cmp = this.priority - that.priority;
            return Integer.compare(cmp, 0);
        }

    }

    //main method
    public static void main(String[] args) {
        
        //this solution implmenets BFS and DFS on above functions
        //it first checks if solution is possible with DFS
        //if possible, it moves with DFS
        //otherwise it checks with BFS and moves with that
        //in both cases it displays path and cost
        String input = "123450678";
        int[][] starterNodes = new int[3][3];
        int[][] endingNodes = {{1, 2, 3}, {4, 5, 6}, {7, 8, 0}};
        int nodePt1 = 0, nodePt2 = 0;
        int iter2 = 0;
        for (int iter1 = 0; iter1 < input.length(); iter1++) {
            starterNodes[iter2][iter1 % 3] = Integer.parseInt(String.valueOf(input.charAt(iter1)));
            if (starterNodes[iter2][iter1 % 3] == 0) {
                nodePt1 = iter2;
                nodePt2 = iter1 % 3;
            }
            if (iter1 == 2 || iter1 == 5) {
                iter2++;
            }
        }
        for (int iter1 = 0; iter1 < 3; iter1++) {
            for (int k = 0; k < 3; k++) {
                System.out.print(starterNodes[iter1][k] + " ");
            }
            System.out.println();
        }

        PathSolver puzzle = new PathSolver(null);
        if (puzzle.canGetNodeResult(starterNodes)) {
            puzzle.solve(starterNodes, endingNodes, nodePt1, nodePt2);
            System.out.println("Test: " + input);
            System.out.println("Goal: 123456780");
            System.out.println("nodes expanded: " + puzzle.nodesCount);
            System.out.println(puzzle.path);
        } else {
            System.out.println("The given starterNodes is impossible to solve");
        }

        System.out.println("A * solution");
        int[][] tiles = {{4, 1, 3},
        {0, 2, 6},
        {7, 5, 8}};

        //this solutions is calling an A* algorithm
        //the function solve here is impleneting A*
        //it returns the total ndoes, and the time taken
        double start = System.currentTimeMillis();
        Board board = new Board(tiles);
        PathSolver solve = new PathSolver(board);
        System.out.printf("nodes expanded: %d\n total nodes: %d\n  time: %f\n, ", solve.moves(), solve.moves, (System.currentTimeMillis() - start) / 1000);
    }

}
