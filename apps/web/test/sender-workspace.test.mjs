import test from 'node:test';
import assert from 'node:assert/strict';
import { readFile } from 'node:fs/promises';

test('sender workspace has a scoped colorful hero and keeps its upload form controls', async () => {
  const template = await readFile(new URL('../src/main.ts', import.meta.url), 'utf8');
  const styles = await readFile(new URL('../src/styles.css', import.meta.url), 'utf8');

  assert.match(template, /\[class\.sender-shell\]="viewMode === 'sender'"/);
  assert.match(template, /class="sender-hero"/);
  assert.match(template, /class="sender-art" aria-hidden="true"/);
  assert.match(template, /class="card sender-upload-card"/);
  assert.match(template, /id="files" class="visually-hidden" type="file"/);
  assert.match(styles, /\.sender-shell\s*\{/);
  assert.match(styles, /\.sender-hero\s*\{/);
});
