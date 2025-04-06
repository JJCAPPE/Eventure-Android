# Eventure Android App

## Project Overview
Eventure is an Android application that integrates with the Ticketmaster API to provide event discovery and management features.

## Setup Instructions
1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/Eventure-Android.git
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select 'Open an Existing Project'
   - Navigate to the cloned repository

3. **Configure API Keys**
   - Create `local.properties` in the project root
   - Add your Ticketmaster API key:
     ```
     ticketmaster.api.key=your_api_key_here
     ```

## Configuration
### Environment Variables
- `ticketmaster.api.key`: Required for Ticketmaster API access

### Gradle Properties
- JDK path is automatically configured in CI (GitHub Actions)
- For local development, ensure you have JDK 17 installed

## Build & Deployment
### Local Build
```bash
./gradlew assembleDebug
```

### CI/CD
- GitHub Actions workflow automatically:
  - Sets up JDK 17
  - Runs tests
  - Builds the APK

## Troubleshooting
### Build Issues
- If encountering JDK path errors, ensure:
  - JDK 17 is installed
  - The hardcoded path in `gradle.properties` is commented out for CI compatibility
  ```properties
  #org.gradle.java.home=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
  ```

### API Issues
- Verify your Ticketmaster API key is correctly set in `local.properties`
- Check network connectivity if API calls fail

## Contributing
1. Fork the repository
2. Create a feature branch
3. Submit a pull request

## License
[Specify your license here]
