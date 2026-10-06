package game;

import java.util.Scanner;

public class GameApp {
	
	public static void main(String[] args) {
		try (Scanner sc = new Scanner(System.in)) {
			System.out.println("Welcome to my game. Kill monsters, collect gold, "
					+ "\nupgrade your equipment, and kill the Dragon!");
			System.out.print("Enter your name: ");
			String name = sc.nextLine();
			Game game = new Game(name);
			game.printHelp();
			game.printMap();
			char input;
			do {
				System.out.println(" - - - - - - - - - - - - - - - - - - ");
				System.out.println();
				System.out.print("Move (W, A, S, D) H to heal, P to check stats, "
						+ "\nM to check map, K for help, Q to Quit: ");
				input = Character.toUpperCase(sc.next().charAt(0));
				System.out.println();
				game.move(input);
			}
			while(input != 'Q');
		}
	}
}
