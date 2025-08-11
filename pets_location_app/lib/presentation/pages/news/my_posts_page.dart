import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:pets_location_app/data/models/post.dart';
import 'package:pets_location_app/data/datasources/news_remote_datasource.dart';
import 'package:pets_location_app/presentation/widgets/post/post_card.dart';
import '../../../core/network/api_client.dart';
import '../../../data/datasources/file_remote_datasource.dart';
import '../../widgets/post/edit_post_page.dart';

class MyPostsPage extends StatefulWidget {
  const MyPostsPage({super.key});

  @override
  State<MyPostsPage> createState() => _MyPostsPageState();
}

class _MyPostsPageState extends State<MyPostsPage> {
  final _storage = const FlutterSecureStorage();
  late final NewsRemoteDataSource _newsDataSource;
  late final FileRemoteDataSource _fileRemoteDataSource;
  String? _userId;
  late Future<List<Post>> _myPostsFuture;

  @override
  void initState() {
    super.initState();
    _newsDataSource = NewsRemoteDataSource(ApiClient.dio);
    _fileRemoteDataSource = FileRemoteDataSource();
    _loadUserId();
  }

  Future<void> _loadUserId() async {
    final storedUserId = await _storage.read(key: 'userId');
    setState(() {
      _userId = storedUserId;
      _myPostsFuture = _newsDataSource.getPostsByUserId(_userId!);
    });
  }

  Future<void> _refreshPosts() async {
    setState(() {
      _myPostsFuture = _newsDataSource.getPostsByUserId(_userId!);
    });
  }

  Future<void> _confirmDelete(Post post) async {
    final confirm = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text("Confirmar eliminación"),
        content: Text("¿Estás seguro de que quieres eliminar \"${post.title}\"?"),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text("Cancelar"),
          ),
          ElevatedButton(
            onPressed: () => Navigator.pop(context, true),
            child: const Text("Eliminar"),
            style: ElevatedButton.styleFrom(backgroundColor: Colors.red),
          ),
        ],
      ),
    );

    if (confirm == true) {
      await _deletePost(post.id);
    }
  }

  Future<void> _deletePost(int postId) async {
    try {
      // 1. Obtener el post para saber qué imágenes borrar
      final post = await _newsDataSource.fetchDetailedPostById(postId);

      // 2. Borrar cada imagen en el servidor de archivos
      for (final imageUrl in post.imageUrls) {
        try {
          await _fileRemoteDataSource.deleteFile(imageUrl);
        } catch (e) {
          print('Error deleting image $imageUrl: $e');
        }
      }

      // 3. Ahora borrar el post en tu backend principal
      final success = await _newsDataSource.deletePost(postId);

      if (success) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text("Post eliminado correctamente")),
        );
        _refreshPosts();
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text("Error al eliminar el post")),
        );
      }
    } catch (e) {
      print('Error deleting post: $e');
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("Error al eliminar el post")),
      );
    }
  }

  void _editPost(Post post) async {
    final updatedPost = await Navigator.push<Post>(
      context,
      MaterialPageRoute(
        builder: (_) => EditPostPage(post: post),
      ),
    );

    if (updatedPost != null) {
      _refreshPosts();
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("Post actualizado correctamente")),
      );
    }
  }


  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Mis publicaciones")),
      body: _userId == null
          ? const Center(child: CircularProgressIndicator())
          : FutureBuilder<List<Post>>(
              future: _myPostsFuture,
              builder: (context, snapshot) {
                if (snapshot.connectionState == ConnectionState.waiting) {
                  return const Center(child: CircularProgressIndicator());
                }
                if (snapshot.hasError) {
                  return const Center(child: Text("Error al cargar tus posts"));
                }
                if (!snapshot.hasData || snapshot.data!.isEmpty) {
                  return const Center(child: Text("No tienes publicaciones aún"));
                }

                final posts = snapshot.data!;
                return RefreshIndicator(
                  onRefresh: _refreshPosts,
                  child: ListView.builder(
                    physics: const AlwaysScrollableScrollPhysics(),
                    itemCount: posts.length,
                    itemBuilder: (context, index) {
                      final post = posts[index];
                      return PostCard(
                        post: post,
                        dataSource: _newsDataSource,
                        showActions: true,
                        onEdit: () => _editPost(post),
                        onDelete: () => _confirmDelete(post),
                      );
                    },
                  ),
                );
              },
            ),
    );
  }

}
