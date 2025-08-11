import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:pets_location_app/presentation/pages/home_page.dart';
import 'package:pets_location_app/presentation/pages/news/news_feed_page.dart';
import 'presentation/pages/welcome_page.dart';
import 'package:firebase_core/firebase_core.dart';
import 'firebase_options.dart';

void main() async {
  // Asegura que Flutter esté inicializado antes de Firebase
  WidgetsFlutterBinding.ensureInitialized();

  // Inicializa Firebase con la configuración de tu proyecto
  await Firebase.initializeApp(
    options: DefaultFirebaseOptions.currentPlatform,
  );
  runApp(const GeoPetApp());
}

class GeoPetApp extends StatelessWidget {
  const GeoPetApp({super.key});

  Future<String?> getStoredUserId() async {
    final storage = FlutterSecureStorage();
    return await storage.read(key: 'userId');
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'PatitasNews',
      debugShowCheckedModeBanner: false,
      home: FutureBuilder<String?>(
        future: getStoredUserId(),
        builder: (context, snapshot) {
          if (snapshot.connectionState != ConnectionState.done) {
            return const Scaffold(
              body: Center(child: CircularProgressIndicator()),
            );
          }

          final userId = snapshot.data;
          return HomePage(userId: userId ?? '');
        },
      ),
    );
  }
}
