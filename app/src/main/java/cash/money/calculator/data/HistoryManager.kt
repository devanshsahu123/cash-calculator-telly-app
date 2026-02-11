package cash.money.calculator.data

import android.content.Context
import android.content.SharedPreferences
import cash.money.calculator.ui.screens.CalculationHistory
import cash.money.calculator.ui.screens.DenominationItem
import org.json.JSONArray
import org.json.JSONObject

class HistoryManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("cash_calculator_history", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_HISTORY = "saved_history"
    }

    fun getHistory(): List<CalculationHistory> {
        val jsonString = sharedPreferences.getString(KEY_HISTORY, "[]") ?: "[]"
        val jsonArray = JSONArray(jsonString)
        val history = mutableListOf<CalculationHistory>()

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val denominationsArray = obj.getJSONArray("denominations")
            val denominations = mutableListOf<DenominationItem>()
            
            for (j in 0 until denominationsArray.length()) {
                val denObj = denominationsArray.getJSONObject(j)
                denominations.add(
                    DenominationItem(
                        value = denObj.getInt("value"),
                        quantity = denObj.getInt("quantity")
                    )
                )
            }

            history.add(
                CalculationHistory(
                    id = obj.getString("id"),
                    total = obj.getInt("total"),
                    denominations = denominations,
                    note = obj.getString("note"),
                    timestamp = obj.getLong("timestamp")
                )
            )
        }
        return history.sortedByDescending { it.timestamp }
    }

    fun saveCalculation(calculation: CalculationHistory) {
        val history = getHistory().toMutableList()
        history.add(0, calculation)
        saveHistoryList(history)
    }

    fun deleteCalculation(id: String) {
        val history = getHistory().filter { it.id != id }
        saveHistoryList(history)
    }

    private fun saveHistoryList(history: List<CalculationHistory>) {
        val jsonArray = JSONArray()
        history.forEach { item ->
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("total", item.total)
            obj.put("note", item.note)
            obj.put("timestamp", item.timestamp)

            val denArray = JSONArray()
            item.denominations.forEach { den ->
                val denObj = JSONObject()
                denObj.put("value", den.value)
                denObj.put("quantity", den.quantity)
                denArray.put(denObj)
            }
            obj.put("denominations", denArray)
            jsonArray.put(obj)
        }
        sharedPreferences.edit().putString(KEY_HISTORY, jsonArray.toString()).apply()
    }
}
