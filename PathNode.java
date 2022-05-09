package pathsolver;

public class PathNode {

    //this class make path nodes
    //on path nodes it draws 2D array for int
    //returns cost and level
	public PathNode topNode;
	public int[][] nodesSet;
	public int nodePt1, nodePt2;
	public int cost;
	public int level;
	
        //path node class is storing node set
        //this is overloaded constructor to store item nodes
        //returns total path nodes
	public PathNode(int[][] nodesSet, int nodePt1, int nodePt2, int newX, int newY, int level, PathNode topNode) {
		this.topNode = topNode;
		this.nodesSet = new int[nodesSet.length][];
		for (int i = 0; i < nodesSet.length; i++) {
			this.nodesSet[i] = nodesSet[i].clone();
		}
		
		this.nodesSet[nodePt1][nodePt2]       = this.nodesSet[nodePt1][nodePt2] + this.nodesSet[newX][newY];
		this.nodesSet[newX][newY] = this.nodesSet[nodePt1][nodePt2] - this.nodesSet[newX][newY];
		this.nodesSet[nodePt1][nodePt2]       = this.nodesSet[nodePt1][nodePt2] - this.nodesSet[newX][newY];
		
		this.cost = Integer.MAX_VALUE;
		this.level = level;
		this.nodePt1 = newX;
		this.nodePt2 = newY;
	}
	
}