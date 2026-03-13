# API Automation Framework Documentation

## 1. Overview

This document describes the APIs automated in the Java API automation framework under `src/test/java`. The framework uses `RestAssured`, `TestNG`, `ApiTestExecutor`, `Report`, and shared stores such as `ProjectStore`, `ConnectionStore`, `PromptStore`, `RoleStore`, `BusinessRequirementStore`, and `TestCaseStore`.

The main orchestration is in `EndToEndFlow`, with queue cleanup covered by `QueueFlowRunner`.

## 2. Framework Execution Pattern

1. Test data is loaded from `src/test/resources/testdata/...json`.
2. Dynamic values are injected from `TokenUtil` or framework stores.
3. `ApiTestExecutor.execute(...)` logs payload, status code, and response.
4. Successful responses push important IDs into stores for downstream APIs.

## 3. Authentication Model

Authentication is handled through `TokenUtil`.

- `TokenUtil.getToken(UserRole role)` logs in through `/api/login` if no cached token exists or the cached token expired.
- Tokens are cached per role for 10 minutes.
- `userId` is extracted from the login response and cached per role.
- Roles used in the framework:
  - `SUPER_ADMIN`
  - `ADMIN`
  - `END_USER`
- Common auth modes in test data:
  - `VALID`
  - `MISSING`
  - `INVALID`

## 4. Core API Documentation

### 4.1 Create Project API

- Endpoint Name: Create Project
- Endpoint URL: `/api/createProject`
- HTTP Method: `POST`
- Purpose / Description: Creates a new project after organization and connection setup. This is the main project bootstrap API in the end-to-end flow.
- Authentication: `Authorization` is injected from `TokenUtil.getToken(UserRole.valueOf(role))`. Usually executed as `SUPER_ADMIN`.
- Request Payload:
```json
{
  "userId": "{{userId}}",
  "projectName": "{{projectName}}",
  "projectDescription": "Demo Project",
  "projectSummary": null,
  "projectStartDate": "{{auto}}",
  "projectEndDate": "{{auto}}",
  "projectCreatedBy": "{{userId}}",
  "webFramework": "Playwright_Java",
  "mobileFrameworks": "Appium_Java",
  "autonomous": 0,
  "projectDomain": "Telecommunications",
  "testType": null,
  "InsightsBasedOnExistingAssets": "yes",
  "refOrgId": "1",
  "storageType": "S3",
  "connectionId": "{{connectionId}}",
  "devopsProjectName": "2702f85d-556d-4d32-a00b-87840ca2739d",
  "teams": "bcc44224-daec-4f79-9bdd-eb381d709b0a",
  "platform": "{{platform}}"
}
```
- Dynamic Data Source:
  - `userId`, `projectCreatedBy`: `TokenUtil`
  - `projectName`: generated in `CreateProjectTest`
  - `connectionId`, `platform`: `ConnectionStore`
- Sample Request Example:
```json
{
  "userId": "32",
  "projectName": "Automation_1763000000000",
  "projectDescription": "Demo Project",
  "projectStartDate": "2026-03-12",
  "projectEndDate": "2026-04-11",
  "projectCreatedBy": "32",
  "webFramework": "Playwright_Java",
  "mobileFrameworks": "Appium_Java",
  "autonomous": 0,
  "projectDomain": "Telecommunications",
  "InsightsBasedOnExistingAssets": "yes",
  "refOrgId": "1",
  "storageType": "S3",
  "connectionId": "87",
  "devopsProjectName": "2702f85d-556d-4d32-a00b-87840ca2739d",
  "teams": "bcc44224-daec-4f79-9bdd-eb381d709b0a",
  "platform": "azure"
}
```
- Sample Response:
```json
{
  "success": true,
  "project_id": 518,
  "message": "Project created successfully"
}
```
- Validation Logic Used in Automation:
  - Validates expected status code from JSON test data
  - Extracts `project_id`
  - Stores `projectId` and `projectName` in `ProjectStore`
  - Marks selected project in `ProjectStore`
  - Logs payload and response in `Report`
- Automation Implementation:
  - Test class: `CreateProjectTest`
  - API class: `CreateProjectApi`
  - JSON test data file: `testdata/project/createProject.json`
- Example Console Logs:
```text
Payload -> {userId=32, projectName=Automation_1763000000000, connectionId=87, platform=azure}
Response Status -> 200
Project Created & Registered -> ID: 518, Name: Automation_1763000000000
Stored ProjectId -> 518
```

### 4.2 Edit Project API

- Endpoint Name: Edit Project
- Endpoint URL: `/api/editProject`
- HTTP Method: `PUT`
- Purpose / Description: Updates the selected project with current dates, framework, storage type, and connection metadata after creation.
- Authentication: Token from `TokenUtil`; typically `SUPER_ADMIN`.
- Request Payload:
```json
{
  "projectId": "{{dynamic}}",
  "userId": "{{dynamic}}",
  "projectName": "{{dynamic}}",
  "projectStartDate": "{{dynamic}}",
  "projectEndDate": "{{dynamic}}",
  "storageType": "{{dynamic}}",
  "webFramework": "{{dynamic}}",
  "devopsProjectName": "2702f85d-556d-4d32-a00b-87840ca2739d",
  "teamName": "bcc44224-daec-4f79-9bdd-eb381d709b0a"
}
```
- Dynamic Data Source:
  - `projectId`, `projectName`, `storageType`, `webFramework`: `ProjectStore`
  - `userId`: `ProjectStore` fallback to `TokenUtil`
  - `connectionId`, `platform`: `ConnectionStore`
- Sample Request Example:
```json
{
  "projectId": "518",
  "userId": "32",
  "projectName": "Automation_1763000000000",
  "projectStartDate": "2026-03-12",
  "projectEndDate": "2026-04-12",
  "storageType": "S3",
  "webFramework": "Playwright_Java",
  "projectDescription": "automations",
  "projectDomain": "Telecommunications",
  "insightsBasedOnExistingAssets": 1,
  "connectionId": 87,
  "platform": "azure",
  "devopsProjectName": "2702f85d-556d-4d32-a00b-87840ca2739d",
  "teamName": "bcc44224-daec-4f79-9bdd-eb381d709b0a"
}
```
- Sample Response:
```json
{
  "success": true,
  "message": "Project updated successfully"
}
```
- Validation Logic Used in Automation:
  - Rebuilds final payload in `buildEditProjectRequest`
  - Logs final payload
  - Validates expected status code through `ApiTestExecutor`
  - Response is captured in report for traceability
