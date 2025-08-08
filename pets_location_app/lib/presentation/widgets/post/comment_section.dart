import 'package:flutter/material.dart';
import 'package:pets_location_app/data/datasources/news_remote_datasource.dart';
import 'package:pets_location_app/data/datasources/session/session_manager.dart';
import 'package:pets_location_app/data/models/comment.dart';
import 'package:pets_location_app/data/models/post.dart';

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
  String? _authorName;

  @override
  void initState() {
    super.initState();
    _comments = List.from(widget.initialComments);
    _loadUserData();
  }

  Future<void> _loadUserData() async {
    final id = await _session.getUserId();
    final name = await _session.getUserName();

    setState(() {
      _authorId = id;
      _authorName = name ?? 'Usuario';
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
        authorName: _authorName ?? 'Usuario',
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

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        const Divider(),
        if (_isLoadingComments)
          const Center(child: CircularProgressIndicator())
        else
          ..._comments.map((comment) => ListTile(
                leading: const CircleAvatar(child: Icon(Icons.person)),
                title: Text(comment.authorName),
                subtitle: Text(comment.content),
                trailing: Text(
                  '${comment.createdAt.day}/${comment.createdAt.month}/${comment.createdAt.year}',
                  style: const TextStyle(fontSize: 12, color: Colors.grey),
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
