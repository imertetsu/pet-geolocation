import 'package:flutter/material.dart';
import 'package:dio/dio.dart';
import 'package:pets_location_app/data/datasources/news_remote_datasource.dart';
import 'package:pets_location_app/data/models/post.dart';
import 'package:pets_location_app/presentation/pages/users/user_helper.dart';
import 'package:pets_location_app/presentation/widgets/post/post_card.dart';
import 'package:pets_location_app/presentation/widgets/post/create_post_page.dart';
import '../../../core/network/api_client.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';


class NewsFeedPage extends StatefulWidget {
  final String userId;

  const NewsFeedPage({super.key, required this.userId});

  @override
  State<NewsFeedPage> createState() => _NewsFeedPageState();
}

class _NewsFeedPageState extends State<NewsFeedPage> {
  final _storage = const FlutterSecureStorage();
  late final NewsRemoteDataSource _newsService;
  late Future<List<Post>> _futurePosts;
  late UserHelper userHelper;
  String? _userId;

  @override
  void initState() {
    super.initState();
    final Dio _dio = ApiClient.dio;
    _newsService = NewsRemoteDataSource(_dio);

    _loadUserId();
  }

  Future<void> _loadUserId() async {
    final storedUserId = await _storage.read(key: 'userId');

    setState(() {
      _userId = storedUserId;
      _futurePosts = _newsService.fetchNews(userId: _userId);
    });
  }

  Future<void> _refreshPosts() async {
    setState(() {
      _futurePosts = _newsService.fetchNews(userId: _userId);
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Noticias'),
      ),
      body: FutureBuilder<List<Post>>(
        future: _futurePosts,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          } else if (snapshot.hasError) {
            return Center(child: Text('Error: ${snapshot.error}'));
          } else {
            final posts = snapshot.data!;
            if (posts.isEmpty) {
              return const Center(child: Text('No hay publicaciones aún.'));
            }

            return RefreshIndicator(
              onRefresh: _refreshPosts,
              child: ListView.builder(
                physics: const AlwaysScrollableScrollPhysics(),
                itemCount: posts.length,
                itemBuilder: (context, index) {
                  return PostCard(
                    post: posts[index],
                    dataSource: _newsService,
                  );
                },
              ),
            );
          }
        },
      ),
        floatingActionButton: _userId != null
          ? FloatingActionButton(
              onPressed: () async {
                await Navigator.push(
                  context,
                  MaterialPageRoute(builder: (_) => CreatePostPage()),
                );
                _refreshPosts(); // refresca después de volver
              },
              child: const Icon(Icons.add),
              tooltip: 'Crear publicación',
            )
          : null, // oculta el botón si no está logeado
    );
  }
}