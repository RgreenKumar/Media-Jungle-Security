// ISO 27001 | Module 4: Risk Management | Task 1 & 8: Mobile Security Risk Reporter & Continuous Risk Monitoring
// Description: Flutter client security risk detector sending mobile application threat telemetry and security risk events to backend Risk Register.

import 'dart:convert';
import 'package:http/http.dart' as http;

class RiskReporter {
  final String backendUrl;

  RiskReporter({required this.backendUrl});

  Future<bool> reportSecurityRisk({
    required String riskTitle,
    required String description,
    required String category,
    int likelihood = 2,
    int impact = 3,
  }) async {
    try {
      final uri = Uri.parse('$backendUrl/api/v2/risk-management/register');
      final response = await http.post(
        uri,
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'riskTitle': '[MOBILE_APP] $riskTitle',
          'description': description,
          'category': category,
          'likelihood': likelihood,
          'impact': impact,
          'controlEffectivenessPercent': 80,
        }),
      );
      return response.statusCode == 200;
    } catch (e) {
      return false;
    }
  }
}
