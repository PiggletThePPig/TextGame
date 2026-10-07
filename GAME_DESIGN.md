# Game Design Document: RPG Reference Specification

This document provides a comprehensive, exhaustive specification of the RPG mechanics, architecture, map layout, bestiary, item progression, and combat system as implemented in `src/game/`.

---

## 1. System Architecture & Object Hierarchy

### Class Structure Overview
- **`GameCharacter`** (Base Class)
  - Attributes: `health` (int), `attack` (int), `defense` (int), `name` (String), `rand` (Random).
- **`Player`** extends `GameCharacter`
  - Attributes: `gold` (int), `maxHealth` (int), `potions` (int), `key` (int), `maxedOut` (boolean), `numUpgrades` (int), `upgradeCost` (int), `maxUpgraded` (boolean[3]), `equipment` (int[3]).
- **`Enemy`** extends `GameCharacter`
  - Attributes: Inherits base stats; includes copy constructor for instantiating mobs.
- **`Game`** (Engine & World Manager)
  - Attributes: `x` (int), `y` (int), `map` (char[10][10]), `mobs` (Enemy[3][5]), `dragon` (Enemy), `player` (Player), `combatBiomes` (char[]), `weightedEnemyChance` (int[]), `delay` (int = 500ms).
- **`GameApp`** (CLI Input/Output Loop)
  - Entry point (`main`), handles user input commands and loop state.

---

## 2. Core Mechanics & Formulas

### 2.1 Character Damage & Defense Calculation
- **Damage Dealt (`dealDamage`)**:
  $$\text{Damage Output} = \text{Attack} + \text{Random}(-3, +2)$$
  *(Using `rand.nextInt(6) - 3`)*
- **Damage Taken (`takeDamage`)**:
  $$\text{Damage Taken} = \max(0, \text{Incoming Damage} - \text{Defense})$$
  $$\text{Health}_{\text{new}} = \max(0, \text{Health}_{\text{current}} - \text{Damage Taken})$$

---

### 2.2 Player Progression & Equipment Mechanics

#### Starting Stats
- **Standard Constructor (`Player(name)`)**:
  - `Health`: 10 / `MaxHealth`: 10
  - `Attack`: 3
  - `Defense`: 1
  - `Gold`: 0
  - `Potions`: 5
  - `Key`: 0 (`false`)
  - Starting position on map: $(x=5, y=5)$
- **Alternative Constructor (`Player(name, code)`)**:
  - `Health`: 10 / `MaxHealth`: 10
  - `Attack`: 3
  - `Defense`: 2
  - `Gold`: 2
  - `Potions`: 6

#### Equipment Slots & Upgrades
There are 3 equipment slots (`equipment[0..2]`), each maxing out at tier index 4:

| Slot Index | Type | Tiers Array | Stat Formula on Upgrade |
| :--- | :--- | :--- | :--- |
| **0** | Weapon | `["fist", "shiv", "short sword", "mace", "the Vanquisher"]` | $\text{Attack} = 3 + (\text{Tier} \times 2)$ |
| **1** | Armor | `["rawhide", "Goblin tusk", "chainmail", "spikemail", "Hero's mail"]` | $\text{Defense} = 1 + (\text{Tier} \times 1)$ |
| **2** | Amulet | `["no amulet", "dull amulet", "polished amulet", "radiant amulet", "magic amulet"]` | $\text{Max Health} = 10 + (\text{Tier} \times 5)$ |

#### Upgrade Cost Formula
The cost to upgrade slot $i$ depends on current tier in slot $i$ and total number of upgrades performed across all slots (`numUpgrades`):
$$\text{Cost}(i) = \lfloor 2^{\text{equipment}[i]} \times 6 + (\text{numUpgrades} \times 3) \rfloor$$

#### Full Upgrade Synergy Set Bonus ("Legendary Hero's Gear")
When all 3 slots reach Tier 4 (`maxedOut` condition):
- Bonus Attack: $+5$
- Bonus Defense: $+5$
- Bonus Max Health: $+10$
- Immediately fully heals player to new `maxHealth`.

---

### 2.3 Potions & Healing
- **Potion Healing (`heal()`)**: Restores $+5$ HP up to `maxHealth`, decrements potion count by 1.
- **Buying Potions (`buyPotion()`)**: Costs $2$ Gold for $+1$ potion (at Potion Master `P`).
- **Full Heal (`fullHeal()`)**: Sets `health = maxHealth`.

