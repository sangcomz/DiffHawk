# 🦅 DiffHawk

> *I believe that smaller PRs make better code reviews and ultimately improve code quality.*

![Version](https://img.shields.io/badge/version-0.0.1-blue.svg)
![IntelliJ Platform](https://img.shields.io/badge/IntelliJ%20IDEA-2024.2+-orange.svg)
![License](https://img.shields.io/badge/license-MIT-green.svg)

**DiffHawk** is an IntelliJ IDEA plugin that provides monitoring and visualization of changes between a selected source branch and the current branch.

## ✨ Features

### 📊 Diff Statistics
- View branch changes at a glance in the status bar
- Number of inserted lines (`+lines`)
- Number of deleted lines (`-lines`) 
- Number of changed files
- Total changed lines count

### 🌿 Branch Management
- Change source branch with a single click
- Support for all local/remote branches
- Branch search functionality

### ⚙️ Configuration Options
- Set line count limit for changes
- Warning notifications when limit is exceeded
- Configure via Tools > Branch Diff Checker

### 🔄 Convenient UI
- Always visible status bar widget
- Manual refresh button for updates
- Show changed files list button
- Automatic VCS change detection

## 📷 Screenshots

Example display in status bar:
```
(main) 3 files changed, +25 / -12 total:37
```

## 🚀 Installation

### Install from JetBrains Marketplace (Coming Soon)
1. Go to `File` → `Settings` → `Plugins`
2. Search for "DiffHawk" in the `Marketplace` tab
3. Click `Install`

### Manual Installation
1. Download the latest `.jar` file from [Releases](https://github.com/sangcomz/DiffHawk/releases)
2. Go to `File` → `Settings` → `Plugins`
3. Click ⚙️ icon → `Install Plugin from Disk...`
4. Select the downloaded file

## 🛠️ Usage

1. **Open Git Project**: DiffHawk only works with projects that have a Git repository.

2. **Check Status Bar**: You can see the DiffHawk widget in the status bar at the bottom of the IDE.

3. **Select Branch**: 
   - Click on the text part of the widget
   - Select the source branch to compare from the dropdown

4. **Real-time Monitoring**: Diff statistics are automatically updated when code changes occur.

5. **Adjust Settings**: Change settings via `File` → `Settings` → `Tools` → `Branch Diff Checker`

## ⚡ Requirements

- **IntelliJ IDEA**: 2024.2 or higher
- **Java**: JDK 21 or higher
- **VCS**: Projects with Git repository

## 🔧 Development Setup

### Prerequisites
- JDK 21
- IntelliJ IDEA 2024.2+
- Git

### Build and Run
```bash
# Clone the project
git clone https://github.com/sangcomz/DiffHawk.git
cd DiffHawk

# Install dependencies and build
./gradlew build

# Run plugin in IDE
./gradlew runIde

# Package plugin for distribution
./gradlew buildPlugin
```

### Project Structure
```
src/main/kotlin/io/github/sangcomz/diffhawk/
├── DiffHawkWidget.kt              # Status bar widget
├── DiffHawkWidgetFactory.kt       # Widget factory
├── GitDiffCalculator.kt           # Git diff calculation logic
├── GitBranchUtil.kt              # Git branch utilities
├── PluginSettingsConfigurable.kt # Settings UI
└── PluginSettingsState.kt        # Settings state management
```

## 🤝 Contributing

Contributions are welcome! 

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 🐛 Issues and Bug Reports

If you encounter any issues, please report them on the [Issues](https://github.com/sangcomz/DiffHawk/issues) page.

When reporting bugs, please include:
- IntelliJ IDEA version
- Plugin version
- OS information
- Steps to reproduce
- Expected vs actual results

## 📄 License

This project is distributed under the MIT License. See [LICENSE](LICENSE) file for more information.
