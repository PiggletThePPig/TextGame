package game;

import java.util.Random;

public class Game {
	private int x;
	private int y;
	private char[] combatBiomes = {'F', 'D', 'M'};
	
	private Random rand = new Random();
	private int[] weightedEnemyChance = {
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			1, 1, 1, 1, 1, 1, 1, 1, 
			2, 2, 2, 2, 2,
			3, 3, 3,
			4};
	
	char[][] map = {
		    {'M', 'M', 'M', 'M', 'M', 'F', 'F', 'F', 'F', 'F'},
		    {'M', 'M', 'M', 'M', 'F', 'F', 'F', 'F', 'P', 'F'},
		    {'C', 'M', 'M', 'F', 'F', 'F', 'F', 'F', 'F', 'D'},
		    {'M', 'M', 'M', 'F', 'F', 'F', 'F', 'F', 'F', 'D'},
		    {'M', 'M', 'F', 'F', 'F', 'F', 'T', 'F', 'D', 'D'},
		    {'M', 'M', 'F', 'F', 'I', 'F', 'F', 'F', 'D', 'D'},
		    {'F', 'A', 'F', 'F', 'F', 'F', 'F', 'D', 'D', 'D'},
		    {'F', 'F', 'F', 'F', 'F', 'F', 'F', 'D', 'D', 'D'},
		    {'F', 'F', 'F', 'F', 'F', 'F', 'F', 'D', 'D', 'D'},
		    {'F', 'F', 'F', 'F', 'F', 'F', 'W', 'D', 'D', 'D'}};
	
	Enemy[][] mobs = {
		    // Forest mobs (easier)
		    {
		        new Enemy(9, 2, 1, "Goblin"),
		        new Enemy(3, 3, 2, "Pixie"),
		        new Enemy(8, 4, 1, "Wolf"),
		        new Enemy(6, 4, 1, "Kobold"),
		        new Enemy(15, 3, 4, "Treant")
		    },
		    
		    // Desert mobs (slightly more difficult)
		    {
		        new Enemy(8, 4, 2, "Sand Serpent"),
		        new Enemy(9, 4, 2, "Fire Beetle"),
		        new Enemy(10, 5, 2, "Scorpion"),
		        new Enemy(14, 5, 4, "Mummy"),
		        new Enemy(18, 6, 4, "Desert Raider")
		    },
		    
		    // Mountain mobs (difficult)
		    {
		        new Enemy(20, 9, 4, "Troll"),
		        new Enemy(22, 10, 5, "Griffin"),
		        new Enemy(25, 8, 6, "Wyvern"),
		        new Enemy(28, 6, 8, "Yeti"),
		        new Enemy(30, 6, 10, "Rock Golem")
		    }
		};
	
	Enemy dragon = new Enemy(35, 12, 14, "The Dragon");

	
	private Player player;
	private int delay = 500;
	
	public Game(String playerName) {
		x = 5;
		y = 5;
		player = new Player(playerName);
	}
	
	public Game(String playerName, int code) {
		x = 5;
		y = 5;
		player = new Player(playerName, 0);
	}
	
	public void printInfo() {
		System.out.println("You are in" + tileInfo(map[y][x]));
		System.out.println();
		if(atShop()) {
			printCost();
			System.out.println(player.getName() + " gold: " + player.getGold());
			System.out.println();;
		}
		else if(atPM()) {
			System.out.println("Would you like to buy a potion for 2 gold? B to buy.");
			System.out.println(player.getName() + " gold: " + player.getGold());
			System.out.println();
		}
		else if(atWizard())
			if(!player.hasKey()) {
				System.out.println("Hello, " + player.getName() + "! To prove your worth, come to me with 250 gold and I will give you"
						+ "\nthe legendary key to enter the cave and slay the dragon! B to buy");
				System.out.println(player.getName() + " gold: " + player.getGold());
				System.out.println();
			}
			else
				System.out.println("I've already given you the legendary key! "
						+ "\nQuit dallying and go face the tyrant Dragon.\n");
		else if(atCave()) {
			if(player.hasKey()) {
				System.out.println("You unlock the gate to the cave, and enter into the depths . . .");
				combat(dragon);
			} else {
				System.out.println("The cave is blocked off by a locked steel gate. Find the key to enter.");
			}
		}
		System.out.print("To the North is"); // North
		if(y > 0)
			System.out.println(tileInfo(map[y-1][x]));
		else
			System.out.println(" nothing.");
		System.out.print("To the South is"); // South
		if(y < 9)
			System.out.println(tileInfo(map[y+1][x]));
		else
			System.out.println(" nothing.");
		System.out.print("To the West is"); // West
		if(x > 0)
			System.out.println(tileInfo(map[y][x-1]));
		else
			System.out.println(" nothing.");
		System.out.print("To the East is"); // East
		if(x < 9)
			System.out.println(tileInfo(map[y][x+1]));
		else
			System.out.println(" nothing.");
		System.out.println();
	}
	
