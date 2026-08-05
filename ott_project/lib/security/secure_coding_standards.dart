// ISO 27001 | Module 3: Code Level Security | Task 1: Secure Coding Standards
// Description: Flutter client security policy guidelines, secure local storage encryption keys, and SSL pinning configuration.

class SecureCodingStandards {
  static const bool isSslPinningEnabled = true;
  static const bool isSecureStorageEnforced = true;
  static const String minTlsVersion = "TLSv1.3";

  static Map<String, dynamic> getSecurityPolicy() {
    return {
      'sslPinning': isSslPinningEnabled,
      'secureStorage': isSecureStorageEnforced,
      'tlsVersion': minTlsVersion,
      'allowRootedDevices': false,
      'allowScreenCapture': false,
    };
  }
}
