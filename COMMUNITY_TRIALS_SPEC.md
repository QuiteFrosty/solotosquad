# Community Trials — Java Plugin Specification

**Purpose of this document:** This is the full functional spec for converting the existing "Community Trials" Skript prototype into a proper Java Bukkit/Paper plugin. It captures everything the Skript version does today, plus the new World Border feature discussed but not yet implemented.

**Target platform:** Paper/Purpur 26.2
**Dependencies:** Vault (optional, for economy rewards if desired later), no hard dependency on Skript/SkBee — this is meant to fully replace them.

---

## 1. Core Concept

A democratic server-progression system. The whole community works together toward a shared goal (e.g. "mine 50,000 blocks"). If the server hits the goal before the deadline, the community unlocks the right to vote on a new plugin. If they fail, an admin-configured punishment fires.

Alongside the main goal, players can also complete individual **Side Quests** for personal rewards.

---

## 2. Community Goal System

### Data to track
- Goal name (string)
- Goal target amount (integer)
- Goal current progress (integer, server-wide, shared across all players)
- Goal type — currently only "mine" (blocks broken) is implemented, but the system should track events generically enough to add `kill`, `craft`, `fish` as goal types later
- Deadline (timestamp, default 1 month from goal start)
- Goal active state (boolean)
- Per-player contribution tracking (integer per player, for the `/trials` hub display)

### Behavior
- While goal is active, every relevant block-break event increments `goal::current` and the breaking player's personal contribution counter.
- Every 1000 blocks (or 10% of target, configurable), broadcast a milestone message to the server.
- When `current >= target`:
  - Goal is marked complete/inactive
  - Server-wide success broadcast
  - Plugin vote automatically unlocks (see Section 4)
- If the deadline passes before the target is reached:
  - Goal is marked failed/inactive
  - Configured punishment fires (see below)
- Admin can start/stop/reset the goal at any time via the admin panel.

### Punishment system
- Admin configures (via GUI):
  - A punishment message (broadcast to all players on failure)
  - An optional console command to execute on failure (e.g. `weather thunder`, or any custom command), applied server-wide once (not per-player) unless designed otherwise
- Punishment only fires on failure (deadline passed without reaching target), not on manual admin stop.

---

## 3. Side Quests

Personal (per-player) challenges, separate from the community goal. Admin creates/edits/deletes these via GUI.

### Quest data model
Each quest has:
- Unique ID (string/int)
- Name (display string, supports color codes)
- Description (string)
- Type: one of `mine`, `kill`, `craft`, `fish`, `manual`
  - `mine` — tracks blocks broken (all block types, or optionally a specific type — decide during implementation)
  - `kill` — tracks mobs killed (all living entities, or a specific type)
  - `craft` — tracks items crafted (all recipes, or a specific item)
  - `fish` — tracks fish caught via the fishing event
  - `manual` — progress is never auto-tracked; only admins can grant progress via command
- Target amount (integer)
- Reward item + amount (ItemStack + quantity)
- Active state (boolean — inactive quests don't track progress and don't show in `/sidequests`)

### Per-player quest state
For every (player, quest) pair, track:
- Current progress (integer)
- Completed (boolean, true when progress >= target)
- Claimed (boolean, true once the player has claimed the reward)

### Behavior
- All active quests track progress automatically based on their `type`, for every online player, simultaneously (a player can be working on multiple quests at once).
- `/sidequests` opens a paginated GUI (6 rows) listing all active quests with:
  - Name, description, type, current progress / target
  - Reward preview
  - Claim button (enabled only when completed + not yet claimed)
- Clicking claim on a completed-but-unclaimed quest gives the reward item(s) and marks it claimed.
- Admin can manually grant progress to any player/quest via `/trialsgrant <questID> <player> <amount>` — mainly intended for `manual`-type quests but should work for any quest type as an override/fix tool.

---

## 4. Plugin Democracy System

