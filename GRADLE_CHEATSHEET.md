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
| `./gradlew allTests` | Runs all tests (Common, Android host, and iOS) across the project. |
| `./gradlew testAndroidHostTest` | Runs tests for the Android host target (including common tests). |
| `./gradlew clean allTests` | Forces all tests to run by clearing the cache first. |

#### 📦 Module-Specific Commands
*Save time by only working on the specific module you are changing (e.g., `listshop`, `analytics`, `allshared`).*
| Command | Description |
| :--- | :--- |
| `./gradlew :listshop:assemble` | Compiles only the `listshop` module. |
| `./gradlew :listshop:testAndroidHostTest` | Runs Android host tests for `listshop`. |
| `./gradlew :analytics:allTests` | Runs all tests for `analytics`. |

#### 🎯 Targeting Specific Tests or Platforms
| Command | Description |
| :--- | :--- |
| `./gradlew testAndroidHostTest` | Runs Android host-side unit tests. |
| `./gradlew iosX64Test` | Runs iOS simulator tests (uses the current simulator architecture). |
| `./gradlew :listshop:testAndroidHostTest --tests "ClassName"` | Runs all tests within a specific file (e.g., `CreateTagTest`). |

---

### 📊 Viewing Test Results

By default, Gradle is quiet about passed tests in the terminal. Here is how you can see the results:

#### 1. HTML Reports (Recommended)
Gradle generates a detailed, visual HTML report for every test task.
*   **Android Host Tests:** `listshop/build/reports/tests/testAndroidHostTest/index.html`
*   **iOS Tests:** `listshop/build/reports/tests/iosX64Test/index.html`
*   **Aggregated (All):** `listshop/build/reports/tests/allTests/index.html`

*Simply open these files in your browser to see a breakdown of tests run, skipped, and failed.*

#### 2. Console Summary (CLI)
If you want to see a summary directly in your terminal, add the `--info` flag:
```bash
./gradlew :listshop:testAndroidHostTest --info
```
*Note: This produces a lot of output. Look for the "Generating HTML test report" line to see where the results were saved.*

#### 3. Enabling Permanent Console Logging
To make Gradle always show which tests passed/failed in the terminal, you can add this to your `build.gradle.kts`:
```kotlin
tasks.withType<Test> {
    testLogging {
        events("passed", "skipped", "failed")
    }
}
```

---

### 📈 Test Coverage

I have enabled the **Kotlinx Kover** plugin to allow you to measure how much of your code is covered by tests.

| Command | Description |
| :--- | :--- |
| `./gradlew :listshop:koverHtmlReport` | Generates a detailed HTML coverage report for the `listshop` module. |
| `./gradlew :listshop:koverLog` | Prints a basic coverage summary directly to the terminal. |

#### Viewing the Report
After running `koverHtmlReport`, you can find the results here:
`listshop/build/reports/kover/html/index.html`

*Note: Coverage is currently collected for the JVM and Android host targets.*

---

### 💡 Pro-Tips
*   **Terminal Syntax:** On **macOS/Linux**, always use the `./` prefix (e.g., `./gradlew ...`). On **Windows**, just use `gradlew` or `gradlew.bat`.
*   **Stop Execution:** If a build is taking too long, press `Ctrl + C` in your terminal to cancel it.
*   **Stack Traces:** If a build fails and you want more detail on the error, add `--stacktrace` or `--info` to the end of any command.
*   **Discovery:** Run `./gradlew tasks` to see a full list of every command available in your project.