	public void printMap() {
		for(int i = 0; i < 10; i++) {
			System.out.println();
			for(int j = 0; j < 10; j++) {
				if(i == y  && x == j)
					System.out.print("X   "); // Print player location
				else
					System.out.print(map[i][j] + "   "); // Print map
			}
			System.out.println();
		}
	}
	
	private String tileInfo(char tile) {
		switch(tile) {
		case 'F':
			return " a forest.";
		case 'M':
			return " the mountains.";
		case 'D':
			return " a desert.";
		case 'A':
			return " the armory.";
		case 'W':
			return " the smithy.";
		case 'P':
			return " the potion master's hut.";
		case 'I':
			return " the inn.";
		case 'T':
			return " the wizard's tower.";
		case 'C':
			return " the cave entrance.";
		default:
			throw new IllegalArgumentException("No such tile.");
		}
	}
	
	public void move(char action) {
		
		switch(action){
		case 'W':
			if(y > 0) {
				y--;
				encounter();
				printMap();
				System.out.println();
				printInfo();
				System.out.println();
				break;
			}
			System.out.println("You cannot move there");
			System.out.println();
			break;
		case 'S':
			if(y < 9) {
				y++;
				encounter();
				printMap();
				System.out.println();
				printInfo();
				System.out.println();
				break;
			}
			System.out.println("You cannot move there");
			System.out.println();
			break;
		case 'A':
			if(x > 0) {
				x--;
				encounter();
				printMap();
				System.out.println();
				printInfo();
				System.out.println();
				break;
			}
			System.out.println("You cannot move there");
			System.out.println();
			break;
		case 'D':
			if(x < 9) {
				x++;
				encounter();
				printMap();
				System.out.println();
				printInfo();
				System.out.println();
				break;
			}
			System.out.println("You cannot move there");
			System.out.println();
			break;
		case 'M':
			printMap();
			System.out.println();
			printInfo();
			System.out.println();
			break;
		case 'K':
			printHelp();
		case 'H':
			if(player.getPotions() > 0) {
				player.heal();
				System.out.println("You use a potion to heal. You now have " + player.getPotions() + " potions.");
				System.out.println(player.getName() + " health: " + player.getHealthBar());
			} else
				System.out.println("You are out of heal potions! Visit the Potion Master to buy more.");
			break;
		case 'B':
			if(atShop()) {
				upgrade();
				System.out.println(player.getName() + " gold: " + player.getGold());
				printCost();
			} else if(atPM()) {
				player.buyPotion();
				System.out.println(player.getName() + " gold: " + player.getGold());
			} else if(atWizard()) {
				player.buyKey();
				System.out.println(player.getName() + " gold: " + player.getGold());
			}
			else
				System.out.println("You cannot do that now!");
			break;
		case 'P':
			printPlayerStats();
			break;
		case 'Q':
			System.out.println("Goodbye.");
			break;

		// - - - - - - - - Cheat Codes - - - - - - - - - -
		case '5':
			System.out.println("You get gold.");
			player.addGold(100);
			System.out.println(player.getName() + " gold: " + player.getGold());
			break;
		case '1':
			player.upgrade(0);
			break;
		case '2':
			player.upgrade(1);
			break;
		case '3':
			player.upgrade(2);
			break;
		case '4':
			player.buyPotion();
			break;
		default:
			System.err.println("Move unexpected case");
			break;
		}
	}

	public void printHelp() {
		System.out.println();
		System.out.println("Type a letter and enter to take an action.");
		System.out.println("Move around the map to encounter enemies. Combat is automatic.");
		System.out.println("Upgrade your equipment and buy more poitions to kill harder enemies.");
		System.out.println("X -> Your location");
		System.out.println("F -> Forest (Easiest enemies, least gold)");
		System.out.println("D -> Desert (Medium enemies, more gold)");
		System.out.println("M -> Mountain (Hardest enemies, most gold)");
		System.out.println("W -> Weaponsmith (Upgrade weapon)");
		System.out.println("A -> Armorer (Upgrade armor)");
		System.out.println("I -> Inn (Upgrade amulet)");
		System.out.println("P -> Potion Master (Refill potions)");
		System.out.println("T -> Wizard's Tower");
		System.out.println("C -> Dragon's Lair\n");
	}

	private void printPlayerStats() {
		System.out.println(player.getName() + " stats:");
		System.out.println("Health: " + player.getHealthBar());
		System.out.println("Weapon: " + player.getEquipment(0) + "(" + player.getAttack() + ")");
		System.out.println("Armor: " + player.getEquipment(1) + "(" + player.getDefense() + ")");
		System.out.println("Amulet: " + player.getEquipment(2) + "(" + player.getMaxHealth() + ")");
		System.out.println("Gold: " + player.getGold());
		System.out.println("Potions: " + player.getPotions());
	}

	private void upgrade() {
		if(map[y][x] == 'W')
			player.upgrade(0);
		else if(map[y][x] == 'A')
			player.upgrade(1);
		else if(map[y][x] == 'I')
			player.upgrade(2);
	}
	