- Automation Implementation:
  - Test class: `EditProjectTest`
  - API class: `EditProjectApi`
  - JSON test data file: `testdata/project/editProject.json`
- Example Console Logs:
```text
DEBUG SelectedProjectId -> 518
Mapped Request -> {projectId=518, userId=32, projectName=Automation_1763000000000, storageType=S3}
Actual Status Code -> 200
```

### 4.3 Register User API

- Endpoint Name: Register User
- Endpoint URL: `/api/registerUser`
- HTTP Method: `POST`
- Purpose / Description: Registers a new user in the selected organization. Executed immediately after organization lookup.
- Authentication: Uses `TokenUtil` unless auth type disables header injection. Normally run as `SUPER_ADMIN`.
- Request Payload:
```json
{
  "userFirstName": "{{firstName}}",
  "userLastName": "{{lastName}}",
  "userEmailId": "{{email}}",
  "userPassword": "{{password}}",
  "userPhoneNumber": "{{phone}}",
  "userPhoneCountryCode": "91",
  "organizationId": "{{orgId}}"
}
```
- Dynamic Data Source:
  - `organizationId`: `OrganizationStore`
  - `userFirstName`, `userLastName`, `userEmailId`, `userPassword`: `TestDataGenerator`
- Sample Request Example:
```json
{
  "userFirstName": "Arun",
  "userLastName": "Kumar",
  "userEmailId": "arun.kumar.1763000000000@testmail.com",
  "userPassword": "Valid@12345",
  "userPhoneNumber": "9876543210",
  "userPhoneCountryCode": "91",
  "organizationId": 5
}
```
- Sample Response:
```json
{
  "success": true,
  "message": "User registered successfully",
  "userId": 41
}
```
- Validation Logic Used in Automation:
  - Validates expected status code `201`
  - Logs generated payload into `Report`
  - Prints full registration response to console
  - Does not persist the created user in a dedicated store
- Automation Implementation:
  - Test class: `RegisterUserTest`
  - API class: `RegisterUserApi`
  - JSON test data file: `testdata/userManagement/registerUser.json`
- Example Console Logs:
```text
Payload -> {userFirstName=Arun, userEmailId=arun.kumar.1763000000000@testmail.com, organizationId=5}
Response Status -> 201
Register User Response -> {success=true, userId=41}
```

### 4.4 Get Organizations API

- Endpoint Name: Get Organizations
- Endpoint URL: `/api/Organizations`
- HTTP Method: `POST`
- Purpose / Description: Fetches the organizations visible to the logged-in user. The flow uses it to identify and store the `Tickingminds` organization.
- Authentication: `TokenUtil` bearer token; generally `SUPER_ADMIN`.
- Request Payload:
```json
{
  "userId": "{{userId}}"
}
```
- Dynamic Data Source:
  - `userId`: `TokenUtil`
- Sample Request Example:
```json
{
  "userId": 32
}
```
- Sample Response:
```json
{
  "data": [
    {
      "clientId": 5,
      "clientName": "Tickingminds"
    }
  ]
}
```
- Validation Logic Used in Automation:
  - Validates status code
  - Parses `data`
  - Searches for `clientName = Tickingminds`
  - Stores `clientId` and `clientName` into `OrganizationStore`
  - Logs payload and response to report
- Automation Implementation:
  - Test class: `GetOrganizationsTest`
  - API class: `GetOrganizationsApi`
  - JSON test data file: `testdata/organization/getOrganizations.json`
- Example Console Logs:
```text
Payload -> {userId=32}
Response Status -> 200
Organization Found -> Tickingminds | ID -> 5
```

### 4.5 Save Connection API

- Endpoint Name: Save Connection
- Endpoint URL: `/api/saveConnection`
- HTTP Method: `POST`
- Purpose / Description: Saves a DevOps or external system connection required for project creation and pipeline-related APIs.
- Authentication: Token via `TokenUtil`; usually `SUPER_ADMIN`.
- Request Payload:
```json
{
  "userId": "{{userId}}",
  "orgId": 1,
  "connection": {
    "id": null,
    "type": "azure",
    "endDate": "__AUTO__",
    "orgName": "TickingMinds",
    "source": "direct",
    "token": "<devops-token>"
  }
}
```
- Dynamic Data Source:
  - `userId`: `TokenUtil`
  - `connection.orgName`, `connection.siteUrl`, `connection.endDate`, `connection.spaceName`: `TestDataGenerator`
  - extracted connection id: `ConnectionStore`
- Sample Request Example:
```json
{
  "userId": 32,
  "orgId": 1,
  "connection": {
    "id": null,
    "type": "azure",
    "endDate": "2026-12-31",
    "orgName": "TM-Auto-1763000000000",
    "source": "direct",
    "token": "********"
  }
}
```
- Sample Response:
```json
{
  "success": true,
  "data": {
    "id": 87,
    "type": "azure"
  }
}
```
- Validation Logic Used in Automation:
  - Validates expected status code
  - Extracts `data.id`
  - Stores connection id in `ConnectionStore`
  - Logs payload and response into `Report`
- Automation Implementation:
  - Test class: `SaveConnectionTest`
  - API class: `SaveConnectionApi`
  - JSON test data file: `testdata/connectionsData/saveConnection.json`
- Example Console Logs:
```text
Payload -> {userId=32, orgId=1, connection={type=azure}}
Response Status -> 200
Stored Connection ID -> 87
```

### 4.6 Get Connections API

- Endpoint Name: Get Connections
- Endpoint URL: `/api/getConnections`
- HTTP Method: `POST`
- Purpose / Description: Returns saved connections for the selected organization and user. Used to confirm connection availability before project creation.
- Authentication: `TokenUtil`; normally `SUPER_ADMIN`.
- Request Payload:
```json
{
  "userId": "{{userId}}",
  "orgId": "{{orgId}}"
}
```
- Dynamic Data Source:
  - `userId`: `TokenUtil`
  - `orgId`: `OrganizationStore`
