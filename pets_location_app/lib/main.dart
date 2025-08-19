import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:pets_location_app/presentation/pages/home_page.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:pets_location_app/presentation/widgets/post/post_detail_page.dart';
import 'firebase_options.dart';
import 'package:app_links/app_links.dart';
import 'dart:async';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  await Firebase.initializeApp(
    options: DefaultFirebaseOptions.currentPlatform,
  );

  runApp(const GeoPetApp());
}

class GeoPetApp extends StatefulWidget {
  const GeoPetApp({super.key});

  @override
  State<GeoPetApp> createState() => _GeoPetAppState();
}

class _GeoPetAppState extends State<GeoPetApp> {
  final _secureStorage = const FlutterSecureStorage();
  String? _userId;
  StreamSubscription? _sub;
  final AppLinks _appLinks = AppLinks();
  final GlobalKey<NavigatorState> navigatorKey = GlobalKey<NavigatorState>();


  @override
  void initState() {
    super.initState();
    _loadUserId();
    _initDeepLinks();
    _checkInitialLink();
  }

  Future<void> _loadUserId() async {
    final id = await _secureStorage.read(key: "userId");
    if (mounted) setState(() => _userId = id);
  }

  void _initDeepLinks() {
    _sub = _appLinks.uriLinkStream.listen((Uri? uri) {
      if (uri == null) return;
      _handleUri(uri);
    }, onError: (err) {
      print("Error en deep link: $err");
    });
  }

  Future<void> _checkInitialLink() async {
    try {
      final uri = await _appLinks.getInitialLink();
      if (uri != null) {
        _handleUri(uri);
      }
    } catch (e) {
      print("Error obteniendo initial link: $e");
    }
  }

  void _handleUri(Uri uri) {
    // 🔹 Deep link con esquema personalizado
    if (uri.scheme == "petsapp" && uri.host == "post") {
      final postId = int.tryParse(uri.pathSegments.first);
      if (postId != null) {
        _openPostDetail(postId);
      }
    }

    // 🔹 App Link con dominio
    if (uri.host == "localhost") {
      if (uri.pathSegments.length >= 4 &&
          uri.pathSegments[0] == "api" &&
          uri.pathSegments[1] == "news" &&
          uri.pathSegments[2] == "public") {
        final postId = int.tryParse(uri.pathSegments[3]);
        if (postId != null) {
          _openPostDetail(postId);
        }
      }
    }
  }

  void _openPostDetail(int postId) {
    WidgetsBinding.instance.addPostFrameCallback((_) {
      navigatorKey.currentState?.push(
        MaterialPageRoute(
          builder: (_) => PostDetailPage(postId: postId),
        ),
      );
    });
  }

  @override
  void dispose() {
    _sub?.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'PatitasNews',
      navigatorKey: navigatorKey,
      debugShowCheckedModeBanner: false,
      home: _userId == null
          ? const Scaffold(
              body: Center(child: CircularProgressIndicator()),
            )
          : HomePage(userId: _userId ?? ''),
    );
  }
}
