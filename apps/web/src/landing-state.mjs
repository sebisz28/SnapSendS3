export function resolveViewMode(pathname, search) {
  const query = new URLSearchParams(search);
  if (query.get('share')) return 'recipient';
  if ((pathname.replace(/\/+$/, '') || '/') === '/upload' || query.has('code')) return 'sender';
  return 'landing';
}
