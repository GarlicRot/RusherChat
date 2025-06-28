<h1 align="center">RusherChat</h1>

<h3 align="center">WebSocket-based global chat for users running the plugin</h3>

<p align="center">
  <img src="https://img.shields.io/github/downloads/GarlicRot/RusherChat/total?label=Downloads" alt="Downloads">
  <img src="https://img.shields.io/badge/Minecraft-1.20.1%20to%201.21.4-62b47a?style=flat&logo=minecraft&logoColor=white" alt="Minecraft Version">
  <img src="https://img.shields.io/badge/%F0%9F%A7%84-Approved%20%E2%9C%94%EF%B8%8F-blue?style=flat" alt="🧄 Approved ✔️">
</p>


## Overview

RusherChat allows users running the plugin to connect and chat across any Minecraft version available to RusherHacks through a shared WebSocket server. Messages are transmitted instantly and displayed in a dedicated chat window within the RusherHacks interface.


## Features

- WebSocket connection to a centralized chat server hosted on `rusherchatserver.fly.dev:443`.
- Global communication between RusherHacks users across supported Minecraft versions.
- Dedicated chat window integrated into the RusherHacks interface.
- Color-coded usernames for consistent and visually distinct chat display.
- Command-based chat control for private messaging and user management.


## Commands

| Command                              | Description                                                  |
|--------------------------------------|--------------------------------------------------------------|
| `/whisper` or `/w <username> <message>` | Sends a private message to the specified user.               |
| `/reply` or `/r <message>`             | Sends a private message to the last user who whispered to you. |
| `/ignore <username>`                   | Toggles ignoring messages from the specified user.           |


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
