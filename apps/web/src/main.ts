import 'zone.js';
import { bootstrapApplication } from '@angular/platform-browser';
import { AfterViewInit, ChangeDetectorRef, Component, ElementRef, NgZone, OnDestroy, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { resolveViewMode, scrollProgress, signInRedirectUri } from './landing-state.mjs';

type ShareResponse = { id: string; token: string; expiresAt: string; status: string; storageWarning?: string };
type UploadIntent = { uploadUrl: string };
type DownloadResponse = { downloadUrl: string; expiresAt: string };

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div [class.landing-shell]="viewMode === 'landing'">
    <header [class.topbar]="viewMode !== 'landing'" [class.landing-topbar]="viewMode === 'landing'">
      <a class="brand" href="/" aria-label="SnapSendS3 home"><span class="brand-mark" aria-hidden="true">S</span> SnapSendS3</a>
      @if (viewMode === 'landing') { <span class="landing-header-note">Private file delivery</span> }
      @if (viewMode === 'sender') { <button class="button secondary" type="button" (click)="accessToken ? signOut() : signIn()" [disabled]="authWorking">{{ authWorking ? 'Signing in…' : accessToken ? 'Sign out' : 'Sign in' }}</button> }
    </header>
    <main [class.page]="viewMode !== 'landing'" [class.landing-page]="viewMode === 'landing'">
      @if (viewMode === 'recipient') {
        <section class="card recipient-card" aria-labelledby="recipient-title">
          <p class="eyebrow">SECURE DELIVERY</p>
          <h1 id="recipient-title">Your files are being prepared</h1>
          <p class="intro">This link only works after every file has passed the security scan.</p>
          <p class="notice" role="status" aria-live="polite">{{ recipientMessage }}</p>
          @if (passwordRequired) {
            <form class="password-form" (submit)="submitRecipientPassword($event)">
              <label for="recipient-password">Share password</label>
              <input id="recipient-password" type="password" autocomplete="current-password" minlength="8" maxlength="128" required [value]="recipientPassword" (input)="setRecipientPassword($event)">
              <button class="button primary" type="submit" [disabled]="recipientWorking">{{ recipientWorking ? 'Checking…' : 'Unlock download' }}</button>
            </form>
          }
          @if (canRetryRecipient && !downloadUrl) { <button class="button secondary retry" type="button" (click)="checkRecipientAgain()" [disabled]="recipientWorking">{{ recipientWorking ? 'Checking…' : 'Check again' }}</button> }
          @if (downloadUrl) { <a class="button primary download" [href]="downloadUrl">Download files</a> }
        </section>
      } @else if (viewMode === 'landing') {
        <section #landingTrack class="landing-track" aria-labelledby="landing-title">
          <div class="landing-scene">
            <div class="landing-copy">
              <p class="landing-eyebrow">PRIVATE FILE DELIVERY</p>
              <h1 id="landing-title">Share files with confidence.</h1>
              <p>Important files deserve a quieter, more considered way to travel.</p>
            </div>
            <div class="box-stage" aria-hidden="true">
              <div class="box-halo"></div>
              <svg class="box-visual" viewBox="0 0 420 380" focusable="false">
                <defs>
                  <linearGradient id="box-front" x1="0" y1="0" x2="1" y2="1"><stop stop-color="#fff"/><stop offset="1" stop-color="#dbe6f5"/></linearGradient>
                  <linearGradient id="box-side" x1="0" y1="0" x2="1" y2="1"><stop stop-color="#8eb8f2"/><stop offset="1" stop-color="#477bc7"/></linearGradient>
                  <linearGradient id="box-lid" x1="0" y1="0" x2="1" y2="1"><stop stop-color="#fff"/><stop offset=".7" stop-color="#e9f0f9"/><stop offset="1" stop-color="#c7d8ed"/></linearGradient>
                  <linearGradient id="box-ribbon" x1="0" y1="0" x2="1" y2="1"><stop stop-color="#5b93e6"/><stop offset="1" stop-color="#245eb6"/></linearGradient>
                  <radialGradient id="box-light"><stop stop-color="#fff7df" stop-opacity=".96"/><stop offset=".48" stop-color="#f4d894" stop-opacity=".58"/><stop offset="1" stop-color="#f4d894" stop-opacity="0"/></radialGradient>
                  <clipPath id="box-interior"><path d="M91 153 210 105 329 153 210 202Z"/></clipPath>
                </defs>
                <ellipse cx="210" cy="333" rx="142" ry="25" fill="#020406" opacity=".54"/>
                <path d="M69 153 210 211 210 346 69 286Z" fill="url(#box-front)"/>
                <path d="M210 211 351 153 351 286 210 346Z" fill="url(#box-side)"/>
                <path d="M187 221 210 230 210 346 187 336Z" fill="url(#box-ribbon)" opacity=".9"/>
                <path d="M210 230 232 221 232 336 210 346Z" fill="#2f69bd" opacity=".88"/>
                <path d="M69 153 210 94 351 153 210 212Z" fill="#d6e0ee"/>
                <path d="M91 153 210 105 329 153 210 202Z" fill="#24313e" opacity=".82"/>
                <ellipse class="box-interior-light" cx="210" cy="165" rx="118" ry="62" fill="url(#box-light)" clip-path="url(#box-interior)"/>
                <g class="box-lid">
                  <path d="M69 143 210 84 351 143 210 202Z" fill="url(#box-lid)" stroke="#fff" stroke-opacity=".78" stroke-width="2"/>
                  <path d="M69 143 210 202 210 226 69 167Z" fill="#d9e5f4"/>
                  <path d="M210 202 351 143 351 167 210 226Z" fill="#7fa7dd"/>
                </g>
                <path d="M69 153 210 212 351 153" fill="none" stroke="#fff" stroke-opacity=".58" stroke-width="2"/>
              </svg>
            </div>
            <p class="scroll-cue"><span aria-hidden="true"></span>Scroll to open</p>
          </div>
        </section>
        <section class="upload-section" aria-labelledby="upload-cta-title">
          <div class="upload-card">
            <p class="landing-eyebrow">READY WHEN YOU ARE</p>
            <h2 id="upload-cta-title">Your files, thoughtfully delivered.</h2>
            <p>Choose your files and create a secure link in moments.</p>
            <button class="upload-cta" type="button" (click)="beginUpload()" [disabled]="authWorking">{{ authWorking ? 'Opening secure sign in…' : 'Upload your files' }} <span aria-hidden="true">→</span></button>
          </div>
        </section>
      } @else {
        <section class="intro-block">
          <p class="eyebrow">SENDER WORKSPACE</p>
          <h1>Share files securely.</h1>
          <p class="intro">Send operational documents through a private link. Files are checked before they can be downloaded.</p>
        </section>
        @if (!accessToken) { <p class="prototype-notice" role="note">Sign in with your Sender account to create a secure link.</p> }
        <section class="card" aria-labelledby="upload-title">
          <div class="section-heading"><span class="step">1</span><div><h2 id="upload-title">Choose your files</h2><p>Up to 100 MB per file and 500 MB per share.</p></div></div>
          <input id="files" class="visually-hidden" type="file" multiple (change)="selectFiles($event)" [disabled]="working || !accessToken" aria-describedby="file-help">
          <label class="file-picker" for="files"><span class="upload-icon" aria-hidden="true">↑</span><strong>Select files to share</strong><span>Choose one or more files from your device</span></label>
          <p class="helper" id="file-help">Files stay private until the recipient’s download is approved.</p>
          @if (files.length) {
            <ul class="file-list" aria-label="Selected files">
              @for (file of files; track $index) {
                <li><span class="file-icon" aria-hidden="true">DOC</span><span class="file-name">{{ file.name }}<small>{{ formatSize(file.size) }}</small></span><button class="remove" type="button" (click)="removeFile($index)" [disabled]="working" [attr.aria-label]="'Remove ' + file.name">Remove</button></li>
              }
            </ul>
            <div class="total"><span>{{ files.length }} {{ files.length === 1 ? 'file' : 'files' }}</span><strong>{{ formatSize(totalBytes) }}</strong></div>
          }
          <div class="password-setting">
            <label for="share-password">Password protect this share <span>(optional)</span></label>
            <input id="share-password" type="password" autocomplete="new-password" minlength="8" maxlength="128" [value]="senderPassword" (input)="setSenderPassword($event)" [disabled]="working || !!shareUrl" aria-describedby="password-help">
            <p class="helper" id="password-help">Use at least 8 characters. Send the password to your recipient separately.</p>
          </div>
          @if (error) { <p class="error" role="alert">{{ error }}</p> }
          @if (message) { <p class="notice" role="status" aria-live="polite">{{ message }}</p> }
          @if (shareUrl) {
            <div class="success" role="status"><strong>Your secure link has been created</strong><p>{{ shareStatusMessage }}</p>@if (storageWarning) { <p class="notice">{{ storageWarning }}</p> }<button class="button secondary retry" type="button" (click)="checkShareStatus()" [disabled]="statusWorking">{{ statusWorking ? 'Checking…' : 'Check status' }}</button><label for="share-link">Share link</label><div class="link-field"><input id="share-link" [value]="shareUrl" readonly><button class="button secondary" type="button" [class.copied]="copied" (click)="copyLink()">{{ copied ? 'Copied' : 'Copy link' }}</button></div>@if (copied) { <p class="copy-feedback" role="status" aria-live="polite" aria-atomic="true">Link copied to clipboard.</p> }<p class="expiry">Expires {{ expiresAt | date:'medium' }}</p><button class="button secondary retry" type="button" (click)="createAnotherLink()">Create another link</button></div>
          }
          <button class="button primary create" type="button" (click)="createAndUpload()" [disabled]="working || !files.length || !!shareUrl || !accessToken">
            {{ working ? 'Preparing secure link…' : 'Create secure link' }}
          </button>
          <p class="footnote">Every file is scanned before download. Your link expires after six hours.</p>
        </section>
      }
      @if (viewMode === 'landing') {
        <footer class="landing-footer">Private storage <span aria-hidden="true">·</span> Security checked before download <span aria-hidden="true">·</span> Six-hour expiry</footer>
      } @else {
        <footer><span class="shield" aria-hidden="true">✓</span> Private storage <span class="dot">·</span> Scan checked before download <span class="dot">·</span> Six-hour expiry</footer>
      }
    </main>
    </div>
  `,
})
class AppComponent implements AfterViewInit, OnDestroy {
  readonly issuer = `${location.origin}/auth/realms/snapsends3`;
  readonly clientId = 'snapsends3-web';
  readonly viewMode = resolveViewMode(location.pathname, location.search);
  readonly recipientToken = this.viewMode === 'recipient' ? new URLSearchParams(location.search).get('share') : null;
  @ViewChild('landingTrack') private landingTrack?: ElementRef<HTMLElement>;
  private readonly reducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)');
  private animationFrame = 0;
  private readonly updateReveal = (): void => {
    const track = this.landingTrack?.nativeElement;
    if (!track) return;
    if (this.reducedMotion.matches) {
      this.setReveal(1);
      return;
    }
    if (this.animationFrame) return;
    this.animationFrame = requestAnimationFrame(() => {
      this.animationFrame = 0;
      const currentTrack = this.landingTrack?.nativeElement;
      if (currentTrack) this.setReveal(scrollProgress(currentTrack.getBoundingClientRect().top, currentTrack.offsetHeight, window.innerHeight));
    });
  };
  private readonly updateMotionPreference = (): void => this.updateReveal();
  readonly fileLimit = 100 * 1024 * 1024;
  readonly shareLimit = 500 * 1024 * 1024;
  files: File[] = [];
  working = false;
  error = '';
  message = '';
  shareUrl = '';
  shareId = '';
  shareStatus = '';
  storageWarning = '';
  statusWorking = false;
  expiresAt = '';
  copied = false;
  private copyFeedbackTimer: number | undefined;
  downloadUrl = '';
  recipientMessage = 'Checking link status…';
  senderPassword = '';
  recipientPassword = '';
  passwordRequired = false;
  recipientWorking = false;
  canRetryRecipient = false;
  accessToken = sessionStorage.getItem('snapsends3.access-token') ?? '';
  refreshToken = sessionStorage.getItem('snapsends3.refresh-token') ?? '';
  authWorking = false;

  constructor(private readonly changeDetector: ChangeDetectorRef, private readonly zone: NgZone) {
    void this.finishSignIn();
    if (this.recipientToken) void this.loadRecipientDownload(this.recipientToken);
  }

  ngAfterViewInit(): void {
    if (this.viewMode !== 'landing' || !this.landingTrack) return;
    this.zone.runOutsideAngular(() => {
      window.addEventListener('scroll', this.updateReveal, { passive: true });
      window.addEventListener('resize', this.updateReveal);
      this.reducedMotion.addEventListener('change', this.updateMotionPreference);
      this.updateReveal();
    });
  }

  ngOnDestroy(): void {
    window.removeEventListener('scroll', this.updateReveal);
    window.removeEventListener('resize', this.updateReveal);
    this.reducedMotion.removeEventListener('change', this.updateMotionPreference);
    if (this.animationFrame) cancelAnimationFrame(this.animationFrame);
  }

  private setReveal(progress: number): void {
    const track = this.landingTrack?.nativeElement;
    if (!track) return;
    track.style.setProperty('--lid-angle', `${-62 * progress}deg`);
    track.style.setProperty('--interior-light', String(0.08 + 0.7 * progress));
    track.style.setProperty('--box-lift', `${-10 * progress}px`);
  }

  get totalBytes(): number {
    return this.files.reduce((total, file) => total + file.size, 0);
  }

  get shareStatusMessage(): string {
    switch (this.shareStatus) {
      case 'UPLOADING': return 'Files are still uploading.';
      case 'SCANNING': return 'Files are being scanned. Recipients cannot download yet.';
      case 'ARCHIVING': return 'Creating the download archive. Recipients cannot download yet.';
      case 'READY': return 'Files are ready for the recipient to download.';
      case 'REJECTED': return 'This share cannot be downloaded because security checks failed.';
      case 'EXPIRED': return 'This share has expired.';
      default: return 'Checking the share status…';
    }
  }

  selectFiles(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.files = [...this.files, ...Array.from(input.files ?? [])];
    input.value = '';
    this.error = this.validateFiles();
  }

  removeFile(index: number): void {
    this.files = this.files.filter((_, itemIndex) => itemIndex !== index);
    this.error = this.validateFiles();
  }

  setSenderPassword(event: Event): void {
    this.senderPassword = (event.target as HTMLInputElement).value;
    this.error = this.validateFiles();
  }

  setRecipientPassword(event: Event): void {
    this.recipientPassword = (event.target as HTMLInputElement).value;
  }

  async submitRecipientPassword(event: Event): Promise<void> {
    event.preventDefault();
    await this.loadRecipientDownload(this.recipientToken!, this.recipientPassword);
  }

  async checkRecipientAgain(): Promise<void> {
    if (this.recipientToken) await this.loadRecipientDownload(this.recipientToken, this.recipientPassword || undefined);
  }

  formatSize(bytes: number): string {
    return bytes < 1024 * 1024 ? `${Math.max(1, Math.round(bytes / 1024))} KB` : `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
  }

  async createAndUpload(): Promise<void> {
    this.error = this.validateFiles();
    if (this.error) return;
    this.working = true;
    this.message = 'Creating your private share…';
    try {
      const share = await this.request<ShareResponse>('/api/v1/shares', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(this.senderPassword ? { password: this.senderPassword } : {}),
      });
      for (let index = 0; index < this.files.length; index++) {
        const file = this.files[index];
        this.message = `Uploading file ${index + 1} of ${this.files.length}…`;
        const intent = await this.request<UploadIntent>(`/api/v1/shares/${share.id}/files`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ fileName: file.name, sizeBytes: file.size, contentType: file.type || 'application/octet-stream' }),
        });
        const upload = await fetch(intent.uploadUrl, {
          method: 'PUT',
          headers: { 'Content-Type': file.type || 'application/octet-stream' },
          body: file,
        });
        if (!upload.ok) throw new Error(`Could not upload ${file.name}. Please try again.`);
      }
      this.message = 'Finalizing and starting security scans…';
      const finalized = await this.request<ShareResponse>(`/api/v1/shares/${share.id}/finalization`, { method: 'POST' });
      this.shareId = finalized.id;
      this.shareStatus = finalized.status;
      this.storageWarning = finalized.storageWarning ?? '';
      this.expiresAt = finalized.expiresAt;
      this.shareUrl = `${location.origin}${location.pathname}?share=${encodeURIComponent(share.token)}`;
      this.message = '';
    } catch (cause) {
      this.error = cause instanceof Error ? cause.message : 'The share could not be created. Please try again.';
      this.message = '';
    } finally {
      this.working = false;
      this.changeDetector.detectChanges();
    }
  }

  async copyLink(): Promise<void> {
    this.copied = false;
    this.error = '';
    window.clearTimeout(this.copyFeedbackTimer);
    try {
      await navigator.clipboard.writeText(this.shareUrl);
      this.copied = true;
      this.copyFeedbackTimer = window.setTimeout(() => {
        this.copied = false;
        this.changeDetector.detectChanges();
      }, 4000);
    } catch {
      this.error = 'Could not copy automatically. Select and copy the link above.';
    }
  }

  createAnotherLink(): void {
    location.assign(location.pathname);
  }

  beginUpload(): void {
    if (this.accessToken) {
      location.assign('/upload');
      return;
    }
    void this.signIn('/upload');
  }

  async signIn(requestedPath = location.pathname): Promise<void> {
    this.authWorking = true;
    const verifier = this.randomValue();
    const state = this.randomValue();
    sessionStorage.setItem('snapsends3.pkce-verifier', verifier);
    sessionStorage.setItem('snapsends3.oauth-state', state);
    const digest = await crypto.subtle.digest('SHA-256', new TextEncoder().encode(verifier));
    const challenge = this.base64Url(new Uint8Array(digest));
    const parameters = new URLSearchParams({ client_id: this.clientId, response_type: 'code', redirect_uri: signInRedirectUri(location.origin, location.pathname, requestedPath), scope: 'openid', state, code_challenge: challenge, code_challenge_method: 'S256' });
    location.assign(`${this.issuer}/protocol/openid-connect/auth?${parameters}`);
  }

  signOut(): void {
    sessionStorage.removeItem('snapsends3.access-token');
    sessionStorage.removeItem('snapsends3.refresh-token');
    this.accessToken = '';
    this.refreshToken = '';
    location.assign(location.pathname);
  }

  async checkShareStatus(): Promise<void> {
    if (!this.shareId) return;
    this.statusWorking = true;
    this.error = '';
    try {
      const share = await this.request<ShareResponse>(`/api/v1/shares/${this.shareId}`, {
      });
      this.shareStatus = share.status;
      this.storageWarning = share.storageWarning ?? '';
      this.expiresAt = share.expiresAt;
    } catch (cause) {
      this.error = cause instanceof Error ? cause.message : 'Could not check the share status.';
    } finally {
      this.statusWorking = false;
      this.changeDetector.detectChanges();
    }
  }

  private async loadRecipientDownload(token: string, password?: string): Promise<void> {
    this.recipientWorking = true;
    this.canRetryRecipient = false;
    try {
      const result = await this.request<DownloadResponse>(`/api/v1/public/shares/${encodeURIComponent(token)}/download`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(password ? { password } : {}),
      });
      this.downloadUrl = result.downloadUrl;
      this.passwordRequired = false;
      this.recipientMessage = `Your files are ready. This share expires ${new Date(result.expiresAt).toLocaleString()}. The download link is short-lived; reload this page if it expires.`;
    } catch (cause) {
      if (cause instanceof Error && 'status' in cause && cause.status === 401) {
        this.passwordRequired = true;
        this.recipientMessage = 'Enter the share password, or check that the password is correct.';
      } else if (cause instanceof Error && 'status' in cause && cause.status === 409) {
        this.canRetryRecipient = true;
        this.recipientMessage = 'The files are still being checked. Check again in a moment.';
      } else {
        this.recipientMessage = cause instanceof Error ? cause.message : 'This share is not ready or has expired.';
      }
    } finally {
      this.recipientWorking = false;
      this.changeDetector.detectChanges();
    }
  }

  private async finishSignIn(): Promise<void> {
    const parameters = new URLSearchParams(location.search);
    const code = parameters.get('code');
    if (!code) return;
    if (parameters.get('state') !== sessionStorage.getItem('snapsends3.oauth-state')) {
      this.error = 'Sign-in verification failed. Please try again.';
      return;
    }
    this.authWorking = true;
    try {
      const body = new URLSearchParams({ grant_type: 'authorization_code', client_id: this.clientId, code, redirect_uri: location.origin + location.pathname, code_verifier: sessionStorage.getItem('snapsends3.pkce-verifier') ?? '' });
      const response = await fetch(`${this.issuer}/protocol/openid-connect/token`, { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, body });
      if (!response.ok) throw new Error('Sign-in could not be completed. Please try again.');
      this.storeTokens(await response.json());
      location.replace(location.pathname);
    } catch (cause) {
      this.error = cause instanceof Error ? cause.message : 'Sign-in could not be completed. Please try again.';
    } finally {
      sessionStorage.removeItem('snapsends3.pkce-verifier');
      sessionStorage.removeItem('snapsends3.oauth-state');
      this.authWorking = false;
    }
  }

  private validateFiles(): string {
    if (this.senderPassword && (this.senderPassword.length < 8 || this.senderPassword.length > 128)) return 'A share password must be between 8 and 128 characters.';
    if (this.files.some(file => file.size <= 0)) return 'Empty files cannot be shared.';
    if (this.files.some(file => file.size > this.fileLimit)) return 'Each file must be 100 MB or smaller.';
    if (this.totalBytes > this.shareLimit) return 'The combined size must be 500 MB or smaller.';
    return '';
  }

  private async request<T = unknown>(url: string, init: RequestInit = {}): Promise<T> {
    const headers = new Headers(init.headers);
    const senderRequest = url.startsWith('/api/v1/shares');
    if (senderRequest && this.accessToken) headers.set('Authorization', `Bearer ${this.accessToken}`);
    let response = await fetch(url, { ...init, headers });
    if (senderRequest && response.status === 401 && await this.refreshAccessToken()) {
      headers.set('Authorization', `Bearer ${this.accessToken}`);
      response = await fetch(url, { ...init, headers });
    }
    if (!response.ok) {
      let detail = '';
      try { detail = (await response.json()).detail ?? ''; } catch { /* Use the status fallback. */ }
      const error = new Error(detail || `Request failed (${response.status}).`);
      Object.assign(error, { status: response.status });
      throw error;
    }
    return response.status === 204 ? undefined as T : await response.json() as T;
  }

  private randomValue(): string {
    const bytes = new Uint8Array(32);
    crypto.getRandomValues(bytes);
    return this.base64Url(bytes);
  }

  private base64Url(bytes: Uint8Array): string {
    return btoa(String.fromCharCode(...bytes)).replaceAll('+', '-').replaceAll('/', '_').replaceAll('=', '');
  }

  private async refreshAccessToken(): Promise<boolean> {
    if (!this.refreshToken) return false;
    try {
      const body = new URLSearchParams({ grant_type: 'refresh_token', client_id: this.clientId, refresh_token: this.refreshToken });
      const response = await fetch(`${this.issuer}/protocol/openid-connect/token`, {
        method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, body,
      });
      if (!response.ok) return false;
      this.storeTokens(await response.json());
      return true;
    } catch {
      return false;
    }
  }

  private storeTokens(tokens: { access_token?: string; refresh_token?: string }): void {
    if (!tokens.access_token) throw new Error('Sign-in did not return an access token.');
    this.accessToken = tokens.access_token;
    sessionStorage.setItem('snapsends3.access-token', this.accessToken);
    if (tokens.refresh_token) {
      this.refreshToken = tokens.refresh_token;
      sessionStorage.setItem('snapsends3.refresh-token', this.refreshToken);
    }
  }
}

bootstrapApplication(AppComponent);
