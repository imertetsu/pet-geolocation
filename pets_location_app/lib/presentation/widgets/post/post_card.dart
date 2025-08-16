import 'dart:async';
import 'package:flutter/material.dart';
import 'package:pets_location_app/data/datasources/news_remote_datasource.dart';
import 'package:pets_location_app/data/models/post.dart';
import 'package:pets_location_app/presentation/widgets/post/post_card_header.dart';
import 'package:pets_location_app/presentation/widgets/post/post_image_carousel.dart';
import 'package:pets_location_app/presentation/widgets/post/post_reaction_bar.dart';
import '../post/category_icon.dart';
import '../post/comment_section.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class PostCard extends StatefulWidget {
  final Post post;
  final NewsRemoteDataSource dataSource;
  final bool showActions;
  final VoidCallback? onEdit; 
  final VoidCallback? onDelete; 

  const PostCard({
    super.key,
    required this.post,
    required this.dataSource,
    this.showActions = false, // por defecto no se muestran los botones
    this.onEdit,
    this.onDelete,
  });

  @override
  State<PostCard> createState() => _PostCardState();
}

class _PostCardState extends State<PostCard> {
  String? _userId;
  bool _isLoading = false;
  OverlayEntry? _overlayEntry;
  final GlobalKey _reactionKey = GlobalKey();
  Timer? _closeTimer;
  bool _showComments = false;

  @override
  void initState() {
    super.initState();
    _loadUserId();
  }

  Future<void> _loadUserId() async {
    final userId = await _secureStorage.read(key: 'userId');
    if (mounted) {
      setState(() => _userId = userId);
    }
  }

  final _secureStorage = const FlutterSecureStorage();

