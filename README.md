# Directory Structure
---
~~~
lsm/
├── .gradle/
├── build/
├── gradle/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── lsm/
│   │   │           ├── LsmApplication.java
│   │   │           ├── aspect/
│   │   │           │   ├── LoggingAspect.java
│   │   │           ├── config/
│   │   │           │   ├── AspectConfig.java
│   │   │           │   ├── CacheConfig.java
│   │   │           │   ├── RedisConfig.java
│   │   │           │   ├── SecurityConfig.java
│   │   │           │   ├── SwaggerConfig.java
│   │   │           ├── controller/
│   │   │           │   ├── ApiResponse_.java
│   │   │           │   ├── AssignmentController.java
│   │   │           │   ├── AttendanceController.java
│   │   │           │   ├── AuthController.java
│   │   │           │   ├── ContentController.java
│   │   │           ├── events/
│   │   │           │   ├── EventPublisher.java
│   │   │           │   ├── UserEvent.java
│   │   │           │   ├── UserLoginEvent.java
│   │   │           │   ├── UserLogoutEvent.java
│   │   │           │   ├── UserRegisteredEvent.java
│   │   │           ├── exception/
│   │   │           │   ├── AuthenticationException.java
│   │   │           │   ├── DuplicateResourceException.java
│   │   │           │   ├── InvalidTokenException.java
│   │   │           │   ├── AccountDisabledException.java
│   │   │           │   ├── AccountLockedException.java
│   │   │           │   ├── InvalidPasswordException.java
│   │   │           │   ├── LogoutException.java
│   │   │           │   ├── RateLimitExceededException.java
│   │   │           │   ├── TokenExpiredException.java
│   │   │           │   ├── TokenValidationException.java
│   │   │           │   ├── UserNotFoundException.java
│   │   │           ├── mapper/
│   │   │           │   ├── UserMapper.java
│   │   │           ├── model/
│   │   │               ├── DTOs/
│   │   │               │   ├── AssignmentDTO.java
│   │   │               │   ├── AssignmentRequestDTO.java
│   │   │               │   ├── AttendanceDTO.java
│   │   │               │   ├── AttendanceRequestDTO.java
│   │   │               │   ├── AttendanceStatsDTO.java
│   │   │               │   ├── AuthenticationResult.java
│   │   │               │   ├── LoginRequestDTO.java
│   │   │               │   ├── LoginResponseDTO.java
│   │   │               │   ├── RegisterRequestDTO.java
│   │   │               │   ├── RegisterResponseDTO.java
│   │   │               │   ├── TokenRefreshResponseDTO.java
│   │   │               │   ├── TokenRefreshResult.java
│   │   │               │   ├── UserDTO.java
│   │   │               ├── entity/
│   │   │                   ├── base/
│   │   │                   │   ├── AppUser.java
│   │   │                   ├── enums/
│   │   │                   │   ├── AssignmentStatus.java
│   │   │                   │   ├── AttendanceStatus.java
│   │   │                   │   ├── Role.java
│   │   │                   ├── Assignment.java
│   │   │                   ├── Attendance.java
│   │   │                   ├── ClassEntity.java
│   │   │                   ├── RefreshToken.java
│   │   │                   ├── StudentDetails.java
│   │   │               ├── validation/
│   │   │                   ├── contraint/
│   │   │                   │   ├── TCConstraint.java
│   │   │                   │   ├── PasswordConstraint.java
│   │   │                   ├── groups/
│   │   │                   │   ├── ValidationGroups.java
│   │   │                   ├── LoginRequestValidator.java
│   │   │                   ├── PasswordConstraintValidator.java
│   │   │                   ├── TCValidator.java
│   │   │            ├── repository/
│   │   │               ├── AppUserRepository.java
│   │   │               ├── AssignmentRepository.java
│   │   │               ├── AttendanceRepository.java
│   │   │               ├── RefreshTokenRepository.java
│   │   │            ├── security/
│   │   │               ├── JwtAuthenticationFilter.java
│   │   │               ├── RateLimiter.java
│   │   │               ├── RateLimitProperties.java
│   │   │            ├── service/
│   │   │               ├── AppUserService.java
│   │   │               ├── AssignmentService.java
│   │   │               ├── AttendanceService.java
│   │   │               ├── AuthService.java
│   │   │               ├── JwtTokenProvider.java
│   │   │               ├── LoginAttemptsService.java
│   │   │       └── resources/
│   │   │           ├── static/
│   │   │               ├── css/
│   │   │               │   ├── ...
│   │   │               ├── js/
│   │   │                   ├── ...
│   │   │           ├── templates/
│   │   │           │   ├── ...
│   │   │           ├── application.properties
│   ├── test/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── lsm/...
├── .env
├── .gitignore
├── Dockerfile
├── docker-compose.yml
├── build.gradle
├── gradlew
├── lsm_database_dump.sql
├── settings.gradle
~~~~
## Explanation
---
~~~
lsm/
├── src/main/java/com/lsm/           # Core application code
│   ├── aspect/                      # AOP configurations for logging
│   ├── config/                      # Application configurations
│   │   ├── AspectConfig            # AOP configuration
│   │   ├── CacheConfig             # Caching setup
│   │   ├── RedisConfig             # Redis configuration
│   │   ├── SecurityConfig          # Security settings
│   │   └── SwaggerConfig           # API documentation
│   ├── controller/                  # REST API endpoints
│   ├── events/                      # Event-driven components
│   ├── exception/                   # Custom exceptions
│   ├── mapper/                      # Object mapping utilities
│   ├── model/                       # Data models
│   │   ├── DTOs/                   # Data Transfer Objects
│   │   ├── entity/                 # Database entities
│   │   └── validation/             # Input validation
│   ├── repository/                  # Data access layer
│   ├── security/                    # Security implementations
│   └── service/                     # Business logic layer
│
├── src/main/resources/             # Application resources
│   ├── static/                     # Static web assets
│   ├── templates/                  # View templates
│   └── application.properties      # Application configuration
│
├── src/test/                       # Test suite
├── Dockerfile                      # Container configuration
├── docker-compose.yml              # Container orchestration
└── build.gradle                    # Build configuration
