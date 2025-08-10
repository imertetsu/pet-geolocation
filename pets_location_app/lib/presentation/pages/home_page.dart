import 'package:flutter/material.dart';
import 'package:pets_location_app/presentation/pages/news/news_feed_page.dart';
import 'pets/pet_list_page.dart';
import 'pets/map_pet_page.dart';
import '../../presentation/pages/welcome_page.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import '../pages/news/my_posts_page.dart';

class HomePage extends StatefulWidget {
  final String userId;

  const HomePage({super.key, required this.userId});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  int _selectedIndex = 0;
  late final List<Widget> _pages;
  String? userName;
  String? userPhotoUrl;
  String? userEmail;
  final _storage = const FlutterSecureStorage();

  @override
  void initState() {
    super.initState();
    _pages = [
      NewsFeedPage(userId: widget.userId),
      PetListPage(userId: widget.userId),
      PetMapPage(userId: widget.userId),
      MyPostsPage(),
    ];
    _loadUserInfo();
  }

  String formatUserName(String fullName) {
    final parts = fullName.trim().split(' ');
    if (parts.length >= 2) {
      return '${parts[0]} ${parts[1][0]}.';
    }
    return fullName;
  }

  Future<void> _loadUserInfo() async {
    final name = await _storage.read(key: 'userName');
    final photoUrl = await _storage.read(key: 'photoUrl');
    final email = await _storage.read(key: 'email');
    setState(() {
      userName = name;
      userPhotoUrl = photoUrl;
      userEmail = email;
    });
  }

  void _onItemTapped(int index) {
    setState(() {
      _selectedIndex = index;
    });
    Navigator.pop(context); // Cierra el Drawer después de navegar
  }

  void _logout() async {
  const storage = FlutterSecureStorage();
  await storage.deleteAll();

  Navigator.pushAndRemoveUntil(
    context,
    MaterialPageRoute(builder: (_) => const WelcomePage()),
    (route) => false,
  );
}

  @override
  Widget build(BuildContext context) {
    final isAuthenticated = userName != null;

    return Scaffold(
      drawer: isAuthenticated
          ? Drawer(
              child: ListView(
                children: [
                  DrawerHeader(
                    decoration: const BoxDecoration(color: Colors.teal),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        CircleAvatar(
                          backgroundImage: userPhotoUrl != null && userPhotoUrl!.isNotEmpty
                              ? NetworkImage(userPhotoUrl!)
                              : null,
                          radius: 28,
                          child: userPhotoUrl == null || userPhotoUrl!.isEmpty
                              ? const Icon(Icons.person, size: 28)
                              : null,
                        ),
                        const SizedBox(height: 12),
                        Text(
                          userName!,
                          style: const TextStyle(color: Colors.white, fontSize: 18),
                        ),
                        Text(
                          userEmail!,
                          style: const TextStyle(color: Colors.white, fontSize: 14),
                        ),
                      ],
                    ),
                  ),
                  ListTile(
                    leading: const Icon(Icons.home),
                    title: const Text('Inicio'),
                    onTap: () => _onItemTapped(0),
                  ),
                  ListTile(
                    leading: const Icon(Icons.pets),
                    title: const Text('Mis Mascotas'),
                    onTap: () => _onItemTapped(1),
                  ),
                  ListTile(
                    leading: const Icon(Icons.map),
                    title: const Text('Ver Mapa'),
                    onTap: () => _onItemTapped(2),
                  ),
                  ListTile(
                    leading: const Icon(Icons.article),
                    title: const Text('Mis Posts'),
                    onTap: () => _onItemTapped(3),
                  ),
                  const Divider(),
                  ListTile(
                    leading: const Icon(Icons.logout),
                    title: const Text('Cerrar Sesión'),
                    onTap: _logout,
                  ),
                ],
              ),
            )
          : null, // Si no está autenticado, no hay Drawer
      appBar: AppBar(
        title: const Text('PetsBol'),
        automaticallyImplyLeading: isAuthenticated,
        actions: [
          if (!isAuthenticated)
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 24.0),
              child: OutlinedButton(
                onPressed: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(builder: (_) => const WelcomePage()),
                  );
                },
                style: OutlinedButton.styleFrom(
                  side: const BorderSide(color: Color(0xFF9C27B0), width: 1.5),
                  foregroundColor: Color(0xFF9C27B0),
                  padding: const EdgeInsets.symmetric(horizontal: 28, vertical: 10),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(10),
                  ),
                  textStyle: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                ),
                child: const Text('Iniciar sesión'),
              ),
            ),
          if (isAuthenticated)
            Padding(
              padding: const EdgeInsets.only(right: 12),
              child: Row(
                children: [
                  Text(
                    formatUserName(userName!),
                    style: const TextStyle(fontSize: 16, fontWeight: FontWeight.w500),
                  ),
                  const SizedBox(width: 8),
                  CircleAvatar(
                    backgroundImage: userPhotoUrl != null && userPhotoUrl!.isNotEmpty
                        ? NetworkImage(userPhotoUrl!)
                        : null,
                    child: userPhotoUrl == null || userPhotoUrl!.isEmpty
                        ? const Icon(Icons.person)
                        : null,
                  ),
                ],
              ),
            ),
        ],
      ),
      body: _pages[isAuthenticated ? _selectedIndex : 0], // Solo muestra NewsFeed si no está autenticado
    );
  }

}

