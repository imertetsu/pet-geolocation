import 'package:flutter/material.dart';
import 'package:pets_location_app/core/network/api_client.dart';
import 'package:pets_location_app/data/models/post.dart';
import '../../../data/datasources/news_remote_datasource.dart';
import '../post/post_card.dart';

class PostDetailPage extends StatefulWidget {
  final int postId;

  const PostDetailPage({
    super.key,
    required this.postId,
  });

  @override
  State<PostDetailPage> createState() => _PostDetailPageState();
}

class _PostDetailPageState extends State<PostDetailPage> {
  Post? _post;
  bool _isLoading = true;
  bool _error = false;
  late final NewsRemoteDataSource dataSource;

  @override
  void initState() {
    super.initState();
    dataSource = NewsRemoteDataSource(ApiClient.dio);
    _loadPost();
  }

  Future<void> _loadPost() async {
    try {
      final post = await dataSource.fetchPostById(widget.postId);
      if (mounted) {
        setState(() {
          _post = post;
          _isLoading = false;
        });
      }
    } catch (e) {
      print("Error al cargar post: $e");
      if (mounted) {
        setState(() {
          _error = true;
          _isLoading = false;
        });
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) {
      return const Scaffold(
        body: Center(child: CircularProgressIndicator()),
      );
    }

    if (_error || _post == null) {
      return Scaffold(
        appBar: AppBar(title: const Text("Detalle de Post")),
        body: const Center(child: Text("Error al cargar el post")),
      );
    }

    return Scaffold(
      appBar: AppBar(title: const Text("Detalle de Post")),
      body: SingleChildScrollView(
        child: Padding(
          padding: const EdgeInsets.all(8.0),
          child: PostCard(
            post: _post!,
            dataSource: dataSource,
            showActions: false,
          ),
        ),
      ),
    );
  }
}