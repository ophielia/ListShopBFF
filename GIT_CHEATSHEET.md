### 🚀 Gradle Command Cheat Sheet

Here is a summary of the most useful Gradle commands for your Kotlin Multiplatform (KMM) project.

#### 🛠 Build & Compilation
| Command | Description |
| :--- | :--- |
| `./gradlew assemble` | Compiles code and packages artifacts (JARs/AARs) **without** running tests. Fastest way to check for syntax errors. |
| `./gradlew build` | Full build: compiles everything **and** runs all tests. |
| `./gradlew clean` | Deletes the `build` folders. Use this if the IDE gets confused or you want a fresh start. |
| `./gradlew clean build` | The "fresh start" option: wipes old builds and does a complete fresh compilation and test run. |

#### 🧪 Running Tests
| Command | Description                                                                                |
| :--- |:-------------------------------------------------------------------------------------------|
| `./gradlew test` | Runs all unit tests across the entire project.                                             |
| `./gradlew allTests` | Runs tests for all KMM targets (Common, Android, iOS) at once.                             |
| `./gradlew testDebugUnitTest` | Runs tests for all Android targets and includes common     |
| `./gradlew clean test` | Forces all tests to run by clearing the cache first (useful to ensure a truly clean pass). |

#### 📦 Module-Specific Commands
*Save time by only working on the specific module you are changing (e.g., `listshop`, `analytics`, `allshared`).*
| Command | Description |
| :--- | :--- |
| `./gradlew :listshop:assemble` | Compiles only the `listshop` module. |
| `./gradlew :listshop:test` | Runs tests only for the `listshop` module. |
| `./gradlew :analytics:test` | Runs tests only for the `analytics` module. |

#### 🎯 Targeting Specific Tests or Platforms
| Command | Description |
| :--- | :--- |
| `./gradlew testDebugUnitTest` | Runs Android-specific unit tests. |
| `./gradlew iosX64Test` | Runs iOS simulator tests (uses the current simulator architecture). |
| `./gradlew :listshop:test --tests "ClassName"` | Runs all tests within a specific file (e.g., `CreateTagTest`). |

---

### 💡 Pro-Tips
*   **Terminal Syntax:** On **macOS/Linux**, always use the `./` prefix (e.g., `./gradlew ...`). On **Windows**, just use `gradlew` or `gradlew.bat`.
*   **Stop Execution:** If a build is taking too long, press `Ctrl + C` in your terminal to cancel it.
*   **Stack Traces:** If a build fails and you want more detail on the error, add `--stacktrace` or `--info` to the end of any command.
*   **Discovery:** Run `./gradlew tasks` to see a full list of every command available in your project.