	private void printCost() {
		if(map[y][x] == 'W')
			if(!player.isMaxUpgraded(0))
				System.out.println("Would you like to upgrade your weapon for " + player.getEquipmentCost(0) + " gold? B to buy.");
			else
				System.out.println("You already have " + player.getEquipment(0));
		else if(map[y][x] == 'A')
			if(!player.isMaxUpgraded(1))
				System.out.println("Would you like to upgrade your armor for " + player.getEquipmentCost(1) + " gold? B to buy.");
			else
				System.out.println("You already have " + player.getEquipment(1));
		else if(map[y][x] == 'I')
			if(!player.isMaxUpgraded(2))
				System.out.println("Would you like to upgrade your max health for " + player.getEquipmentCost(2) + " gold? B to buy.");
			else
				System.out.println("You already have " + player.getEquipment(2));
	}

	private void encounter() {
		boolean combatE = false;
		for(int i = 0; i < combatBiomes.length; i++) {
			if(combatBiomes[i] == map[y][x]) {
				combatE = true;
				break;
			}
		}
		
		if(combatE) {
			double combatC = (Math.random() * 10);
			if(combatC < 2) {
				combat();
			}
		}
	}

	private void combat() {
		int difficulty = 0;
		for(int i = 0; i < combatBiomes.length; i++) {
			if(map[y][x] == combatBiomes[i])
				difficulty = i;
		}
		
		int mobSelector = weightedEnemyChance[rand.nextInt(weightedEnemyChance.length)];
		Enemy e = new Enemy(mobs[difficulty][mobSelector]);
		System.out.println("You find a " + e.getName() + " blocking your path!");
		System.out.println("Fight!");
		System.out.println();
		while(e.getHealth() > 0 && player.getHealth() > 0) {
			try {
				System.out.println("You attack the " + e.getName() + ".");
				Thread.sleep(delay);
				System.out.println("You dealt " + e.takeDamage(player.dealDamage()) + " damage.");
				if(e.getHealth() <= 0)
					break;
				Thread.sleep(delay);
				System.out.println("The " + e.getName() + " attacks you.");
				Thread.sleep(delay);
				System.out.println("You took " + player.takeDamage(e.dealDamage()) + " damage.");
				Thread.sleep(delay);
				System.out.println(player.getName() + " health: " + player.getHealthBar());
				Thread.sleep(delay);
			} catch (InterruptedException e1) {
				System.err.println("Sleep interrupted");
				e1.printStackTrace();
			}
		}
		if(e.getHealth() <= 0) {
			System.out.println();
			System.out.println("You killed the " + e.getName() + "!");
			int loot = 2 + 6 * difficulty + 2 * mobSelector + rand.nextInt(2);
			player.addGold(loot);
			System.out.println("You looted " + loot + " gold from its body.");
			System.out.println(player.getName() + " gold: " + player.getGold());
			System.out.println(player.getName() + " health: " + player.getHealthBar());
			System.out.println();
		}
		else {
			System.out.println();
			System.out.println("You died! Game over!");
			System.exit(0);
		}
	}
	
	private void combat(Enemy e) {
		System.out.println("You find " + e.getName() + " guarding his lair!");
		System.out.println("Fight!");
		System.out.println();
		while(e.getHealth() > 0 && player.getHealth() > 0) {
			try {
				System.out.println("You attack " + e.getName() + ".");
				Thread.sleep(delay);
				System.out.println("You dealt " + e.takeDamage(player.dealDamage()) + " damage.");
				if(e.getHealth() <= 0)
					break;
				Thread.sleep(delay);
				System.out.println(e.getName() + " attacks you.");
				Thread.sleep(delay);
				System.out.println("You took " + player.takeDamage(e.dealDamage()) + " damage.");
				Thread.sleep(delay);
				System.out.println(player.getName() + " health: " + player.getHealthBar());
				Thread.sleep(delay);
			} catch (InterruptedException e1) {
				System.err.println("Sleep interrupted");
				e1.printStackTrace();
			}
		}
		if(e.getHealth() <= 0) {
			System.out.println();
			System.out.println("You killed " + e.getName() + "!");
			System.out.println("You looted " + 1000 + " gold from its lair.");
			player.addGold(1000);
			System.out.println(player.getName() + " gold: " + player.getGold());
			System.out.println();
			gameWon();
		}
		else {
			System.out.println();
			System.out.println("You died! Game over!");
			System.exit(0);
		}
	}
	
	private void gameWon() {
		System.out.println("Congratulations! You've defeated the dragon and rid the land of evil!"
				+ "\nYou also made a tidy profit in the process! ");
		printPlayerStats();
		System.exit(0);
	}

	public boolean atShop() {
		if(map[y][x] == 'A' || map[y][x] == 'W' || map[y][x] == 'I')
			return true;
		return false;
	}
	
	public boolean atPM() {
		if(map[y][x] == 'P')
			return true;
		return false;
	}
	
	private boolean atWizard() {
		if(map[y][x] == 'T')
			return true;
		return false;
	}
	
	private boolean atCave() {
		if(map[y][x] == 'C')
			return true;
		return false;
	}

	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
}
