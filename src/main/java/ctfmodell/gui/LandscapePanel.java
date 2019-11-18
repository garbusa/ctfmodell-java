package ctfmodell.gui;

import ctfmodell.model.Flag;
import ctfmodell.model.Landscape;
import ctfmodell.model.enums.DirectionEnum;
import ctfmodell.model.enums.FieldEnum;
import ctfmodell.model.exception.LandscapeException;
import ctfmodell.util.PixelRectangle;
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

    private PixelRectangle[][] landscapeCoordinates;

    private static final int RECT_SIZE = 35;
    private static final int GAP_SIZE = 1;
    private static final int TOTAL_RECT_SIZE = RECT_SIZE + GAP_SIZE;
    private static final double ICON_PADDING = 7.5;

    private boolean moveEnabled = false;
    private FieldEnum itemToAdd = FieldEnum.OUT_OF_FIELD;
    private boolean deleteEnabled = false;

    public LandscapePanel() {
        this(new Landscape(), 500, 500);
    }

    public void setFieldCanvas(Canvas fieldCanvas) {
        this.fieldCanvas = fieldCanvas;
    }

    public void setIconCanvas(Canvas iconCanvas) {
        this.iconCanvas = iconCanvas;
    }

    public LandscapePanel(Landscape landscape, double prefHeight, double prefWidth) {
        this.landscape = landscape;
        this.landscapeCoordinates = new PixelRectangle[landscape.getLandscape().length][landscape.getLandscape()[0].length];
        this.prefHeight(prefWidth);
        this.prefWidth(prefHeight);

        iconCanvas = new Canvas(getCanvasWidth(), getCanvasHeight());
        iconCanvas.setOnMousePressed(policePressedEventHandler);
        iconCanvas.setOnMouseDragged(policeDraggedEventHandler);
        iconCanvas.setOnMouseClicked(itemEventHandler);
        fieldCanvas = new Canvas(getCanvasWidth(), getCanvasHeight());

        this.draw();
        this.getChildren().add(fieldCanvas);
        this.getChildren().add(iconCanvas);

    }

    public int getCanvasWidth() {
        return this.landscape.getWidth() * TOTAL_RECT_SIZE;
    }

    public int getCanvasHeight() {
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
                landscapeCoordinates[y][x] = new PixelRectangle(posY, posY+RECT_SIZE, posX, posX+RECT_SIZE);
                posX += RECT_SIZE + GAP_SIZE;
            }
            posX = 0;
            posY += RECT_SIZE + GAP_SIZE;

        }
        System.out.println(counter);

    }

    private void clear() {
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

    private boolean dragged;

    private Pair<Integer, Integer> originFieldYX;

    private EventHandler<MouseEvent> policePressedEventHandler = new EventHandler<MouseEvent>()
    {
        @Override
        public void handle(MouseEvent mouseEvent)
        {
            if(isMoveEnabled()){
                int originX = (int) Math.floor(mouseEvent.getX() - 1);
                int originY = (int) Math.floor(mouseEvent.getY() - 1);
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
        }
    };


    private EventHandler<MouseEvent> policeDraggedEventHandler = new EventHandler<MouseEvent>()
    {
        @Override
        public void handle(MouseEvent mouseEvent)
        {
            if(isDragged()) {
                int newX = (int) Math.floor(mouseEvent.getX());
                int newY = (int) Math.floor(mouseEvent.getY());
                Pair<Integer, Integer> destinationFieldYX = getFieldByCoordinates(newY, newX);
                System.out.println("dragging: "+newY+"-"+newX);
                if(destinationFieldYX != null) {
                    FieldEnum field = landscape.getLandscape()[destinationFieldYX.getKey()][destinationFieldYX.getValue()];

                    //Ist origin Pair ungleich translate Pair?
                    if(!originFieldYX.getKey().equals(destinationFieldYX.getKey()) || !originFieldYX.getValue().equals(destinationFieldYX.getValue())) {
                        // Wenn ja, prüfe ob verschieben möglich ist
                        System.out.println("ungleich");
                        if (field == FieldEnum.EMPTY || field == FieldEnum.BASE || field == FieldEnum.FLAG) {
                            // Wenn möglich, lösche Akteur aus altem Feld und füge ins neue ein
                            System.out.println("Hole Akteur aus ("+originFieldYX.getKey()+","+originFieldYX.getValue()+")");
                            System.out.println("Setze in Akteur aus ("+ destinationFieldYX.getKey()+","+ destinationFieldYX.getValue()+")");
                            clearOriginField(originFieldYX.getKey(), originFieldYX.getValue());
                            setDestinationField(destinationFieldYX.getKey(), destinationFieldYX.getValue());
                            originFieldYX = destinationFieldYX;
                            draw();

                        } else {
                            System.err.println("Police Officer kann nicht auf dieses Feld gesetzt werden!");
                        }
                    }

                }

            }
        }
    };


    private EventHandler<MouseEvent> itemEventHandler = new EventHandler<MouseEvent>()
    {
        @Override
        public void handle(MouseEvent mouseEvent)
        {
            FieldEnum item = getItemToAdd();

            int originX = (int) Math.floor(mouseEvent.getX() - 1);
            int originY = (int) Math.floor(mouseEvent.getY() - 1);
            if(!isDeleteEnabled() && item == FieldEnum.OUT_OF_FIELD) return;
            originFieldYX = getFieldByCoordinates(originY, originX);
            System.out.println("(Item Add) Clicked Coordinates: "+ originY +"-"+ originX);
            if(originFieldYX == null) return;

            if(isDeleteEnabled()){
                    FieldEnum field = landscape.getLandscape()[originFieldYX.getKey()][originFieldYX.getValue()];
                    switch (field) {
                        case BASE:
                            System.err.println("Es gibts nichts zu löschen.");
                            break;
                        case OFFICER_AND_BASE:
                        case POLICE_OFFICER:
                            System.err.println("Der Akteur kann nicht gelöscht werden.");
                            break;
                        case FLAG:
                        case UNARMED_TERRORIST:
                        case ARMED_TERRORIST:
                            landscape.getLandscape()[originFieldYX.getKey()][originFieldYX.getValue()] = FieldEnum.EMPTY;
                            break;
                        case OFFICER_AND_FLAG:
                            landscape.getLandscape()[originFieldYX.getKey()][originFieldYX.getValue()] = FieldEnum.FLAG;
                            break;
                    }
                    draw();
            } else if(item != FieldEnum.OUT_OF_FIELD) {
                switch (item) {
                    case FLAG:
                        Flag flagToAdd = new Flag(originFieldYX.getValue(), originFieldYX.getKey());
                        try {
                            landscape.addFlag(flagToAdd);
                            draw();
                        } catch (LandscapeException e) {
                            System.err.println(e.getMessage());
                        }
                        break;
                    case UNARMED_TERRORIST:
                        try {
                            landscape.addUnarmedTerrorist(originFieldYX.getKey(), originFieldYX.getValue());
                            draw();
                        } catch (LandscapeException e) {
                            System.err.println(e.getMessage());
                        }
                        break;
                    case ARMED_TERRORIST:
                        try {
                            landscape.addArmedTerrorist(originFieldYX.getKey(), originFieldYX.getValue());
                            draw();
                        } catch (LandscapeException e) {
                            System.err.println(e.getMessage());
                        }
                        break;
                }
            }

        }
    };


    private void clearOriginField(Integer y, Integer x) {
        FieldEnum field = landscape.getLandscape()[y][x];
        switch (field) {
            case OFFICER_AND_BASE:
                landscape.getLandscape()[y][x] = FieldEnum.BASE;
                break;
            case OFFICER_AND_FLAG:
                landscape.getLandscape()[y][x] = FieldEnum.FLAG;
                break;
            case POLICE_OFFICER:
                landscape.getLandscape()[y][x] = FieldEnum.EMPTY;
        }

    }

    private void setDestinationField(Integer y, Integer x) {
        FieldEnum field = landscape.getLandscape()[y][x];
        landscape.getPoliceOfficer().setyPos(y);
        landscape.getPoliceOfficer().setxPos(x);
        switch (field) {
            case EMPTY:
                landscape.getLandscape()[y][x] = FieldEnum.POLICE_OFFICER;
                break;
            case FLAG:
                landscape.getLandscape()[y][x] = FieldEnum.OFFICER_AND_FLAG;
                break;
            case BASE:
                landscape.getLandscape()[y][x] = FieldEnum.OFFICER_AND_BASE;
        }

    }


    private Pair<Integer, Integer> getFieldByCoordinates(int yPos, int xPos) {
        for (int y = 0; y < landscapeCoordinates.length; y++) {
            for(int x = 0; x < landscapeCoordinates[y].length; x++) {
                if(landscapeCoordinates[y][x].isValidPixel(yPos, xPos)){
                    return new Pair<>(y,x);
                }
            }
        }

        return null;
    }


    public boolean isMoveEnabled() {
        return moveEnabled;
    }

    public void setMoveEnabled(boolean moveEnabled) {
        this.moveEnabled = moveEnabled;
    }

    public FieldEnum getItemToAdd() {
        return itemToAdd;
    }

    public void setItemToAdd(FieldEnum itemToAdd) {
        this.itemToAdd = itemToAdd;
    }

    public boolean isDeleteEnabled() {
        return deleteEnabled;
    }

    public void setDeleteEnabled(boolean deleteEnabled) {
        this.deleteEnabled = deleteEnabled;
    }

    public boolean isDragged() {
        return dragged;
    }

    public void setDragged(boolean dragged) {
        this.dragged = dragged;
    }

    public void setLandscapeCoordinates(PixelRectangle[][] landscapeCoordinates) {
        this.landscapeCoordinates = landscapeCoordinates;
    }

    public void updateCanvasSizeAndDraw() {
        this.iconCanvas = new Canvas(getCanvasWidth(), getCanvasHeight());
        this.fieldCanvas = new Canvas(getCanvasWidth(), getCanvasHeight());
        iconCanvas.setOnMousePressed(policePressedEventHandler);
        iconCanvas.setOnMouseDragged(policeDraggedEventHandler);
        iconCanvas.setOnMouseClicked(itemEventHandler);
        this.draw();
        this.getChildren().clear();
        this.getChildren().add(fieldCanvas);
        this.getChildren().add(iconCanvas);
    }

}
