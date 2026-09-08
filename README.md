# CommunityTrials

A self-contained Paper plugin implementing a democratic server-progression system:
a shared community goal, personal side quests, a plugin-suggestion/voting system,
and a weekly-growing world border tied to the goal's outcome. See
`COMMUNITY_TRIALS_SPEC.md` for the full functional specification this plugin
implements.

## Building

```
mvn clean package
```

The build depends on `io.papermc.paper:paper-api`, which is published on
PaperMC's own Maven repository (`https://repo.papermc.io/repository/maven-public/`),
not Maven Central. Building requires network access to that repository.

The `paper.version` property in `pom.xml` is pinned to the latest Paper API
available at the time this project was written
(`1.21.4-R0.1-SNAPSHOT`). Before shipping, bump both that property and the
`api-version` in `src/main/resources/plugin.yml` to match whatever current
Paper build the target server actually runs.

> **Note:** this project was developed in a sandboxed environment without
> network access to `repo.papermc.io`, so the build could not be compiled or
> run here. The code was written and reviewed carefully against the Bukkit/Paper
> API, but it has not been build-verified — please run `mvn clean package`
> (and a smoke test on a real/test server) before deploying to production.

## Installation

1. Build the jar (see above) and drop `target/CommunityTrials-1.0.0.jar` into
   your server's `plugins/` folder.
2. Start the server once to generate `plugins/CommunityTrials/config.yml` and
   the `data/` folder.
3. Edit `config.yml` to taste (goal deadline, milestone interval, world
   border amounts, punishment defaults, world list for the border).
4. In-game, an operator (or anyone with `communitytrials.admin`) runs
   `/trialsadmin` to set the community goal's name/target/deadline and start
   it, configure the punishment, and create side quests.

## Commands

| Command | Permission | Description |
|---|---|---|
| `/trials` | `communitytrials.use` | Open the Community Trials hub |
| `/sidequests` | `communitytrials.use` | Browse and claim side quests |
| `/suggestplugin [name]` | `communitytrials.use` | Suggest a plugin for the next vote |
| `/trialshelp` | `communitytrials.use` | Show the help page |
| `/trials_vote` | `communitytrials.use` | Vote for the next plugin |
| `/trialsadmin` | `communitytrials.admin` | Open the admin panel |
| `/trialsgrant <questId> <player> <amount>` | `communitytrials.admin` | Grant manual quest progress |
| `/trialsreload` | `communitytrials.admin` | Reload `config.yml` |

## Data

All state (goal progress, punishment config, quest definitions, per-player
quest/contribution data, plugin suggestions/votes, world border config) is
persisted as YAML under the plugin's data folder and survives restarts.
