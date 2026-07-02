<h1 align="center">RusherChat</h1>

<h3 align="center">Cross-version chat for RusherHack</h3>

<p align="center">
  <img src="https://img.shields.io/github/downloads/GarlicRot/RusherChat/total?label=Downloads" alt="Downloads">
  <img src="https://img.shields.io/badge/Minecraft-1.20.1%20to%201.21.11-62b47a?style=flat&logo=minecraft&logoColor=white" alt="Minecraft Version">
  <img src="https://img.shields.io/badge/%F0%9F%A7%84-Approved%20%E2%9C%94%EF%B8%8F-blue?style=flat" alt="Garlic Approved">
</p>

## Overview

RusherChat is a WebSocket-powered chat plugin for RusherHack.

It lets players chat across supported Minecraft versions through a shared server, with messages shown in a dedicated RusherHack window.

## Features

- Global chat
- Online user list
- Colored names
- End-to-end encrypted whispers
- Secure WebSocket transport

## Commands

| Command | Description |
|---|---|
| `/w <user> <message>` | Send a private whisper |
| `/r <message>` | Reply to the last whisper |
| `/i <user>` | Ignore or unignore a user |

Whispers are encrypted on the sender's client and decrypted only on the recipient's client. The server cannot read whisper contents.

## Installation

1. Download the latest `.jar` from [Releases](https://github.com/GarlicRot/RusherChat/releases).
2. Place it in your `rusherhack/plugins` folder.
3. Launch Minecraft with RusherHack.

RusherChat starts automatically when the plugin is installed.

## Related

- Server: [RusherChatServer](https://github.com/GarlicRot/RusherChatServer)
- Changelog: [CHANGELOG.md](CHANGELOG.md)

## Issues

Use the issue tracker for bugs, requests, or general feedback:

- [Bug Report](https://github.com/GarlicRot/RusherChat/issues/new?template=bug_report.md)
- [Feature Request](https://github.com/GarlicRot/RusherChat/issues/new?template=feature_request.md)
- [General Issue](https://github.com/GarlicRot/RusherChat/issues/new?template=custom_issue.md)

## License

MIT
