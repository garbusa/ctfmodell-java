package ctfmodell;

import ctfmodell.model.Flag;
import ctfmodell.model.Landscape;
import ctfmodell.model.PoliceOfficer;
import ctfmodell.model.enums.DirectionEnum;

import java.util.Scanner;

public class Aufgabenblatt02 {

    public static void main(String... args) {
        Landscape landscape = new Landscape(5, 8, 7, 4);
        PoliceOfficer policeOfficer = new PoliceOfficer(0, 0, DirectionEnum.SOUTH);
        policeOfficer.setLandscape(landscape);
        landscape.setPoliceOfficer(policeOfficer);
        Flag flagOne = new Flag(2, 2);
        Flag flagTwo = new Flag(3, 3);

        landscape.addFlag(flagOne);
        landscape.addFlag(flagTwo);
        landscape.addUnarmedTerrorist(0, 1);
        landscape.addArmedTerrorist(1, 1);
        landscape.addArmedTerrorist(1, 0);

        Scanner scanner = new Scanner(System.in);
        while (!policeOfficer.hasWon()) {
            printBoard(landscape);
            System.out.println("actions: (f) forward | (l) turn left | (a) attack | (p) pick up | (d) drop all");
            String input = scanner.nextLine().toLowerCase();
            try {
                if (input.length() == 1) {
                    if (input.contains("f")) {
                        policeOfficer.forward();
                    } else if (input.contains("l")) {
                        policeOfficer.turnLeft();
                    } else if (input.contains("a")) {
                        policeOfficer.attack();
                    } else if (input.contains("p")) {
                        policeOfficer.pick();
                    } else if (input.contains("d")) {
                        policeOfficer.drop();
                    } else {
                        System.out.println("ungültige Eingabe");
                    }
                } else {
                    System.out.println("ungültige Eingabe");
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }

        }
        System.out.println("Mission erledigt!");

    }

    private static void printBoard(Landscape landscape) {
        // Print Field
        DirectionEnum direction = landscape.getPoliceOfficer().getDirection();
        for (int x = 0; x < landscape.getLandscape().length; x++) {
            for (int y = 0; y < landscape.getLandscape()[x].length; y++) {
                switch (landscape.getLandscape()[x][y]) {
                    case POLICE_OFFICER:
                        if (direction == DirectionEnum.NORTH) System.out.print("^ ");
                        if (direction == DirectionEnum.WEST) System.out.print("< ");
                        if (direction == DirectionEnum.SOUTH) System.out.print("V ");
                        if (direction == DirectionEnum.EAST) System.out.print("> ");
                        break;
                    case FLAG:
                        System.out.print("F ");
                        break;
                    case OFFICER_AND_FLAG:
                        if (direction == DirectionEnum.NORTH) System.out.print("^&F ");
                        if (direction == DirectionEnum.WEST) System.out.print("<&F ");
                        if (direction == DirectionEnum.SOUTH) System.out.print("V&F ");
                        if (direction == DirectionEnum.EAST) System.out.print(">&F ");
                        break;
                    case UNARMED_TERRORIST:
                        System.out.print("UT ");
                        break;
                    case ARMED_TERRORIST:
                        System.out.print("AT ");
                        break;
                    case BASE:
                        System.out.print("B ");
                        break;
                    case OFFICER_AND_BASE:
                        if (direction == DirectionEnum.NORTH) System.out.print("^&B ");
                        if (direction == DirectionEnum.WEST) System.out.print("<&B ");
                        if (direction == DirectionEnum.SOUTH) System.out.print("V&B ");
                        if (direction == DirectionEnum.EAST) System.out.print(">&B ");
                        break;
                    default:
                        System.out.print("0 ");
                }
            }
            System.out.println();
        }
        System.out.println("------------------------");
    }

}
