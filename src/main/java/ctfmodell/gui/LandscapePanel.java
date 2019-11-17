package ctfmodell.gui;

import ctfmodell.model.Landscape;
import ctfmodell.model.enums.DirectionEnum;
import ctfmodell.model.enums.FieldEnum;
import ctfmodell.model.exception.PoliceException;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.util.Pair;


@SuppressWarnings("ConstantConditions")
public class LandscapePanel extends Region {

    private Landscape landscape;
    private Canvas fieldCanvas;
    private Canvas iconCanvas;
    private Pair[][] landscapeCoordinates;
    private static final int RECT_SIZE = 35;
    private static final int GAP_SIZE = 1;
    private static final int TOTAL_RECT_SIZE = RECT_SIZE + GAP_SIZE;
    private static final double ICON_PADDING = 7.5;

    public LandscapePanel() {
        this(new Landscape(), 500, 500);
    }

    public LandscapePanel(Landscape landscape, double prefHeight, double prefWidth) {
        this.landscape = landscape;
        this.landscapeCoordinates = new Pair[landscape.getLandscape().length][landscape.getLandscape()[0].length];
        this.prefHeight(prefWidth);
        this.prefWidth(prefHeight);

        iconCanvas = new Canvas(getCanvasWidth(), getCanvasHeight());
        iconCanvas.setOnMousePressed(canvasOnMousePressedEventHandler);
        iconCanvas.setOnMouseDragged(canvasOnMouseDraggedEventHandler);
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

    public void draw() {
        this.clear();
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
                landscapeCoordinates[y][x] = new Pair<>(posY, posX);
            }
            posX = 0;
            landscapeCoordinates[y][0] = new Pair<>(posY, posX);
            posY += RECT_SIZE + GAP_SIZE;
        }
        System.out.println(counter);

    }

    public void clear() {
        GraphicsContext fieldGc = fieldCanvas.getGraphicsContext2D();
        GraphicsContext iconGc = iconCanvas.getGraphicsContext2D();
        fieldGc.clearRect(0,0, fieldCanvas.getWidth(), fieldCanvas.getHeight());
        iconGc.clearRect(0,0, iconCanvas.getWidth(), iconCanvas.getHeight());
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

    public Landscape getLandscape() {
        return this.landscape;
    }

    boolean dragged;
    int originX, originY;

    Pair<Integer, Integer> originFieldYX, destinationFieldYX;

    EventHandler<MouseEvent> canvasOnMousePressedEventHandler = new EventHandler<MouseEvent>()
    {
        @Override
        public void handle(MouseEvent mouseEvent)
        {
            originX = (int) mouseEvent.getX();
            originY = (int) mouseEvent.getY();
            originFieldYX = getFieldByCoordinates(originY, originX);
            System.out.println("Pressed Coordinates: "+ originY +"-"+ originX);
            if(originFieldYX != null) {
                FieldEnum field = landscape.getLandscape()[originFieldYX.getKey()][originFieldYX.getValue()];
                System.out.println("Pressed: Field found: "+field.name());
                if(field == FieldEnum.POLICE_OFFICER || field == FieldEnum.OFFICER_AND_BASE || field == FieldEnum.OFFICER_AND_FLAG) {
                    dragged = true;
                } else {
                    dragged = false;
                }
            } else {
                dragged = false;
            }

        }
    };


    EventHandler<MouseEvent> canvasOnMouseDraggedEventHandler = new EventHandler<MouseEvent>()
    {
        @Override
        public void handle(MouseEvent mouseEvent)
        {
            if(dragged) {

                int newX = (int) mouseEvent.getX();
                int newY = (int) mouseEvent.getY();
                destinationFieldYX = getFieldByCoordinates(newY, newX);
                System.out.println("dragging: "+newY+"-"+newX);
                if(destinationFieldYX != null) {
                    FieldEnum field = landscape.getLandscape()[destinationFieldYX.getKey()][destinationFieldYX.getValue()];

                    //Ist origin Pair ungleich translate Pair?
                    if(!originFieldYX.getKey().equals(destinationFieldYX.getKey()) || !originFieldYX.getValue().equals(destinationFieldYX.getValue())) {
                        // Wenn ja, prüfe ob verschieben möglich ist
                        System.out.println("ungleich");
                        if (field == FieldEnum.EMPTY || field == FieldEnum.BASE || field == FieldEnum.FLAG) {
                            // Wenn möglich, lösche Akteur aus altem Feld und füge ins neue ein
                            landscape.getLandscape()[originFieldYX.getKey()][originFieldYX.getValue()] = FieldEnum.EMPTY;
                            landscape.getLandscape()[destinationFieldYX.getKey()][destinationFieldYX.getKey()] = FieldEnum.POLICE_OFFICER;
                            draw();

                        } else {
                            System.err.println("Police Officer kann nicht auf dieses Feld gesetzt werden!");
                        }
                    }

                }

            }
        }
    };

    private Pair<Integer, Integer> getFieldByCoordinates(int yPos, int xPos) {
        Pair<Integer, Integer> pairToFind = new Pair<>(yPos, xPos);
        for (int y = 0; y < landscapeCoordinates.length; y++) {
            for(int x = 0; x < landscapeCoordinates[y].length; x++) {
                if(pairToFind.equals(landscapeCoordinates[y][x])){
                    return new Pair<>(y,x);
                }
            }
        }

        return null;
    }


}
