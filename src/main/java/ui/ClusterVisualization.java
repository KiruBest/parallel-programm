package ui;//import require classes and packages

import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;
import java.util.ArrayList;

public class ClusterVisualization extends JPanel {

    public ClusterVisualization(ArrayList<Double> cordX, ArrayList<Double> cordY, ArrayList<Color> colors) {
        this.cordX = cordX;
        this.cordY = cordY;
        this.colors = colors;
    }

    ArrayList<Double> cordX;
    ArrayList<Double> cordY;

    ArrayList<Color> colors;
    int marg = 60;

    Graphics2D graph;

    protected void paintComponent(Graphics grf) {
        //create instance of the Graphics to use its methods
        super.paintComponent(grf);
        graph = (Graphics2D) grf;

        //Sets the value of a single preference for the rendering algorithms.  
        graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // get width and height  
        int width = getWidth();
        int height = getHeight();

        // draw graph  
        graph.draw(new Line2D.Double(marg, marg, marg, height - marg));
        graph.draw(new Line2D.Double(marg, height - marg, width - marg, height - marg));

        //find value of scale to plot points
        double xScale = (double) (width - 2 * marg) / getMax(cordX);
        double yScale = (double) (height - 2 * marg) / getMax(cordY);

        //set color for points  
        graph.setPaint(Color.RED);

        // set points to the graph  
        for (int i = 0; i < cordX.size(); i++) {
            graph.setPaint(colors.get(i));
            double x1 = marg + xScale * cordX.get(i);
            double y1 = height - marg - yScale * cordY.get(i);
            graph.fill(new Ellipse2D.Double(x1, y1, 4, 4));
        }
    }

    //getMax() method to find maximum value
    private double getMax(ArrayList<Double> list) {
        double max = -Double.MAX_VALUE;
        for (Double cord : list) {
            if (cord > max)
                max = cord;

        }
        return max;
    }

    //main() method start  
    public void draw(ArrayList<Double> cordX, ArrayList<Double> cordY, ArrayList<Color> colors) {
        //create an instance of JFrame class  
        JFrame frame = new JFrame();
        // set size, layout and location for frame.  
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ClusterVisualization(cordX, cordY, colors));
        frame.setSize(600, 600);
        frame.setLocation(200, 200);
        frame.setVisible(true);
    }
}  