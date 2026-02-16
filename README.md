DESCRIPTION DU PROJET
====================================================

Pay My Buddy est une application web développée en Java avec Spring Boot
permettant aux utilisateurs :

<li>de s’enregistrer via une adresse email unique</li>
<li>de se connecter de manière sécurisée</li>
<li>d’ajouter des relations (amis)</li>
<li>d’effectuer des transactions d’argent entre utilisateurs</li>
<li>de consulter l’historique des transactions</li><br />

<p>Le projet respecte les bonnes pratiques de développement :</p>

<li>Architecture en couches (Controller / Service / Repository))</li>
<li>Gestion des transactionss</li>
<li>Sécurisation de l’accès aux données</li>
<li>Respect des standards d’interface et d’accessibilité (WCAG)</li>

MODÈLE PHYSIQUE DE DONNÉES (MPD)
====================================================

<img width="707" height="318" alt="MPD" src="https://github.com/user-attachments/assets/b85aaf9f-823b-4cd4-8615-90bd5168139c" />

COUCHE DAL / RÉFÉRENTIEL
====================================================

L’accès aux données est assuré via :

<li>Spring Data JPA</li>
<li>Interfaces Repository</li>
<li>ervices métier intermédiaires</li><br />

La gestion des transactions est assurée via :

<li>@Transactional</li>
<li>Commit automatique en cas de succès</li>
<li>Rollback automatique en cas d’exception</li><br />

SÉCURISATION
====================================================

<li>Authentification via Spring Security</li>
<li>Mot de passe encodé (BCrypt)</li>
<li>Accès protégé aux routes sécurisées</li>
<li>Identifiants BDD non exposés en dur (configuration via application.properties)</li><br />

INTERFACE WEB
====================================================

L’interface est développée avec :

<li>Thymeleaf</li>
<li>HTML / CSS</li>
<li>Respect et amélioration des maquettes Figma</li><br />

Bonnes pratiques respectées :

<li>Ergonomie claire</li>
<li>Navigation intuitive</li>
<li>Messages d’erreur explicites</li>
<li>Accessibilité</li><br />

TESTS
====================================================

Tests réalisés avec :

<li>JUnit</li>
<li>Mockito</li>
<li>Spring Boot Test</li>
<li>MockMvc</li><br />

Les tests couvrent :

<li>Sécurité</li>
<li>Accès aux pages</li>
<li>Cas d’utilisation principaux</li><br />
