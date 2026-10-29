export type ViewMode = 'landing' | 'sender' | 'recipient';
export function resolveViewMode(pathname: string, search: string): ViewMode;