---

## 3. World Map & Navigation

### 10x10 World Map Grid
Coordinates: `y` = Row (0 to 9, top to bottom), `x` = Column (0 to 9, left to right). Player starts at $(x=5, y=5)$.

```
     0   1   2   3   4   5   6   7   8   9  (x)
0   [M] [M] [M] [M] [M] [F] [F] [F] [F] [F]
1   [M] [M] [M] [M] [F] [F] [F] [F] [P] [F]
2   [C] [M] [M] [F] [F] [F] [F] [F] [F] [D]
3   [M] [M] [M] [F] [F] [F] [F] [F] [F] [D]
4   [M] [M] [F] [F] [F] [F] [T] [F] [D] [D]
5   [M] [M] [F] [F] [I] [F] [F] [F] [D] [D]  <-- Player starts at (5,5)
6   [F] [A] [F] [F] [F] [F] [F] [D] [D] [D]
7   [F] [F] [F] [F] [F] [F] [F] [D] [D] [D]
8   [F] [F] [F] [F] [F] [F] [F] [D] [D] [D]
9   [F] [F] [F] [F] [F] [F] [W] [D] [D] [D]
(y)
```

### Tile Legend & Interactions

| Tile Code | Name | Function / Interaction |
| :--- | :--- | :--- |
| **`F`** | Forest | Easy Combat Biome |
| **`D`** | Desert | Medium Combat Biome |
| **`M`** | Mountain | Hard Combat Biome |
| **`W`** | Weaponsmith | Shop: Buy Weapon Upgrades |
| **`A`** | Armorer | Shop: Buy Armor Upgrades |
| **`I`** | Inn | Shop: Buy Amulet (Max Health) Upgrades |
| **`P`** | Potion Master | Shop: Buy Potions for 2 Gold (`B` to buy) |
| **`T`** | Wizard's Tower | NPC: Buy Legendary Key for 250 Gold (`B` to buy) |
| **`C`** | Cave Entrance | Boss Dungeon: Boss encounter with **The Dragon** (Requires Legendary Key) |

---

## 4. Encounters & Combat System

### 4.1 Encounter Rate
Moving onto any combat biome tile (`F`, `D`, `M`) triggers a random roll:
- Probability of random combat encounter per move: **20%** (`Math.random() * 10 < 2`).

---

### 4.2 Weighted Enemy Selection Logic
When combat triggers, an enemy index (0 to 4) is selected from a weighted pool of size 30:
$$\text{Weighted Array} = [\underbrace{0,\dots,0}_{14 \text{ times}}, \underbrace{1,\dots,1}_{8 \text{ times}}, \underbrace{2,\dots,2}_{5 \text{ times}}, \underbrace{3,\dots,3}_{3 \text{ times}}, \underbrace{4}_{1 \text{ time}}]$$

- Enemy Tier 0: 46.67% chance
- Enemy Tier 1: 26.67% chance
- Enemy Tier 2: 16.67% chance
- Enemy Tier 3: 10.00% chance
- Enemy Tier 4: 3.33% chance

---

### 4.3 Bestiary / Enemy Stats Table

