import 'dart:convert';
import 'package:stomp_dart_client/stomp.dart';
import 'package:stomp_dart_client/stomp_config.dart';
import 'package:stomp_dart_client/stomp_frame.dart';

class LocationStompClient {
  StompClient? _client;

  void connect({
    required int petId,
    required Function(double lat, double lon) onLocationReceived,
  }) {
    _client = StompClient(
      config: StompConfig(
        url: 'ws://181.114.109.198:9090/api/ws-location',
        onConnect: (StompFrame frame) {
          _client?.subscribe(
            destination: '/topic/location/$petId',
            callback: (StompFrame frame) {
              final data = frame.body;
              if (data != null) {
                final json = jsonDecode(data);
                final lat = json['latitude'] as double;
                final lon = json['longitude'] as double;
                onLocationReceived(lat, lon);
              }
            },
          );
        },
        onWebSocketError: (dynamic error) => print('WebSocket error: $error'),
        onDisconnect: (_) => print('Disconnected from WebSocket'),
      ),
    );

    _client?.activate();
  }

  void disconnect() {
    _client?.deactivate();
  }
}

