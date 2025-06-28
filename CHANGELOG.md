# Changelog

All notable changes to **RusherChat** are documented here.

## [Unreleased]
- `docs: overhaul README to reflect command-based functionality` – [`8d49645`](https://github.com/GarlicRot/RusherChat/commit/8d49645) by GarlicRot
  - Updated subtitle to concise h3 header and removed "A RusherHacks Plugin".
  - Revised Overview to use "Minecraft version available to RusherHacks" and improve wording.
  - Updated Features to remove settings and add command-based control and color-coded usernames.
  - Removed Settings section as module settings are no longer supported.
  - Added Commands section with /whisper, /w, /reply, /r, and /ignore.
  - Streamlined formatting by removing redundant separators and adjusting spacing.

- `chore(workflow): fix changelog auto-update to capture new commits` – [`f23425e`](https://github.com/GarlicRot/RusherChat/commit/f23425e) by GarlicRot
  - Updated GitHub Actions workflow to use last CHANGELOG.md commit hash instead of timestamp.
  - Removed strict conventional commit filter to include all non-merge, non-changelog commits.
  - Added debug output for recent commits and new changelog entries.
  - Ensured proper insertion of new entries under [Unreleased] section.

## [1.0.5] - 2025-06-27
- `fix(chat): implement proper tracking for /reply command` – [`a3a70af`](https://github.com/GarlicRot/RusherChat/commit/a3a70af) by GarlicRot
  - Fixed `lastWhisperer` tracking to update when receiving a whisper, enabling correct `/reply` functionality.
  - Removed incorrect `lastWhisperer` update on sent whispers.

- `fix(chat): resolve whisper duplication in ChatClient` – [`0a77153`](https://github.com/GarlicRot/RusherChat/commit/0a77153) by GarlicRot
  - Removed local display of whispers in `send` method to prevent duplicate messages.
  - Ensured whispers are only displayed via server confirmation.

- `fix(chat): add ignore functionality and fix message display` – [`a9db614`](https://github.com/GarlicRot/RusherChat/commit/a9db614) by GarlicRot
  - Implemented case-insensitive `/ignore` command to hide messages from specified users.
  - Fixed issue where messages were not showing in `RusherChatWindow`.
  - Ensured compatibility with `ChatServer` broadcasting.
  - Added INFO-level logging for operational clarity.

- `fix(chat): enhance /ignore command privacy and consistency` – [`fca7907`](https://github.com/GarlicRot/RusherChat/commit/fca7907) by GarlicRot
  - Standardized case handling in `ignoredUsers` and `stripColor` for consistent username matching.
  - Fixed issue where ignored users' messages were still visible.
  - Added logging in `onMessage` to debug ignored message filtering.
  - Ensured `/ignore` command feedback remains private via `sendPrivate`.

## [1.0.4] - 2025-06-26
- `feat(chat): add static user color mapping` – [`a281251`](https://github.com/GarlicRot/RusherChat/commit/a281251) by GarlicRot
  - Integrated static user color system for consistent display in chat.
  - Updated `ChatClient` to reflect new color formatting from server.
  - Enhanced `Message` class handling for colored usernames.
  - Improved `RusherChatWindow` message rendering to support color codes.
  - Made minor UI and logic adjustments in `RusherChatModule` and `RusherChatPlugin`.

- `docs: finalize README and CHANGELOG for v1.0.4 release` – [`f1028a1`](https://github.com/GarlicRot/RusherChat/commit/f1028a1) by GarlicRot
  - Updated README with Garlic Approved badge and cleaned formatting.
  - Created initial `CHANGELOG.md` with full version history.
  - Set version 1.0.4 as latest, reflecting current server and plugin state.

- `docs: update README with badges and clean layout` – [`58d37a7`](https://github.com/GarlicRot/RusherChat/commit/58d37a7) by GarlicRot
  - Improved README with badges and cleaner layout.

- `feat(plugin): update RusherChatModule for Fly.io and logging` – [`8cecf16`](https://github.com/GarlicRot/RusherChat/commit/8cecf16) by GarlicRot
  - Changed `ChatClient` host to `rusherchatserver.fly.dev` and port to 443 for `wss://`.
  - Added `java.util.logging` with programmatic `ConsoleHandler` configuration.
  - Added logging for module enable/disable, window management, and message handling.
  - Used FINE for detailed logs and WARNING for errors.

## [1.0.3] - 2025-06-25
- `feat(plugin): switch ChatClient to WebSocket protocol` – [`48cba58`](https://github.com/GarlicRot/RusherChat/commit/48cba58) by GarlicRot
  - Switched from raw socket to Java-WebSocket client for improved connection stability and message parsing.

- `feat(plugin): initial release of RusherChat plugin` – [`2bc129e`](https://github.com/GarlicRot/RusherChat/commit/2bc129e) by GarlicRot
  - Implemented `ChatClient` to connect to live `RusherChatServer` on port 42424.
  - Created `RusherChatModule` with toggle, window UI, and settings (auto-reconnect, show join, show history).
  - Replaced example plugin classes with new plugin structure.
  - Added custom plugin metadata in `rusherhack-plugin.json`.
  - Cleaned up unused example assets and modules.

## [1.0.0] - 2025-06-24
- `docs: initialize repository` – [`a6dc147`](https://github.com/GarlicRot/RusherChat/commit/a6dc147) by GarlicRot
  - Updated `README.md` with initial project description.

- `Initial commit` – [`9bcbdc5`](https://github.com/GarlicRot/RusherChat/commit/9bcbdc5) by GarlicRot
  - Set up repository structure for RusherChat project.
