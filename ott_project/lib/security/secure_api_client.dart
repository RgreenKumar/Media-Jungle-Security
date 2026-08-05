// ISO 27001 | Module 3: Code Level Security | Task 5 & 6: CSRF Protection & Secure API Development
// Description: Secure API HTTP client interceptor attaching Authorization JWT headers and CSRF tokens to outgoing mobile API requests.

import 'dart:convert';
import 'package:http/http.dart' as http;

class SecureApiClient {
  final String baseUrl;
  String? _jwtToken;
  String? _csrfToken;

  SecureApiClient({required this.baseUrl});

  void setAuthToken(String token) {
    _jwtToken = token;
  }

  void setCsrfToken(String token) {
    _csrfToken = token;
  }

  Map<String, String> _buildHeaders() {
    final headers = <String, String>{
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    };
    if (_jwtToken != null) {
      headers['Authorization'] = 'Bearer $_jwtToken';
    }
    if (_csrfToken != null) {
      headers['X-CSRF-TOKEN'] = _csrfToken!;
    }
    return headers;
  }

  Future<http.Response> get(String endpoint) async {
    final uri = Uri.parse('$baseUrl$endpoint');
    return await http.get(uri, headers: _buildHeaders());
  }

  Future<http.Response> post(String endpoint, Map<String, dynamic> body) async {
    final uri = Uri.parse('$baseUrl$endpoint');
    return await http.post(uri, headers: _buildHeaders(), body: jsonEncode(body));
  }
}
