package gauss;
/**
 * @author Robert Abratkiewicz
 */
public class Node {
    private Node left = null;
    private Node right = null;
    private Item elements = null;
    
    public Node (int column, double value) {
        elements = new Item (column, value);
    }
    
    public int getColumn () {
        return elements.getColumn();
    }
    
    public double getValue () {
        return elements.getValue();
    }
    
    public Node getLeft () {
        return left;
    }
    
    public Node getRight () {
        return right;
    }
    
    public void update (double value) {
        elements.update(value);
    }
    
    public void addValue (double value) {
        elements.add(value);
    }
    public void addLeft (int column, double value) {
        left = new Node(column, value);
    }
    
    public void addRight (int column, double value) {
        right = new Node(column, value);
    }
}
