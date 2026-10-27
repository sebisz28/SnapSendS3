# Acceptance Criteria

## Epic Link

Addresses Epic AC: SNS001 recipient download delivery and archive behavior.

## Happy Path Criteria

- [ ] AC1: A ready share containing one uploaded file returns a downloadable response whose saved filename equals the uploaded filename and whose bytes equal the uploaded bytes.
- [ ] AC2: A ready share containing two or more uploaded files returns a valid `snapsends3-share.zip` archive containing each uploaded file.

## Error Handling Criteria

- [ ] AC3: A non-ready share continues to return HTTP 409 before a download URL is issued.
- [ ] AC4: An expired share continues to return HTTP 410 before a download URL is issued.
- [ ] AC5: A password-protected share without the correct password continues to return HTTP 401 before a download URL is issued.

## Edge Case Criteria

- [ ] AC6: A filename containing a quote, carriage return, or line feed cannot inject response headers and remains downloadable under a safe filename.

## Integration Criteria

- [ ] AC7: At `https://localhost:8443`, the Caddy-proxied object response has an attachment disposition matching the signed delivery name.
