package cash.money.calculator.data

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("cash_calculator_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_DENOMINATIONS = "enabled_denominations"
        val DEFAULT_DENOMINATIONS = listOf(500, 200, 100, 50, 20, 10, 5, 2, 1)
    }

    fun getEnabledDenominations(): List<Int> {
        val saved = sharedPreferences.getStringSet(KEY_DENOMINATIONS, null)
        return if (saved == null) {
            DEFAULT_DENOMINATIONS
        } else {
            saved.map { it.toInt() }.sortedByDescending { it }
        }
    }

    fun saveEnabledDenominations(denominations: List<Int>) {
        sharedPreferences.edit()
            .putStringSet(KEY_DENOMINATIONS, denominations.map { it.toString() }.toSet())
            .apply()
    }

    fun getAppTheme(): String {
        return sharedPreferences.getString("app_theme", "Light") ?: "Light"
    }

    fun saveAppTheme(theme: String) {
        sharedPreferences.edit()
            .putString("app_theme", theme)
            .apply()
    }
}
