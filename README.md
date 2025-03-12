# OSRS Goals Service

A Java service for tracking Old School RuneScape player goals and progress.

## Overview

This service provides a set of AWS Lambda functions for managing OSRS player goals and tracking progress. It follows a Layered Service Architecture (LSA) pattern and is built with Java 21.

## Documentation

- [Project Structure and Architecture](docs/ARCHITECTURE.md) - Detailed overview of the project's layered architecture and development guidelines
- [Handler Interfaces](docs/HANDLERS.md) - Lambda function entry points, inputs, and outputs
- [Service Interfaces](docs/SERVICES.md) - Service layer interfaces and functionality
- [Data Models](docs/MODELS.md) - Core data models and their relationships

## Architecture

### Domain Layer
The service is organized into domain-specific modules (e.g., User, Character, Goal) that handle core business logic.

### Orchestration Layer
The orchestration layer manages cross-domain interactions through events. It includes:
- Event models (e.g., GoalCreationEvent)
- Event handlers
- Event-driven workflows

## Requirements

- JDK 21
- Gradle 8.x

## Quick Start

1. Install dependencies:
```bash
./gradlew build
```

2. Run tests:
```bash
./gradlew test
```

3. Build all Lambda handlers:
```bash
./gradlew buildAllHandlers
```

Each handler will be built into its own JAR file in `build/libs/`.

## Dependencies

- AWS Lambda Core - Lambda function support
- AWS Lambda Events - Event handling
- AWS DynamoDB - Database operations
- Google Guice - Dependency injection
- Jackson - JSON serialization
- Log4j2 - Logging
- Lombok - Boilerplate reduction
- JUnit 5 - Testing
- Mockito - Mocking for tests

## AWS CodeArtifact Integration

This project is configured to automatically publish artifacts to AWS CodeArtifact when changes are pushed to the main branch or when a new version tag is created.

### GitHub Action for CodeArtifact Publishing

The project includes a GitHub Action workflow that automatically builds and publishes artifacts to AWS CodeArtifact. The workflow can be triggered in three ways:

1. **Push to main branch**: Automatically builds and publishes with a timestamp-based version
2. **Pull request to main branch**: Builds but doesn't publish (validation only)
3. **Manual trigger**: Can be manually triggered with a specific version number

#### Required GitHub Secrets

This workflow uses organization-level secrets to allow sharing credentials across multiple repositories. You need to set up the following secrets in your GitHub organization settings:

- `AWS_ACCESS_KEY_ID`: AWS access key with permissions to publish to CodeArtifact
- `AWS_SECRET_ACCESS_KEY`: Corresponding AWS secret key
- `AWS_REGION`: AWS region where your CodeArtifact repository is located
- `CODEARTIFACT_DOMAIN`: Your CodeArtifact domain name
- `CODEARTIFACT_DOMAIN_OWNER`: AWS account ID that owns the CodeArtifact domain
- `CODEARTIFACT_REPOSITORY`: Name of your CodeArtifact repository

To set up organization secrets:

1. Go to your GitHub organization's main page
2. Click on "Settings" in the top navigation bar
3. Select "Secrets and variables" from the left sidebar
4. Click on "Actions"
5. Click "New organization secret" to add each of the required secrets
6. For each secret, you can control which repositories can access it by selecting specific repositories or allowing all repositories

#### IAM Permissions Required

The AWS user associated with the access key needs the following permissions:

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "codeartifact:GetAuthorizationToken",
                "codeartifact:GetRepositoryEndpoint",
                "codeartifact:ReadFromRepository",
                "codeartifact:PublishPackageVersion"
            ],
            "Resource": "*"
        },
        {
            "Effect": "Allow",
            "Action": "sts:GetServiceBearerToken",
            "Resource": "*",
            "Condition": {
                "StringEquals": {
                    "sts:AWSServiceName": "codeartifact.amazonaws.com"
                }
            }
        }
    ]
}
```

#### Manual Workflow Trigger

To manually trigger the workflow with a specific version:

1. Go to the "Actions" tab in your GitHub repository
2. Select the "Build and Publish to AWS CodeArtifact" workflow
3. Click "Run workflow"
4. Enter the desired version number (e.g., "1.0.0")
5. Click "Run workflow"

### Published Artifacts

The following artifacts are published to AWS CodeArtifact:

1. **Service JAR**: Contains the core service functionality without handlers
   - Artifact ID: `hiscore-service`

2. **Lambda Handler JARs**: Each Lambda handler is published as a separate artifact
   - Artifact ID format: `<handler-name>-lambda`

### Versioning

- **Tagged Releases**: When a tag in the format `v1.2.3` is pushed, artifacts are published with that version number (e.g., `1.2.3`)
- **Development Builds**: When code is pushed to the main branch without a tag, artifacts are published with version `0.0.0-<commit-hash>`

### Adding New Handlers

New handlers are automatically detected and published to AWS CodeArtifact. To add a new handler:

1. Create a new handler class in the `com.osrsGoalTracker.hiscore.handler` package with a name ending in `Handler.java`
2. The build system will automatically detect the new handler and create a corresponding Lambda JAR
3. When pushed to GitHub, the new handler will be published to AWS CodeArtifact

### Using Published Artifacts

To use the published artifacts in another project:

```gradle
repositories {
    maven {
        name = "CodeArtifact"
        url = "https://your-domain-id.d.codeartifact.region.amazonaws.com/maven/your-repo/"
        credentials {
            username = "aws"
            password = System.getenv('CODEARTIFACT_AUTH_TOKEN')
        }
    }
}

dependencies {
    // Service dependency
    implementation 'com.osrsGoalTracker:hiscore-service:1.0.0'
    
    // Lambda handler dependency (if needed)
    implementation 'com.osrsGoalTracker:getCharacterHiscores-lambda:1.0.0'
}
```

## Infrastructure

The service is deployed using AWS CDK with the following components:

- API Gateway for REST endpoints
- Lambda functions for business logic
- DynamoDB tables for data storage 
