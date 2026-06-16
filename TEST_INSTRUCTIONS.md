# FoodShare Automation Test Instructions

Use this guide to run the automation tests and see the pass/fail results.

## 1. AI Logic Validation (Unit Tests)
Validates the ML-based expiry prediction model.
- **Command**: `./gradlew test`
- **Expected Output**: `BUILD SUCCESSFUL` with 3+ tests passed.

## 2. Web E2E (Selenium)
Validates the React web deployment.
- **Prerequisites**: Node.js installed.
- **Commands**:
  ```bash
  cd web_app/selenium-tests
  npm install
  npm test
  ```
- **Expected Output**: A browser window opens, performs login, and reports `3 passing`.

## 3. API Security (DAST)
Runs security probes against the backend.
- **Setup**: Create `input.json` with your `baseUrl`.
- **Command**: `bash automated_test/security_audit.sh`
- **Expected Output**: A severity-ranked list of findings in `report.json`.

## 4. Android UI Tests (Espresso)
Validates the native Android registration flow.
- **Command**: `./gradlew connectedAndroidTest`
- **Expected Output**: The emulator performs a registration automatically and reports `SUCCESS`.
