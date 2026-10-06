package game;

public class Player extends GameCharacter {
	private int gold;
	private int maxHealth;
	private int potions;
	private int key;
	private boolean maxedOut;
	private int numUpgrades;
	private int upgradeCost;
	private boolean[] maxUpgraded = new boolean[3];
	
	private String[] weapons = {"fist", "shiv", "short sword", "mace", "the Vanquisher"};
	private String[] armors = {"rawhide", "Goblin tusk", "chainmail", "spikemail", "Hero's mail"};
	private String[] amulets = {"no amulet", "dull amulet", "polished amulet", "radiant amulet", "magic amulet"};
	private int[] equipment = {0, 0, 0};
	
	public Player(String name) {
		super(10, 3, 1);
		gold = 0;
		this.setName(name);
		this.maxHealth = this.getHealth();
		potions = 5;
		key = 0;
		numUpgrades = 0;
		maxedOut = false;
		for(int i = 0; i < maxUpgraded.length; i++)
			maxUpgraded[i] = false;
	}
	
	public Player(String name, int code) {
		super(10, 3, 2);
		gold = 2;
		this.setName(name);
		this.maxHealth = this.getHealth();
		potions = 6;
		key = 0;
		numUpgrades = 0;
		maxedOut = false;
		upgradeCost = 5;
	}
	
	public void addGold(int loot) {
		gold += loot;
	}
	
	public void spendGold(int cost) {
		gold -= cost;
	}
	
	public int getGold() {
		return gold;
	}
	
	public void addHealth(int heal) {
		this.health += heal;
		if(health > maxHealth)
			health = maxHealth;
	}
	
	public void heal() {
		this.health += 5;
		this.potions--;
		if(health > maxHealth)
			health = maxHealth;
	}
	
	public int getPotions() {
		return potions;
	}
	
	public void fullHeal() {
		health = maxHealth;
	}
	
	public String getHealthBar() {
		return health + " / " + maxHealth;
	}
	
	public int getMaxHealth() {
		return maxHealth;
	}

	public void upgrade(int i) {
		if(equipment[i] < weapons.length - 1) {
			int cost = getEquipmentCost(i);
			if(gold >= cost) {
				equipment[i]++;
				if(equipment[i] == 4)
					maxUpgraded[i] = true;
				switch(i) {
				case 0:
					this.setAttack(3 + equipment[i] * 2);
					System.out.println("You upgraded your weapon! You are now fighting with: " + getEquipment(i));
					System.out.println("Attack: " + this.getAttack());
					break;
				case 1:
					this.setDefense(1 + equipment[i] * 1);
					System.out.println("You upgraded your armor! You are now protected by: " + getEquipment(i));
					System.out.println("Defense: " + this.getDefense());
					break;
				case 2:
					maxHealth = 10 + equipment[i] * 5;
					System.out.println("You upgraded your amulet! You are now wearing: " + getEquipment(i));
					System.out.println("Health: " + this.getHealthBar());
					break;
				}
				spendGold(cost);
				numUpgrades++;
				if(isMaxedOut()) {
					System.out.println("You've obtained the legendary hero's gear! You feel much stronger.");
					this.setAttack(this.getAttack() + 5);
					this.setDefense(this.getDefense() + 5);
					maxHealth += 10;
					this.health = maxHealth;
				}
			} else {
				System.out.println("You cannot afford that!");
			}
		} else
			System.out.println("You've already purchased the max upgrade!");
	}
	
	public int getEquipmentCost(int i) {
		return (int) (Math.pow(2, equipment[i]) * 6 + (numUpgrades * 3));
	}

	private boolean isMaxedOut() {
		for(int i = 0; i < equipment.length; i++) {
			if(equipment[i] < weapons.length - 1)
				return false;
		}
		return true;
	}

	public String getEquipment(int i) {
		switch(i) {
		case 0:
			return weapons[equipment[i]];
		case 1:
			return armors[equipment[i]];
		case 2:
			return amulets[equipment[i]];
		default:
			return null;
		}
	}

	public void buyPotion() {
		if(gold >= 2) {
			potions++;
			System.out.println("You buy a potion. You now have: " + potions + " potions.");
			gold -= 2;
		} else
			System.out.println("You don't have enough gold for that!");
	}

	public void buyKey() {
		if(gold >= 250 && !hasKey()) {
			System.out.println("You've purchased the legendary key! You can now enter the cave.\n");
			key++;
			gold -= 250;
		}else if(hasKey())
			System.out.println("You already have the legendary key, you fool!\n");
		else
			System.out.println("You cannot afford the legendary key yet! "
					+ "\nCome back after you've slain more monsters.\n");
	}
	
	public boolean hasKey() {
		if(key >= 1)
			return true;
		return false;
	}
	
	public int getNumUpgrades() {
		return numUpgrades;
	}
	
	public boolean isMaxUpgraded(int i) {
		return maxUpgraded[i];
	}
}
