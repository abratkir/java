package gauss;
/**
 * @author Robert Abratkiewicz
 */
public class Item {
    private int column;
    private double value;
    
    public Item (int column, double value) {
        this.column = column;
        this.value = value;
    }
    
    public int getColumn () {
        return column;
    }
    
    public double getValue () {
        return value;
    }
    
    public void update (double value) {
        this.value = value;
    }
    public void add (double value) {
        this.value -= value;
    }
}
