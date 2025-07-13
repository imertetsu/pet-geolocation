import 'package:flutter/material.dart';
import 'package:dio/dio.dart';
import 'package:pets_location_app/data/datasources/news_remote_datasource.dart';
import 'package:pets_location_app/data/models/post.dart';
import 'package:pets_location_app/presentation/widgets/post/post_card.dart';
import '../../../core/network/api_client.dart';

class NewsFeedPage extends StatefulWidget {
  final String userId;

  const NewsFeedPage({super.key, required this.userId});

  @override
  State<NewsFeedPage> createState() => _NewsFeedPageState();
}

class _NewsFeedPageState extends State<NewsFeedPage> {
  late final NewsRemoteDataSource _newsService;
  late Future<List<Post>> _futurePosts;

  @override
  void initState() {
    super.initState();
    final Dio _dio = ApiClient.dio;
    _newsService = NewsRemoteDataSource(_dio);

    // Asegúrate de pasar el userId
    _futurePosts = _newsService.fetchNews(userId: widget.userId);
  }

  Future<void> _refreshPosts() async {
    setState(() {
      _futurePosts = _newsService.fetchNews(userId: widget.userId); // aquí también
    });
  }

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<List<Post>>(
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
                  userId: widget.userId,
                  dataSource: _newsService, // pasar el userId si lo necesitas ahí también
                );
              },
            ),
          );
        }
      },
    );
  }
}