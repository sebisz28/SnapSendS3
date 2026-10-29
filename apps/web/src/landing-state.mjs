export function resolveViewMode(pathname, search) {
  const query = new URLSearchParams(search);
  if (query.get('share')) return 'recipient';
  if ((pathname.replace(/\/+$/, '') || '/') === '/upload' || query.has('code')) return 'sender';
  return 'landing';
}

export function scrollProgress(trackTop, trackHeight, viewportHeight) {
  const travel = Math.max(trackHeight - viewportHeight, 1);
  return Math.min(1, Math.max(0, -trackTop / travel));
}
