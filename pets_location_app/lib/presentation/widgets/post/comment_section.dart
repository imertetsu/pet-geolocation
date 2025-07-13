import 'package:flutter/material.dart';
import 'package:pets_location_app/data/datasources/news_remote_datasource.dart';
import 'package:pets_location_app/data/models/comment.dart';

class CommentSection extends StatefulWidget {
  final int postId;
  final String authorId;
  final String authorName;
  final List<Comment> initialComments;
  final NewsRemoteDataSource dataSource;

  const CommentSection({
    super.key,
    required this.postId,
    required this.authorId,
    required this.authorName,
    required this.initialComments,
    required this.dataSource,
  });

  @override
  State<CommentSection> createState() => _CommentSectionState();
}

class _CommentSectionState extends State<CommentSection> {
  final TextEditingController _controller = TextEditingController();
  bool _isSubmitting = false;
  late List<Comment> _comments;

  @override
  void initState() {
    super.initState();
    _comments = List.from(widget.initialComments);
    _refreshComments(); // opcional: refrescar al abrir
  }

  Future<void> _submitComment() async {
    final content = _controller.text.trim();
    if (content.isEmpty) return;

    setState(() => _isSubmitting = true);

    try {
      await widget.dataSource.addComment(
        newsId: widget.postId,
        authorId: widget.authorId,
        authorName: widget.authorName,
        content: content,
      );

      _controller.clear();
      await _refreshComments(); // actualizamos desde el backend
    } catch (e) {
      print("Error al enviar comentario: $e");
    } finally {
      setState(() => _isSubmitting = false);
    }
  }

  Future<void> _refreshComments() async {
    try {
      final updatedPost = await widget.dataSource.fetchPostById(widget.postId);
      setState(() {
        _comments = List.from(updatedPost.comments);
      });
    } catch (e) {
      print('Error al refrescar comentarios: $e');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        const Divider(),
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
              child: const Text("Enviar"),
            ),
          ],
        ),
      ],
    );
  }
}
