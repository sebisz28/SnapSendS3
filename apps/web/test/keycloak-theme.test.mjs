import test from 'node:test';
import assert from 'node:assert/strict';
import { readFile } from 'node:fs/promises';

const keycloakRoot = new URL('../../../ops/keycloak/', import.meta.url);

test('Keycloak serves the SnapSendS3 theme and the realm selects it', async () => {
  const realm = JSON.parse(await readFile(new URL('snapsends3-realm.json', keycloakRoot), 'utf8'));
  const properties = await readFile(new URL('themes/snapsends3/login/theme.properties', keycloakRoot), 'utf8');
  const stylesheet = await readFile(new URL('themes/snapsends3/login/resources/css/snapsends3.css', keycloakRoot), 'utf8');
  const compose = await readFile(new URL('../../../compose.yaml', import.meta.url), 'utf8');

  assert.equal(realm.loginTheme, 'snapsends3');
  assert.match(properties, /^parent=keycloak\.v2$/m);
  assert.match(properties, /styles=css\/styles\.css css\/snapsends3\.css/);
  assert.match(stylesheet, /#kc-page-title/);
  assert.match(stylesheet, /\.pf-v5-c-button\.pf-m-primary/);
  assert.match(compose, /ops\/keycloak\/themes\/snapsends3:\/opt\/keycloak\/themes\/snapsends3:ro/);
});