- Sample Request Example:
```json
{
  "userId": 32,
  "orgId": 5
}
```
- Sample Response:
```json
{
  "success": true,
  "data": [
    {
      "id": 87,
      "type": "azure",
      "platform": "azure"
    }
  ]
}
```
- Validation Logic Used in Automation:
  - Validates `200`
  - Validates `success = true`
  - Validates connection list is not empty
  - Logs response size to console
- Automation Implementation:
  - Test class: `GetConnectionsTest`
  - API class: `GetConnectionsApi`
  - JSON test data file: `testdata/connectionsData/getConnections.json`
- Example Console Logs:
```text
Payload -> {userId=32, orgId=5}
Response Status -> 200
Total Connections Found -> 3
```

### 4.7 Get My Projects API

- Endpoint Name: Get My Projects
- Endpoint URL: `/api/getMyProjects`
- HTTP Method: `POST`
- Purpose / Description: Fetches projects visible to the logged-in user. Used early in the flow before a new project is created.
- Authentication: `TokenUtil`; executed as `SUPER_ADMIN`.
- Request Payload:
```json
{
  "userId": "{{userId}}",
  "isSuperAdmin": true
}
```
- Dynamic Data Source:
  - `userId`: `TokenUtil`
- Sample Request Example:
```json
{
  "userId": "32",
  "isSuperAdmin": true
}
```
- Sample Response:
```json
{
  "projects": [
    {
      "projectId": 518,
      "projectName": "Automation_1763000000000"
    }
  ]
}
```
- Validation Logic Used in Automation:
  - Validates response is non-null
  - Validates `200`
  - Validates `projects` array is present and non-empty
  - Logs full response to console
- Automation Implementation:
  - Test class: `GetMyProjectsTest`
  - API class: `GetMyProjectsApi`
  - JSON test data file: `testdata/project/getMyProjects.json`
- Example Console Logs:
```text
Payload -> {userId=32, isSuperAdmin=true}
Response Status -> 200
Total Projects Found -> 12
```

### 4.8 Get Config API

- Endpoint Name: Get Project Config
- Endpoint URL: `/api/getConfig`
- HTTP Method: `POST`
- Purpose / Description: Retrieves saved configuration for the selected project after provider mapping and environment setup.
- Authentication: `TokenUtil`; commonly `SUPER_ADMIN`.
- Request Payload:
```json
{
  "userId": "{{userId}}",
  "projectId": "{{projectId}}"
}
```
- Dynamic Data Source:
  - `userId`: `TokenUtil`
  - `projectId`: `ProjectStore`
- Sample Request Example:
```json
{
  "userId": 32,
  "projectId": 518
}
```
- Sample Response:
```json
{
  "success": true,
  "data": {
    "projectId": 518,
    "storageType": "S3",
    "environment": "QA"
  }
}
```
- Validation Logic Used in Automation:
  - Validates `200`
  - Validates `success = true`
  - Logs success message and full response in report
- Automation Implementation:
  - Test class: `GetConfigTest`
  - API class: `GetConfigApi`
  - JSON test data file: `testdata/configuration/getConfig.json`
- Example Console Logs:
```text
Payload -> {userId=32, projectId=518}
Response Status -> 200
Config fetched successfully
```

### 4.9 Create Prompt API

- Endpoint Name: Create Prompt
- Endpoint URL: `/api/createPrompt`
- HTTP Method: `POST`
- Purpose / Description: Creates reusable prompt templates for AI-assisted generation flows such as `BR_TO_TS` and `TS_TO_TC`.
- Authentication: `TokenUtil`; usually `SUPER_ADMIN`.
- Request Payload:
```json
{
  "promptType": "BR_TO_TS",
  "promptText": "<promptText>",
  "promptDescription": "Business Requirement to Test Scenario prompt",
  "promptSource": "BR",
  "promptDestination": "TS",
  "userId": "{{userId}}"
}
```
- Dynamic Data Source:
  - `userId`: `TokenUtil`
  - `promptText`: original template plus runtime suffix in test class
  - created `promptId`: `PromptStore`
- Sample Request Example:
```json
{
  "promptType": "BR_TO_TS",
  "promptText": "Understand project overview...\n\n[Test Run ID: 1763000000000]",
  "promptDescription": "Business Requirement to Test Scenario prompt",
  "promptSource": "BR",
  "promptDestination": "TS",
  "userId": 32
}
```
- Sample Response:
```json
{
  "success": true,
  "insertedId": 91,
  "message": "Prompt created successfully"
}
```
- Validation Logic Used in Automation:
  - Validates `200`
  - Extracts `insertedId`
  - Stores `promptId` in `PromptStore` by `promptType`
  - Logs response in `Report`
- Automation Implementation:
  - Test class: `CreatePromptTest`
  - API class: `CreatePromptApi`
  - JSON test data file: `testdata/Prompt/createprompt.json`
- Example Console Logs:
```text
Payload -> {promptType=BR_TO_TS, userId=32}
Response Status -> 200
Successfully Created Prompt -> Type: BR_TO_TS | ID: 91
```

### 4.10 Update Prompt API

- Endpoint Name: Update Prompt
- Endpoint URL: `/api/updatePrompt`
- HTTP Method: `POST`
- Purpose / Description: Updates an existing prompt after it has been created and stored by type.
- Authentication: `TokenUtil`; usually `SUPER_ADMIN`.
- Request Payload:
```json
{
  "promptText": "{{generatedPromptText}}",
  "promptDescription": "{{generatedDescription}}",
  "promptSource": "BR",
  "promptDestination": "TS",
  "userId": "{{userId}}",
  "promptId": "{{promptId}}",
  "promptActive": true
}
```
- Dynamic Data Source:
  - `promptId`: `PromptStore`
  - `userId`: `TokenUtil`
  - `promptText`, `promptDescription`: `TestDataGenerator`
