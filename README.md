# NBUltimate
*The ultimate NBS player and manipulator plugin.*

> :warning: Warning! This is currently **indev**. Soon, I will make an actions system with builds available for download.

--- 

## What is this?
This is a plugin, which allows you to play and manipulate NBS (**N**ote **B**lock **S**ong) files.

If you know about the plugin [NoteBlockMusicPlayer](https://www.spigotmc.org/resources/noteblockmusicplayer.37295/), then this is a more advanced version, with more features.

This plugin includes:
- An NBS player (global, regional and personal)
- An NBS editor (for quick edits, for making entire songs, use [OpenNBS](https://opennbs.org).)
- A flexible API
- Skript support (coming soon)

## NBS Player types
In NBUltimate, we have 3 player types. The priority is like this:

**Global** -> **Regional** -> **Personal**

The priority can be changed in the plugin config, to your liking.

|                                           | Global                                     | Regional                                                 | Personal                  |
|-------------------------------------------|--------------------------------------------|----------------------------------------------------------|---------------------------|
| Who is listening to the songs?            | All players, regardless of their location. | All players in a specific region of the regional player. | To a single player.       |
| How many song players can there be?       | 1 song player for the entire server        | 1 song player per region                                 | 1 song player per player. |
| Can the players autoplay from a playlist? | No                                         | Yes                                                      | No                        |


## Builds
Builds are available in this repo's GitHub Actions. Here are steps to retrieve the latest build:

1. Visit the **Actions** tab in the repo. 
![](http://media.eternalsproperty.online/u/16335dcf-edf9-4dc7-a10b-5533d7bafcf1.png)

2. Click on the latest workflow run.
![](http://media.eternalsproperty.online/u/f24547e7-43f8-447f-b6dd-20c58c05d40f.png)

3. Click on the **Package** button, it should download a zip file named **Package.zip**.
![](http://media.eternalsproperty.online/u/c723388b-39d9-42b1-9b84-ea4f1ec71240.png)

4. When the zip has been download, make sure to get the jar starting with **NBUltimate** (NOT **original-NBUltimate**).
![](http://media.eternalsproperty.online/u/eec79b3e-33ac-4cc3-9681-f3783411a405.png)

5. Put that jar into your server, and restart your server. **RELOADING VIA /RELOAD OR PLUGMAN IS NOT SUPPORTED!**

---

**Support Discord:** https://discord.gg/8EX7SfMdGG