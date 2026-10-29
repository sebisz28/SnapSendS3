# SnapSendS3 Keycloak login theme

The `themes/snapsends3/login` theme extends Keycloak 26's `keycloak.v2` login theme and changes presentation with CSS only. Keycloak continues to render and validate its existing sign-in, password recovery, and error forms.

The base Compose configuration mounts the theme read-only at `/opt/keycloak/themes/snapsends3`. The realm import selects `snapsends3` as the login theme for a new realm.

## Existing realms

Keycloak skips a realm import at startup when that realm already exists. Deploying the files therefore does not change an existing realm's selected theme automatically. To activate the presentation without overwriting users or realm state:

1. Open the Keycloak Admin Console and select the `snapsends3` realm.
2. Open **Realm settings → Themes**.
3. Set **Login theme** to `snapsends3` and save.
4. Open the application sign-in page and confirm the themed credential form renders.

Do not delete realm data or force an import just to activate the login theme. Authentication flows, credentials, client settings, and user data remain Keycloak-managed.
