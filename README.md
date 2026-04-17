This is a Kotlin Multiplatform project targeting Android, iOS, Web, Desktop (JVM).

* [/composeApp](./composeApp/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./composeApp/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./composeApp/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./composeApp/src/jvmMain/kotlin)
    folder is the appropriate location.

* [/iosApp](./iosApp/iosApp) contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE’s toolbar or build it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :composeApp:assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:assembleDebug
  ```

### Build and Run Desktop (JVM) Application

To build and run the development version of the desktop app, use the run configuration from the run widget
in your IDE’s toolbar or run it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :composeApp:run
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:run
  ```

### Build and Run Web Application

To build and run the development version of the web app, use the run configuration from the run widget
in your IDE's toolbar or run it directly from the terminal:
- for the Wasm target (faster, modern browsers):
  - on macOS/Linux
    ```shell
    ./gradlew :composeApp:wasmJsBrowserDevelopmentRun
    ```
  - on Windows
    ```shell
    .\gradlew.bat :composeApp:wasmJsBrowserDevelopmentRun
    ```
- for the JS target (slower, supports older browsers):
  - on macOS/Linux
    ```shell
    ./gradlew :composeApp:jsBrowserDevelopmentRun
    ```
  - on Windows
    ```shell
    .\gradlew.bat :composeApp:jsBrowserDevelopmentRun
    ```

### Build and Run iOS Application

To build and run the development version of the iOS app, use the run configuration from the run widget
in your IDE’s toolbar or open the [/iosApp](./iosApp) directory in Xcode and run it from there.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html),
[Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform),
[Kotlin/Wasm](https://kotl.in/wasm/)…

We would appreciate your feedback on Compose/Web and Kotlin/Wasm in the public Slack channel [#compose-web](https://slack-chats.kotlinlang.org/c/compose-web).
If you face any issues, please report them on [YouTrack](https://youtrack.jetbrains.com/newIssue?project=CMP).

this is about food Menu
// 文件结构说明：
// - commonMain/kotlin/com/example/cookingheart/Main.kt (应用入口与导航)
// - commonMain/kotlin/com/example/cookingheart/ui/screens/ (各页面)
// - commonMain/kotlin/com/example/cookingheart/ui/components/ (雷达图、日历等组件)
// - commonMain/kotlin/com/example/cookingheart/data/ (数据模型与模拟数据)
// - commonMain/kotlin/com/example/cookingheart/viewmodel/ (状态管理)
//
// 依赖配置 (build.gradle.kts 关键部分):
// kotlin {
//     sourceSets {
//         commonMain.dependencies {
//             implementation(compose.runtime)
//             implementation(compose.foundation)
//             implementation(compose.material3)
//             implementation(compose.materialIconsExtended)
//             implementation(compose.ui)
//             implementation("org.jetbrains.compose.navigation:navigation-compose:1.6.0")
//             implementation("io.coil-kt.coil3:coil-compose:3.0.0-alpha10")
//             implementation("io.coil-kt.coil3:coil-network-ktor:3.0.0-alpha10")
// }

## 项目结构 (App Source)

以下是本项目 `composeApp/src/commonMain/kotlin/org/xg/project` 目录下的核心文件树形结构：

```text
composeApp/src/commonMain/kotlin/org/xg/project
├── App.kt                          // Compose 顶层入口，配置了 Navigation 3 的多栈路由 (MainNavDisplay & BottomNavDisplay)
├── Greeting.kt                     // Kotlin Multiplatform 默认欢迎示例
├── NavBackStack.kt                 // Navigation 3 自定义 `rememberAppNavBackStack` 及序列化配置 (Polymorphic Serialization)
├── Platform.kt                     // KMP 平台相关接口定义
├── PlatformBackHandler.kt          // 各平台返回键处理封装
├── Routes                          // 路由定义层 (基于 @Serializable sealed class)
│   └── Routes.kt
├── data                            // 数据层
│   ├── remote                      // 网络请求与 API
│   │   ├── HttpPlatform.kt
│   │   ├── KtorClient.kt
│   │   └── UploadService.kt
│   └── repository                  // 数据仓库实现
│       └── FoodRepository.kt
├── di                              // 依赖注入层 (Koin)
│   └── AppModule.kt
├── domain                          // 领域层 (业务模型与用例)
│   ├── model                       // 核心实体类
│   │   ├── Ingredient.kt
│   │   ├── Models.kt
│   │   └── RecipeDraft.kt
│   └── usecase                     // 业务用例
│       └── CheckMealReviewEligibilityUseCase.kt
├── presentation                    // 表现层 (MVI / MVVM 架构架构)
│   ├── history
│   │   ├── HistoryContract.kt
│   │   └── HistoryViewModel.kt
│   ├── index
│   │   ├── IndexContract.kt
│   │   └── IndexViewModel.kt
│   ├── manualrecipeinput
│   │   ├── ManualRecipeInputIntent.kt
│   │   ├── ManualRecipeInputMappers.kt
│   │   ├── ManualRecipeInputState.kt
│   │   ├── ManualRecipeInputUiEvent.kt
│   │   └── ManualRecipeInputViewModel.kt
│   ├── profile
│   │   ├── ProfileContract.kt
│   │   └── ProfileViewModel.kt
│   └── recipes
│       ├── RecipesContract.kt
│       └── RecipesViewModel.kt
└── screen                          // UI 视图层 (Jetpack Compose 界面)
    ├── GlassStyle.kt               // 玻璃拟物化风格统一定义
    ├── HistoryScreen.kt            // 历史记录页面
    ├── IndexScreen.kt              // 首页 (今日点餐记录流)
    ├── ManualRecipeInputScreen.kt  // 手动录入食谱二级页面
    ├── PlanningScreen.kt           // 点餐/计划页面
    ├── ProfileScreen.kt            // 个人中心页面
    ├── RadarChart.kt               // 自定义口味雷达图组件
    ├── RecipesScreen.kt            // 食谱灵感库页面
    └── TabBar.kt                   // 底部 TabBar 组件 (BottomTabBar)
```