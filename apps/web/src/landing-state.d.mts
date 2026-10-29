export type ViewMode = 'landing' | 'sender' | 'recipient';
export function resolveViewMode(pathname: string, search: string): ViewMode;
export function scrollProgress(trackTop: number, trackHeight: number, viewportHeight: number): number;
