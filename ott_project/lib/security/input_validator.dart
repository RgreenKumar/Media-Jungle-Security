// ISO 27001 | Module 3: Code Level Security | Task 2 & 4: Input Validation Framework & XSS Protection
// Description: Flutter input validator and text sanitizer enforcing regex bounds and escaping dangerous HTML/script characters.

class InputValidator {
  static final RegExp _emailRegExp = RegExp(r'^[a-zA-Z0-9.]+@[a-zA-Z0-9]+\.[a-zA-Z]+');

  static bool isValidEmail(String? email) {
    if (email == null || email.isEmpty) return false;
    return _emailRegExp.hasMatch(email);
  }

  static bool isSafeText(String? input) {
    if (input == null) return true;
    return !input.contains('<script>') && !input.contains("javascript:") && !input.contains("1=1");
  }

  static String sanitizeText(String input) {
    return input
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#x27;');
  }
}
