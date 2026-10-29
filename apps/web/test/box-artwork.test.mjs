import test from 'node:test';
import assert from 'node:assert/strict';
import { readFile } from 'node:fs/promises';

test('box opening shows clipped interior light without a blue ribbon crossing it', async () => {
  const source = await readFile(new URL('../src/main.ts', import.meta.url), 'utf8');

  assert.match(source, /<clipPath id="box-interior">/);
  assert.match(source, /class="box-interior-light"[^>]*clip-path="url\(#box-interior\)"/);
  assert.doesNotMatch(source, /M192 111 210 103 228 111/);
  assert.doesNotMatch(source, /M192 91 210 83 228 91/);
});
