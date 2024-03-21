package dk.sdu.mmmi.cbse.common.entitysegments;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;

public class RenderingSegment implements EntitySegment{
    private double[] polygonCoordinates;
    private int[] rgb = new int[3];

    public RenderingSegment() {
        rgb[0] = 255;
        rgb[1] = 255;
        rgb[2] = 255;
    }

    public void setPolygonCoordinates(double... coordinates ) {
        this.polygonCoordinates = coordinates;
    }

    public void setPolygonArray(double[] arr) {
        this.polygonCoordinates = arr;
    }
    public double[] getPolygonCoordinates() {
        return polygonCoordinates;
    }

    public void setColor(int r, int g, int b) {
        rgb[0] = r;
        rgb[1] = g;
        rgb[2] = b;
    }

    public void setColor(int[] color) {
        if (color.length == 3)
            rgb = color;
    }
    public int[] getColor() {
        return rgb;
    }
    @Override
    public void process(GameData gameData, Entity entity) {

    }
}
