Étapes

1. [1Vérifier Java](#java)
2. [2Régler Eclipse](#eclipse)
3. [3Installer JADE](#jade)
4. [4Projet Java](#classique)
5. [5Port 1099](#port)
6. [6Projet Maven](#maven)
7. [7Erreur SSL](#ssl)
8. [+Erreurs fréquentes](#erreurs)

TP SMA · Eclipse · Java 8 · JADE 4.6.0

# Guide TP JADE et Maven

Installer Eclipse, Java 8 et JADE, lancer un premier agent, puis refaire la même chose avec un projet Maven. Ce guide vient d'une installation réelle sous Windows, avec les erreurs qu'on a rencontrées et ce qui les a réglées.

## Confirmé

- Étapes 1 à 6, jusqu'à l'agent qui tourne dans un projet Java classique.
- Le projet Maven et l'agent lancé depuis lui, avec le contournement de l'étape 7 (option B).

## Non confirmé

- Le contournement SSL par `eclipse.ini` (option A). Il n'a pas permis le téléchargement chez nous.
- Le `pom.xml` du prof avec des majuscules dans `com.tilab.Jade`. On ne l'a pas testé sans problème SSL.

## 1Vérifier Java

Il faut un **JDK** 1.8, pas seulement un JRE. Le JRE sert à exécuter du Java. Le JDK contient en plus le compilateur `javac`, et il inclut déjà un JRE. Ouvrez l'invite de commandes (Windows, tapez `cmd`) :

```
java -version
javac -version
```

**Résultat attendu**

Les deux commandes affichent une version `1.8.0_...`. Si `javac` est introuvable, vous n'avez que le JRE : installez un JDK 8 (par exemple Temurin 8).

## 2Régler Eclipse

1. Dans *Window > Preferences > Java > Installed JREs*, cochez votre JDK 8. S'il n'est pas dans la liste : *Add > Standard VM > Directory*, puis le dossier du JDK (celui qui contient `bin`, `lib` et `jre`).
2. Dans *Java > Compiler*, mettez *Compiler compliance level* sur **1.8**.
3. Cliquez sur *Apply and Close*.

## 3Installer JADE

JADE n'est pas un programme à installer. C'est une bibliothèque Java : un fichier `jade.jar`.

1. Sur la page *Download* de [jade.tilab.com](https://jade.tilab.com), téléchargez **jadeBin** (2,5 Mo). Ajoutez **jadeExamples** si vous voulez les exemples du cours.
2. Extrayez dans un **dossier à part**, par exemple `D:\JADE`. N'extrayez pas dans le dossier du JDK ni dans un dossier plein de fichiers. Le site recommande la commande `jar xvf` plutôt que WinZip ou WinRAR.

```
mkdir D:\JADE
cd /d D:\JADE
jar xvf C:\chemin\vers\JADE-bin-4.6.0.zip
dir /s /b jade.jar
```

**Résultat attendu**

La dernière commande affiche `D:\JADE\jade\lib\jade.jar`.

Testez JADE en ligne de commande :

```
cd /d D:\JADE\jade\lib
java -cp jade.jar jade.Boot -gui
```

**Résultat attendu**

La console affiche `This is JADE 4.6.0`, puis `Agent container Main-Container@... is ready`. Une fenêtre *Remote Monitoring Agent* s'ouvre. Si Windows demande une autorisation pare-feu pour Java, cliquez sur *Autoriser*. Arrêtez ensuite JADE avec Ctrl + C.

## 4Projet Java classique et premier agent

1. *File > New > Java Project*. Donnez un nom, laissez *JavaSE-1.8* comme JRE. Pour choisir où sauvegarder, décochez *Use default location* et écrivez le chemin complet, y compris le nom du projet à la fin.
2. Clic droit sur le projet > *Build Path > Configure Build Path... > Libraries > Classpath > Add External JARs...*, puis choisissez `D:\JADE\jade\lib\jade.jar`. Un dossier *Referenced Libraries* apparaît dans le projet.
3. Clic droit sur `src` > *New > Class*, package `agents`, nom `HelloAgent`, puis collez :

```
package agents;

import jade.core.Agent;

public class HelloAgent extends Agent {
    protected void setup() {
        System.out.println("Hello! I am agent " + getLocalName());
    }
}
```

4. *Run > Run Configurations...*, clic droit sur *Java Application* > *New Configuration*.
5. Onglet *Main* : *Project* = votre projet, *Main class* = `jade.Boot`.
6. Onglet *Arguments*, dans *Program arguments* :

```
-gui Hello:agents.HelloAgent
```

**Résultat attendu**

La console affiche `Hello! I am agent Hello` puis `Main-Container ... is ready`. Dans la fenêtre JADE, l'agent `Hello` apparaît sous *AgentPlatforms > ... > Main-Container*. Le texte rouge de la console est normal : JADE y écrit ses logs.

Le format d'un argument est `nom:classe`. Pour plusieurs agents, séparez-les par `;`.

## 5Erreur sur le port 1099

Symptôme dans la console : `Cannot bind server socket to localhost port 1099`, puis `No ICP active`. La cause est toujours la même : une ancienne plateforme JADE tourne encore et occupe le port.

1. Cliquez sur le **carré rouge** de la console Eclipse pour arrêter la plateforme, puis relancez. Faites-le avant chaque nouveau lancement.
2. Si l'erreur reste, cherchez le processus qui occupe le port :

```
netstat -ano | findstr 1099
tasklist /FI "PID eq 12345"
taskkill /PID 12345 /F
```

Remplacez `12345` par le dernier nombre de la ligne `LISTENING`. Vérifiez avec `tasklist` que le processus est bien `java.exe` ou `javaw.exe` avant de le tuer. Relancez `netstat` : plus aucune ligne ne doit s'afficher.

## 6Projet Maven

Maven gère les bibliothèques à votre place : on les décrit dans `pom.xml`, et Maven les cherche d'abord dans le dépôt local (`.m2`), puis sur Internet. Créez le projet Maven comme le demande le professeur, puis ouvrez son `pom.xml` et mettez ceci :

```
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>UMBB.FS..DZ</groupId>
  <artifactId>TPmaven</artifactId>
  <version>0.0.1-SNAPSHOT</version>

  <properties>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  </properties>

  <dependencies>
    <dependency>
      <groupId>com.tilab.jade</groupId>
      <artifactId>jade</artifactId>
      <version>4.6.0</version>
    </dependency>
  </dependencies>

  <repositories>
    <repository>
      <id>tilab</id>
      <url>https://jade.tilab.com/maven/</url>
    </repository>
  </repositories>
</project>
```

Le haut du fichier (`groupId` et `artifactId` du projet) décrit **votre projet**. Le bloc `<dependency>` décrit **JADE**. Ne les mélangez pas : si le projet porte le même nom que JADE, Maven s'embrouille.

Le `pom.xml` du prof écrit `com.tilab.Jade` et `Jade` avec des majuscules. Utilisez les minuscules ci-dessus : c'est le chemin publié par Tilab (`.../com/tilab/jade/jade/4.6.0/`).

1. Enregistrez avec Ctrl + S.
2. Clic droit sur le projet > *Maven > Update Project...* (ou Alt + F5), cochez *Force Update of Snapshots/Releases*, puis *OK*. Attendez la fin de la barre de progression en bas à droite.

**Résultat attendu**

Un dossier *Maven Dependencies* contenant `jade-4.6.0.jar` apparaît dans le projet, et la croix rouge du `pom.xml` disparaît. Si ce n'est pas le cas, passez à l'étape 7.

Ensuite, créez la classe `HelloAgent` dans `src/main/java` (même code qu'à l'étape 4) et lancez-la avec une nouvelle configuration : *Project* = votre projet Maven, *Main class* = `jade.Boot`, *Arguments* = `-gui Hello:agents.HelloAgent`.

## 7Maven ne télécharge pas JADE : erreur SSL

Symptômes : la croix rouge du `pom.xml` disparaît un instant puis revient, et l'onglet *Problems* affiche `Missing artifact com.tilab.jade:jade:jar:4.6.0` ou `Failed to read artifact descriptor`. Pas de dossier *Maven Dependencies*.

### Lire la vraie cause

Le message dans *Problems* est coupé. Maven garde la cause complète dans un fichier :

```
type "%USERPROFILE%\.m2\repository\com\tilab\jade\jade\4.6.0\jade-4.6.0.pom.lastUpdated"
```

Si le texte contient `PKIX path building failed ... unable to find valid certification path`, c'est le problème SSL : Java 8 ne fait pas confiance au certificat du serveur de Tilab, alors que votre navigateur, lui, y arrive. C'est ce que le professeur appelle « éliminer la vérification SSL ».

Non confirmé

### Option A : désactiver la vérification SSL

Fermez Eclipse. Dans `eclipse.ini` (dans le dossier d'Eclipse), ajoutez ces lignes **tout à la fin**, après les lignes `-Xms`, `-Xmx` et `--add-modules`. Une option par ligne, sans espace :

```
-Dmaven.wagon.http.ssl.insecure=true
-Dmaven.wagon.http.ssl.allowall=true
-Dmaven.wagon.http.ssl.ignore.validity.dates=true
-Daether.connector.https.securityMode=insecure
```

Puis supprimez le cache des échecs, relancez Eclipse et refaites *Update Project* avec *Force Update* :

```
rmdir /s /q "%USERPROFILE%\.m2\repository\com\tilab"
```

Chez nous, aucun téléchargement n'a réussi après cette modification, et on ne sait pas si Eclipse l'a prise en compte. Cette option coupe aussi la vérification de sécurité pour tous les téléchargements Maven : acceptable pour un TP, pas pour un vrai projet. Demandez au professeur la méthode qu'il utilise.

Confirmé

### Option B : placer le jar dans le dépôt local

Vous avez déjà `jade.jar` sur le disque. On le range là où Maven le cherche en premier, et il n'a plus besoin du serveur. Tapez les commandes **une par une**, avec Entrée après chacune. Deux commandes collées sur la même ligne provoquent une erreur de syntaxe.

```
mkdir "%USERPROFILE%\.m2\repository\com\tilab\jade\jade\4.6.0"
del /q "%USERPROFILE%\.m2\repository\com\tilab\jade\jade\4.6.0\*"
copy "D:\JADE\jade\lib\jade.jar" "%USERPROFILE%\.m2\repository\com\tilab\jade\jade\4.6.0\jade-4.6.0.jar"
notepad "%USERPROFILE%\.m2\repository\com\tilab\jade\jade\4.6.0\jade-4.6.0.pom"
```

La première commande peut répondre que le dossier existe déjà : c'est normal. Notepad propose de créer le fichier : cliquez sur *Oui*, collez ceci et enregistrez :

```
<project xmlns="http://maven.apache.org/POM/4.0.0">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.tilab.jade</groupId>
  <artifactId>jade</artifactId>
  <version>4.6.0</version>
</project>
```

Vérifiez le dossier :

```
dir "%USERPROFILE%\.m2\repository\com\tilab\jade\jade\4.6.0"
```

Il doit contenir exactement `jade-4.6.0.jar` et `jade-4.6.0.pom`. Si vous voyez `jade-4.6.0.pom.txt`, renommez le fichier. Puis, dans Eclipse : *Maven > Update Project...*, cette fois **sans** cocher *Force Update*. Le dossier *Maven Dependencies* apparaît avec `jade-4.6.0.jar`.

À savoir

Ce n'est pas le téléchargement par Maven que le TP demande : le projet fonctionne, mais l'étape « Maven télécharge JADE » n'a pas eu lieu. Dites-le au professeur. Cela marche parce que JADE n'a pas besoin d'autres bibliothèques. Une dépendance qui en demande d'autres échouera de la même façon.

## Erreurs fréquentes

| Ce que vous voyez | Cause | Solution |
| --- | --- | --- |
| `port 1099`, `No ICP active` | Une ancienne plateforme JADE tourne encore. | Carré rouge de la console, sinon `taskkill` (étape 5). |
| `PKIX path building failed` | Java 8 ne fait pas confiance au certificat de Tilab. | Étape 7, option B (ou A). |
| `javac` introuvable | Seul le JRE est installé. | Installer un JDK 8. |
| Croix rouge sur `pom.xml`, `Missing artifact` | Maven n'a pas pu télécharger JADE. | Lire le fichier `.lastUpdated` (étape 7). |
| Pas de dossier *Maven Dependencies* | Le jar n'est pas dans le dépôt local. | Étape 7. |
| `Could not download sources` ou `javadoc` | Maven cherche aussi les sources et la doc de JADE. | Sans importance si `jade-4.6.0.jar` est dans *Maven Dependencies*. |
| Avertissement `serializable class ... serialVersionUID` | Simple avertissement Java sur la classe de l'agent. | À ignorer. |
| `jade.jar` introuvable après extraction | L'extraction n'a pas eu lieu, ou elle est allée ailleurs. | Refaire l'étape 3 dans un dossier à part, puis `dir /s /b jade.jar`. |

Testé sous Windows avec un JDK 8 (Temurin), Eclipse et JADE 4.6.0. Les chemins comme `D:\JADE` sont des exemples : adaptez-les à votre machine.