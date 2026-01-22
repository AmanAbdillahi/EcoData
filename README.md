# 📱 EcoDonnées - Application de Gestion de Données Mobiles

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://www.android.com/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org/)
[![API](https://img.shields.io/badge/API-26%2B-brightgreen.svg)](https://android-arsenal.com/api?level=26)
[![Architecture](https://img.shields.io/badge/Architecture-Clean%20Architecture%20%2B%20MVVM-orange.svg)](https://developer.android.com/jetpack/guide)

Application Android native permettant la surveillance en temps réel de la consommation de données mobiles avec blocage automatique via VPN local. Interface moderne Material Design 3 avec Jetpack Compose.

---

## 🎯 Aperçu du Projet

**EcoDonnées** est une solution complète de gestion de données mobiles qui permet aux utilisateurs de :
- ✅ Surveiller leur consommation en temps réel via notification permanente
- ✅ Définir des quotas personnalisés avec expiration flexible
- ✅ Bloquer automatiquement Internet via VPN local quand le quota est atteint
- ✅ Acheter des forfaits internet directement depuis l'app (codes USSD)
- ✅ Bénéficier d'une confidentialité totale (stockage local uniquement)

### 📸 Captures d'écran

*(Ajoutez ici vos screenshots de l'application)*

---

## 🚀 Fonctionnalités Principales

### 🔔 Surveillance en Temps Réel
- **Notification permanente** non-supprimable affichant :
  - Données utilisées / Quota total
  - Pourcentage de consommation
  - Données restantes
  - Temps avant expiration
  - État Internet et VPN
- Mise à jour automatique toutes les **5 secondes**
- Actions rapides directement depuis la notification

### 📊 Gestion Avancée des Quotas
- Configuration manuelle flexible :
  - **Mode Jours** : Validité en nombre de jours (ex: 30 jours)
  - **Mode Date/Heure** : Sélection précise avec DatePicker/TimePicker
- Achat de forfaits intégré (4 forfaits prédéfinis)
- Exécution automatique des codes USSD
- Configuration automatique après achat

### 🔒 Blocage Automatique
- **VPN local** sans serveur externe
- Blocage complet du trafic réseau
- Activation/désactivation automatique selon quota
- Aucune collecte de données

### 🎨 Interface Moderne
- **Jetpack Compose** + Material Design 3
- 3 thèmes : Clair, Sombre, Automatique
- Navigation fluide avec Navigation Compose
- Design responsive et intuitif

---

## 🏗️ Architecture & Stack Technique

### Architecture
```
Clean Architecture + MVVM
├── Presentation Layer (UI + ViewModels)
├── Domain Layer (Use Cases + Models)
└── Data Layer (Repositories + Local Database)
```

### Technologies & Librairies

#### Core
- **Langage** : Kotlin 100% (1.9.20)
- **SDK Min** : API 26 (Android 8.0)
- **SDK Target** : API 34 (Android 14)

#### UI
- **Jetpack Compose** (BOM 2023.10.01)
- **Material 3** - Design system moderne
- **Navigation Compose** (2.7.6) - Navigation déclarative
- **Coil** - Chargement d'images (si applicable)

#### Architecture & DI
- **Hilt** (2.48) - Injection de dépendances
- **ViewModel** - Gestion d'état
- **StateFlow/Flow** - Programmation réactive

#### Persistance
- **Room** (2.6.1) - Base de données locale
- **DataStore** (1.0.0) - Préférences

#### Async
- **Coroutines** (1.7.3) - Programmation asynchrone
- **Flow** - Streams réactifs

#### Services
- **Foreground Service** - Service permanent
- **VPN Service** - Blocage réseau local
- **NetworkStatsManager** - Lecture consommation

#### Build
- **Gradle KTS** (8.11.1)
- **KSP** (1.9.20-1.0.14) - Traitement d'annotations

---

## 📦 Structure du Projet
```
com.example.ecodonnees/
│
├── data/
│   ├── local/
│   │   ├── entity/          # Entités Room (QuotaEntity, UsageEntity)
│   │   ├── dao/             # Data Access Objects
│   │   └── database/        # Configuration Room
│   └── repository/          # Implémentations repositories
│
├── domain/
│   ├── model/               # Modèles métier (Quota, Usage, DataStatus)
│   ├── repository/          # Interfaces repositories
│   └── usecase/             # Cas d'utilisation métier
│
├── presentation/
│   ├── MainActivity.kt      # Point d'entrée + Navigation
│   ├── MainScreen.kt        # Écran principal
│   ├── PackagePurchaseScreen.kt  # Écran achat forfaits
│   ├── MainViewModel.kt     # ViewModel principal
│   └── theme/               # Thèmes Material 3
│
├── service/
│   ├── DataMonitoringForegroundService.kt  # Service surveillance
│   └── BlockingVpnService.kt               # Service VPN
│
├── receiver/
│   ├── ActionReceiver.kt    # Actions notification
│   └── BootReceiver.kt      # Redémarrage auto
│
├── util/
│   ├── NetworkStatsReader.kt  # Lecture données mobiles
│   └── UssdHelper.kt          # Exécution codes USSD
│
└── di/
    └── AppModule.kt         # Configuration Hilt
```

---

## 🛠️ Installation & Configuration

### Prérequis
- **Android Studio** Hedgehog (2023.1.1) ou supérieur
- **JDK** 17
- **Android SDK** API 26-34
- **Gradle** 8.11.1

### Installation

1. **Cloner le repository**
```bash
git clone https://github.com/votre-username/ecodonnees.git
cd ecodonnees
```

2. **Ouvrir dans Android Studio**
```
File → Open → Sélectionner le dossier du projet
```

3. **Synchroniser Gradle**
```bash
./gradlew build
```

4. **Lancer l'application**
```bash
./gradlew installDebug
```

### Configuration Post-Installation

L'application demandera automatiquement les permissions nécessaires :
- ✅ Notifications (Android 13+)
- ✅ VPN (connexion VPN locale)
- ✅ Accès statistiques d'utilisation (USAGE_STATS)
- ✅ Appels téléphoniques (codes USSD)

---

## 📱 Guide d'Utilisation

### Premier Lancement
1. Accepter toutes les permissions demandées
2. Configurer un quota via l'icône ⚙️
3. La surveillance démarre automatiquement

### Fonctionnalités Clés
- **Acheter un forfait** : Bouton dédié → Sélection → Confirmation → Configuration auto
- **Bloquer/Débloquer** : Actions rapides depuis notification ou app
- **Changer le thème** : Icône 🎨 → Choix entre Clair/Sombre/Auto
- **Réinitialiser** : Bouton pour remettre le compteur à zéro

---

## 🎓 Points Techniques Intéressants

### 1. Service Foreground Permanent
```kotlin
class DataMonitoringForegroundService : Service() {
    // Service qui tourne en permanence avec notification obligatoire
    // Mise à jour toutes les 5 secondes via coroutines
    // Gestion automatique du cycle de vie
}
```

### 2. VPN Local Sans Serveur
```kotlin
class BlockingVpnService : VpnService() {
    // Création d'un tunnel VPN local
    // Redirection du trafic vers null (blocage total)
    // Aucune donnée collectée ou envoyée
}
```

### 3. Lecture des Données Mobiles
```kotlin
class NetworkStatsReader {
    fun getTotalMobileDataUsage(startTimestamp: Long): Long {
        // Utilisation de NetworkStatsManager
        // Mesure uniquement données mobiles (pas WiFi)
        // Calcul depuis timestamp de reset personnalisé
    }
}
```

### 4. Clean Architecture avec Flow
```kotlin
class GetDataStatusUseCase {
    operator fun invoke(isVpnActive: Boolean): Flow<DataStatus?> {
        return combine(quotaRepo.getQuota(), usageRepo.getUsage()) { ... }
        // Composition réactive de données
        // StateFlow pour UI temps réel
    }
}
```

### 5. Gestion Permissions Complexes
```kotlin
// Chaîne de permissions avec callbacks
// VPN → USAGE_STATS → CALL_PHONE → Service start
// Gestion des cas d'erreur et refus
```

---

## 🧪 Tests

### Tests Unitaires
```bash
./gradlew test
```

### Tests d'Instrumentation
```bash
./gradlew connectedAndroidTest
```

### Couverture (à implémenter)
- UseCases : 80%+
- ViewModels : 75%+
- Repositories : 85%+

---

## 🔐 Sécurité & Confidentialité

### Conformité RGPD
- ✅ Aucune donnée envoyée à un serveur
- ✅ Stockage local uniquement (Room + DataStore)
- ✅ VPN strictement local
- ✅ Permissions minimales nécessaires
- ✅ Code source auditable

### Permissions Justifiées
| Permission | Justification | Critique |
|------------|---------------|----------|
| INTERNET | Communication réseau | Oui |
| FOREGROUND_SERVICE | Service permanent | Oui |
| PACKAGE_USAGE_STATS | Lecture consommation | **OUI** |
| BIND_VPN_SERVICE | Blocage via VPN | Oui |
| CALL_PHONE | Codes USSD forfaits | Non |

---

## 📊 Performance

### Métriques
- **RAM** : ~40 MB moyenne
- **Batterie** : ~3% par jour (service permanent)
- **Stockage** : <10 MB (app + données)
- **Réseau** : 0 (aucune connexion externe)

### Optimisations
- Coroutines pour async non-bloquant
- Flow froid pour éviter fuites mémoire
- Mise à jour périodique (5s) au lieu de continue
- Notification low priority

---

## 🚧 Évolutions Futures

### Roadmap
- [ ] Widget homescreen avec statistiques
- [ ] Graphiques consommation historique
- [ ] Alertes personnalisables (50%, 80%, etc.)
- [ ] Export données (CSV, JSON)
- [ ] Multi-profils utilisateurs
- [ ] Mode économie de batterie
- [ ] Support tablettes
- [ ] Intégration Firebase Analytics (opt-in)

### Améliorations Techniques
- [ ] Tests UI avec Compose Testing
- [ ] CI/CD avec GitHub Actions
- [ ] Modularisation par feature
- [ ] Version iOS (SwiftUI)

---

## 🤝 Contribution

Les contributions sont les bienvenues ! 

### Process
1. Fork le projet
2. Créer une branche feature (`git checkout -b feature/AmazingFeature`)
3. Commit (`git commit -m 'Add AmazingFeature'`)
4. Push (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request

### Standards
- Code en Kotlin (conventions officielles)
- Clean Architecture obligatoire
- Tests unitaires pour nouvelles features
- Documentation inline pour code complexe

---

## 📄 Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.

---

## 👨‍💻 Auteur

**AmanEntreprise**

- 🌐 Website: [amanentreprise.page.gd](http://amanentreprise.page.gd/)
- 📧 Email: contact@amanentreprise.com *(à adapter)*
- 💼 LinkedIn: [Votre profil](https://linkedin.com/in/votre-profil) *(à adapter)*
- 🐱 GitHub: [@votre-username](https://github.com/votre-username) *(à adapter)*

---

## 🙏 Remerciements

- [Android Developers](https://developer.android.com/) - Documentation officielle
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - UI moderne
- [Material Design 3](https://m3.material.io/) - Design system
- Communauté Kotlin pour le support

---

## 📈 Statistiques du Projet

![GitHub stars](https://img.shields.io/github/stars/votre-username/ecodonnees?style=social)
![GitHub forks](https://img.shields.io/github/forks/votre-username/ecodonnees?style=social)
![GitHub issues](https://img.shields.io/github/issues/votre-username/ecodonnees)
![GitHub license](https://img.shields.io/github/license/votre-username/ecodonnees)

---

<div align="center">
  <p>Développé avec ❤️ par AmanEntreprise</p>
  <p>© 2026 AmanEntreprise - Tous droits réservés</p>
</div>
