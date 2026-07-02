# Changelog

All notable changes to **RusherChat** are documented here.

## [Unreleased]
- `Merge RusherChat production cleanup` – [`323fec4`](https://github.com/GarlicRot/RusherChat/commit/323fec4) by GarlicRot
- `Fix RusherChat reconnect shutdown lifecycle` – [`118eb64`](https://github.com/GarlicRot/RusherChat/commit/118eb64) by GarlicRot
- `Prepare RusherChat plugin for production` – [`68bfebb`](https://github.com/GarlicRot/RusherChat/commit/68bfebb) by GarlicRot


- `Merge remote-tracking branch 'origin/master'` – [`3d2a1ed`](https://github.com/GarlicRot/RusherChat/commit/3d2a1ed) by GarlicRot
- `feat: enable RusherChat by default chore: version bump` – [`87e3982`](https://github.com/GarlicRot/RusherChat/commit/87e3982) by GarlicRot
- `Update build-release.yml` – [`7e7849c`](https://github.com/GarlicRot/RusherChat/commit/7e7849c) by GarlicRot
- `feat(plugin): add client version detection and login version reporting` – [`bd37c81`](https://github.com/GarlicRot/RusherChat/commit/bd37c81) by GarlicRot
- `` – [`- Included Implementation-Version in plugin jar manifest`](https://github.com/GarlicRot/RusherChat/commit/- Included Implementation-Version in plugin jar manifest) by 
- `` – [`- Client now sends version during LOGIN handshake`](https://github.com/GarlicRot/RusherChat/commit/- Client now sends version during LOGIN handshake) by 
- `` – [`- Server can notify outdated clients`](https://github.com/GarlicRot/RusherChat/commit/- Server can notify outdated clients) by 
- `` – [`- Graceful fallback to "dev" when running from IDE`](https://github.com/GarlicRot/RusherChat/commit/- Graceful fallback to "dev" when running from IDE) by 
- `Update v1.0.6 to 1.0.7` – [`641a89a`](https://github.com/GarlicRot/RusherChat/commit/641a89a) by GarlicRot
- `Update chat endpoint` – [`222baa5`](https://github.com/GarlicRot/RusherChat/commit/222baa5) by GarlicRot
- `Fix formatting and improve changelog update workflow` – [`8e06245`](https://github.com/GarlicRot/RusherChat/commit/8e06245) by GarlicRot
- `Revise README for RusherChat details and features` – [`17bd6cd`](https://github.com/GarlicRot/RusherChat/commit/17bd6cd) by GarlicRot
- `chore: set plugin version to 1.0.6` – [`95d9636`](https://github.com/GarlicRot/RusherChat/commit/95d9636) by github-actions[bot]
- `RusherChat: add Online tab + shared input footer, update client to handle online user list` – [`5a58593`](https://github.com/GarlicRot/RusherChat/commit/5a58593) by GarlicRot
- `` – [`- Moved message input + Send button to shared bottom footer (visible across tabs)`](https://github.com/GarlicRot/RusherChat/commit/- Moved message input + Send button to shared bottom footer (visible across tabs)) by 
- `` – [`- Updated ChatClient to parse ONLINE_LIST messages and update Online tab`](https://github.com/GarlicRot/RusherChat/commit/- Updated ChatClient to parse ONLINE_LIST messages and update Online tab) by 
- `` – [`- Updated RusherChatModule to route incoming ONLINE_LIST events to window`](https://github.com/GarlicRot/RusherChat/commit/- Updated RusherChatModule to route incoming ONLINE_LIST events to window) by 
- `` – [`- Minor UI refinements and cleanup`](https://github.com/GarlicRot/RusherChat/commit/- Minor UI refinements and cleanup) by 
- `Reduce ChatClient logging noise while keeping E2EE diagnostics` – [`a6d0efe`](https://github.com/GarlicRot/RusherChat/commit/a6d0efe) by GarlicRot
- `Implement E2EE for whisper messages` – [`f2278b8`](https://github.com/GarlicRot/RusherChat/commit/f2278b8) by GarlicRot
- `Add publicKey field and its getters/setters` – [`263836a`](https://github.com/GarlicRot/RusherChat/commit/263836a) by GarlicRot
- `Clarify whisper message encryption in README` – [`8676023`](https://github.com/GarlicRot/RusherChat/commit/8676023) by GarlicRot
- `chore: set plugin version to 1.0.5` – [`67b199d`](https://github.com/GarlicRot/RusherChat/commit/67b199d) by github-actions[bot]
- `Refactor RusherChatModule for improved logging and connection` – [`30f1462`](https://github.com/GarlicRot/RusherChat/commit/30f1462) by GarlicRot
- `Enable SSL for secure WebSocket connections` – [`ff22608`](https://github.com/GarlicRot/RusherChat/commit/ff22608) by GarlicRot
- `Merge remote-tracking branch 'origin/master'` – [`5ac7dcf`](https://github.com/GarlicRot/RusherChat/commit/5ac7dcf) by GarlicRot
- `chore: set plugin version to 1.0.4` – [`a6a3637`](https://github.com/GarlicRot/RusherChat/commit/a6a3637) by github-actions[bot]
- `feat: decrypt encrypted whispers on client` – [`2225df2`](https://github.com/GarlicRot/RusherChat/commit/2225df2) by GarlicRot
- `` – [`- Keep CHAT and SYSTEM messages unchanged`](https://github.com/GarlicRot/RusherChat/commit/- Keep CHAT and SYSTEM messages unchanged) by 
- `` – [`- Preserve existing ignore list and rate limit logic`](https://github.com/GarlicRot/RusherChat/commit/- Preserve existing ignore list and rate limit logic) by 
- `` – [`- Show decrypted whisper content with existing color formatting`](https://github.com/GarlicRot/RusherChat/commit/- Show decrypted whisper content with existing color formatting) by 
- `` – [`- Log failures to decrypt but still display raw content as fallback`](https://github.com/GarlicRot/RusherChat/commit/- Log failures to decrypt but still display raw content as fallback) by 
- `chore: set plugin version to 1.0.3` – [`37094fe`](https://github.com/GarlicRot/RusherChat/commit/37094fe) by github-actions[bot]
- `feat: add protocol types and auto-login handshake` – [`19eb098`](https://github.com/GarlicRot/RusherChat/commit/19eb098) by GarlicRot
- `` – [`- Updated ChatClient to send LOGIN packet automatically on websocket open using MC username.`](https://github.com/GarlicRot/RusherChat/commit/- Updated ChatClient to send LOGIN packet automatically on websocket open using MC username.) by 
- `` – [`- Enforced message shape consistency with server-side protocol.`](https://github.com/GarlicRot/RusherChat/commit/- Enforced message shape consistency with server-side protocol.) by 
- `` – [`- Updated ChatClient to serialize Message type and fields for future encrypted whisper support.`](https://github.com/GarlicRot/RusherChat/commit/- Updated ChatClient to serialize Message type and fields for future encrypted whisper support.) by 
- `chore: set plugin version to 1.0.2` – [`90af2a0`](https://github.com/GarlicRot/RusherChat/commit/90af2a0) by github-actions[bot]
- `Add default host and port for ChatClient initialization` – [`55dc077`](https://github.com/GarlicRot/RusherChat/commit/55dc077) by GarlicRot
- `Refactor logging format and connection handling` – [`9487a19`](https://github.com/GarlicRot/RusherChat/commit/9487a19) by GarlicRot
- `Merge remote-tracking branch 'origin/master'` – [`4f932de`](https://github.com/GarlicRot/RusherChat/commit/4f932de) by GarlicRot
- `chore(gradle) update version number v1.0.1` – [`684c38c`](https://github.com/GarlicRot/RusherChat/commit/684c38c) by GarlicRot
- `Update README.md` – [`57fa472`](https://github.com/GarlicRot/RusherChat/commit/57fa472) by GarlicRot
- `feat: improve chat formatting and command handling for RusherChat` – [`795e053`](https://github.com/GarlicRot/RusherChat/commit/795e053) by GarlicRot
- `` – [`- Restored original [Whisper] and [Whisper ->] tags without replacing them`](https://github.com/GarlicRot/RusherChat/commit/- Restored original [Whisper] and [Whisper ->] tags without replacing them) by 
- `` – [`- Enhanced logging and singleton handling in RusherChatModule`](https://github.com/GarlicRot/RusherChat/commit/- Enhanced logging and singleton handling in RusherChatModule) by 
- `` – [`- Ensured message queuing works correctly if the chat window is not yet initialized`](https://github.com/GarlicRot/RusherChat/commit/- Ensured message queuing works correctly if the chat window is not yet initialized) by 
- `` – [`- Updated RusherChatWindow to better handle message input and rendering`](https://github.com/GarlicRot/RusherChat/commit/- Updated RusherChatWindow to better handle message input and rendering) by 
- `` – [`- General cleanup and formatting consistency across all classes`](https://github.com/GarlicRot/RusherChat/commit/- General cleanup and formatting consistency across all classes) by 
- `Merge remote-tracking branch 'origin/master'` – [`8704c4d`](https://github.com/GarlicRot/RusherChat/commit/8704c4d) by GarlicRot
- `` – [`#	src/main/java/garlicrot/rusherchat/ChatClient.java`](https://github.com/GarlicRot/RusherChat/commit/#	src/main/java/garlicrot/rusherchat/ChatClient.java) by 
- `Fix /ignore command filtering and enhance privacy in ChatClient` – [`4c5141d`](https://github.com/GarlicRot/RusherChat/commit/4c5141d) by GarlicRot
- `` – [`  consistent matching of usernames, fixing the issue where ignored`](https://github.com/GarlicRot/RusherChat/commit/  consistent matching of usernames, fixing the issue where ignored) by 
- `` – [`  users' messages were still visible.`](https://github.com/GarlicRot/RusherChat/commit/  users' messages were still visible.) by 
- `` – [`- Added logging in onMessage to debug ignored message filtering.`](https://github.com/GarlicRot/RusherChat/commit/- Added logging in onMessage to debug ignored message filtering.) by 
- `` – [`- Ensured /ignore command feedback remains private to the issuing user`](https://github.com/GarlicRot/RusherChat/commit/- Ensured /ignore command feedback remains private to the issuing user) by 
- `` – [`  using sendPrivate, maintaining the existing private command behavior.`](https://github.com/GarlicRot/RusherChat/commit/  using sendPrivate, maintaining the existing private command behavior.) by 
- `` – [`- Updated documentation in method comments for clarity.`](https://github.com/GarlicRot/RusherChat/commit/- Updated documentation in method comments for clarity.) by 
- `Update README.md` – [`1459cb3`](https://github.com/GarlicRot/RusherChat/commit/1459cb3) by GarlicRot
- `Update README.md` – [`fbd81b2`](https://github.com/GarlicRot/RusherChat/commit/fbd81b2) by GarlicRot
- `Create CODE_OF_CONDUCT.md` – [`70747c9`](https://github.com/GarlicRot/RusherChat/commit/70747c9) by GarlicRot
- `Update README.md` – [`d547430`](https://github.com/GarlicRot/RusherChat/commit/d547430) by GarlicRot
- `docs(readme): replace broken total downloads badge with working v1.0.0 badge` – [`c47d92b`](https://github.com/GarlicRot/RusherChat/commit/c47d92b) by GarlicRot
- `` – [`- Ensures proper download count displays until shields.io recognizes total count`](https://github.com/GarlicRot/RusherChat/commit/- Ensures proper download count displays until shields.io recognizes total count) by 
- `fix(ci): ensure JAR is included in GitHub release and validate Gradle wrapper` – [`3d4dfed`](https://github.com/GarlicRot/RusherChat/commit/3d4dfed) by GarlicRot
- `` – [`- Updated release job to use `needs.build.outputs.new_version` for file path`](https://github.com/GarlicRot/RusherChat/commit/- Updated release job to use `needs.build.outputs.new_version` for file path) by 
- `` – [`- Set `min-wrapper-count: 1` and `allow-snapshots: false` in wrapper validation step`](https://github.com/GarlicRot/RusherChat/commit/- Set `min-wrapper-count: 1` and `allow-snapshots: false` in wrapper validation step) by 
- `fix(ci): pass plugin version between jobs to include JAR in release` – [`4be82a7`](https://github.com/GarlicRot/RusherChat/commit/4be82a7) by GarlicRot
- `` – [`- Replaced environment variable usage with `needs.build.outputs.new_version` in the release job`](https://github.com/GarlicRot/RusherChat/commit/- Replaced environment variable usage with `needs.build.outputs.new_version` in the release job) by 
- `` – [`- Ensured the correct JAR file is renamed and uploaded for the GitHub Release`](https://github.com/GarlicRot/RusherChat/commit/- Ensured the correct JAR file is renamed and uploaded for the GitHub Release) by 
- `chore(workflow): fix RusherChat build and release workflow for v1.0.0` – [`528baab`](https://github.com/GarlicRot/RusherChat/commit/528baab) by GarlicRot
- `` – [`- Added debug steps to list JAR files and downloaded artifacts for troubleshooting.`](https://github.com/GarlicRot/RusherChat/commit/- Added debug steps to list JAR files and downloaded artifacts for troubleshooting.) by 
- `` – [`- Maintained Java 17 build for Minecraft 1.20.1 to 1.21.4.`](https://github.com/GarlicRot/RusherChat/commit/- Maintained Java 17 build for Minecraft 1.20.1 to 1.21.4.) by 
- `` – [`- Kept v1.0.0 initialization for first release and manual trigger.`](https://github.com/GarlicRot/RusherChat/commit/- Kept v1.0.0 initialization for first release and manual trigger.) by 
- `chore(workflow): add build and release workflow for RusherChat v1.0.0` – [`b2fbb5d`](https://github.com/GarlicRot/RusherChat/commit/b2fbb5d) by GarlicRot
- `` – [`- Initializes first release at v1.0.0 for master branch (Minecraft 1.20.1 to 1.21.4).`](https://github.com/GarlicRot/RusherChat/commit/- Initializes first release at v1.0.0 for master branch (Minecraft 1.20.1 to 1.21.4).) by 
- `` – [`- Sets plugin version in gradle.properties and creates tagged release.`](https://github.com/GarlicRot/RusherChat/commit/- Sets plugin version in gradle.properties and creates tagged release.) by 
- `` – [`- Builds with Java 17 and uploads artifact as RusherChat-1.0.0.jar.`](https://github.com/GarlicRot/RusherChat/commit/- Builds with Java 17 and uploads artifact as RusherChat-1.0.0.jar.) by 
- `` – [`- Manually triggered via workflow_dispatch.`](https://github.com/GarlicRot/RusherChat/commit/- Manually triggered via workflow_dispatch.) by 


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