  void _showReactionMenu(BuildContext context) {
    if (_overlayEntry != null || _reactionKey.currentContext == null) return;

    final overlay = Overlay.of(context);
    final renderBox = _reactionKey.currentContext!.findRenderObject() as RenderBox;
    final position = renderBox.localToGlobal(Offset.zero);
    final size = renderBox.size;

    _overlayEntry = OverlayEntry(
      builder: (context) => Positioned(
        top: position.dy + size.height,
        left: position.dx + size.width / 2 - 60,
        child: MouseRegion(
          onEnter: (_) => _cancelCloseTimer(),
          onExit: (_) => _startCloseTimer(),
          child: Material(
            elevation: 4,
            color: Colors.white,
            borderRadius: BorderRadius.circular(8),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                _reactionOption("LIKE", "👍 Like"),
                _reactionOption("LOVE", "❤️ Love"),
                _reactionOption("SAD", "😢 Sad"),
                _reactionOption("HOPE", "⭐ Hope"),
              ],
            ),
          ),
        ),
      ),
    );

    overlay.insert(_overlayEntry!);
  }
  Future<void> _loadComments() async {
    try {
      final detailedPost = await widget.dataSource.fetchDetailedPostById(widget.post.id);
      setState(() {
        widget.post.comments.clear();
        widget.post.comments.addAll(detailedPost.comments);
      });
    } catch (e) {
      print("Error al cargar comentarios: $e");
    }
  }

  void _startCloseTimer() {
    _closeTimer = Timer(const Duration(milliseconds: 200), () {
      _removeOverlay();
    });
  }

  void _cancelCloseTimer() {
    _closeTimer?.cancel();
  }

  Widget _reactionOption(String value, String label) {
    return InkWell(
      onTap: () {
        _handleReaction(value);
        _removeOverlay();
      },
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
        child: Text(label),
      ),
    );
  }

  void _removeOverlay() {
    _cancelCloseTimer();
    _overlayEntry?.remove();
    _overlayEntry = null;
  }

  void _handleReaction(String selectedReaction) async {
    if (_isLoading) return;

    setState(() => _isLoading = true);

    try {
      final currentReaction = widget.post.userReaction;

      if (currentReaction == null) {
        await widget.dataSource.addReaction(
          newsId: widget.post.id,
          userId: _userId!,
          reaction: selectedReaction,
        );
        _incrementReaction(selectedReaction);
      } else if (currentReaction == selectedReaction) {
        await widget.dataSource.deleteReaction(
          newsId: widget.post.id,
          userId: _userId!,
        );
        _decrementReaction(selectedReaction);
        selectedReaction = '';
      } else {
        await widget.dataSource.updateReaction(
          newsId: widget.post.id,
          userId: _userId!,
          newReaction: selectedReaction,
        );
        _decrementReaction(currentReaction);
        _incrementReaction(selectedReaction);
      }

      setState(() {
        widget.post.userReaction = selectedReaction.isEmpty ? null : selectedReaction;
      });
    } catch (e) {
      print("Error al reaccionar: $e");
    } finally {
      setState(() => _isLoading = false);
    }
  }

  void _incrementReaction(String type) {
    widget.post.reactions[type] = (widget.post.reactions[type] ?? 0) + 1;
  }

  void _decrementReaction(String type) {
    if (widget.post.reactions.containsKey(type) && widget.post.reactions[type]! > 0) {
      widget.post.reactions[type] = widget.post.reactions[type]! - 1;
    }
  }

  IconData _getIconForReaction(String? reaction) {
    switch (reaction) {
      case 'LIKE':
        return Icons.thumb_up;
      case 'LOVE':
        return Icons.favorite;
      case 'SAD':
        return Icons.sentiment_dissatisfied_rounded;
      case 'HOPE':
        return Icons.star;
      default:
        return Icons.emoji_emotions;
    }
  }

  @override
  Widget build(BuildContext context) {
    final images = widget.post.imageUrls.take(3).toList();
    final likeCount = widget.post.reactions['LIKE'] ?? 0;
    final loveCount = widget.post.reactions['LOVE'] ?? 0;
    final sadCount = widget.post.reactions['SAD'] ?? 0;
    final hopeCount = widget.post.reactions['HOPE'] ?? 0;
    final userReaction = widget.post.userReaction;

    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
      child: Padding(
        padding: const EdgeInsets.all(12),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Expanded(
                  child: PostCardHeader(
                    authorName: widget.post.author.name,
                    date: widget.post.createdAt,
                    categoryIcon: CategoryIcon(category: widget.post.category),
                  ),
                ),
                if (widget.showActions) // <-- solo en MyPosts y Admin
                  PopupMenuButton<String>(
                    onSelected: (value) {
                      if (value == 'edit') {
                        widget.onEdit?.call();
                      } else if (value == 'delete') {
                        widget.onDelete?.call();
                      }
                    },
                    itemBuilder: (context) => [
                      const PopupMenuItem(value: 'edit', child: Text('Editar')),
                      const PopupMenuItem(value: 'delete', child: Text('Eliminar')),
                    ],
                  ),
              ],
            ),
            const SizedBox(height: 10),
            Text(
              widget.post.title,
              style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 6),
            Text(widget.post.content),
            const SizedBox(height: 10),
            PostImageCarousel(imageUrls: images),
            const SizedBox(height: 10),
            PostReactionsBar(
              likeCount: likeCount,
              loveCount: loveCount,
              sadCount: sadCount,
              hopeCount: hopeCount,
            ),
            const Divider(),
            if (_userId != null)
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceAround,
                children: [
                  MouseRegion(
                    onEnter: (_) {
                      _cancelCloseTimer();
                      _showReactionMenu(context);
                    },
                    onExit: (_) => _startCloseTimer(),
                    child: GestureDetector(
                      onTap: () => _showReactionMenu(context),
                      child: Icon(
                        key: _reactionKey,
                        _getIconForReaction(userReaction),
                        color: userReaction != null ? Colors.blue : Colors.grey,
                      ),
                    ),
                  ),
                  TextButton.icon(
                    onPressed: () async {
                      setState(() {
                        _showComments = !_showComments;
                      });
                      if (!_showComments) return;
                      await _loadComments();
                    },
                    icon: const Icon(Icons.comment),
                    label: Text("(${widget.post.comments.length}) Comentar"),
                  ),
                ],
              ),
            if (_showComments && _userId != null)
              CommentSection(
                postId: widget.post.id,
                initialComments: widget.post.comments,
                dataSource: widget.dataSource,
                onCommentsUpdated: _loadComments,
              ),
          ],
        ),
      ),
    );
  }
}