- Sample Request Example:
```json
{
  "promptText": "Updated prompt text generated for runtime",
  "promptDescription": "Updated automation description",
  "promptSource": "BR",
  "promptDestination": "TS",
  "userId": "32",
  "promptId": 91,
  "promptActive": true
}
```
- Sample Response:
```json
{
  "success": true,
  "message": "Prompt updated successfully"
}
```
- Validation Logic Used in Automation:
  - Resolves prompt type from source and destination
  - Fetches `promptId` from `PromptStore`
  - Validates status code
  - Logs final request and response in `Report`
- Automation Implementation:
  - Test class: `UpdatePromptTest`
  - API class: `UpdatePromptApi`
  - JSON test data file: `testdata/Prompt/updatePrompt.json`
- Example Console Logs:
```text
Payload -> {promptId=91, userId=32, promptActive=true}
Response Status -> 200
Update Prompt | Type=BR_TO_TS
```

### 4.11 Map Prompt API

- Endpoint Name: Map Prompt
- Endpoint URL: `/api/mapPrompt`
- HTTP Method: `POST`
- Purpose / Description: Maps a prompt template to the active project so generation APIs use the correct prompt configuration.
- Authentication: `TokenUtil`; executed as `SUPER_ADMIN`.
- Request Payload:
```json
{
  "promptType": "BR_TO_TS",
  "refprojectId": "DYNAMIC_PROJECT",
  "mappingComment": "Mapped BR to TS via automation",
  "promptId": "{{promptId}}",
  "userId": "{{userId}}"
}
```
- Dynamic Data Source:
  - `refprojectId`: `ProjectStore`
  - `userId`: `TokenUtil`
  - `promptId`: static values `9` and `10` for `BR_TO_TS` and `TS_TO_TC` in current implementation, fallback `PromptStore`
- Sample Request Example:
```json
{
  "promptType": "BR_TO_TS",
  "refprojectId": 518,
  "mappingComment": "Mapped BR to TS via automation",
  "promptId": 9,
  "userId": 32
}
```
- Sample Response:
```json
{
  "success": true,
  "message": "Prompt mapped successfully"
}
```
- Validation Logic Used in Automation:
  - Resolves project id dynamically
  - Resolves prompt id by prompt type
  - Validates expected status code through `ApiTestExecutor`
  - Logs payload and response
- Automation Implementation:
  - Test class: `MapPromptTest`
  - API class: `MapPromptApi`
  - JSON test data file: `testdata/Prompt/mapprompt.json`
- Example Console Logs:
```text
Payload -> {promptType=BR_TO_TS, refprojectId=518, promptId=9, userId=32}
Response Status -> 200
Map Prompt API Validation - BR_TO_TS
```

### 4.12 List Files API

- Endpoint Name: List Files
- Endpoint URL: `/api/listFiles`
- HTTP Method: `POST`
- Purpose / Description: Lists files attached to a business requirement. Used after BR file upload.
- Authentication: Token via `TokenUtil`; usually `SUPER_ADMIN`.
- Request Payload:
```json
{
  "userProjectId": "DYNAMIC_PROJECT",
  "userBrId": "DYNAMIC_BR",
  "userId": "DYNAMIC_USER",
  "storageType": "DYNAMIC_STORAGE",
  "platform": null
}
```
- Dynamic Data Source:
  - `userProjectId`: `ProjectStore`
  - `userBrId`: `BusinessRequirementStore`
  - `userId`: `TokenUtil`
  - `storageType`: `ProjectStore`
- Sample Request Example:
```json
{
  "userProjectId": "518",
  "userBrId": 701,
  "userId": "32",
  "storageType": "S3",
  "platform": null
}
```
- Sample Response:
```json
{
  "success": true,
  "files": [
    {
      "fileName": "sample.png",
      "fileId": "a1b2c3"
    }
  ]
}
```
- Validation Logic Used in Automation:
  - Dynamically injects project, BR, user, and storage values
  - Logs final payload
  - Validates expected status code through `ApiTestExecutor`
  - Response body is captured in report for downstream debugging
- Automation Implementation:
  - Test class: `ListFilesTest`
  - API class: `ListFilesApi`
  - JSON test data file: `testdata/uploadFiles/listFiles.json`
- Example Console Logs:
```text
Injected Project ID -> 518
Injected BR ID -> 701
Final ListFiles Payload -> {userProjectId=518, userBrId=701, userId=32, storageType=S3}
```

### 4.13 Delete Role API

- Endpoint Name: Delete Role
- Endpoint URL: `/deleteRole`
- HTTP Method: `POST`
- Purpose / Description: Deletes project roles created earlier in the flow. Executed after role-based user-management validations.
- Authentication: Token from `TokenUtil.getToken(role)`; generally `SUPER_ADMIN`.
- Request Payload:
```json
{
  "roleId": "{{roleId}}",
  "userId": "{{userId}}",
  "action": "delete"
}
```
- Dynamic Data Source:
  - `roleId`: `RoleStore`
  - `userId`: `TokenUtil`
- Sample Request Example:
```json
{
  "roleId": 205,
  "userId": 32,
  "action": "delete"
}
```
- Sample Response:
```json
{
  "success": true,
  "message": "Role deleted successfully"
}
```
- Validation Logic Used in Automation:
  - Iterates through all stored roles in `RoleStore`
  - Builds one request per role
  - Validates expected status code
  - Logs each deletion in `Report`
- Automation Implementation:
  - Test class: `DeleteRoleTest`
  - API class: `DeleteRoleApi`
  - JSON test data file: `testdata/rolesData/deleteRole.json`
- Example Console Logs:
```text
Payload -> {roleId=205, userId=32, action=delete}
Response Status -> 200
Delete Role | RoleId: 205
```

### 4.14 List Azure Pipelines API

- Endpoint Name: List Azure Pipelines
- Endpoint URL: `/api/listAzurePipelines`
- HTTP Method: `POST`
- Purpose / Description: Fetches Azure pipelines linked to the selected project after ATS files are loaded.
- Authentication: Token via `TokenUtil`; `SUPER_ADMIN` in the main flow.
- Request Payload:
```json
{
  "includeYaml": true,
  "includeClassic": true,
  "repoNameFilter": "ATS S3",
  "projectId": "{{projectId}}",
  "userId": "{{userId}}"
}
```
- Dynamic Data Source:
  - `projectId`: `ProjectStore`
  - `userId`: `TokenUtil`
