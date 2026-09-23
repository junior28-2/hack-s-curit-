package com.example.vulnerableapp

import android.content.Context

/**
 * Explicit opt-in gate for optional, privacy-preserving research telemetry.
 *
 * This class does not collect or transmit data by itself. Callers must obtain
 * consent before recording any event and must implement the approved, local
 * export path separately. No identifiers, credentials, payloads, contacts,
 * location, or network content may be added to research records.
 */
class ResearchConsentStore(context: Context) {
    private val preferences = context.getSharedPreferences(
        "ResearchConsent",
        Context.MODE_PRIVATE
    )

    fun hasConsent(currentPolicyVersion: String): Boolean =
        preferences.getBoolean(KEY_GRANTED, false) &&
            preferences.getString(KEY_POLICY_VERSION, null) == currentPolicyVersion

    fun grant(currentPolicyVersion: String) {
        preferences.edit()
            .putBoolean(KEY_GRANTED, true)
            .putString(KEY_POLICY_VERSION, currentPolicyVersion)
            .putLong(KEY_GRANTED_AT, System.currentTimeMillis())
            .apply()
    }

    fun revoke() {
        preferences.edit().clear().apply()
    }

    fun grantedAtMillis(): Long? =
        if (preferences.contains(KEY_GRANTED_AT)) {
            preferences.getLong(KEY_GRANTED_AT, 0L)
        } else {
            null
        }

    companion object {
        private const val KEY_GRANTED = "granted"
        private const val KEY_POLICY_VERSION = "policy_version"
        private const val KEY_GRANTED_AT = "granted_at_millis"
    }
}
