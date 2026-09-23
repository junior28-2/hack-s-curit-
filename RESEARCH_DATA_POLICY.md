# LabPentestIA — cadre de recherche responsable

Cette application est un **laboratoire Android volontairement vulnérable** destiné aux chercheurs autorisés, à l'enseignement et aux tests sur émulateur ou appareils appartenant à l'équipe de recherche.

## Règles impératives

- Ne pas installer l'APK sur un appareil ou un réseau sans autorisation écrite.
- Ne pas utiliser de données réelles, identifiants réels, secrets de production ou données de tiers.
- Utiliser uniquement des comptes, jetons et serveurs synthétiques de laboratoire.
- Ne pas distribuer l'APK publiquement. Distribuer uniquement un artefact signé et contrôlé aux chercheurs autorisés.
- Ne pas intégrer de capture réseau générale, de collecte en arrière-plan, de persistance ou de contournement de consentement.
- Détruire les journaux et artefacts de test selon la politique de rétention de l'organisation.

## Recherche avec collecte de données

La collecte facultative doit être :

1. **Opt-in**, jamais activée par défaut ;
2. expliquée dans un écran de consentement avant toute collecte ;
3. limitée aux événements et champs décrits dans un protocole approuvé ;
4. dépourvue de contenu réseau, mots de passe, jetons, contacts, localisation, identifiants publicitaires et identifiants persistants ;
5. révocable à tout moment, avec suppression locale des données ;
6. documentée par une version de politique et une durée de conservation ;
7. exportée uniquement vers un emplacement contrôlé par l'équipe de recherche, après validation.

`ResearchConsentStore` fournit uniquement le mécanisme de consentement et de révocation. Il ne collecte ni n'envoie de données automatiquement. Toute collecte supplémentaire doit faire l'objet d'une revue sécurité, confidentialité et éthique avant intégration.

## Build

Produire uniquement un APK de laboratoire (`assembleDebug`) et conserver l'artefact dans un espace privé avec contrôle d'accès. Ne pas utiliser une clé de signature de production et ne pas publier cet APK sur un store public.
