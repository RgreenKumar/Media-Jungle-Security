// ISO 27001 | Module 5: Audit Logging & Monitoring | Task 1 & 4: Mobile User Activity & Security Event Logger
// Description: Flutter mobile client audit logger sending user playback telemetry and mobile security events to backend Audit Logging API.

import 'dart:convert';
import 'package:http/http.dart' as http;

class AuditLogger {
  final String backendUrl;

  AuditLogger({required this.backendUrl});

  Future<bool> logUserActivity({
    required String userEmail,
    required String action,
    required String targetResource,
  }) async {
    try {
      final uri = Uri.parse('$backendUrl/api/v2/audit-logging/user-activities');
      final response = await http.post(
        uri,
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'userEmail': userEmail,
          'action': action,
          'targetResource': targetResource,
          'userAgent': 'Flutter OTT Mobile/1.0.0',
        }),
      );
      return response.statusCode == 200;
    } catch (e) {
      return false;
    }
  }

  Future<bool> logSecurityEvent({
    required String eventType,
    required String severity,
    required String eventDetails,
    String? userEmail,
  }) async {
    try {
      final uri = Uri.parse('$backendUrl/api/v2/audit-logging/security-events');
      final response = await http.post(
        uri,
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'eventType': eventType,
          'severity': severity,
          'eventDetails': eventDetails,
          'userEmail': userEmail ?? 'anonymous_mobile_user',
        }),
      );
      return response.statusCode == 200;
    } catch (e) {
      return false;
    }
  }
}
