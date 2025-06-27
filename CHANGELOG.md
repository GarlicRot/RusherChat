# Changelog

All notable changes to **RusherChat** are documented here.

---

### 📅 2025-06-22
- `feat: initial plugin setup` – [`1e6a259`](https://github.com/GarlicRot/RusherChat/commit/1e6a259) by GarlicRot  
  Added plugin base structure and RusherChat module.  
  Created custom chat window with message rendering.  
  Enabled basic message sending and receiving logic.

---

### 📅 2025-06-23
- `refactor: improved chat formatting and layout` – [`e823df9`](https://github.com/GarlicRot/RusherChat/commit/e823df9) by GarlicRot  
  Refactored internal window handling.  
  Improved layout for username and message formatting.  
  Added support for colored usernames and roles.

---

### 📅 2025-06-24
- `feat: added backend WebSocket support` – [`9bb2963`](https://github.com/GarlicRot/RusherChat/commit/9bb2963) by GarlicRot  
  Added WebSocket server for global chat communication.  
  Enabled message history and join announcements.  
  Improved client connection handling.

---

### 📅 2025-06-25
- `fix: plugin stability and reconnect` – [`acc6e10`](https://github.com/GarlicRot/RusherChat/commit/acc6e10) by GarlicRot  
  Fixed plugin crash when disconnected.  
  Added auto-reconnect toggle in settings.  
  Improved shutdown handling for the server.

---

### 📅 2025-06-26
- `docs: add final README and CHANGELOG for v1.0.4 release` – [`7e80c8f`](https://github.com/GarlicRot/RusherChat/commit/7e80c8f) by GarlicRot  
  Updated README with Garlic Approved badge and cleaned formatting.  
  Created initial `CHANGELOG.md` with full version history.  
  Set version 1.0.4 as latest reflecting current server and plugin state.

---

### 📅 2025-06-26
- `docs(changelog): restructure CHANGELOG to match bot format` – [`f7dd5de`](https://github.com/GarlicRot/RusherChat/commit/f7dd5de) by GarlicRot  
  Reformatted all past entries using date headers and markdown style matching the GitHub Actions workflow.  
  Ensured each commit includes hash, author, and consistent layout for automation.  
  Prepares project for changelog auto-updates on future commits.
