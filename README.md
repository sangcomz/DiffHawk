# 🦅 DiffHawk

> *I believe that smaller PRs make better code reviews and ultimately improve code quality.*

![Version](https://img.shields.io/badge/version-0.2.3-blue.svg)
![IntelliJ Platform](https://img.shields.io/badge/IntelliJ%20IDEA-2024.2+-orange.svg)
![License](https://img.shields.io/badge/license-MIT-green.svg)

**DiffHawk** is an IntelliJ IDEA plugin that provides monitoring and visualization of changes between a selected source branch and the current branch.

## ✨ Features

### Diff Statistics & Visualization
- View branch changes at a glance in the status bar
- Number of inserted lines (`+lines`)
- Number of deleted lines (`-lines`) 
- Number of changed files
- Total changed lines count
- **NEW**: Multiple rendering options:
  - **Text Format**: Traditional text display
  - **Compact**: Minimal space usage
  - **Progress Bar**: Visual progress indicator with limit tracking

### Branch Management
- Change source branch with a single click
- Support for all local/remote branches
- Branch search functionality

### Advanced Configuration Options
- **Line Count Limit**: Set maximum allowed changes with customizable warnings
- **File Exclusion Patterns**: Exclude specific files/directories (tests, build files, etc.)
- **Custom Display Templates**: Fully customizable status bar format with variables
- **Auto Refresh**: Automatic updates at configurable intervals (1-60 minutes)
- **Alert Management**: Toggle warning notifications when limits are exceeded
- Configure via `Tools` → `Diff Hawk`

### Customizable Display Templates

Create your own display format using these variables:
- `{branch}` - Source branch name (e.g., main)
- `{fileCount}` - Number of changed files (e.g., 3)
- `{fileText}` - Singular/plural form ("file" or "files")
- `{fileChangedText}` - Complete text (e.g., "3 files changed")
- `{addedLine}` - Added lines count (e.g., 25)
- `{removedLine}` - Deleted lines count (e.g., 12)
- `{total}` - Total changed lines (e.g., 37)
- `{limitCount}` - Your line limit setting (e.g., 250 or ∞)
- `{remainingLines}` - Lines remaining before limit (e.g., 213)

**Example Templates:**
```
Default: ({branch}) {fileCount} files changed, +{addedLine} / -{removedLine} total:{total}
Compact: {branch} / {fileCount} / {addedLine} / {removedLine} / {total}
With Limits: [{branch}] {total}/{limitCount} lines ({remainingLines} left)
Simple: {branch}: +{addedLine} -{removedLine} ({fileCount} {fileText})
```

### Features
- **File Filtering**: Automatically exclude common non-source files:
  - Test directories (`**/test/**`)
  - Build files (`**/*.gradle.kts`, `**/*.xml`)
  - Configuration files (`**/*.yml`, `**/*.sh`, `**/*.pro`)
- **Limit Enforcement**: Visual warnings and alerts when changes exceed your set limits
- **Real-time Updates**: Automatic refresh with VCS change detection
- **Customizable Intervals**: Set auto-refresh from 1 to 60 minutes
- **"Don't show again" options**: Manage notification preferences

### Alert System
- Configurable line count limits (default: 250 lines)
- Warning dialogs when limits are exceeded
- Option to disable alerts permanently or temporarily
- Visual progress indicators showing proximity to limits

## Installation

### Install from JetBrains Marketplace (Coming Soon)
1. Go to `File` → `Settings` → `Plugins`
2. Search for "DiffHawk" in the `Marketplace` tab
3. Click `Install`

### Manual Installation
1. Download the latest `.jar` file from [Releases](https://github.com/sangcomz/DiffHawk/releases)
2. Go to `File` → `Settings` → `Plugins`
3. Click ⚙️ icon → `Install Plugin from Disk...`
4. Select the downloaded file

## Usage

1. **Open Git Project**: DiffHawk only works with projects that have a Git repository.

2. **Check Status Bar**: You can see the DiffHawk widget in the status bar at the bottom of the IDE.

3. **Select Branch**: 
   - Click on the text part of the widget
   - Select the source branch to compare from the dropdown

4. **Customize Display**:
   - Go to `File` → `Settings` → `Tools` → `Diff Hawk`
   - Choose your preferred renderer type
   - Create custom display templates
   - Set line count limits and exclusion patterns

5. **Configure Auto-Refresh**:
   - Enable automatic updates (default: every 3 minutes)
   - Adjust refresh interval to your preference
   - Monitor changes without manual refreshing

6. **Manage Alerts**:
   - Set appropriate line count limits for your workflow
   - Configure when to show/hide warning dialogs
   - Use progress bar renderer for visual limit tracking

## Requirements

- **IntelliJ IDEA**: 2024.2 or higher
- **Java**: JDK 21 or higher
- **VCS**: Projects with Git repository

## Development Setup

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

## Configuration Options

### File Exclusion Patterns (Default)
```
**/test/**        # Test directories
**/*.gradle.kts   # Gradle build files
**/*.xml          # XML configuration files
**/*.yml          # YAML configuration files
**/*.sh           # Shell scripts
**/*.pro          # Qt project files
```

## Contributing

Contributions are welcome! 

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## Issues and Bug Reports

If you encounter any issues, please report them on the [Issues](https://github.com/sangcomz/DiffHawk/issues) page.

When reporting bugs, please include:
- IntelliJ IDEA version
- Plugin version (v0.2.3)
- OS information
- Current renderer type and display template
- Steps to reproduce
- Expected vs actual results

## Changelog

### v0.2.3
- 🎨 New plugin icon design

### v0.2.1
- Fix issue (#1)

### v0.2.0
- **Multiple Renderer Support**: Text, Compact, and Progress Bar formats
- **Custom Display Templates**: Fully customizable status bar formatting
- **Progress Bar Renderer**: Visual progress indicator with limit tracking
- **File Exclusion Patterns**: Filter out test files, build files, and more
- **Auto-Refresh**: Configurable automatic updates (1-60 minutes)
- **Enhanced Alert System**: Line count limits with "don't show again" options
- **Advanced Settings UI**: Comprehensive configuration panel
- **Real-time Settings**: Changes apply immediately without restart

### v0.1.0
- 🎉 Initial release
- Basic diff statistics display
- Branch selection functionality
- Simple status bar widget

## License

This project is distributed under the MIT License. See [LICENSE](LICENSE) file for more information.