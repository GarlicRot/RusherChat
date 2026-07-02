<h1 align="center">RusherChat</h1>

<h3 align="center">WebSocket-powered chat for RusherHack</h3>

<p align="center">
  <img src="https://img.shields.io/github/downloads/GarlicRot/RusherChat/total?label=Downloads" alt="GitHub Downloads (all assets, all releases)">
  <img src="https://img.shields.io/badge/Minecraft-1.20.1%20to%201.21.11-62b47a?style=flat&logo=minecraft&logoColor=white" alt="Minecraft Version">
  <img src="https://img.shields.io/badge/%F0%9F%A7%84-Approved%20%E2%9C%94%EF%B8%8F-blue?style=flat" alt="🧄 Approved ✔️">
</p>


## Overview

RusherChat allows users running the plugin to connect and chat across any Minecraft version available to RusherHack through a shared WebSocket server. Messages are transmitted instantly and displayed in a dedicated chat window within RusherHack.


## Features

- Global chat
- Online list
- Colored names
- E2EE whispers
- Secure transport


## Commands

| Command                              | Description                                                  |
|--------------------------------------|--------------------------------------------------------------|
| `/whisper` or `/w <username> <message>` | Sends a private message to the specified user.               |
| `/reply` or `/r <message>`             | Sends a private message to the last user who whispered to you. |
| `/ignore` or `/i <username>`           | Toggles ignoring messages from the specified user.           |

> [!NOTE]
> Whispers are now fully end-to-end encrypted. Messages are encrypted on the sender's client and decrypted only on the recipient's client. The server cannot read whisper contents.

## Installation

1. Download the latest `.jar` from the [Releases](https://github.com/GarlicRot/RusherChat/releases) page.
2. Place the file into your `rusherhacks/plugins` directory.
3. Launch Minecraft with RusherHacks installed.
4. Enable the **RusherChat** module from the client interface.


## Changelog

See [CHANGELOG.md](CHANGELOG.md) for version history and release notes.


## Issues

Use the issue tracker for bug reports and feature requests:

- [Bug Report](https://github.com/GarlicRot/RusherChat/issues/new?template=bug_report.md)
- [Feature Request](https://github.com/GarlicRot/RusherChat/issues/new?template=feature_request.md)
- [General Issue](https://github.com/GarlicRot/RusherChat/issues/new?template=custom_issue.md)


## License

This project is licensed under the MIT License.
