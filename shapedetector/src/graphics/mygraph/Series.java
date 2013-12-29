package graphics.mygraph;
import java.awt.Color;
import java.util.ArrayList;

import std.StdDraw;

/**
 *
 * @author Sean
 */
public final class Series<E extends Object> extends ArrayList<E> {
	private static final long serialVersionUID = 1L;
	
    protected Color colour = Color.black;
    protected String label = "";
    
    public Series() {
    }
    
    public Series(String label, Color clr) {
        setLabel(label);
        setColor(clr);
    }
    
    public void setColor(Color colour) {
        this.colour = colour;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
//    public void add(double d) {
//        data.add(d);
//    }
    
    public double getMax() {
        double max = 0;
        for (int i = 0; i < size(); i++) {
          if ((Double) get(i) > max) {
              max = (Double) get(i);
          }
        }
//        for (int i = 0; i < data.size(); i++) {
//          if (data.get(i).doubleValue() > max) {
//              max = data.get(i).doubleValue();
//          }
//        }
        return max;
    }
    
//    public double size() {
//        return data.size();
//    }
//    
//    public double get(int i) {
//        return data.get(i);
//    }
}
