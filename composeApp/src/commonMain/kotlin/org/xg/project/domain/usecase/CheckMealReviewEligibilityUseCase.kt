package org.xg.project.domain.usecase

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.xg.project.domain.model.MealType
import kotlin.time.Clock

/**
 * 检查当前时间是否允许对特定餐段进行评价。
 * 业务规则：
 * - 早餐：9点以后
 * - 午餐：12点以后
 * - 晚餐：19点30分以后
 * - 宵夜：21点以后
 */
class CheckMealReviewEligibilityUseCase {

    operator fun invoke(mealType: MealType, clock: Clock = Clock.System): Boolean {
        val now = clock.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val currentHour = now.hour
        val currentMinute = now.minute

        return when (mealType) {
            MealType.BREAKFAST -> currentHour >= 9
            MealType.LUNCH -> currentHour >= 12
            MealType.DINNER -> currentHour > 19 || (currentHour == 19 && currentMinute >= 30)
            MealType.SNACK -> currentHour >= 21
        }
    }
}