# Omni

Omni est une application mobile React Native / Expo qui réunit des outils du quotidien : todo-list, défis, horloge, météo, GPS, mesures physiques et agenda.

## État actuel

Le projet contient le squelette Expo Router, un tableau de bord navigable, une Todo-list, un outil Défis, des rappels locaux configurables et un splash screen animé. Les tâches et les défis sont conservés localement sur l’appareil; aucun compte ni backend n'est nécessaire. Les autres fonctionnalités métier restent à développer.

## Prérequis

- Node.js LTS et npm
- Un téléphone avec Expo Go compatible avec la version Expo du projet, ou un émulateur Android
- La compilation locale iOS requiert macOS et Xcode; un iPhone peut aussi se connecter avec Expo Go si les versions sont compatibles

## Installation et démarrage

Pour comprendre en détail l’architecture, le trajet des données, la persistance
locale, les outils Todo et Défis ainsi que le rapport hebdomadaire, consultez le
[guide technique](./GUIDE-TECHNIQUE.md).

Depuis la racine du projet :

```bash
npm.cmd install
npm.cmd start
```

Dans PowerShell sous Windows, utilisez `npm.cmd` si la politique d'exécution bloque `npm.ps1`. Le serveur Expo affiche un QR code. Connectez le téléphone et l'ordinateur au même réseau, puis scannez le code avec **Expo Go**. Si Expo Go signale une incompatibilité de SDK, utilisez un development build.

Commandes utiles :

```bash
npm.cmd run android   # ouvrir dans un émulateur Android
npm.cmd run ios       # ouvrir le simulateur iOS (macOS seulement)
npm.cmd run web       # lancer la version navigateur
npm.cmd run typecheck # vérifier les types TypeScript
```

## Architecture

```text
omni/
├── app/                       # Routes Expo Router et assemblage des écrans
│   ├── _layout.tsx
│   ├── index.tsx              # Tableau de bord
│   └── modules/[module].tsx   # Importe et affiche le module choisi
├── assets/                    # Icônes et ressources Expo
├── src/
│   ├── components/            # Composants d'interface partagés
│   ├── hooks/                 # Hooks réutilisables
│   ├── i18n/                  # Traductions
│   ├── modules/               # Un fichier d'écran par module
│   │   ├── todo/
│   │   │   ├── todo.tsx
│   │   │   ├── TodoForm.tsx
│   │   │   ├── TodoFormModal.tsx
│   │   │   ├── TodoDatePicker.tsx
│   │   │   ├── TodoPrioritySelector.tsx
│   │   │   ├── TodoLabelManager.tsx
│   │   │   ├── TodoItem.tsx
│   │   │   ├── TodoWeeklyReport.tsx
│   │   │   ├── types.ts
│   │   │   ├── todoStorage.ts
│   │   │   ├── todoLabelStorage.ts
│   │   │   └── useTodos.ts
│   │   ├── challenges/        # Défis, épreuves et progression quotidienne
│   │   ├── clock/clock.tsx
│   │   ├── weather/weather.tsx
│   │   ├── gps/gps.tsx
│   │   ├── sensors/sensors.tsx
│   │   ├── calendar/calendar.tsx
│   │   └── catalog.ts
│   ├── services/              # Stockage local et accès aux services externes
│   └── types/                 # Types partagés
├── app.json                   # Configuration Expo
├── package.json
└── tsconfig.json
```

Les routes restent dans ``. Chaque écran de module est un composant React Native dans `src/modules/`, importé par `app/modules/[module].tsx`. Les composants Todo (formulaire, modal, calendrier, priorité et ligne de tâche) sont séparés dans leur dossier pour garder les fichiers courts et réutilisables. Pour ajouter un module, créez son fichier d'écran puis ajoutez-le à cette route et au catalogue.

## Données et services

- **Todo-list** : tâches et étiquettes enregistrées localement sur l'appareil avec AsyncStorage, puis rechargées au prochain lancement. Chaque tâche peut activer un rappel à l’heure de son choix, le jour de son échéance; l’activation globale des notifications Todo reste nécessaire. Les tâches peuvent avoir plusieurs étiquettes optionnelles; les étiquettes prédéfinies Travail, Perso, Courses et Santé peuvent être renommées, recolorées ou supprimées. Le rapport hebdomadaire affiche les tâches créées et terminées, avec un graphique journalier des tâches terminées (Chart Kit sur mobile, rendu adapté au Web). Les dates d’activité sont conservées localement; les tâches antérieures à l’ajout de ce suivi n’ont pas d’historique rétroactif.
- **Défis** : défis à dates fixes, composés de compteurs quotidiens ou de chronomètres continus de 24 h. Les compteurs gardent leur historique par date; les chronomètres continuent en arrière-plan et conservent les jours validés. Les données sont sauvegardées localement avec AsyncStorage.
- **Notifications** : rappels locaux pour les échéances Todo, les priorités quotidiennes et les défis actifs. Les heures par défaut sont 06:00 pour les tâches et 08:00 pour les rappels quotidiens / défis; elles se règlent depuis « Rappels & notifications » sur l’accueil. L’autorisation système est demandée uniquement à l’activation d’un rappel. Disponible sur iOS et Android, sans push distant ni serveur.
- **Outils** : les cartes du tableau de bord utilisent une grille compacte et une couleur d’accent dédiée à chaque module pour les distinguer.
- **Agenda, mesures** : stockage local à ajouter (par exemple SQLite avec `expo-sqlite`).
- **Horloge, chronomètre, minuteur** : calculs locaux, sans requête réseau.
- **Capteurs et GPS** : accès via les modules Expo appropriés, avec demande des permissions au moment nécessaire.
- **Météo** : Open-Meteo peut être appelé directement, sans clé API.
- **Cartes et adresses** : OpenStreetMap fournit des services utilisables directement; leurs conditions d'utilisation et limites de débit devront être respectées.

Les appels externes seront isolés dans `src/services/`. Aucune configuration `.env` ou clé API n'est requise pour le squelette actuel. Un backend ne deviendra utile que pour la synchronisation entre appareils, les comptes, le partage ou certaines clés privées.

L’animation du logo intervient après le splash natif et s’exécute à chaque lancement à froid. Les notifications locales requièrent un appareil iOS ou Android et l’autorisation de l’utilisateur; la version Web explique cette limite. Après l’ajout ou la modification d’un plugin Expo natif, reconstruisez le development build si vous en utilisez un.

## Feuille de route

- [x] Structure Expo / React Native et navigation initiale
- [x] Todo-list avec persistance locale
- [x] Défis avec plusieurs épreuves, objectifs quotidiens et historique local
- [x] Rappels locaux configurables et animation du splash screen
- [ ] Horloge, chronomètre, minuteurs et horloges internationales
- [ ] Météo
- [ ] GPS et carte
- [ ] Mesures physiques selon les capteurs disponibles
- [ ] Agenda et rappels locaux
- [ ] Import / export et synchronisation optionnelle

## Confidentialité

La première version est pensée en local-first : aucune donnée personnelle n'est envoyée à un serveur Omni. Les requêtes vers la météo, la cartographie ou la géolocalisation externe ne seront ajoutées que pour les fonctions qui en ont besoin.
