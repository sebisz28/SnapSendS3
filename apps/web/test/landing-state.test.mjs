import test from 'node:test';
import assert from 'node:assert/strict';
import { resolveViewMode, scrollProgress } from '../src/landing-state.mjs';

test('view selection keeps landing, sender, callback, and recipient paths distinct', () => {
  // Given the requested browser pathname and query, when the view is selected,
  // then recipient links win, /upload and OIDC callbacks show the sender, and root shows landing.
  const cases = [
    ['/', '', 'landing'],
    ['/upload', '', 'sender'],
    ['/upload/', '', 'sender'],
    ['/', '?code=auth-code&state=oauth-state', 'sender'],
    ['/', '?share=legacy-token', 'recipient'],
    ['/upload', '?share=new-token', 'recipient'],
    ['/', '?share=', 'landing'],
  ];

  for (const [path, query, expected] of cases) {
    assert.equal(resolveViewMode(path, query), expected, `${path}${query}`);
  }
});

test('scroll progress clamps at the scene boundaries and interpolates in between', () => {
  // Given the scene top, scene height, and viewport height, when progress is calculated,
  // then it stays between 0 and 1 and reaches 0.5 halfway through the sticky travel.
  assert.equal(scrollProgress(20, 200, 100), 0);
  assert.equal(scrollProgress(0, 200, 100), 0);
  assert.equal(scrollProgress(-50, 200, 100), 0.5);
  assert.equal(scrollProgress(-100, 200, 100), 1);
  assert.equal(scrollProgress(-150, 200, 100), 1);
});