- Sample Request Example:
```json
{
  "includeYaml": true,
  "includeClassic": true,
  "repoNameFilter": "ATS S3",
  "projectId": "518",
  "userId": 32
}
```
- Sample Response:
```json
{
  "success": true,
  "pipelines": [
    {
      "id": 64,
      "name": "ATS S3 Regression"
    }
  ]
}
```
- Validation Logic Used in Automation:
  - Injects `projectId` and `userId`
  - Validates expected status code
  - Logs payload before execution
  - Logs the returned response in `Report`
- Automation Implementation:
  - Test class: `ListAzurePipelinesTest`
  - API class: `ListAzurePipelinesApi`
  - JSON test data file: `testdata/pipeline/listAzurePipelines.json`
- Example Console Logs:
```text
List Azure Pipelines Payload -> {includeYaml=true, includeClassic=true, repoNameFilter=ATS S3, projectId=518, userId=32}
Response Status -> 200
Pipelines fetched successfully
```

### 4.15 Update Test Case Step Order API

- Endpoint Name: Update Test Case Step Order
- Endpoint URL: `/api/updateTestCaseStepOrder`
- HTTP Method: `POST`
- Purpose / Description: Reorders steps for a generated test case. Used after step creation and before step update validation.
- Authentication: Token via `TokenUtil`; normally `SUPER_ADMIN`.
- Request Payload:
```json
{
  "steps": [
    {
      "tcStepId": "{{tcStepId}}",
      "sortOrder": 1,
      "newStepNumber": "TCS-<testCaseId>-001"
    }
  ],
  "userId": "{{userId}}"
}
```
- Dynamic Data Source:
  - source test case ids: `TestCaseStore`
  - source steps: fetched dynamically from `GetTestCaseWithStepsApi`
  - `userId`: `TokenUtil`
- Sample Request Example:
```json
{
  "steps": [
    {
      "tcStepId": 1901,
      "sortOrder": 1,
      "newStepNumber": "TCS-880-001"
    },
    {
      "tcStepId": 1900,
      "sortOrder": 2,
      "newStepNumber": "TCS-880-002"
    }
  ],
  "userId": "32"
}
```
- Sample Response:
```json
{
  "success": true,
  "message": "Step order updated successfully"
}
```
- Validation Logic Used in Automation:
  - Reads all ids from `TestCaseStore`
  - Calls `GetTestCaseWithStepsApi` until it finds a test case with at least two valid steps
  - Reverses the step list
  - Builds new `sortOrder` and `newStepNumber`
  - Validates expected status code via `ApiTestExecutor`
- Automation Implementation:
  - Test class: `UpdateTestCaseStepOrderTest`
  - API class: `UpdateTestCaseStepOrderApi`
  - JSON test data file: `testdata/testCase/updateTestCaseStepOrder.json`
- Example Console Logs:
```text
FINAL STEP ORDER PAYLOAD -> {steps=[{tcStepId=1901, sortOrder=1}, {tcStepId=1900, sortOrder=2}], userId=32}
Response Status -> 200
```

### 4.16 Get Version API

- Endpoint Name: Get System Version
- Endpoint URL: `/api/version`
- HTTP Method: `GET`
- Purpose / Description: Fetches the current application version toward the end of the flow.
- Authentication: Token via `TokenUtil`; executed as `SUPER_ADMIN`.
- Request Payload:
```json
{}
```
- Dynamic Data Source:
  - No body data is required
  - Token is sourced from `TokenUtil`
- Sample Request Example:
```json
{}
```
- Sample Response:
```json
{
  "version": "2.8.14"
}
```
- Validation Logic Used in Automation:
  - Validates expected status code
  - Validates the `version` field is present and non-empty
  - Logs application version to console and response to report
- Automation Implementation:
  - Test class: `GetVersionTest`
  - API class: `GetVersionApi`
  - JSON test data file: `testdata/system/getVersion.json`
- Example Console Logs:
```text
Payload -> {}
Response Status -> 200
Application Version -> 2.8.14
```

### 4.17 Get Test Case With Steps API

- Endpoint Name: Get Test Case With Steps
- Endpoint URL: `/api/getTestCaseWithStepsForTestCaseId`
- HTTP Method: `POST`
- Purpose / Description: Fetches a specific generated test case and its steps. This is used directly and also as a supporting call for step reorder validation.
- Authentication: Token via `TokenUtil`; usually `SUPER_ADMIN`.
- Request Payload:
```json
{
  "testCaseId": "{{testCaseId}}",
  "userId": "{{userId}}"
}
```
- Dynamic Data Source:
  - `testCaseId`: `TestCaseStore`
  - `userId`: `TokenUtil`
- Sample Request Example:
```json
{
  "testCaseId": 880,
  "userId": 32
}
```
- Sample Response:
```json
{
  "testCaseId": 880,
  "tcNumber": "TC-880",
  "steps": [
    {
      "tcStepId": 1900,
      "tcStep": "Open application",
      "tcResult": "Login page is displayed"
    }
  ]
}
```
- Validation Logic Used in Automation:
  - Validates that `TestCaseStore` contains at least one id before running
  - Injects dynamic `testCaseId` and `userId`
  - Validates expected status code
  - Captures full response in report for downstream APIs
- Automation Implementation:
  - Test class: `GetTestCaseWithStepsTest`
  - API class: `GetTestCaseWithStepsApi`
  - JSON test data file: `testdata/testCase/getTestCaseWithSteps.json`
- Example Console Logs:
```text
Using TestCaseId -> 880
Payload -> {testCaseId=880, userId=32}
Response Status -> 200
```

### 4.18 Get Projects API

- Endpoint Name: Get Projects
- Endpoint URL: `/api/getProjects`
- HTTP Method: `POST`
- Purpose / Description: Queue-flow support API that fetches all projects visible to the active user and hydrates `ProjectStore`.
- Authentication: Token from `TokenUtil`.
- Request Payload:
```json
{
  "userId": 28
}
```
- Dynamic Data Source:
  - current implementation uses static JSON request
  - response hydrates `ProjectStore`
