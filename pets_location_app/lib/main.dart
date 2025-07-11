import 'package:flutter/material.dart';
import 'presentation/pages/welcome_page.dart';

void main() {
  runApp(const GeoPetApp());
}

class GeoPetApp extends StatelessWidget {
  const GeoPetApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'GeoPet',
      debugShowCheckedModeBanner: false,
      home: const WelcomePage(),
    );
  }
}