### Suggestion flow
1. Any player can run `/suggestplugin <name>` to submit an idea.
2. Submission goes into a pending queue, visible to admins in `/trialsadmin` → Plugin Submissions GUI (6 rows, paginated).
3. Admin approves or denies each submission from that GUI.
4. Approved submissions become vote-eligible options.

### Voting flow
1. Voting is **locked** until the community goal succeeds.
2. Once the goal succeeds, admin (or auto-trigger, decide during implementation) opens voting via the Vote Manager admin GUI.
3. Players use `/trials_vote` to open a 3-row GUI showing all approved options; each player can cast one vote.
4. Admin can end voting from the Vote Manager GUI, which tallies votes and announces the winner.
5. Admin can clear/reset vote options for the next cycle.

### Admin Vote Manager GUI controls
- Start vote
- End vote (tally + announce winner)
- View current results (running tally, admin-only)
- Clear all vote options (reset for next cycle)

---

## 5. Commands

### Player-facing
| Command | Description |
|---|---|
| `/trials` | Opens the main hub GUI (4 rows): community goal name/progress/deadline, player's personal contribution, vote status (locked/open/completed), quick links to help and plugin suggestion |
| `/sidequests` | Opens side quest browser GUI (6 rows), shows all active quests, lets player claim completed ones |
| `/suggestplugin <name>` | Submits a plugin name for admin review |
| `/trialshelp` | Prints a formatted help/reference page in chat |
| `/trials_vote` | Opens the plugin vote GUI (3 rows) — only meaningfully usable once voting is open |

### Admin-facing (should be permission-gated, e.g. `communitytrials.admin`)
| Command | Description |
|---|---|
| `/trialsadmin` | Opens master admin panel (3 rows) with buttons linking to the 5 sub-menus below |
| `/trialsadmin_goal` | Goal Manager GUI (4 rows): set name, set target, set deadline, start/stop/reset goal |
| `/trialsadmin_punishment` | Punishment Setup GUI (3 rows): set punishment message, set punishment console command |
| `/trialsadmin_sqmenu` | Side Quest Manager GUI (6 rows): list all quests, create new, edit existing, delete, toggle active |
| `/trialsadmin_questedit` | Edit Quest GUI (4 rows): edit a specific quest's name/description/type/target/reward |
| `/trialsadmin_plugins` | Plugin Submissions GUI (6 rows, paginated): approve/deny pending suggestions |
| `/trialsadmin_vote` | Vote Manager GUI (3 rows): start/end vote, view results, clear options |
| `/trialsgrant <questID> <player> <amount>` | Manually add progress to a player's quest (works for any quest type, primarily for `manual` type) |

All GUI-triggering admin subcommands (`/trialsadmin_goal`, `_punishment`, `_sqmenu`, `_questedit`, `_plugins`, `_vote`) should ideally NOT be directly player-typed commands in the final plugin — they were only separate Skript commands because Skript GUIs are simplest that way. **In the Java version, these should just be internal menu-navigation methods triggered by clicking buttons inside `/trialsadmin`, not separate slash commands.** Keep `/trialsadmin` and `/trialsgrant` as the only registered admin commands.

---

## 6. GUI Summary

All GUIs are chest inventories with named titles and glass-pane filler in unused slots.

| GUI | Rows | Trigger |
|---|---|---|
| Trials Hub | 4 | `/trials` |
| Side Quests | 6 (paginated) | `/sidequests` |
| Plugin Vote | 3 | `/trials_vote` |
| Admin Master Panel | 3 | `/trialsadmin` |
| Admin Goal Manager | 4 | button from master panel |
| Admin Punishment Setup | 3 | button from master panel |
| Admin Side Quest Manager | 6 (paginated) | button from master panel |
| Admin Edit Quest | 4 | button from Side Quest Manager, per-quest |
| Admin Plugin Submissions | 6 (paginated) | button from master panel |
| Admin Vote Manager | 3 | button from master panel |

