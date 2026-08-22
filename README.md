# Dungeons Online

A multiplayer terminal dungeon crawler. One server hosts an 11×11 map; up to nine players
connect over TCP, explore, collect items, fight minions and each other.

Client and server talk over a line-based JSON protocol. Every action a player takes is sent
as a request, applied by the server, and broadcast back to everyone as a new game state plus
a short message.

## Requirements

- **JDK 25** — the project uses instance `main` methods and flexible constructor bodies
- **Maven 3.9+**
- **A real terminal** — the client puts the console into raw mode through JLine.
  It will not work in the IntelliJ Run window; use Terminal, PowerShell, or the IntelliJ
  *Terminal* tab.
- **A large terminal window** — at least **190 columns × 60 rows**. The map alone is 133
  columns wide and the side panel starts at column 137.

## Build

```bash
mvn clean package
```

To run from the command line you also need the runtime dependencies next to the classes:

```bash
mvn dependency:copy-dependencies -DincludeScope=runtime
```

## Running

Start the server first, then one client per player, each in its own terminal window.

**Server:**

```bash
java -cp "target/classes;target/dependency/*" bg.sofia.uni.fmi.mjt.dungeonsonline.server.EntryPoint
```

**Client:**

```bash
java -cp "target/classes;target/dependency/*" bg.sofia.uni.fmi.mjt.dungeonsonline.client.PlayerClient
```

On Linux and macOS replace the `;` in the classpath with `:`.

If `java` is not on your `PATH`, use the JDK directly — in PowerShell:

```bash
& "$env:JAVA_HOME\bin\java.exe" -cp "target/classes;target/dependency/*" bg.sofia.uni.fmi.mjt.dungeonsonline.server.EntryPoint
```

From IntelliJ you can instead run `EntryPoint` and `PlayerClient` as ordinary run
configurations, but the client must be started in the **Terminal** tab, not the Run window.

The server listens on **port 4444** and accepts **9 players**. The client connects to
`localhost:4444`. Both are constants at the top of `GameServer` / `PlayerClient` if you need
to change them. A tenth connection is rejected with a "server is full" notice.

## Controls

| Key | Action |
| --- | --- |
| `W` `A` `S` `D` or arrow keys | Move one tile |
| `1`–`9`, `0` | Select backpack slot (`0` selects slot 10) |
| `E` | Use the selected item — or attack, if something is on your tile |
| `F` | Pick up a treasure from your tile |
| `G` | Give the selected item to a player on your tile |
| `R` | Drop the selected item on the ground |
| `Esc` | Quit the game |

`E`, `F` and `G` open a chooser when there is one or more candidate on your tile:

| Key | Action |
| --- | --- |
| `↑` `↓` | Cycle through the candidates |
| `Enter` | Confirm |
| `Q` | Cancel |

Pressing `E` on an empty tile attacks with bare hands, which does nothing useful — it only
matters when a minion or another player is standing with you.

## Reading the map

| Symbol | Meaning |
| --- | --- |
| `1`–`9` | A player, by id. Yours is highlighted in yellow |
| `M` | A minion |
| `X` | An obstacle — you cannot walk through it |
| `⚔` | A weapon lying on the ground |
| `⚗` | A spell |
| `❤` | A health potion |
| `✦` | A mana potion |

Your stats, backpack and the recent messages are shown in the panel to the right of the map.

## Rules

**Starting out.** You spawn at the top-left corner at level 1 with 100 health, 100 mana,
50 attack, 50 defense and an empty 10-slot backpack.

**Damage.** An attack deals `attack − defense / 2` damage, never less than zero. Your attack
is your base attack plus the attack of the weapon in your selected slot, if you have one
selected.

**Items.** Weapons and spells have a level requirement and are refused if you are too low.
A spell costs mana and hits *every* other living actor on your tile at once — useful when
you are surrounded, dangerous when a teammate is standing next to you. Potions are consumed
when used: health potions restore health, mana potions restore mana.

**Experience.** Picking up a treasure gives 20 XP; killing a minion gives `50 + 5 × (level − 1)`.
Every 100 XP is a level, and each level adds +10 max health, +10 max mana, +5 attack and
+5 defense. Levelling up raises your maximums but does not heal you. Items dropped by dead
players give no XP.

**Minions.** They spawn at levels 1 to 5. A level 1 minion has 100 health, 50 attack and
50 defense; each level above that adds +10 health, +5 attack and +5 defense. Kill one and a
replacement spawns at a random free tile.

**Dying.** You drop one random item from your backpack, respawn at the starting corner with
full health and mana, and keep your level and everything else you were carrying.

**Trading.** Stand on the same tile as another player, select an item and press `G` to hand
it over. Their backpack has to have a free slot.

## What is on the map

| Item | Kind | Level | Effect |
| --- | --- | --- | --- |
| Sword | Weapon | 1 | +20 attack |
| Axe | Weapon | 2 | +35 attack |
| Excalibur | Weapon | 4 | +70 attack |
| Spark | Spell | 1 | 30 damage, 20 mana |
| Fireball | Spell | 2 | 50 damage, 40 mana |
| Bandage ×2 | Health potion | — | +30 health |
| Elixir of life | Health potion | — | +60 health |
| Mana flask | Mana potion | — | +30 mana |
| Arcane brew | Mana potion | — | +60 mana |

Eight minions of levels 1 to 5 are placed at fixed positions when the server starts.

## Tests

```bash
mvn test
```

## Logs

Both sides write to a `logs/` directory next to where they were started — `server.log.*` for
the server and `client-<n>.log` for each client. The client prints only a short message on
failure and points at these files.

## Layout

```
shared/    protocol shared by both sides — requests, responses, DTOs, JSON mappers
server/    engine (map, actors, items, combat), connection handling, request routing
client/    socket listener, JLine console, input loop, renderers
```

`GameEngineImpl` holds all mutable game state and every public method on it is
`synchronized`, so the engine is the single lock in the server. Connections are handled on
virtual threads.
