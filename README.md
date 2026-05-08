# Lab 16 : Chronomètre avec Foreground Service

Cette application Android démontre l'utilisation d'un **Service de premier plan (Foreground Service)** et d'un **Service lié (Bound Service)** pour créer un chronomètre qui continue de fonctionner même si l'application est en arrière-plan.

## Fonctionnalités
- **Foreground Service** : Assure que le chronomètre ne soit pas tué par le système lorsque l'utilisateur quitte l'application.
- **Notification Persistante** : Affiche le temps écoulé en temps réel dans la barre de notifications.
- **Bound Service** : Permet à l'activité principale de communiquer avec le service pour afficher le temps sur l'écran.
- **Gestion des Permissions** : Gère dynamiquement la permission de notification (Android 13+) et les types de services de premier plan (Android 14+).

## Architecture
- `ChronometreService.java` : Gère la logique du temps, l'exécuteur (executor) et la mise à jour de la notification.
- `MainActivity.java` : Interface utilisateur, gestion des boutons et synchronisation de l'affichage avec le service.
- `AndroidManifest.xml` : Configuration des permissions et déclaration du service avec le type `dataSync`.

## Prérequis
- Android Studio
- SDK Android 26 (Android 8.0) ou version supérieure.
- Android 14 (API 34) supporté.

## Comment utiliser
1. Lancez l'application sur un émulateur ou un appareil physique.
2. Cliquez sur **DÉMARRER SERVICE** : Le chronomètre commence et une notification apparaît.
3. Quittez l'application : Le chronomètre continue de tourner dans la notification.
4. Revenez dans l'application : Le temps affiché à l'écran se synchronise automatiquement.
5. Cliquez sur **ARRÊTER SERVICE** : Le chronomètre s'arrête et la notification disparaît.
