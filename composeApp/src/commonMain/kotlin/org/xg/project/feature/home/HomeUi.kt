package org.xg.project.feature.home

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import org.xg.project.domain.model.DailyMenuRecord
import org.xg.project.domain.model.MealType
import org.xg.project.domain.model.MenuItemData
import org.xg.project.feature.home.HomeState

object HomeBreakpoints {
    val CompactMax = 720.dp
    val WideMin = 1100.dp
}

/** 宽屏桌面端字号比例 */
object HomeDesktopFonts {
    val topNav = 15.sp
    val searchPlaceholder = 14.sp
    val actionButton = 14.sp
    val pageTitle = 26.sp
    val pageSubtitle = 15.sp
    val dayTab = 14.sp
    val mealTitle = 18.sp
    val mealAction = 14.sp
    val mealMeta = 14.sp
    val mealMetaSmall = 13.sp
    val tipTitle = 16.sp
    val tipBody = 14.sp
    val footer = 13.sp
}

object HomeColors {
    val PageBackground = Color(0xFFF8F5F0)
    val BrandBrown = Color(0xFF8B5E3C)
    val BrandOrange = Color(0xFFF0883A)
    val CardWhite = Color.White
    val TextPrimary = Color(0xFF1F2937)
    val TextSecondary = Color(0xFF6B7280)
    val TimelineLine = Color(0xFFE5E7EB)
    val BreakfastAccent = Color(0xFFF0883A)
    val LunchAccent = Color(0xFF8B5E3C)
    val DinnerAccent = Color(0xFF5B7C99)
    val SnackAccent = Color(0xFF9CA3AF)
    val PeachPanel = Color(0xFFFFF3E8)
    val TipPanel = Color(0xFFFFF8F0)
    val DashedBorder = Color(0xFFD1D5DB)
}

data class MealSlotUi(
    val mealType: MealType,
    val title: String,
    val timeRange: String,
    val items: List<MenuItemData>,
    val canReview: Boolean,
    val accent: Color,
    val badge: String?,
)

data class HomeContentUi(
    val dateLine: String,
    val greetingTitle: String,
    val greetingSubtitle: String,
    val menuDateLabel: String,
    val heroTitle: String,
    val heroSubtitle: String,
    val heroImageUrl: String?,
    val mealSlots: List<MealSlotUi>,
)

fun MealType.timeRangeLabel(): String = when (this) {
    MealType.BREAKFAST -> "07:00 - 09:00"
    MealType.LUNCH -> "12:00 - 14:00"
    MealType.DINNER -> "18:00 - 20:00"
    MealType.SNACK -> "21:00 - 23:00"
}

fun MealType.accentColor(): Color = when (this) {
    MealType.BREAKFAST -> HomeColors.BreakfastAccent
    MealType.LUNCH -> HomeColors.LunchAccent
    MealType.DINNER -> HomeColors.DinnerAccent
    MealType.SNACK -> HomeColors.SnackAccent
}

/** 今日餐单区块序号：早餐 1、午餐 2、晚餐 3、宵夜 4 */
fun MealType.slotIndex(): String = when (this) {
    MealType.BREAKFAST -> "1"
    MealType.LUNCH -> "2"
    MealType.DINNER -> "3"
    MealType.SNACK -> "4"
}

private fun DayOfWeek.toChineseLabel(): String = when (this) {
    DayOfWeek.MONDAY -> "星期一"
    DayOfWeek.TUESDAY -> "星期二"
    DayOfWeek.WEDNESDAY -> "星期三"
    DayOfWeek.THURSDAY -> "星期四"
    DayOfWeek.FRIDAY -> "星期五"
    DayOfWeek.SATURDAY -> "星期六"
    DayOfWeek.SUNDAY -> "星期日"
}

fun LocalDate.formatChineseDateWithWeekday(): String =
    "${year}年${month.number}月${day}日 · ${dayOfWeek.toChineseLabel()}"

fun LocalDate.formatChineseDateOnly(): String =
    "${year}年${month.number}月${day}日"

fun HomeState.toHomeContentUi(today: LocalDate, emptyRecord: DailyMenuRecord): HomeContentUi {
    val record = todayRecord ?: emptyRecord
    val featured = record.breakfast.firstOrNull()
        ?: record.lunch.firstOrNull()
        ?: record.dinner.firstOrNull()
        ?: record.snack.firstOrNull()

    return HomeContentUi(
        dateLine = today.formatChineseDateWithWeekday(),
        greetingTitle = "你好，准备开始烹饪吗？",
        greetingSubtitle = "今天的营养计划已经准备就绪。",
        menuDateLabel = record.date.ifBlank { today.formatChineseDateOnly() },
        heroTitle = featured?.name ?: "三色椒炒西兰花：清新爽口的一餐",
        heroSubtitle = featured?.desc?.ifBlank { "今日推荐" } ?: "今日推荐 · 开启健康一餐",
        heroImageUrl = record.imgUrl,
        mealSlots = listOf(
            buildMealSlot(MealType.BREAKFAST, record.breakfast, canReviewBreakfast),
            buildMealSlot(MealType.LUNCH, record.lunch, canReviewLunch),
            buildMealSlot(MealType.DINNER, record.dinner, canReviewDinner),
            buildMealSlot(MealType.SNACK, record.snack, canReviewSnack),
        ),
    )
}

private fun buildMealSlot(
    mealType: MealType,
    items: List<MenuItemData>,
    canReview: Boolean,
): MealSlotUi {
    val primary = items.firstOrNull()
    val badge = primary?.desc
        ?.takeIf { it.contains("kcal", ignoreCase = true) || it.contains("min", ignoreCase = true) }
        ?: primary?.desc?.take(12)
    return MealSlotUi(
        mealType = mealType,
        title = mealType.title,
        timeRange = mealType.timeRangeLabel(),
        items = items,
        canReview = canReview,
        accent = mealType.accentColor(),
        badge = badge,
    )
}
