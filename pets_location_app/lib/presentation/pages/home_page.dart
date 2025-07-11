import 'package:flutter/material.dart';
import 'pets/pet_list_page.dart';
import 'pets/map_pet_page.dart';
import '../../presentation/pages/welcome_page.dart';

class HomePage extends StatefulWidget {
  final String userId;

  const HomePage({super.key, required this.userId});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  int _selectedIndex = 0;

  late final List<Widget> _pages;

  @override
  void initState() {
    super.initState();
    _pages = [
      const _FeedPlaceholderPage(),
      PetListPage(userId: widget.userId),
      PetMapPage(userId: widget.userId),
    ];
  }

  void _onItemTapped(int index) {
    setState(() {
      _selectedIndex = index;
    });
    Navigator.pop(context); // Cierra el Drawer después de navegar
  }

  void _logout() {
    Navigator.pushAndRemoveUntil(
      context,
      MaterialPageRoute(builder: (_) => const WelcomePage()),
      (route) => false,
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      drawer: Drawer(
        child: ListView(
          children: [
            const DrawerHeader(
              decoration: BoxDecoration(color: Colors.teal),
              child: Text('Menú Principal', style: TextStyle(color: Colors.white, fontSize: 24)),
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
            const Divider(),
            ListTile(
              leading: const Icon(Icons.logout),
              title: const Text('Cerrar Sesión'),
              onTap: _logout,
            ),
          ],
        ),
      ),
      appBar: AppBar(
        title: const Text('Geolocalizador de Mascotas'),
        automaticallyImplyLeading: true,
      ),
      body: _pages[_selectedIndex],
    );
  }
}

// Placeholder para el feed futuro
class _FeedPlaceholderPage extends StatelessWidget {
  const _FeedPlaceholderPage();

  @override
  Widget build(BuildContext context) {
    return const Center(
      child: Text(
        '¡Bienvenido! Próximamente verás publicaciones de mascotas aquí.',
        textAlign: TextAlign.center,
        style: TextStyle(fontSize: 18),
      ),
    );
  }
}