Text input for things like renaming a goal or setting a punishment message should use anvil GUIs or chat-input capture (player types in chat, plugin listens for next message) — decide whichever is cleaner in Java; chat-input-capture was used in the Skript prototype.

---

## 7. World Border Expansion (NEW FEATURE — not yet in the Skript version)

This is a new addition, integrated into the same plugin rather than built separately, since it reinforces the same "work together, get rewarded" loop.

### Recommended default configuration (change freely via config.yml or admin GUI)
- **Weekly auto-expansion:** +100 blocks to the world border, applied automatically every 7 real-world days regardless of goal state (steady baseline growth so the server always feels like it's progressing)
- **Goal-success bonus:** +500 blocks added to the world border immediately when a community goal succeeds (on top of that week's normal growth)
- **Goal-failure behavior:** No shrink. Failing just means missing out on the +500 bonus that cycle — border simply doesn't get the bonus, keeps growing from weekly ticks only. (This was chosen to avoid punishing already-frustrated players twice, but should be configurable in case the server admin wants a shrink-on-failure option later.)
- All three numbers (weekly amount, success bonus, failure shrink-if-enabled) should live in `config.yml` and ideally also be editable live from a new "World Border" tab inside `/trialsadmin`.

### Behavior requirements
- Runs on a repeating scheduled task (weekly tick) independent of the goal system.
- Border expansion should be smooth/animated using Bukkit's `WorldBorder#setSize(double, long)` with a transition time (e.g. expand over 10–30 seconds) rather than an instant snap, so it doesn't feel jarring or teleport players oddly.
- Broadcast a message to the server whenever the border expands ("The world border has grown by 100 blocks!") and a bigger, more celebratory one for goal-success bonus expansions.
- Should apply to whichever world(s) are configured (support multi-world servers — default to the main/overworld unless configured otherwise).
- Current border size and next scheduled expansion time should be viewable somewhere in `/trials` (nice-to-have, not required for v1).

---

## 8. Data Persistence

The Skript prototype used Skript variables (which persist to a `variables.csv` under the hood). For the Java plugin, replace this with proper persistent storage:
- **Recommended:** YAML files (`data/goal.yml`, `data/quests.yml`, `data/players/<uuid>.yml`) or a single SQLite database if the server owner wants more robustness/performance at scale.
- Data that must persist across restarts:
  - Goal state (name, target, current, deadline, active)
  - Punishment config (message, command)
  - All quest definitions
  - Per-player quest progress/completion/claimed state
  - Per-player goal contribution totals
  - Plugin suggestion queue (pending/approved/denied) and vote tallies
  - World border config (weekly amount, bonus amount, last-expansion timestamp)

---

## 9. Permissions

Suggested permission nodes:
- `communitytrials.use` — access to all player commands (default: true)
- `communitytrials.admin` — access to `/trialsadmin` and `/trialsgrant` (default: op)

---

## 10. Nice-to-haves (not required for v1, but worth designing around)

- Configurable goal types beyond `mine` (kill/craft/fish community goals, not just side quests)
- PlaceholderAPI support for goal progress / border size in scoreboards or tab lists
- A `/trialsreload` command to reload config without restarting
- Sound effects on milestone broadcasts and quest completion
- Support for multiple simultaneous community goals (currently only one at a time)

---

## Summary of what to hand to whoever builds this

Build a self-contained Paper plugin called **CommunityTrials** implementing everything in Sections 2–9 above, using proper Bukkit event listeners (`BlockBreakEvent`, `EntityDeathEvent`, `CraftItemEvent`, `PlayerFishEvent`, `InventoryClickEvent`, `AsyncPlayerChatEvent` or a chat-input-capture pattern, `WorldBorder` API), YAML/SQLite persistence, and a config.yml exposing all the tunable numbers (goal deadline length, milestone interval, border weekly amount, border success bonus, border failure behavior). No SkBee or Skript dependency — pure Java/Bukkit API.
