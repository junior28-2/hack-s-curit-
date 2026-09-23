package com.example.vulnerableapp

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.atomic.AtomicLong

/** Local-only, data-minimized research records. No payloads or identifiers. */
class ResearchEventStore(context: Context) {
    private val preferences = context.getSharedPreferences("ResearchEvents", Context.MODE_PRIVATE)
    private val sequence = AtomicLong(preferences.getLong(KEY_SEQUENCE, 0L))

    @Synchronized
    fun record(event: String) {
        require(event.matches(Regex("[a-z0-9_]{1,64}")))
        val array = read()
        array.put(JSONObject().apply {
            put("sequence", sequence.incrementAndGet())
            put("event", event)
            put("app_version", "lab-v1")
        })
        preferences.edit()
            .putString(KEY_RECORDS, array.toString())
            .putLong(KEY_SEQUENCE, sequence.get())
            .apply()
    }

    fun hasRecords(): Boolean = read().length() > 0

    fun exportJson(): String = JSONObject().apply {
        put("schema", "lab-research-metrics-v1")
        put("data_minimization", true)
        put("records", read())
    }.toString(2)

    fun deleteAll() {
        preferences.edit().clear().apply()
        sequence.set(0L)
    }

    private fun read(): JSONArray = try {
        JSONArray(preferences.getString(KEY_RECORDS, "[]"))
    } catch (_: Exception) {
        JSONArray()
    }

    companion object {
        private const val KEY_RECORDS = "records"
        private const val KEY_SEQUENCE = "sequence"
    }
}