- Sample Response:
```json
[
  {
    "projectId": 518,
    "projectName": "Automation_1763000000000"
  }
]
```
- Validation Logic Used in Automation:
  - Uses `ApiTestExecutor` status validation
  - Parses root array
  - Stores projects in `ProjectStore`
- Automation Implementation:
  - Test class: `GetProjectsTest`
  - API class: `GetProjectsApi`
  - JSON test data file: `testdata/project/getProjects.json`

### 4.19 Get Project Users API

- Endpoint Name: Get Project Users
- Endpoint URL: `/api/getProjectUsers`
- HTTP Method: `POST`
- Purpose / Description: Queue-flow support API that fetches users for each project and hydrates `ProjectUserStore`.
- Authentication: Token from `TokenUtil`.
- Request Payload:
```json
{
  "projectId": "{{PROJECT_ID}}",
  "userId": "{{userId}}"
}
```
- Dynamic Data Source:
  - `projectId`: `ProjectStore`
  - `userId`: `TokenUtil`
- Sample Response:
```json
{
  "users": [
    {
      "userId": 41,
      "userFirstName": "Arun",
      "userLastName": "Kumar"
    }
  ]
}
```
- Validation Logic Used in Automation:
  - Builds one request per project
  - Stores returned users in `ProjectUserStore`
  - Skips empty user lists safely
- Automation Implementation:
  - Test class: `GetProjectUsersTest`
  - API class: `GetProjectUsersApi`
  - JSON test data file: `testdata/project/getProjectUsers.json`

### 4.20 Get Generation Queue API

- Endpoint Name: Get Generation Queue
- Endpoint URL: `/api/getGenerationQueue`
- HTTP Method: `POST`
- Purpose / Description: Queue maintenance API that fetches generation queue records per project and per project user.
- Authentication: Token from `TokenUtil`.
- Request Payload:
```json
{
  "projectId": "{{projectId}}",
  "userIdFromAPI": "{{targetUserId}}",
  "userId": "{{loggedInUserId}}"
}
```
- Dynamic Data Source:
  - `projectId`: `ProjectStore`
  - `userIdFromAPI`: `ProjectUserStore`
  - `userId`: `TokenUtil`
  - queue ids are stored into `GenerationQueueStore`
- Sample Response:
```json
{
  "data": [
    {
      "queueId": 1938,
      "status": "Queued"
    }
  ]
}
```
- Validation Logic Used in Automation:
  - Executes per user per project
  - Skips empty queues
  - Extracts `queueId`
  - Stores queue ids in `GenerationQueueStore`
- Automation Implementation:
  - Test class: `GetGenerationQueueTest`
  - API class: `GetGenerationQueueApi`
  - JSON test data file: `testdata/queueData/getGenerationQueue.json`

### 4.21 Delete Generation Queue API

- Endpoint Name: Delete Generation Queue
- Endpoint URL: `/api/deleteGenerationQueue`
- HTTP Method: `POST`
- Purpose / Description: Deletes queued generation jobs found by the queue retrieval flow.
- Authentication: Token from `TokenUtil`; the test refreshes the token before execution.
- Request Payload:
```json
{
  "userIdFromAPI": "{{targetUserId}}",
  "userId": "{{loggedInUserId}}",
  "projectId": "{{projectId}}",
  "queueIds": [1938, 1940]
}
```
- Dynamic Data Source:
  - `userIdFromAPI`, `projectId`, `queueIds`: `GenerationQueueStore`
  - `userId`: `TokenUtil`
- Sample Response:
```json
{
  "success": true,
  "message": "Generation queue deleted successfully"
}
```
- Validation Logic Used in Automation:
  - Iterates over all queue ids in `GenerationQueueStore`
  - Builds one deletion request per project-user combination
  - Validates expected status code via `ApiTestExecutor`
- Automation Implementation:
  - Test class: `DeleteGenerationQueueTest`
  - API class: `DeleteGenerationQueueApi`
  - JSON test data file: `testdata/queueData/deleteGenerationQueue.json`

## 5. Additional APIs Referenced in the Automation Flow

The following APIs are also exercised in the framework and should be included in project handover scope. Their execution pattern is the same: JSON test data -> dynamic injection -> `ApiTestExecutor` -> store update or validation.

