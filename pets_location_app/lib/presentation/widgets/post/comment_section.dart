import 'package:flutter/material.dart';
import 'package:pets_location_app/data/datasources/news_remote_datasource.dart';
import 'package:pets_location_app/data/datasources/session/session_manager.dart';
import 'package:pets_location_app/data/models/comment.dart';

class CommentSection extends StatefulWidget {
  final int postId;
  final List<Comment> initialComments;
  final NewsRemoteDataSource dataSource;
  final Future<void> Function() onCommentsUpdated;

  const CommentSection({
    super.key,
    required this.postId,
    required this.initialComments,
    required this.dataSource,
    required this.onCommentsUpdated,
  });

  @override
  State<CommentSection> createState() => _CommentSectionState();
}

class _CommentSectionState extends State<CommentSection> {
  final TextEditingController _controller = TextEditingController();
  final SessionManager _session = SessionManager();

  bool _isSubmitting = false;
  bool _isLoadingComments = false;
  late List<Comment> _comments;

  String? _authorId;

  @override
  void initState() {
    super.initState();
    _comments = List.from(widget.initialComments);
    _loadUserData();
  }

  Future<void> _loadUserData() async {
    final id = await _session.getUserId();

    setState(() {
      _authorId = id;
    });
  }

  Future<void> _refreshComments() async {
    try {
      final detailedPost = await widget.dataSource.fetchDetailedPostById(widget.postId);
      setState(() {
        _comments = detailedPost.comments;
      });
    } catch (e) {
      print("Error al refrescar comentarios: $e");
    }
  }

  Future<void> _submitComment() async {
    final content = _controller.text.trim();
    if (content.isEmpty || _authorId == null) return;

    setState(() => _isSubmitting = true);

    try {
      // 1. Enviar comentario
      await widget.dataSource.addComment(
        newsId: widget.postId,
        authorId: _authorId!,
        content: content,
      );

      _controller.clear();

      // 2. Refrescar comentarios desde el backend
      await widget.onCommentsUpdated();

      // 3. Obtener los comentarios actualizados directamente
      await _refreshComments();
    } catch (e) {
      print("Error al enviar comentario: $e");
    } finally {
      setState(() => _isSubmitting = false);
    }
  }
  Future<void> _confirmDelete(int commentId) async {
    final confirm = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text("Eliminar comentario"),
        content: const Text("¿Estás seguro de que quieres eliminar este comentario?"),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(false),
            child: const Text("Cancelar"),
          ),
          ElevatedButton(
            onPressed: () => Navigator.of(context).pop(true),
            style: ElevatedButton.styleFrom(backgroundColor: Colors.red),
            child: const Text("Eliminar"),
          ),
        ],
      ),
    );

    if (confirm == true) {
      await _deleteComment(commentId);
    }
  }

  Future<void> _deleteComment(int commentId) async {
    if (_authorId == null) return;

    setState(() => _isLoadingComments = true);

    try {
      await widget.dataSource.deleteComment(
        newsId: widget.postId,
        commentId: commentId,
        requesterId: _authorId!,
      );

      await widget.onCommentsUpdated();
      await _refreshComments();
    } catch (e) {
      print("Error al eliminar comentario: $e");
    } finally {
      setState(() => _isLoadingComments = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        const Divider(),
        if (_isLoadingComments)
          const Center(child: CircularProgressIndicator())
        else
          ..._comments.map((comment) => ListTile(
                leading: CircleAvatar(
                  radius: 16,
                  backgroundImage: comment.author.photoUrl.isNotEmpty
                      ? NetworkImage(comment.author.photoUrl)
                      : null,
                  child: comment.author.photoUrl.isEmpty
                      ? const Icon(Icons.person)
                      : null,
                ),
                title: Text(comment.author.name),
                subtitle: Text(comment.content),
                trailing: Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Text(
                      '${comment.createdAt.day}/${comment.createdAt.month}/${comment.createdAt.year}',
                      style: const TextStyle(fontSize: 12, color: Colors.grey),
                    ),
                    if (_authorId != null && comment.author.id == _authorId)
                      IconButton(
                        icon: const Icon(Icons.delete, color: Colors.red),
                        onPressed: () => _confirmDelete(comment.id),
                      ),
                  ],
                ),
              )),
        const SizedBox(height: 8),
        Row(
          children: [
            Expanded(
              child: TextField(
                controller: _controller,
                decoration: const InputDecoration(
                  hintText: "Escribe un comentario...",
                  border: OutlineInputBorder(),
                ),
              ),
            ),
            const SizedBox(width: 8),
            ElevatedButton(
              onPressed: _isSubmitting ? null : _submitComment,
              child: _isSubmitting
                  ? const SizedBox(
                      width: 16,
                      height: 16,
                      child: CircularProgressIndicator(strokeWidth: 2),
                    )
                  : const Text("Enviar"),
            ),
          ],
        ),
      ],
    );
  }
}
