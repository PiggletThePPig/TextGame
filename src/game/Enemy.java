package game;

public class Enemy extends GameCharacter{
	
	public Enemy(int health, int attack, int defense, String name) {
		super(health, attack, defense);
		this.setName(name);
	}
	
	public Enemy(Enemy e) {
		super(e.getHealth(), e.getAttack(), e.getDefense());
		this.setName(e.getName());
	}
}
