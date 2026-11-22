<#macro content>
<#-- Pied de la page de connexion : point d'extension prévu par Keycloak (seul modèle surchargé).
     Liens vers les CGU et la politique de confidentialité, lisibles dans la PWA sans être connecté. -->
<nav class="lm-legal" aria-label="${msg("lmLegalLabel")}">
  <a href="${properties.lmPwaUrl!}/#/cgu" target="_blank" rel="noopener">${msg("lmTermsLink")}</a>
  <span aria-hidden="true">·</span>
  <a href="${properties.lmPwaUrl!}/#/confidentialite" target="_blank" rel="noopener">${msg("lmPrivacyLink")}</a>
</nav>
</#macro>
