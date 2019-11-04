package ctfmodell.gui;

import ctfmodell.model.Landscape;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;


public class LandscapePanel extends Region {

    private Landscape landscape;
    private Canvas canvas;
    private static final int rectSize = 30;

    public LandscapePanel(Landscape landscape, double prefHeight, double prefWidth) {
        this.landscape = landscape;
        this.prefHeight(prefWidth);
        this.prefWidth(prefHeight);
        canvas = new Canvas(getCanvasWidth(), getCanvasHeight());
        this.draw();
        this.getChildren().add(canvas);
    }

    private int getCanvasWidth() {
        return this.landscape.getWidth() * rectSize;
    }

    private int getCanvasHeight() {
        return this.landscape.getHeight() * rectSize;
    }

    private void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.strokeRect(0, 0, getCanvasWidth(), getCanvasHeight());
        gc.setFill(Color.GREY);

        int posX = 1;
        int posY = 1;
        for(int y = 0; y < this.landscape.getLandscape().length; y++) {

            for(int x = 0; x < this.landscape.getLandscape()[y].length; x++) {
                gc.fillRoundRect(posX, posY, rectSize, rectSize, 10, 10);
            }

        }

    }

}
