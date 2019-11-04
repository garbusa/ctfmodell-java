package ctfmodell.gui;

import ctfmodell.model.Landscape;
import ctfmodell.model.enums.DirectionEnum;
import ctfmodell.model.enums.FieldEnum;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;


@SuppressWarnings("ConstantConditions")
public class LandscapePanel extends Region {

    private Landscape landscape;
    private Canvas fieldCanvas;
    private Canvas iconCanvas;
    private static final int RECT_SIZE = 35;
    private static final int GAP_SIZE = 1;
    private static final int TOTAL_RECT_SIZE = RECT_SIZE + GAP_SIZE;
    private static final double ICON_PADDING = 7.5;

    public LandscapePanel(Landscape landscape, double prefHeight, double prefWidth) {
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
        return this.landscape.getWidth() * TOTAL_RECT_SIZE;
    }

    private int getCanvasHeight() {
        return this.landscape.getHeight() * TOTAL_RECT_SIZE;
    }

    private void draw() {
        GraphicsContext fieldGc = fieldCanvas.getGraphicsContext2D();
        GraphicsContext iconGc = iconCanvas.getGraphicsContext2D();

        int posX = 0;
        int posY = 0;
        int counter = 0;
        for(int y = 0; y < this.landscape.getLandscape().length; y++) {
            for(int x = 0; x < this.landscape.getLandscape()[y].length; x++) {
                FieldEnum field = this.landscape.getLandscape()[y][x];
                Color c = (field == FieldEnum.BASE || field == FieldEnum.OFFICER_AND_BASE) ? Color.RED : Color.GREY;

                Image image = getIcon(field);
                iconGc.drawImage(image,  posX + ICON_PADDING, posY + ICON_PADDING);

                fieldGc.setFill(c);
                fieldGc.fillRoundRect(posX, posY, RECT_SIZE, RECT_SIZE, 10, 10);
                posX += RECT_SIZE + GAP_SIZE;
            }
            posX = 0;
            posY += RECT_SIZE + GAP_SIZE;
        }
        System.out.println(counter);

    }

    private Image getIcon(FieldEnum field) {
        DirectionEnum direction = this.landscape.getPoliceOfficer().getDirection();
        Image imageToPrint = null;
        switch (field) {
            case OFFICER_AND_BASE:
            case POLICE_OFFICER:
                if (direction == DirectionEnum.NORTH)
                    imageToPrint = new Image(getClass().getClassLoader().getResourceAsStream("image/menu/police_north.png"));
                if (direction == DirectionEnum.WEST)
                    imageToPrint = new Image(getClass().getClassLoader().getResourceAsStream("image/menu/police_west.png"));
                if (direction == DirectionEnum.SOUTH)
                    imageToPrint = new Image(getClass().getClassLoader().getResourceAsStream("image/menu/police_south.png"));
                if (direction == DirectionEnum.EAST)
                    imageToPrint = new Image(getClass().getClassLoader().getResourceAsStream("image/menu/police_east.png"));
                break;
            case FLAG:
                imageToPrint = new Image(getClass().getClassLoader().getResourceAsStream("image/menu/flag.png"));
                break;
            case OFFICER_AND_FLAG:
                if (direction == DirectionEnum.NORTH)
                    imageToPrint = new Image(getClass().getClassLoader().getResourceAsStream("image/menu/police_flag_north.png"));
                if (direction == DirectionEnum.WEST)
                    imageToPrint = new Image(getClass().getClassLoader().getResourceAsStream("image/menu/police_flag_west.png"));
                if (direction == DirectionEnum.SOUTH)
                    imageToPrint = new Image(getClass().getClassLoader().getResourceAsStream("image/menu/police_flag_south.png"));
                if (direction == DirectionEnum.EAST)
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

}
