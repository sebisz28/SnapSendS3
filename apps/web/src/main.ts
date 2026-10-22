import { bootstrapApplication } from '@angular/platform-browser';
import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  standalone: true,
  template: `<main><h1>SnapSendS3</h1><p>Secure, temporary file sharing.</p></main>`
})
class AppComponent {}

bootstrapApplication(AppComponent);
