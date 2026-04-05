# Simple Wireless Redstone

A lightweight NeoForge mod for Minecraft 1.21.1 that adds wireless redstone signal transmission. No fuss, no complex setup — place, set a channel, done.

---

## Blocks

### Wireless Transmitter
Broadcasts a redstone signal wirelessly to all Receivers on the same channel within range.

### Wireless Receiver
Outputs a redstone signal when a Transmitter on the same channel is powered.

### Timer
A configurable redstone clock that pulses a signal at a set interval.
- Always running by default
- Can be started/stopped from the GUI
- Outputs signal from the front face only
- Horizontal facing, placed toward the player

---

## How It Works

1. Place a **Wireless Transmitter** and right-click it to set a channel number (e.g. `5`)
2. Place a **Wireless Receiver** and set it to the same channel
3. Power the Transmitter with a redstone signal — the Receiver will output a signal

Channels are numerical. Any positive integer is valid. Multiple Transmitters and Receivers can share the same channel.

**Channels are player-specific.** On a multiplayer server, your Transmitters will only communicate with your own Receivers — other players' blocks on the same channel number will not interfere.

---

## Configuration

The mod includes a common config file (`swr-common.toml`) with the following options:

| Option | Default | Min | Max | Description |
|--------|---------|-----|-----|-------------|
| `transmission_range` | `128` | `16` | `512` | Radius in blocks within which Transmitters can reach Receivers |

> **Warning:** Very large range values (above 256) may impact server performance on busy servers.

---

## Jade Support

If [Jade](https://www.curseforge.com/minecraft/mc-mods/jade) is installed, hovering over blocks will display:

- **Transmitter** — Current channel, number of connected Receivers
- **Receiver** — Current channel
- **Timer** — Pulse interval, running state