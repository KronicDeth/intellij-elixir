package platform

import org.gradle.api.logging.Logger

/**
 * Represents the platform (operating system) on which the build is running.
 * Used to select platform-specific implementations for Elixir and Quoter services.
 */
enum class Platform {
    /** Windows (any environment: native cmd/PowerShell, Git Bash, WSL, MSYS2) */
    WINDOWS,

    /** POSIX systems (Linux, macOS, BSD) */
    POSIX
}

/**
 * Detects the current platform based on system properties.
 * @return WINDOWS if os.name contains "windows", otherwise POSIX
 */
fun detectPlatform(): Platform {
    val osName = System.getProperty("os.name").lowercase()
    return if (osName.contains("windows")) Platform.WINDOWS else Platform.POSIX
}

/**
 * Logs platform detection information for debugging.
 * @param logger Gradle logger instance
 */
fun logPlatformDetection(logger: Logger) {
    val platform = detectPlatform()
    val osName = System.getProperty("os.name")
    val osVersion = System.getProperty("os.version")
    logger.lifecycle("Platform detected: $platform (OS: $osName $osVersion)")
}
