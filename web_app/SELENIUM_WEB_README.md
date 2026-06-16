# Web E2E Automation Testing - FoodShare

This folder contains the Selenium scripts to validate the deployed web application.

## Prerequisites
1.  **Node.js** installed on your machine.
2.  **Chrome Browser** and **ChromeDriver** installed.

## Setup
1.  Navigate to the test directory:
    ```bash
    cd web_app/selenium-tests
    ```
2.  Install dependencies:
    ```bash
    npm install
    ```

## Running Tests
To execute the login validation test:
```bash
npm test
```

## Test Coverage
- Navigates to the deployed URL.
- Enters test credentials into `#email` and `#password`.
- Clicks `#login-button`.
- Verifies successful redirection to the dashboard.