| API | Method | Endpoint | Primary Purpose | Test Class | API Class | JSON Test Data |
|---|---|---|---|---|---|---|
| Login | POST | `/api/login` | Generate auth token used by `TokenUtil` | `LoginTest` | `LoginApi` | N/A |
| Logout | POST | `/api/logout` | End session at end of flow | `LogOutTest` | `LogOutApi` | `testdata/authData/logOut.json` |
| Check User Roles | POST | `/api/checkUserRoles` | Validate role visibility | `CheckUserRolesTest` | `CheckUserRolesApi` | `testdata/rolesData/checkUserRoles.json` |
| Create Role | POST | `/createRole` | Create role for project access control | `CreateRoleTest` | `CreateRoleApi` | `testdata/rolesData/createRole.json` |
| Edit Role | POST | `/editRole` | Update previously created role | `EditRoleTest` | `EditRoleApi` | `testdata/rolesData/editRole.json` |
| Get Roles | POST | `/api/getRoles` | Fetch roles for project | `GetRolesTest` | `GetRolesApi` | `testdata/rolesData/getRoles.json` |
| Disable Users By Role | POST | `/disableUsersByRole` | Bulk disable users under a role | `DisableUsersByRoleTest` | `DisableUsersByRoleApi` | `testdata/rolesData/disableUsersByRole.json` |
| Add / Update Project User | POST | `/addUpdateProjectUser` | Assign user to project and role | `AddUpdateProjecTest` | `AddUserApi` | `testdata/userManagement/addUpdateProjectUser.json` |
| Toggle User Status | POST | `/toggleUserStatus` | Activate / deactivate project user | `ToggleUserStatusTest` | `ToggleUserStatusApi` | `testdata/userManagement/toggleUserStatus.json` |
| Get User Management Details | POST | `/api/getUserManagementDetailsForProjectId` | Read project-level user mapping details | `GetUserManagementDetailsTest` | `GetUserManagementDetailsApi` | `testdata/userManagement/getUserManagementDetails.json` |
| Get Project By Id | POST | `/api/getProjectById` | Fetch specific project | `GetProjectByIdTest` | `GetProjectByIdApi` | `testdata/project/GetProjectById.json` |
| Get Project Details | POST | `/api/getProjectDetailsByProjectId` | Fetch project details after creation | `GetProjectDetailsTest` | `GetProjectDetailsApi` | `testdata/project/GetProjectDetails.json` |
| Get Project Provider Config | POST | `/api/get-project-provider-config` | Read provider config for project | `GetProjectProviderConfigTest` | `GetProjectProviderConfigApi` | `testdata/project/getProjectProviderConfig.json` |
| Add DB Config | POST | `/api/addDbInfo` | Attach DB config to project | `DbConfigTest` | `DbConfigApi` | `testdata/configuration/dbConfig.json` |
| Get Environment Details | POST | `/api/getEnvironmentDetails` | Resolve environment names | `EnvironmentDetailsTest` | `EnvironmentApi` | `testdata/configuration/getEnvironment.json` |
| Save Config | POST | `/api/saveConfig` | Save project configuration | `SaveConfigTest` | `SaveConfigApi` | `testdata/configuration/saveConfig.json` |
| Test Connection | POST | `/api/testConnection` | Validate a connection without persisting it | `TestConnectionTest` | `TestConnectionApi` | `testdata/connectionsData/testConnection.json` |
| Delete Connection | POST | `/api/deleteConnection` | Remove existing connection | `DeleteConnectionTest` | `DeleteConnectionApi` | `testdata/connectionsData/deleteConnection.json` |
| Get All Prompts | GET | `/api/getAllPrompts` | Fetch available prompts | `GetAllPromptTest` | `GetAllPromptApi` | N/A |
| Get Azure DevOps Sprints | POST | `/api/getAzureDevOpsSprints` | Fetch Azure sprint list | `GetAzureSprintsTest` | `GetAzureDevOpsSprintsApi` | `testdata/project/getAzureDevOpsSprints.json` |
| Get User Stories | POST | `/api/getUserStories` | Fetch user stories from Azure | `GetUserStoriesTest` | `GetUserStoriesApi` | `testdata/project/getUserStories.json` |
| Import Azure User Stories | POST | `/api/importAzureUserStories` | Import selected Azure stories into the product | `ImportAzureUserStoriesTest` | `ImportAzureUserStoriesApi` | `testdata/project/importUserStoriesAzure.json` |
| Upload Business Requirement | POST | `/api/uploadBusinessRequirement` | Upload BR documents | `UploadBusinessRequirementTest` | `UploadBusinessRequirementApi` | `testdata/br/UploadBusinessRequirement.json` |
| Get BR By Project | POST | `/api/getBRforProjectId` | Fetch BR ids for project | `GetBusinessRequirementTest` | `GetBRByProjectApi` | `testdata/br/getBRByProject.json` |
| Export BR Excel | POST | `/api/getBRforProjectIdExcel` | Export BR list to Excel | `ExportBRExcelTest` | `ExportBRExcelApi` | `testdata/export/exportBRExcel.json` |
| Delete Business Requirement | PUT | `/api/deleteBusinessRequirement` | Soft-delete BR | `DeleteBusinessRequirementTest` | `DeleteBRApi` | `testdata/br/deleteBR.json` |
| Check GCS Path For BR | POST | `/api/checkgcspathforbr` | Validate BR storage path | `CheckGcsPathForBrTest` | `CheckGcsPathForBrApi` | `testdata/br/checkGcsPathForBr.json` |
| Upload Files For BR | POST | `/api/upload-files` | Upload image or other files to BR | `UploadFilesForBRTest` | `UploadFilesForBRApi` | `testdata/uploadFiles/uploadFilesForBR.json` |
| Download File | POST | `/api/downloadFile` | Download BR file | `DownloadFilesTest` | `DownloadFilesApi` | `testdata/uploadFiles/downloadFiles.json` |
| Delete File | POST | `/api/deleteFile` | Delete BR file | `DeleteFilesTest` | `DeleteFilesApi` | `testdata/uploadFiles/deleteFiles.json` |
| Generate TS | POST | `/api/generateTS` | Generate test scenarios from BRs | `GenerateTSTest` | `GenerateTSApi` | `testdata/generation/generateTS.json` |
| Get Generation Status | POST | `/getGenerationStatus` | Poll TS / TC / ATS generation state | `GetGenerationStatusTest` | `GetGenerationStatusApi` | `testdata/generation/getGenerationStatus.json` |
| Get TS By BR | POST | `/api/getTSforBrId` | Fetch generated scenarios | `GetTSByBRTest` | `GetTSByBRApi` | `testdata/testScenario/getTSByBR.json` |
| Export TS Excel | POST | `/api/getTSExcel` | Export scenarios to Excel | `ExportTSExcelTest` | `ExportTSExcelApi` | `testdata/export/exportTSExcel.json` |
| Delete Test Scenario | PUT | `/api/deleteTestScenario` | Delete scenario | `DeleteTestScenarioTest` | `DeleteTestScenarioApi` | `testdata/testScenario/deleteTS.json` |
| Add Test Scenario | POST | `/api/addTestScenario` | Add scenario manually | `AddTestScenarioTest` | `AddTestScenarioApi` | `testdata/testScenario/addTestScenario.json` |
| Update Test Scenario | PUT | `/api/updateTestScenario` | Edit scenario | `UpdateTestScenarioTest` | `UpdateTestScenarioApi` | `testdata/testScenario/updateTestScenario.json` |
| Generate TC | POST | `/api/generateTC` | Generate test cases from scenarios | `GenerateTCTest` | `GenerateTCApi` | `testdata/generation/generateTC.json` |
| Export TC Excel | POST | `/api/getTestCaseWithStepsExcel` | Export test cases with steps | `ExportTCExcelTest` | `ExportTCExcelApi` | `testdata/export/exportTCExcel.json` |
| Get Test Case Summary For TS | POST | `/api/getTestCaseSummaryForTestScenarioId` | Fetch test case summary by scenario | `GetTestCaseSummaryForTSTest` | `GetTestCaseSummaryForTSApi` | `testdata/testCase/getTestCaseSummaryForTS.json` |
| Add Test Case Step | POST | `/api/addTestCaseStep` | Add step to test case | `AddTestCaseStepTest` | `AddTestCaseStepApi` | `testdata/testCase/addTestCaseStep.json` |
| Update Test Case Step | PUT | `/api/updateTestCaseStep` | Update a step | `UpdateTestCaseStepTest` | `UpdateTestCaseStepApi` | `testdata/testCase/updateTestCaseStep.json` |
| Delete Test Case Step | PUT | `/api/deleteTestCaseStep` | Delete step from test case | `DeleteTestCaseStepTest` | `DeleteTestCaseStepApi` | `testdata/testCase/deleteTestCaseStep.json` |
| Update Test Case | PUT | `/api/updateTestCase` | Update main test case fields | `UpdateTestCaseTest` | `UpdateTestCaseApi` | `testdata/testCase/updateTestCase.json` |
| Insert Test Case | POST | `/api/insertTC` | Add test case manually | `InsertTestCaseTest` | `InsertTestCaseApi` | `testdata/testCase/addTestCase.json` |
| Delete Test Case | PUT | `/api/deleteTestCase` | Delete test case | `DeleteTestCaseTest` | `DeleteTestCaseApi` | `testdata/testCase/deleteTestCase.json` |
| Update BDD | PUT | `/api/updateBdd` | Update BDD content after TC changes | `UpdateBddTest` | `UpdateBddApi` | `testdata/bdd/updateBdd.json` |
| Generate ATS | POST | `/api/generateATS` | Generate automation code | `GenerateATSTest` | `GenerateATSApi` | `testdata/generation/generateATS.json` |
| Download ATS Framework | POST | `/api/downloadAtsFramework` | Download generated framework zip | `DownloadAtsFrameworkTest` | `DownloadAtsFrameworkApi` | `testdata/framework/downloadAtsFramework.json` |
| Load ATS Files | POST | `/api/loadAtsFiles` | Upload generated ATS files back to service | `LoadATSFilesTest` | `LoadATSFilesApi` | `testdata/ats/loadATSFiles.json` |
| Execute Tests | POST | `/api/executeTests` | Trigger automation execution | `ExecuteTestsTest` | `ExecuteTestsApi` | `testdata/pipeline/executeTests.json` |
| List Videos | POST | `/api/listVideos` | List automation run videos | `ListVideosTest` | `ListVideosApi` | `testdata/video/listVideos.json` |
| Get Automation Video | POST | `/api/getAutomationVideo` | Download selected run video | `GetAutomationVideoTest` | `GetAutomationVideoApi` | `testdata/video/getAutomationVideo.json` |
| Get Log Files | POST | `/api/log-files` | List log files | `GetLogFilesTest` | `GetLogFilesApi` | `testdata/logs/getLogFiles.json` |
| Download Log File | POST | `/api/log-files/download` | Download a log file | `DownloadLogFileTest` | `DownloadLogFileApi` | `testdata/logs/downloadLogFile.json` |
| Get LLM Models | GET | `/api/get-llm-models` | Fetch models for mapping | `GetLlmModelsTest` | `GetLlmModelsApi` | N/A |
| Get LLM Credentials | GET | `/api/get-llm-credentials` | Fetch credentials for current user | `GetLlmCredentialsTest` | `GetLlmCredentialsApi` | `testdata/model/getLlmCredentials.json` |
| Map Credential To Project | POST | `/api/map-credentials-to-project` | Map provider credential to project | `MapCredentialToProjectTest` | `MapCredentialToProjectApi` | `testdata/model/mapCredentialToProject.json` |
| Map LLM To Project | POST | `/api/map-llm-to-project` | Map chosen LLM model to project | `MapLlmToProjectTest` | `MapLlmToProjectApi` | `testdata/model/mapLlmToProject.json` |