| Biome (Difficulty Index) | Enemy Name | HP | Attack | Defense | Loot Gold Formula |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Forest (`F`, index 0)** | Goblin | 9 | 2 | 1 | $2 + (6 \times 0) + (2 \times 0) + \text{rand}(0..1) = \mathbf{2 - 3}$ |
| | Pixie | 3 | 3 | 2 | $2 + (6 \times 0) + (2 \times 1) + \text{rand}(0..1) = \mathbf{4 - 5}$ |
| | Wolf | 8 | 4 | 1 | $2 + (6 \times 0) + (2 \times 2) + \text{rand}(0..1) = \mathbf{6 - 7}$ |
| | Kobold | 6 | 4 | 1 | $2 + (6 \times 0) + (2 \times 3) + \text{rand}(0..1) = \mathbf{8 - 9}$ |
| | Treant | 15 | 3 | 4 | $2 + (6 \times 0) + (2 \times 4) + \text{rand}(0..1) = \mathbf{10 - 11}$ |
| **Desert (`D`, index 1)** | Sand Serpent | 8 | 4 | 2 | $2 + (6 \times 1) + (2 \times 0) + \text{rand}(0..1) = \mathbf{8 - 9}$ |
| | Fire Beetle | 9 | 4 | 2 | $2 + (6 \times 1) + (2 \times 1) + \text{rand}(0..1) = \mathbf{10 - 11}$ |
| | Scorpion | 10 | 5 | 2 | $2 + (6 \times 1) + (2 \times 2) + \text{rand}(0..1) = \mathbf{12 - 13}$ |
| | Mummy | 14 | 5 | 4 | $2 + (6 \times 1) + (2 \times 3) + \text{rand}(0..1) = \mathbf{14 - 15}$ |
| | Desert Raider | 18 | 6 | 4 | $2 + (6 \times 1) + (2 \times 4) + \text{rand}(0..1) = \mathbf{16 - 17}$ |
| **Mountain (`M`, index 2)**| Troll | 20 | 9 | 4 | $2 + (6 \times 2) + (2 \times 0) + \text{rand}(0..1) = \mathbf{14 - 15}$ |
| | Griffin | 22 | 10 | 5 | $2 + (6 \times 2) + (2 \times 1) + \text{rand}(0..1) = \mathbf{16 - 17}$ |
| | Wyvern | 25 | 8 | 6 | $2 + (6 \times 2) + (2 \times 2) + \text{rand}(0..1) = \mathbf{18 - 19}$ |
| | Yeti | 28 | 6 | 8 | $2 + (6 \times 2) + (2 \times 3) + \text{rand}(0..1) = \mathbf{20 - 21}$ |
| | Rock Golem | 30 | 6 | 10 | $2 + (6 \times 2) + (2 \times 4) + \text{rand}(0..1) = \mathbf{22 - 2 3}$ |
| **Boss (`C`)** | **The Dragon** | **35** | **12** | **14** | **1000 Gold** (Triggers Victory Sequence) |

#### Gold Reward General Formula
$$\text{Loot} = 2 + (6 \times \text{biomeDifficulty}) + (2 \times \text{mobSelector}) + \text{rand}(0, 1)$$

---

### 4.4 Combat Execution Turn Loop
Combat is fully automated:
1. Player attacks enemy $\rightarrow$ Enemy takes damage $\rightarrow$ Check if enemy HP $\le 0$ (if so, player wins).
2. 500ms delay (`Thread.sleep(500)`).
3. Enemy attacks player $\rightarrow$ Player takes damage.
4. Print player current health.
5. Repeat until either HP reaches 0.
- If player HP reaches 0: Output `"You died! Game over!"` and call `System.exit(0)`.

---

## 5. Controls & User Commands

| Key | Input Action | Description |
| :--- | :--- | :--- |
| **`W`** | North | Move 1 tile North ($y - 1$) |
| **`S`** | South | Move 1 tile South ($y + 1$) |
| **`A`** | West | Move 1 tile West ($x - 1$) |
| **`D`** | East | Move 1 tile East ($x + 1$) |
| **`H`** | Heal | Consume 1 potion to restore 5 HP |
| **`B`** | Buy / Upgrade | Buy equipment upgrade at `W`/`A`/`I`, potion at `P`, or key at `T` |
| **`P`** | Stats | Display Player stats, equipment, attack/defense, gold, potions |
| **`M`** | Map | Print the 10x10 map grid and tile info |
| **`K`** | Help | Display legend and controls help |
| **`Q`** | Quit | Exit game |

---

## 6. Development & Debug Cheat Codes

The input switch block includes built-in cheat commands:
- **`5`**: Grants $+100$ Gold instantly.
- **`1`**: Directly triggers Weapon upgrade without checking location or cost.
- **`2`**: Directly triggers Armor upgrade without checking location or cost.
- **`3`**: Directly triggers Amulet upgrade without checking location or cost.
- **`4`**: Directly triggers Potion purchase without checking location (still checks for 2 gold).

---

## 7. Victory Condition & Game Ending
1. Player earns **250 Gold**.
2. Player visits Wizard's Tower (`T` at $x=6, y=4$) and uses `B` to buy the Legendary Key.
3. Player travels to Cave Entrance (`C` at $x=0, y=2$).
4. Cave gate unlocks, triggering boss fight against **The Dragon** (HP 35, ATK 12, DEF 14).
5. Defeating the Dragon awards 1000 gold, outputs victory text, prints final player stats, and exits the game cleanly.
