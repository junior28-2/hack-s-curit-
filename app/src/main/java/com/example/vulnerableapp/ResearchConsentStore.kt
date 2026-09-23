package com.example.vulnerableapp

import android.content.Context

/** Explicit consent for optional local research metrics. */
class ResearchConsentStore(context: Context) {
    private val preferences = context.getSharedPreferences("ResearchConsent", Context.MODE_PRIVATE)

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

    fun revoke() = preferences.edit().clear().apply()

    companion object {
        private const val KEY_GRANTED = "granted"
        private const val KEY_POLICY_VERSION = "policy_version"
        private const val KEY_GRANTED_AT = "granted_at_millis"
    }
}
