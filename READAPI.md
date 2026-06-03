# Foodaigent 接口与实体类梳理

> 本文档梳理 **锅铲黄小厨（foodaigent）** 项目中各页面使用的 HTTP 接口、Repository 调用链，以及对应的请求/响应实体类。  
> 代码路径基准：`composeApp/src/commonMain/kotlin/org/xg/project/`

---

## 目录

1. [架构概览](#1-架构概览)
2. [后端服务地址](#2-后端服务地址)
3. [通用响应结构](#3-通用响应结构)
4. [接口清单](#4-接口清单)
5. [实体类清单](#5-实体类清单)
6. [页面与接口映射](#6-页面与接口映射)
7. [未接入 / 本地 Mock 说明](#7-未接入--本地-mock-说明)
8. [源码索引](#8-源码索引)

> 实体类字段标注说明：**✅ 已使用** = 当前代码中有读取或展示；**⬜ 未使用** = 已定义/可反序列化，但页面或业务逻辑未引用。

---

## 1. 架构概览

```
Screen（页面）
  └── ViewModel / 直接调用
        └── Repository / Service
              └── Ktor HttpClient
                    └── 后端 API
```

| 层级 | 主要文件 | 职责 |
|------|----------|------|
| 网络客户端 | `data/remote/KtorClient.kt`、`data/remote/HttpPlatform.*.kt` | 创建全局 `httpClient`，JSON 序列化、超时配置 |
| 响应解析 | `data/remote/ApiResponse.kt` | `decodeBaseResponse<T>()` 解析 `BaseResponse` 包装 |
| 业务仓库 | `data/repository/FoodRepository.kt` | 餐单、食谱、口味雷达、创建食谱 |
| 本地会话 | `data/session/UserSessionRepository.kt` | 登录态（内存，无 HTTP） |
| 领域模型 | `domain/model/*.kt` | 与 API 交互的核心实体 |
| 页面状态 | `presentation/*/*Contract.kt` | 各页面 ViewModel 状态（UI 层） |

**依赖注入（Koin）**：`di/AppModule.kt` 注册 `httpClient`、`FoodRepository`、`UserSessionRepository` 及各 ViewModel。

---

## 2. 后端服务地址

| 服务 | Base URL | 用途 |
|------|----------|------|
| 餐单 / 食谱 API | `http://43.167.217.211:8090/api` | 每日餐单、食谱 CRUD、口味雷达 |

> 当前 Ktor 客户端 **未配置 Authorization 等鉴权 Header**，登录与后端账号体系未打通。

---

## 3. 通用响应结构

### 3.1 餐单 / 食谱 API 统一包装

**类名**：`BaseResponse<T>`  
**文件**：`data/model/BaseResponse.kt`

| 字段 | 类型 | 说明 | 使用 |
|------|------|------|------|
| `success` | `Boolean` | 是否成功 | ✅ `decodeBaseResponse` 判断 |
| `data` | `T` | 业务数据 | ✅ 成功时返回 |
| `message` | `String` | 错误/提示信息 | ✅ 失败时作为错误文案 |

解析方式：`HttpResponse.decodeBaseResponse<T>()`（`data/remote/ApiResponse.kt`）

### 3.2 应用内 Result 封装

| 类型 | 文件 | 说明 |
|------|------|------|
| `org.xg.project.domain.Result<T>` | `domain/Result.kt` | Repository 层：`Success` / `Error` |
| `org.xg.project.data.model.ResponseResult<T>` | `data/model/ResponseResult.kt` | Recipes 模块：`Success` / `Error` / `Loading` |

---

## 4. 接口清单

### 4.1 获取每日餐单记录

| 项 | 值 |
|----|-----|
| **方法** | `GET` |
| **路径** | `/daily-records` |
| **完整 URL** | `http://43.167.217.211:8090/api/daily-records` |
| **请求体** | 无 |
| **响应类型** | `BaseResponse<List<DailyMenuRecord>>` |
| **Repository 方法** | `FoodRepository.fetchDailyRecords()` |
| **调用页面** | 首页（HomeScreen）、历史（HistoryScreen） |

**响应使用字段**：见 [DailyMenuRecord](#dailymenurecord)、[MenuItemData](#menuitemdata)

**降级策略**（`FoodRepository.fetchDailyRecords`）：

1. 优先请求 `/daily-records`
2. 失败时调用 `/recipe`，客户端组装为 `DailyMenuRecord`
3. 仍失败则返回当日空餐单

---

### 4.2 获取食谱列表

| 项 | 值 |
|----|-----|
| **方法** | `GET` |
| **路径** | `/recipe` |
| **完整 URL** | `http://43.167.217.211:8090/api/recipe` |
| **请求体** | 无 |
| **响应类型** | `BaseResponse<List<RecipeMenu>>` |
| **Repository 方法** | `FoodRepository.getAllRecipe()` / 内部 `fetchRecipes()` |
| **调用页面** | 食谱库（RecipesScreen）、食谱详情（RecipeDetailScreen）、首页降级、历史降级 |

**响应使用字段**：见 [RecipeMenu](#recipemenu)、[RecipeMenuIngredients](#recipemenuingredients)

---

### 4.3 创建食谱

| 项 | 值 |
|----|-----|
| **方法** | `POST` |
| **路径** | `/recipe` |
| **完整 URL** | `http://43.167.217.211:8090/api/recipe` |
| **Content-Type** | `application/json` |
| **请求体** | `RecipeDraft` |
| **响应类型** | `BaseResponse<CreateRecipeResponse>` |
| **Repository 方法** | `FoodRepository.createRecipe(recipeDraft)` |
| **调用页面** | 手动录入食谱（ManualRecipeInputScreen） |

**请求使用字段**：见 [RecipeDraft](#recipedraft)、[RecipeDraftIngredient](#recipedraftingredient)

**响应使用字段**：`CreateRecipeResponse` 整包反序列化成功即视为保存成功，**`recipeId` 未在 UI 中使用**

---

### 4.4 获取口味雷达数据

| 项 | 值 |
|----|-----|
| **方法** | `GET` |
| **路径** | `/taste-radar` |
| **完整 URL** | `http://43.167.217.211:8090/api/taste-radar` |
| **请求体** | 无 |
| **响应类型** | `BaseResponse<Map<String, Float>>` |
| **Repository 方法** | `FoodRepository.fetchTasteRadar()` |
| **调用页面** | **当前无页面调用**（已实现但未接入 UI） |

**响应说明**：`Map` 的 key 为口味维度名，value 为 0~1 浮点；无页面消费该数据。

---

## 5. 实体类清单

### 5.1 餐单相关（API 响应）

#### `DailyMenuRecord`

**文件**：`domain/model/Models.kt`  
**用途**：GET `/daily-records` 列表元素；首页/历史展示

| 字段 | 类型 | 说明 | 使用 |
|------|------|------|------|
| `date` | `String` | 日期，如 `2024年6月3日` | ✅ 首页日期标签、历史日期标题 |
| `breakfast` | `List<MenuItemData>` | 早餐 | ✅ 首页餐段、历史餐次 |
| `lunch` | `List<MenuItemData>` | 午餐 | ✅ 同上 |
| `dinner` | `List<MenuItemData>` | 晚餐 | ✅ 同上 |
| `snack` | `List<MenuItemData>` | 宵夜 | ✅ 同上 |
| `stars` | `Float` | 评分 | ⬜ 未使用 |
| `comment` | `String` | 点评 | ✅ 历史页展示 |
| `imgUrl` | `String?` | 封面图 URL | ✅ 首页 Hero 图、历史餐次配图 |

---

#### `MenuItemData`

**文件**：`domain/model/Models.kt`  
**用途**：嵌套于 `DailyMenuRecord` 各餐段

| 字段 | 类型 | 说明 | 使用 |
|------|------|------|------|
| `name` | `String` | 菜名 | ✅ 首页餐段卡片、历史餐次标题 |
| `desc` | `String` | 描述 | ✅ 首页副标题/徽章、历史餐次描述 |
| `chef` | `String` | 厨师 / 提交者 | ⬜ 未在 UI 展示（降级组装时会写入） |

---

#### `MealType`

**文件**：`domain/model/Models.kt`  
**用途**：餐段枚举；食谱筛选、详情、手动录入

| 枚举值 | 中文 `title` | 使用 |
|--------|--------------|------|
| `BREAKFAST` | 早餐 | ✅ 首页餐段、食谱筛选、详情标签 |
| `LUNCH` | 午餐 | ✅ 同上 |
| `DINNER` | 晚餐 | ✅ 同上 |
| `SNACK` | 宵夜 | ✅ 同上 |

---

### 5.2 食谱相关（API 请求 / 响应）

#### `RecipeMenu`

**文件**：`domain/model/RecipeMenu.kt`  
**用途**：GET `/recipe` 列表元素；食谱库、详情页

| 字段 | 类型 | 说明 | 使用 |
|------|------|------|------|
| `id` | `Int` | 食谱 ID | ✅ 详情路由、列表多选、详情过滤 |
| `name` | `String` | 名称 | ✅ 食谱卡片、详情标题 |
| `duration` | `String` | 耗时 | ✅ 卡片元信息、详情 Pill、营养估算 |
| `difficulty` | `String` | 难度 | ✅ 卡片元信息、详情 Pill、搜索 |
| `tag` | `String` | 标签 | ✅ 详情文案、`displayTags()`、搜索 |
| `mealType` | `MealType` | 餐段 | ✅ 食谱筛选、详情标签、降级餐单分组 |
| `ingredients` | `List<RecipeMenuIngredients>` | 食材 | ✅ 详情页食材列表 |
| `steps` | `List<String>` | 步骤 | ✅ 详情页步骤列表 |
| `img` | `String?` | 图片 URL（旧字段） | ✅ 卡片/详情图（优先与 `imageUrl` 二选一） |
| `imageUrl` | `String?` | 图片 URL | ✅ 卡片/详情图、降级餐单封面 |
| `submitter` | `String?` | 提交者 | ⬜ 仅用于降级组装 `MenuItemData.chef`，UI 未展示 |
| `submitTime` | `Long?` | 提交时间戳 | ⬜ 未使用 |

---

#### `RecipeMenuIngredients`

**文件**：`domain/model/RecipeMenu.kt`

| 字段 | 类型 | 说明 | 使用 |
|------|------|------|------|
| `name` | `String` | 食材名 | ✅ 详情页食材行 |
| `number` | `String` | 用量 | ✅ 详情页食材行 |

---

#### `RecipeDraft`

**文件**：`domain/model/RecipeDraft.kt`  
**用途**：POST `/recipe` 请求体（全部字段均会提交）

| 字段 | 类型 | 说明 | 使用 |
|------|------|------|------|
| `name` | `String` | 食谱名称 | ✅ 表单 `recipeName` 映射后提交 |
| `ingredients` | `List<RecipeDraftIngredient>` | 食材 | ✅ 表单食材列表映射后提交 |
| `steps` | `List<String>` | 步骤 | ✅ 表单步骤列表映射后提交 |
| `duration` | `String` | 如 `15分钟` | ✅ 由 `durationMinutes` 生成 |
| `difficulty` | `String` | 如 `3星` | ✅ 由 `difficultyStars` 生成 |
| `tag` | `String` | 标签 | ✅ 表单 `tag` |
| `mealType` | `String` | 餐段枚举名，如 `LUNCH` | ✅ 表单 `selectedMealType` |
| `imageUrl` | `String?` | 封面图 URL | ✅ 上传成功后写入并提交 |
| `submitter` | `String?` | 提交者 | ✅ 固定传 `"admin"` |

---

#### `RecipeDraftIngredient`

**文件**：`domain/model/RecipeDraft.kt`

| 字段 | 类型 | 说明 | 使用 |
|------|------|------|------|
| `name` | `String` | 食材名 | ✅ 由 `Ingredient.name` 映射 |
| `number` | `String` | 用量 | ✅ 由 `Ingredient.quantity` 映射 |

---

#### `CreateRecipeResponse`

**文件**：`data/repository/FoodRepository.kt`  
**用途**：POST `/recipe` 响应 `data` 字段

| 字段 | 类型 | 说明 | 使用 |
|------|------|------|------|
| `recipeId` | `Int` | 新建食谱 ID | ⬜ 反序列化成功即可，未读取该字段 |

---

### 5.3 口味雷达（API 响应，未接入 UI）

| 类型 | 说明 | 使用 |
|------|------|------|
| `Map<String, Float>` | GET `/taste-radar` 的 `data` | ⬜ Repository 已实现，无页面调用 |

---

### 5.4 登录 / 会话（本地，无 HTTP）

#### `LoginCredentials`

**文件**：`presentation/login/LoginContract.kt`  
**用途**：登录表单，**未发送到后端**

| 字段 | 类型 | 使用 |
|------|------|------|
| `username` | `String` | ✅ 表单输入、校验、Mock 登录、写入 `UserAccount.displayName` |
| `password` | `String` | ✅ 表单输入、校验（至少 6 位） |

---

#### `UserAccount`

**文件**：`data/session/UserSessionRepository.kt`  
**用途**：登录成功后写入内存会话

| 字段 | 类型 | 说明 | 使用 |
|------|------|------|------|
| `displayName` | `String` | 展示名 | ✅ 侧栏账号、个人中心用户名 |
| `tierLabel` | `String` | 等级标签，默认 `Pro Tier` | ✅ 侧栏账号副标题 |
| `avatarUrl` | `String?` | 头像 URL | ✅ 侧栏/个人中心头像（登录流程未赋值，通常为 `null`） |

---

### 5.5 页面 UI 状态实体（非 API 传输）

以下类仅用于 ViewModel → Screen，**不直接对应 HTTP 请求/响应**。

#### `IndexState`

**文件**：`presentation/index/IndexContract.kt` · **页面**：首页

| 字段 | 类型 | 使用 |
|------|------|------|
| `isLoading` | `Boolean` | ✅ 加载态 |
| `todayRecord` | `DailyMenuRecord?` | ✅ 转为 `HomeContentUi` |
| `canReviewBreakfast` | `Boolean` | ✅ 早餐评价按钮显隐 |
| `canReviewLunch` | `Boolean` | ✅ 午餐评价按钮显隐 |
| `canReviewDinner` | `Boolean` | ✅ 晚餐评价按钮显隐 |
| `canReviewSnack` | `Boolean` | ✅ 宵夜评价按钮显隐 |
| `error` | `String?` | ⬜ 赋值但未在 UI 展示 |

---

#### `HistoryState`

**文件**：`presentation/history/HistoryContract.kt` · **页面**：历史

| 字段 | 类型 | 使用 |
|------|------|------|
| `isLoading` | `Boolean` | ✅ 加载态 |
| `dailyRecords` | `List<DailyMenuRecord>` | ✅ 转为历史列表 UI |
| `error` | `String?` | ✅ 错误展示 |

---

#### `RecipesState`

**文件**：`presentation/recipes/RecipesContract.kt` · **页面**：食谱库

| 字段 | 类型 | 使用 |
|------|------|------|
| `isLoading` | `Boolean` | ✅ 加载态 |
| `allRecipes` | `List<RecipeMenu>` | ✅ 列表数据源 |
| `selectedMealFilter` | `MealType?` | ✅ 餐段筛选 |
| `searchQuery` | `String` | ✅ 搜索过滤 |
| `selectedRecipeIds` | `Set<Int>` | ✅ 多选状态 |
| `error` | `String?` | ✅ 错误 Toast |
| `currentRecipes` | `List<RecipeMenu>`（计算属性） | ✅ 筛选/搜索后的展示列表 |

---

#### `ProfileState`

**文件**：`presentation/profile/ProfileContract.kt` · **页面**：个人中心

| 字段 | 类型 | 使用 |
|------|------|------|
| `isLoading` | `Boolean` | ✅ 加载 Spinner |
| `userName` | `String` | ✅ 头部用户名 |
| `avatarUrl` | `String?` | ✅ 头部头像 |
| `familyName` | `String` | ✅ 家庭卡片标题 |
| `healthGuardDays` | `Int` | ✅ 家庭卡片副文案 |
| `familySharingEnabled` | `Boolean` | ✅ 家庭共享徽章显隐 |
| `familyMeals` | `Int` | ✅ 统计卡片 |
| `favoriteRecipes` | `Int` | ✅ 统计卡片 |
| `weeklyNewFavorites` | `Int` | ✅ 桌面端统计副文案 |
| `healthScore` | `String` | ✅ 健康评分 |
| `healthScoreHint` | `String` | ✅ 桌面端健康评分说明 |
| `extraMemberCount` | `Int` | ✅ 头像堆叠 `+N` |
| `members` | `List<ProfileMemberUi>` | ✅ 家庭成员区 |
| `preferences` | `List<ProfilePreferenceUi>` | ✅ 饮食偏好区 |
| `achievements` | `List<ProfileAchievementUi>` | ✅ 成就区 |

---

#### `ManualRecipeInputState`

**文件**：`presentation/manualrecipeinput/ManualRecipeInputState.kt` · **页面**：手动录入

| 字段 | 类型 | 使用 |
|------|------|------|
| `recipeName` | `String` | ✅ 表单 → `RecipeDraft.name` |
| `ingredients` | `List<Ingredient>` | ✅ 食材编辑区 |
| `steps` | `List<String>` | ✅ 步骤编辑区 |
| `durationMinutes` | `Int` | ✅ 时长滑块 → `RecipeDraft.duration` |
| `difficultyStars` | `Int` | ✅ 难度星级 → `RecipeDraft.difficulty` |
| `tag` | `String` | ✅ 标签输入 → `RecipeDraft.tag` |
| `selectedMealType` | `String` | ✅ 餐段选择 → `RecipeDraft.mealType` |
| `uploadedImageUrl` | `String?` | ✅ 封面预览 → `RecipeDraft.imageUrl` |
| `isUploading` | `Boolean` | ✅ 上传中禁用/进度 |
| `isSaving` | `Boolean` | ✅ 保存按钮 loading |
| `error` | `String?` | ✅ 错误提示 |

---

#### `Ingredient`（表单专用，映射为 API 实体）

**文件**：`domain/model/Ingredient.kt`

| 字段 | 类型 | 使用 |
|------|------|------|
| `id` | `String` | ✅ 列表增删改唯一标识 |
| `name` | `String` | ✅ 表单输入 → `RecipeDraftIngredient.name` |
| `quantity` | `String` | ✅ 表单输入 → `RecipeDraftIngredient.number` |

---

#### 个人中心 UI 实体（本地 Mock）

**文件**：`screen/profile/ProfileUi.kt`

**`ProfileMemberUi`**

| 字段 | 使用 |
|------|------|
| `name` | ✅ 成员名称 |
| `dietaryLabel` | ✅ 饮食标签文案 |
| `style` | ✅ 头像底色与图标样式 |

**`ProfilePreferenceUi`**

| 字段 | 使用 |
|------|------|
| `label` | ✅ 偏好标签 |
| `description` | ✅ 桌面端偏好卡片说明 |
| `style` | ✅ 标签颜色与图标 |

**`ProfileAchievementUi`**

| 字段 | 使用 |
|------|------|
| `title` | ✅ 成就名称 |
| `style` | ✅ 成就图标 |

**`ProfileContentUi`**：由 `ProfileState.toProfileContentUi()` 聚合，字段与 `ProfileState` 一一对应，全部 ✅ 已使用。

---

## 6. 页面与接口映射

### 6.1 总览矩阵

| 页面 | 路由 | ViewModel | 数据层 | HTTP 接口 | 主要实体 | 接口状态 |
|------|------|-----------|--------|-----------|----------|----------|
| **LoginScreen** | `AppRoute.Login` | `LoginViewModel` | `UserSessionRepository` | — | `LoginCredentials` → `UserAccount` | Mock 登录 |
| **HomeScreen** | `BottomTabRoute.Home` | `IndexViewModel` | `FoodRepository` | GET `/daily-records`（降级 GET `/recipe`） | `DailyMenuRecord`, `MenuItemData` | 真实 + 降级 |
| **HistoryScreen** | `BottomTabRoute.History` | `HistoryViewModel` | `FoodRepository` | 同首页 | `DailyMenuRecord` | 真实 + 降级 |
| **RecipesScreen** | `BottomTabRoute.Recipes` / `RecipesInternalRoute.FromHome` | `RecipesViewModel` | `FoodRepository` | GET `/recipe` | `RecipeMenu` | 加载真实；保存 Mock |
| **RecipeDetailScreen** | `AppRoute.RecipeDetail(id)` | 无（直接调 Repository） | `FoodRepository` | GET `/recipe`（客户端按 id 过滤） | `RecipeMenu`, `RecipeMenuIngredients` | 加载真实 |
| **ManualRecipeInputScreen** | `AppRoute.ManualRecipeInput` | `ManualRecipeInputViewModel` | `FoodRepository` | POST `/recipe` | `RecipeDraft`, `RecipeDraftIngredient` | 真实 |
| **ProfileScreen** | `BottomTabRoute.Profile` | `ProfileViewModel` | `UserSessionRepository` | — | `UserAccount` + 本地 Mock | Mock |

---

### 6.2 LoginScreen — 登录页

| 项 | 说明 |
|----|------|
| **Screen** | `screen/LoginScreen.kt` |
| **ViewModel** | `presentation/login/LoginViewModel.kt` |
| **接口** | 无 HTTP；`performLogin()` 内 `delay(400)` 模拟 |
| **使用实体字段** | `LoginCredentials.username`、`LoginCredentials.password` → `UserAccount.displayName` |
| **待办** | `// TODO: 接入真实认证 API` |

---

### 6.3 HomeScreen — 首页 / 今日菜单

| 项 | 说明 |
|----|------|
| **Screen** | `screen/HomeScreen.kt` |
| **ViewModel** | `presentation/index/IndexViewModel.kt` |
| **Repository 调用** | `repository.fetchDailyRecords()` |
| **接口链** | ① GET `/daily-records` → ② 失败则 GET `/recipe` 组装 → ③ 空餐单 |
| **使用实体字段** | `DailyMenuRecord`：`date`、`breakfast`/`lunch`/`dinner`/`snack`、`imgUrl`；`MenuItemData`：`name`、`desc` |

---

### 6.4 HistoryScreen — 历史餐单

| 项 | 说明 |
|----|------|
| **Screen** | `screen/HistoryScreen.kt` |
| **ViewModel** | `presentation/history/HistoryViewModel.kt` |
| **接口** | 同首页 |
| **使用实体字段** | `DailyMenuRecord`：`date`、`breakfast`/`lunch`/`dinner`/`snack`、`comment`、`imgUrl`；`MenuItemData`：`name`、`desc` |

---

### 6.5 RecipesScreen — 食谱库

| 项 | 说明 |
|----|------|
| **Screen** | `screen/RecipesScreen.kt` |
| **ViewModel** | `presentation/recipes/RecipesViewModel.kt` |
| **加载** | GET `/recipe` |
| **使用实体字段** | `RecipeMenu`：`id`、`name`、`duration`、`difficulty`、`tag`、`mealType`、`img`/`imageUrl` |
| **保存选中** | **无 API**，仅本地 `println` |

---

### 6.6 RecipeDetailScreen — 食谱详情

| 项 | 说明 |
|----|------|
| **Screen** | `screen/RecipeDetailScreen.kt` |
| **接口** | GET `/recipe`，按 `recipeId` 客户端过滤 |
| **使用实体字段** | `RecipeMenu`：`id`、`name`、`duration`、`difficulty`、`tag`、`mealType`、`ingredients`（`name`/`number`）、`steps`、`img`/`imageUrl` |
| **备注** | 营养信息为 `rememberNutrition()` **本地计算**，非 API 字段 |

---

### 6.7 ManualRecipeInputScreen — 手动录入食谱

| 项 | 说明 |
|----|------|
| **Screen** | `screen/ManualRecipeInputScreen.kt` |
| **ViewModel** | `presentation/manualrecipeinput/ManualRecipeInputViewModel.kt` |
| **接口** | POST `/recipe` |
| **请求实体** | `RecipeDraft` 全字段（见 [RecipeDraft](#recipedraft)） |
| **响应实体** | `CreateRecipeResponse`（仅判断成功，未使用 `recipeId`） |

**调用链**：

```
ManualRecipeInputState
  → BuildRecipeDraftUseCase → RecipeDraft
  → CreateRecipeUseCase → FoodRepository.createRecipe()
  → POST /recipe
```

---

### 6.8 ProfileScreen — 个人中心

| 项 | 说明 |
|----|------|
| **Screen** | `screen/ProfileScreen.kt` |
| **ViewModel** | `presentation/profile/ProfileViewModel.kt` |
| **接口** | 无 HTTP |
| **使用实体字段** | `UserAccount.displayName`、`UserAccount.avatarUrl`；`ProfileState` 全部字段（Mock 数据） |

---

## 7. 未接入 / 本地 Mock 说明

| 功能 | 状态 | 说明 |
|------|------|------|
| 用户登录 | Mock | 无认证 API |
| 个人中心数据 | Mock | 家庭成员、偏好、成就、统计均为客户端默认值 |
| 食谱库「保存选中」 | Mock | 未调用后端 |
| 口味雷达 GET `/taste-radar` | 已实现未使用 | 无 ViewModel 调用 |
| `DailyMenuRecord.stars` | 未使用 | API 可返回，UI 未展示 |
| `MenuItemData.chef` | 未使用 | 降级组装会写入，UI 未展示 |
| `RecipeMenu.submitTime` | 未使用 | — |
| `CreateRecipeResponse.recipeId` | 未使用 | 保存成功即可，未跳转详情 |
| 首页/历史 API 失败 | 静默降级 | `IndexViewModel` 失败时 `error = null` |
| 食谱详情营养信息 | 本地计算 | 非 API 字段 |

---

## 8. 源码索引

### 8.1 网络与数据层

| 文件 | 内容 |
|------|------|
| `data/repository/FoodRepository.kt` | 餐单、食谱、口味雷达、创建食谱 |
| `data/remote/ApiResponse.kt` | `decodeBaseResponse`、网络错误文案 |
| `data/remote/KtorClient.kt` | 全局 HttpClient |
| `data/model/BaseResponse.kt` | 通用 API 包装 |
| `data/model/ResponseResult.kt` | Recipes 模块 Result |
| `data/session/UserSessionRepository.kt` | 本地用户会话 |

### 8.2 领域模型

| 文件 | 实体 |
|------|------|
| `domain/model/Models.kt` | `MenuItemData`, `DailyMenuRecord`, `MealType` |
| `domain/model/RecipeMenu.kt` | `RecipeMenu`, `RecipeMenuIngredients` |
| `domain/model/RecipeDraft.kt` | `RecipeDraft`, `RecipeDraftIngredient` |
| `domain/model/Ingredient.kt` | `Ingredient`（UI 表单） |
| `domain/Result.kt` | `Result<T>` |

### 8.3 ViewModel 与页面

| 页面 | ViewModel | Contract / State |
|------|-----------|------------------|
| LoginScreen | `LoginViewModel` | `LoginContract.kt` |
| HomeScreen | `IndexViewModel` | `IndexContract.kt` |
| HistoryScreen | `HistoryViewModel` | `HistoryContract.kt` |
| RecipesScreen | `RecipesViewModel` | `RecipesContract.kt` |
| RecipeDetailScreen | — | 直接使用 `FoodRepository` |
| ManualRecipeInputScreen | `ManualRecipeInputViewModel` | `ManualRecipeInputState.kt` |
| ProfileScreen | `ProfileViewModel` | `ProfileContract.kt` |

### 8.4 路由定义

**文件**：`Routes/Routes.kt`

| 路由类 | 说明 |
|--------|------|
| `AppRoute.Login` | 登录 |
| `AppRoute.Home` | 主壳（Tab 容器） |
| `AppRoute.ManualRecipeInput` | 手动录入 |
| `AppRoute.RecipeDetail(id)` | 食谱详情 |
| `BottomTabRoute.Home / Recipes / History / Profile` | 底部 Tab |
| `RecipesInternalRoute.FromHome(mealType)` | 从首页进入食谱库 |

---

*文档依据项目 `release3.0` 分支代码整理。字段「使用」状态随代码变更需同步更新。*
