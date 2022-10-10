package partie2;

public class Edge {
    private Node departNode;
    private Node arriveNode;
    
    public Edge(Node departNode, Node arriveNode) {
        super();
        this.departNode = departNode;
        this.arriveNode = arriveNode;
    }

    public Node getDepartNode() {
        return departNode;
    }

    public void setDepartNode(Node departNode) {
        this.departNode = departNode;
    }

    public Node getArriveNode() {
        return arriveNode;
    }

    public void setArriveNode(Node arriveNode) {
        this.arriveNode = arriveNode;
    }
    
    
}
