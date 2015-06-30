package gauss;
/**
 * @author Robert Abratkiewicz
 */
public class Tree {
    private Node ColumnTree = null;
    
    public Tree () {   
    }
    public Tree (int column, double value) {
        ColumnTree = new Node(column, value);
    }
    
    public void addNode (int column, double value) {
        if (ColumnTree == null) ColumnTree = new Node(column, value);
        else addNode(column, value, ColumnTree);
    }
    private void addNode (int column, double value, Node root) {
        if (root.getColumn() > column) {
            if (root.getLeft() != null) addNode(column, value, root.getLeft());
            else root.addLeft(column, value);
        }
        else if (root.getColumn() < column) {
            if (root.getRight() != null) addNode(column,value, root.getRight());
            else root.addRight(column, value);
        }
        else root.update(value);
    }
    public double getValue (int column) {
        if (ColumnTree == null) return 0;
        return getValue(column, ColumnTree);
    }
    private double getValue (int column, Node root) {
        if (root.getColumn() == column) return root.getValue();
        else if ((root.getColumn() > column) && (root.getLeft() != null)) return getValue (column, root.getLeft());
        else if ((root.getColumn() < column) && (root.getRight() != null)) return getValue (column, root.getRight());
        else return 0;
    }
    
    public Node getRoot(){
        return ColumnTree;
    }
    
    public void addValue (double value, int column) {
        if (ColumnTree == null) ColumnTree = new Node(column, -value);
        else addValue(value, column, ColumnTree);
    }
    private void addValue (double value, int column, Node root) {
        if (root.getColumn() == column) root.addValue(value);
        else if (root.getColumn() > column) {
            if (root.getLeft() != null) addValue(value, column, root.getLeft());
            else root.addLeft(column, -value);
        }
        else if (root.getColumn() < column) {
            if (root.getRight() != null) addValue(value, column, root.getRight());
            else root.addRight(column, -value);
        }
    }
        
    public void setRoot(Node newRoot) {
        ColumnTree = newRoot;
    }
}
