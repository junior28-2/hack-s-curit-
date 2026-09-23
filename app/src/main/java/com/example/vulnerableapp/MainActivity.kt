package com.example.vulnerableapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

/**
 * Consent-first laboratory entry point.
 *
 * The authorization checkbox is an explicit researcher attestation, not an
 * authentication mechanism. Distribution access must be enforced outside the
 * APK through a private repository, MDM or other approved access system.
 */
class MainActivity : Activity() {
    private val policyVersion = "2026-09-v1"
    private lateinit var consentStore: ResearchConsentStore
    private lateinit var researchStore: ResearchEventStore
    private lateinit var status: TextView
    private lateinit var consent: CheckBox
    private lateinit var researcherAttestation: CheckBox
    private lateinit var enableButton: Button
    private lateinit var revokeButton: Button
    private lateinit var exportButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        consentStore = ResearchConsentStore(this)
        researchStore = ResearchEventStore(this)
        buildScreen()
        refreshState()
    }

    private fun buildScreen() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        fun add(view: android.view.View) {
            root.addView(view, LinearLayout.LayoutParams(-1, -2))
        }

        add(TextView(this).apply {
            text = "LABORATOIRE DE RECHERCHE ANDROID"
            textSize = 20f
        })
        add(TextView(this).apply {
            text = "Usage autorisé uniquement. Aucune donnée réelle, aucun secret et aucun contenu réseau ne doivent être utilisés."
            textSize = 15f
        })

        researcherAttestation = CheckBox(this).apply {
            text = "Je suis autorisé par le protocole de recherche et j'utilise un appareil de test."
        }
        add(researcherAttestation)

        consent = CheckBox(this).apply {
            text = "J'accepte la collecte locale limitée de métriques synthétiques décrite dans la politique."
        }
        add(consent)

        enableButton = Button(this).apply {
            text = "Activer la recherche"
            setOnClickListener { grantResearchAccess() }
        }
        add(enableButton)

        revokeButton = Button(this).apply {
            text = "Révoquer et supprimer les données locales"
            setOnClickListener {
                consentStore.revoke()
                researchStore.deleteAll()
                consent.isChecked = false
                researcherAttestation.isChecked = false
                refreshState()
                toast("Consentement révoqué et données supprimées")
            }
        }
        add(revokeButton)

        exportButton = Button(this).apply {
            text = "Exporter un rapport synthétique"
            setOnClickListener { exportResearchReport() }
        }
        add(exportButton)

        status = TextView(this).apply { textSize = 14f }
        add(status)
        setContentView(root)
    }

    private fun grantResearchAccess() {
        if (!researcherAttestation.isChecked || !consent.isChecked) {
            toast("Les deux consentements explicites sont requis")
            return
        }
        consentStore.grant(policyVersion)
        researchStore.record("research_access_enabled")
        refreshState()
        toast("Recherche locale activée")
    }

    private fun refreshState() {
        val enabled = consentStore.hasConsent(policyVersion)
        status.text = if (enabled) {
            "Statut : ACTIVE — métriques synthétiques locales uniquement\nPolitique : $policyVersion"
        } else {
            "Statut : INACTIVE — aucune collecte"
        }
        exportButton.isEnabled = enabled && researchStore.hasRecords()
        revokeButton.isEnabled = enabled || researchStore.hasRecords()
        enableButton.isEnabled = !enabled
    }

    private fun exportResearchReport() {
        if (!consentStore.hasConsent(policyVersion)) {
            toast("Consentement requis")
            return
        }
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_TITLE, "research-report.json")
        }
        startActivityForResult(intent, EXPORT_REQUEST)
    }

    @Deprecated("Use Activity Result API in a future UI migration")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EXPORT_REQUEST && resultCode == RESULT_OK && data?.data != null) {
            contentResolver.openOutputStream(data.data!!)?.use { output ->
                output.write(researchStore.exportJson().toByteArray(Charsets.UTF_8))
            }
            toast("Rapport exporté")
        }
    }

    private fun toast(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    companion object {
        private const val EXPORT_REQUEST = 7001
    }
}
