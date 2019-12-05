package ctfmodell.view;

import ctfmodell.model.Landscape;
import ctfmodell.model.PoliceOfficer;
import ctfmodell.model.enums.Direction;
import ctfmodell.model.enums.Field;
import ctfmodell.util.GraphicSize;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import java.util.Observable;
import java.util.Observer;


@SuppressWarnings("ConstantConditions")
public class LandscapePanel extends Region implements Observer {

    private Canvas fieldCanvas;
    private Canvas iconCanvas;
    private Landscape landscape;

    public LandscapePanel(Landscape landscape) {
        this(landscape, 500, 500);
    }

    private LandscapePanel(Landscape landscape, double prefHeight, double prefWidth) {
        this.landscape = landscape;
        this.prefHeight(prefWidth);
        this.prefWidth(prefHeight);

        iconCanvas = new Canvas(getCanvasWidth(), getCanvasHeight());
        fieldCanvas = new Canvas(getCanvasWidth(), getCanvasHeight());

        this.draw();
        this.getChildren().add(fieldCanvas);
        this.getChildren().add(iconCanvas);

    }

    private int getCanvasWidth() {
        return this.landscape.getWidth() * GraphicSize.TOTAL_RECT_SIZE;
    }

    private int getCanvasHeight() {
        return this.landscape.getHeight() * GraphicSize.TOTAL_RECT_SIZE;
    }

    private void draw() {
        this.clear();
        GraphicsContext fieldGc = fieldCanvas.getGraphicsContext2D();
        GraphicsContext iconGc = iconCanvas.getGraphicsContext2D();

        int posX = 0;
        int posY = 0;
        for (int y = 0; y < this.landscape.getLandscape().length; y++) {
            for (int x = 0; x < this.landscape.getLandscape()[y].length; x++) {
                Field field = this.landscape.getLandscape()[y][x];
                Color c = (field == Field.BASE || field == Field.OFFICER_AND_BASE) ? Color.RED : Color.GREY;

                Image image = getIcon(field);
                iconGc.drawImage(image, posX + GraphicSize.ICON_PADDING, posY + GraphicSize.ICON_PADDING);

                fieldGc.setFill(c);
                fieldGc.fillRoundRect(posX, posY, GraphicSize.RECT_SIZE, GraphicSize.RECT_SIZE, 10, 10);
                posX += GraphicSize.RECT_SIZE + GraphicSize.GAP_SIZE;
            }
            posX = 0;
            posY += GraphicSize.RECT_SIZE + GraphicSize.GAP_SIZE;

        }

    }

    private void clear() {
        GraphicsContext fieldGc = fieldCanvas.getGraphicsContext2D();
        GraphicsContext iconGc = iconCanvas.getGraphicsContext2D();
        fieldGc.clearRect(0, 0, fieldCanvas.getWidth(), fieldCanvas.getHeight());
        iconGc.clearRect(0, 0, iconCanvas.getWidth(), iconCanvas.getHeight());
    }

    private Image getIcon(Field field) {
        Direction direction = this.landscape.getPoliceOfficer().getDirection();
        Image imageToPrint = null;
        switch (field) {
            case OFFICER_AND_BASE:
            case POLICE_OFFICER:
                if (direction == Direction.NORTH)
                    imageToPrint = new Image(getClass().getClassLoader().getResourceAsStream("image/menu/police_north.png"));
                if (direction == Direction.WEST)
                    imageToPrint = new Image(getClass().getClassLoader().getResourceAsStream("image/menu/police_west.png"));
                if (direction == Direction.SOUTH)
                    imageToPrint = new Image(getClass().getClassLoader().getResourceAsStream("image/menu/police_south.png"));
                if (direction == Direction.EAST)
                    imageToPrint = new Image(getClass().getClassLoader().getResourceAsStream("image/menu/police_east.png"));
                break;
            case FLAG:
                imageToPrint = new Image(getClass().getClassLoader().getResourceAsStream("image/menu/flag.png"));
                break;
            case OFFICER_AND_FLAG:
                if (direction == Direction.NORTH)
                    imageToPrint = new Image(getClass().getClassLoader().getResourceAsStream("image/menu/police_flag_north.png"));
                if (direction == Direction.WEST)
                    imageToPrint = new Image(getClass().getClassLoader().getResourceAsStream("image/menu/police_flag_west.png"));
                if (direction == Direction.SOUTH)
                    imageToPrint = new Image(getClass().getClassLoader().getResourceAsStream("image/menu/police_flag_south.png"));
                if (direction == Direction.EAST)
                    imageToPrint = new Image(getClass().getClassLoader().getResourceAsStream("image/menu/police_flag_east.png"));
                break;
            case UNARMED_TERRORIST:
                imageToPrint = new Image(getClass().getClassLoader().getResourceAsStream("image/menu/terrorist.png"));
                break;
            case ARMED_TERRORIST:
                imageToPrint = new Image(getClass().getClassLoader().getResourceAsStream("image/menu/terrorist_2.png"));
                break;
        }

        return imageToPrint;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (!(arg instanceof PoliceOfficer))
            this.landscape = (Landscape) o;
            Platform.runLater(() -> {
                this.iconCanvas = new Canvas(getCanvasWidth(), getCanvasHeight());
                this.fieldCanvas = new Canvas(getCanvasWidth(), getCanvasHeight());
                this.draw();
                this.getChildren().clear();
                this.getChildren().add(fieldCanvas);
                this.getChildren().add(iconCanvas);
            });

    }
}
