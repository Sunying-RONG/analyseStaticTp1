package partie2;

import java.util.ArrayList;
import java.util.List;

public class CallGraph {
    private List<Node> nodeList = new ArrayList<Node>(); // no need
    private List<Edge> edgeList = new ArrayList<Edge>();
    
    public CallGraph() {
    }
    
    public CallGraph(List<Node> nodeList, List<Edge> edgeList) {
        super();
        this.nodeList = nodeList;
        this.edgeList = edgeList;
    }
    
    public List<Node> getNodeList() {
        return nodeList;
    }
    public void setNodeList(List<Node> nodeList) {
        this.nodeList = nodeList;
    }
    public List<Edge> getEdgeList() {
        return edgeList;
    }
    public void setEdgeList(List<Edge> edgeList) {
        this.edgeList = edgeList;
    }
}