## 6. Dynamic Data Source Summary

| Dynamic Field | Primary Source |
|---|---|
| `userId` | `TokenUtil` |
| `projectId` | `ProjectStore` |
| `projectName` | `ProjectStore` or generated in test |
| `connectionId` | `ConnectionStore` |
| `platform` | `ConnectionStore` |
| `promptId` | `PromptStore` or fixed prompt mapping in `MapPromptTest` |
| `BRId` / `userBrId` | `BusinessRequirementStore` |
| `roleId` | `RoleStore` |
| `testCaseId` | `TestCaseStore` |
| `storageType` | `ProjectStore` |
| `organizationId` / `orgId` | `OrganizationStore` |
| `queueIds` | `GenerationQueueStore` |

## 7. Reporting and Validation Notes

- `ApiTestExecutor` automatically logs:
  - request payload
  - actual status code
  - actual response
  - failure message, if any
- It also validates:
  - `expectedStatusCode`
  - optional `expectedStatus`
- Binary responses are safely omitted from pretty logging.
- The framework supplements `ApiTestExecutor` with test-specific validation such as:
  - extracting IDs into stores
  - validating business-critical flags like `success`
  - validating list sizes and required response keys

## 8. Key Handover Observations

- Some endpoints do not use the `/api` prefix, for example `/createRole`, `/editRole`, `/deleteRole`, `/disableUsersByRole`, `/addUpdateProjectUser`, and `/toggleUserStatus`.
- `ConnectionStore.getConnectionId()` and `ConnectionStore.getPlatform()` currently return hardcoded values (`87` and `azure`) even though the save-connection flow also stores a runtime connection id.
- `MapPromptTest` currently hardcodes prompt ids `9` and `10` for `BR_TO_TS` and `TS_TO_TC` before falling back to `PromptStore`.
- Queue-flow APIs depend on store hydration order:
  - `GetProjects` -> `GetProjectUsers` -> `GetGenerationQueue` -> `DeleteGenerationQueue`
