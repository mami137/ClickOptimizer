# ⚡ ClickOptimizer (SubTick Input Engine)

Have you ever felt like Minecraft ignores some of your extremely fast clicks? You are not crazy, it actually does. 

In Vanilla Minecraft, your mouse and keyboard inputs are bottlenecked by the game's internal `20 TPS` (50ms) tick loop. If you make multiple fast clicks or flick your mouse rapidly within a single 50ms window, the game engine might drop or miscalculate those inputs. 

ClickOptimizer fixes this entirely by tracking your input at the **Render-Frame (FPS)** level instead of the Tick level.

## ✨ Features
* **Real-time CPS HUD:** Displays a sleek, color-coded Clicks Per Second counter on your screen. (Press `H` in-game to toggle it on/off).
* **Zero Input Loss:** Tracks your mouse clicks (Left/Right) at your exact FPS rate to ensure no physical click is ever dropped.
* **100% Client-Side & Legit:** This mod acts purely as a passive observer. It **does not** generate extra clicks, it **does not** bypass cooldowns, and it **does not** send custom packets to the server. It is completely safe, vanilla-friendly, and perfect for competitive PvP servers.

## 🛠️ How It Works
The mod listens to the raw GLFW inputs at the render thread and timestamps them. This allows you to monitor your true hardware input speed without the restrictions of the game's 50ms tick rate.

## 📦 Compatibility
* Requires **Fabric API**.
* Fully compatible with Sodium, Iris, and other performance mods.
* Works flawlessly on **Minecraft 1.20.4 up to 1.21.x and beyond.**

## 📥 Download
You can download the compiled JAR files directly from our [Modrinth Page](https://modrinth.com/mod/subtick-input-engine).

## 📄 License
This project is licensed under the MIT License - see the LICENSE file for details.
