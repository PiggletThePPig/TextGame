package game;

import java.util.Random;

public class GameCharacter {
	public int health;
	private int attack;
	private int defense;
	private String name;
	Random rand = new Random();
	
	public GameCharacter(int health, int attack, int defense) {
		this.health = health;
		this.attack = attack;
		this.defense = defense;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int takeDamage(int incoming) {
		int damageTaken;
		if(incoming >= defense)
			damageTaken = (incoming - defense);
		else
			damageTaken = 0;
		health -= damageTaken;
		if(health < 0)
			health = 0;
		return damageTaken;
	}
	
	public int getHealth() {
		return health;
	}
	
	public int dealDamage() {
		int vary = rand.nextInt(6);
		vary -= 3;
		return attack + vary;
	}
	
	public void setAttack(int attack) {
		this.attack = attack;
	}
	
	public int getAttack() {
		return attack;
	}
	
	public int getDefense() {
		return defense;
	}
	
	public void setDefense(int defense) {
		this.defense = defense;
	}
	
	public String getName() {
		return name;
	}
}
