# Changelog

All notable changes to **RusherChat** are documented here.

### 📅 2025-06-27
- `Merge remote-tracking branch 'origin/master'` – [`0702425`](https://github.com/GarlicRot/RusherChat/commit/0702425) by GarlicRot

- `Fix whisper functionality in ChatClient` – [`0a77153`](https://github.com/GarlicRot/RusherChat/commit/0a77153) by GarlicRot
  - Updated onMessage to filter and display whispers correctly:

---

### 📅 
- `` – [`  - Show whispers only if they are to or from the current user.`](https://github.com/GarlicRot/RusherChat/commit/  - Show whispers only if they are to or from the current user.) by 

- `` – [`  - Store lastWhisperer for replies when whispers are received.`](https://github.com/GarlicRot/RusherChat/commit/  - Store lastWhisperer for replies when whispers are received.) by 

- `` – [`- Enhanced send method to handle whisper commands:`](https://github.com/GarlicRot/RusherChat/commit/- Enhanced send method to handle whisper commands:) by 

- `` – [`  - Support /w and /whisper with target username and whisper flag.`](https://github.com/GarlicRot/RusherChat/commit/  - Support /w and /whisper with target username and whisper flag.) by 

- `` – [`  - Added /r (reply) to respond to the last whisperer.`](https://github.com/GarlicRot/RusherChat/commit/  - Added /r (reply) to respond to the last whisperer.) by 

- `` – [`- Improved privacy by checking whisper targets and sender.`](https://github.com/GarlicRot/RusherChat/commit/- Improved privacy by checking whisper targets and sender.) by 

- `` – [``](https://github.com/GarlicRot/RusherChat/commit/) by 

### 📅 2025-06-27
- `docs(changelog): auto-update CHANGELOG.md` – [`1d1c72c`](https://github.com/GarlicRot/RusherChat/commit/1d1c72c) by github-actions[bot]

- `Fix message display and add ignore functionality in ChatClient` – [`a9db614`](https://github.com/GarlicRot/RusherChat/commit/a9db614) by GarlicRot
  - Resolved issue where messages were not showing in RusherChatWindow.

---

### 📅 
- `` – [`- Implemented case-insensitive /ignore command to hide messages from specified users.`](https://github.com/GarlicRot/RusherChat/commit/- Implemented case-insensitive /ignore command to hide messages from specified users.) by 

- `` – [`- Ensured compatibility with ChatServer broadcasting.`](https://github.com/GarlicRot/RusherChat/commit/- Ensured compatibility with ChatServer broadcasting.) by 

- `` – [`- Cleaned up code with INFO-level logging for operational clarity.`](https://github.com/GarlicRot/RusherChat/commit/- Cleaned up code with INFO-level logging for operational clarity.) by 

- `` – [``](https://github.com/GarlicRot/RusherChat/commit/) by 

### 📅 2025-06-27
- `docs(changelog): auto-update CHANGELOG.md` – [`90c6390`](https://github.com/GarlicRot/RusherChat/commit/90c6390) by github-actions[bot]

- `Merge remote-tracking branch 'origin/master'` – [`c7f3487`](https://github.com/GarlicRot/RusherChat/commit/c7f3487) by GarlicRot

- `Fix /ignore command filtering and enhance privacy in ChatClient` – [`fca7907`](https://github.com/GarlicRot/RusherChat/commit/fca7907) by GarlicRot
  - Standardized case handling in ignoredUsers and stripColor to ensure

---

### 📅 
- `` – [`  consistent matching of usernames, fixing the issue where ignored`](https://github.com/GarlicRot/RusherChat/commit/  consistent matching of usernames, fixing the issue where ignored) by 

- `` – [`  users' messages were still visible.`](https://github.com/GarlicRot/RusherChat/commit/  users' messages were still visible.) by 

- `` – [`- Added logging in onMessage to debug ignored message filtering.`](https://github.com/GarlicRot/RusherChat/commit/- Added logging in onMessage to debug ignored message filtering.) by 

- `` – [`- Ensured /ignore command feedback remains private to the issuing user`](https://github.com/GarlicRot/RusherChat/commit/- Ensured /ignore command feedback remains private to the issuing user) by 

- `` – [`  using sendPrivate, maintaining the existing private command behavior.`](https://github.com/GarlicRot/RusherChat/commit/  using sendPrivate, maintaining the existing private command behavior.) by 

- `` – [`- Updated documentation in method comments for clarity.`](https://github.com/GarlicRot/RusherChat/commit/- Updated documentation in method comments for clarity.) by 

- `` – [``](https://github.com/GarlicRot/RusherChat/commit/) by 

### 📅 2025-06-27
- `docs(changelog): auto-update CHANGELOG.md` – [`dd10a0e`](https://github.com/GarlicRot/RusherChat/commit/dd10a0e) by github-actions[bot]

---

### 📅 2025-06-26
- `Merge remote-tracking branch 'origin/master'` – [`c1a8f80`](https://github.com/GarlicRot/RusherChat/commit/c1a8f80) by GarlicRot

- `feat: update RusherChat plugin to support static user color mapping` – [`a281251`](https://github.com/GarlicRot/RusherChat/commit/a281251) by GarlicRot
  - Integrated static user color system for consistent display in chat

---

### 📅 
- `` – [`- Updated ChatClient to reflect new color formatting from server`](https://github.com/GarlicRot/RusherChat/commit/- Updated ChatClient to reflect new color formatting from server) by 

- `` – [`- Enhanced Message class handling for colored usernames`](https://github.com/GarlicRot/RusherChat/commit/- Enhanced Message class handling for colored usernames) by 

- `` – [`- Improved RusherChatWindow message rendering to support color codes`](https://github.com/GarlicRot/RusherChat/commit/- Improved RusherChatWindow message rendering to support color codes) by 

- `` – [`- Minor UI and logic adjustments in RusherChatModule and RusherChatPlugin`](https://github.com/GarlicRot/RusherChat/commit/- Minor UI and logic adjustments in RusherChatModule and RusherChatPlugin) by 

- `` – [``](https://github.com/GarlicRot/RusherChat/commit/) by 

### 📅 2025-06-27
- `docs(changelog): auto-update CHANGELOG.md` – [`4c04f87`](https://github.com/GarlicRot/RusherChat/commit/4c04f87) by github-actions[bot]

---

### 📅 2025-06-26
- `fix(workflow): update trigger to use master branch` – [`e62df13`](https://github.com/GarlicRot/RusherChat/commit/e62df13) by GarlicRot
  - Changed the GitHub Actions workflow trigger from `main` to `master`

---

### 📅 
- `` – [`  to match the repository's default branch`](https://github.com/GarlicRot/RusherChat/commit/  to match the repository's default branch) by 

### 📅 2025-06-26
- `chore(workflow): add GitHub Actions workflow to auto-update CHANGELOG` – [`9798a82`](https://github.com/GarlicRot/RusherChat/commit/9798a82) by GarlicRot
  - Introduced a new GitHub Actions workflow that appends the latest commit to CHANGELOG.md

---

### 📅 
- `` – [`- Formats entries with commit message, short hash, author, and commit date`](https://github.com/GarlicRot/RusherChat/commit/- Formats entries with commit message, short hash, author, and commit date) by 

- `` – [`- Inserts under the correct date section or creates a new one if necessary`](https://github.com/GarlicRot/RusherChat/commit/- Inserts under the correct date section or creates a new one if necessary) by 

- `` – [`- Automates changelog updates for each push to the main branch`](https://github.com/GarlicRot/RusherChat/commit/- Automates changelog updates for each push to the main branch) by 

### 📅 2025-06-26
- `docs(changelog): restructure CHANGELOG to match bot format` – [`eed13a1`](https://github.com/GarlicRot/RusherChat/commit/eed13a1) by GarlicRot
  - Reformatted all past entries using date headers and markdown style matching the GitHub Actions workflow

---

### 📅 
- `` – [`- Ensured each commit includes hash, author, and consistent layout for automation`](https://github.com/GarlicRot/RusherChat/commit/- Ensured each commit includes hash, author, and consistent layout for automation) by 

- `` – [`- Prepares project for changelog auto-updates on future commits`](https://github.com/GarlicRot/RusherChat/commit/- Prepares project for changelog auto-updates on future commits) by 

### 📅 2025-06-26
- `docs: add final README and CHANGELOG for v1.0.4 release` – [`f1028a1`](https://github.com/GarlicRot/RusherChat/commit/f1028a1) by GarlicRot
  - Updated README with Garlic Approved badge and cleaned formatting

---

### 📅 
- `` – [`- Created initial CHANGELOG.md with full version history`](https://github.com/GarlicRot/RusherChat/commit/- Created initial CHANGELOG.md with full version history) by 

- `` – [`- Set version 1.0.4 as latest reflecting current server and plugin state`](https://github.com/GarlicRot/RusherChat/commit/- Set version 1.0.4 as latest reflecting current server and plugin state) by 

### 📅 2025-06-26
- `docs: update README with badges and clean layout for RusherChat` – [`58d37a7`](https://github.com/GarlicRot/RusherChat/commit/58d37a7) by GarlicRot

- `Update RusherChatModule for Fly.io compatibility and java.util.logging` – [`8cecf16`](https://github.com/GarlicRot/RusherChat/commit/8cecf16) by GarlicRot
  - Changed ChatClient host to rusherchatserver.fly.dev and port to 443 for wss://

---

### 📅 
- `` – [`- Added java.util.logging with programmatic ConsoleHandler configuration`](https://github.com/GarlicRot/RusherChat/commit/- Added java.util.logging with programmatic ConsoleHandler configuration) by 

- `` – [`- Added logging for module enable/disable, window management, and message handling`](https://github.com/GarlicRot/RusherChat/commit/- Added logging for module enable/disable, window management, and message handling) by 

- `` – [`- Used FINE for detailed logs and WARNING for errors`](https://github.com/GarlicRot/RusherChat/commit/- Used FINE for detailed logs and WARNING for errors) by 

- `` – [`- No new dependencies or files added`](https://github.com/GarlicRot/RusherChat/commit/- No new dependencies or files added) by 

- `` – [``](https://github.com/GarlicRot/RusherChat/commit/) by 

### 📅 2025-06-25
- `feat(plugin): update ChatClient to use WebSocket protocol` – [`48cba58`](https://github.com/GarlicRot/RusherChat/commit/48cba58) by GarlicRot
  - Switched from raw socket to Java-WebSocket client

---

### 📅 
- `` – [`- Improved connection stability and message parsing`](https://github.com/GarlicRot/RusherChat/commit/- Improved connection stability and message parsing) by 

- `` – [``](https://github.com/GarlicRot/RusherChat/commit/) by 

### 📅 2025-06-25
- `feat: initial release of RusherChat plugin` – [`2bc129e`](https://github.com/GarlicRot/RusherChat/commit/2bc129e) by GarlicRot
  - Implemented ChatClient to connect to live RusherChatServer on port 42424

---

### 📅 
- `` – [`- Created RusherChatModule with toggle, window UI, and settings (auto-reconnect, show join, show history)`](https://github.com/GarlicRot/RusherChat/commit/- Created RusherChatModule with toggle, window UI, and settings (auto-reconnect, show join, show history)) by 

- `` – [`- Replaced example plugin classes with new plugin structure`](https://github.com/GarlicRot/RusherChat/commit/- Replaced example plugin classes with new plugin structure) by 

- `` – [`- Added custom plugin metadata in rusherhack-plugin.json`](https://github.com/GarlicRot/RusherChat/commit/- Added custom plugin metadata in rusherhack-plugin.json) by 

- `` – [`- Cleaned up unused example assets and modules`](https://github.com/GarlicRot/RusherChat/commit/- Cleaned up unused example assets and modules) by 

- `` – [``](https://github.com/GarlicRot/RusherChat/commit/) by 

### 📅 2025-06-24
- `Update README.md` – [`a6dc147`](https://github.com/GarlicRot/RusherChat/commit/a6dc147) by GarlicRot

- `Initial commit` – [`9bcbdc5`](https://github.com/GarlicRot/RusherChat/commit/9bcbdc5) by GarlicRot

