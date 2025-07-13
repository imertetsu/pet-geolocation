import 'dart:async';

import 'package:flutter/material.dart';
import 'package:pets_location_app/data/datasources/news_remote_datasource.dart';
import 'package:pets_location_app/data/models/post.dart';
import 'package:pets_location_app/presentation/widgets/post/post_card_header.dart';
import 'package:pets_location_app/presentation/widgets/post/post_image_carousel.dart';
import 'package:pets_location_app/presentation/widgets/post/post_reaction_bar.dart';
import '../post/category_icon.dart';
import '../post/comment_section.dart';

class PostCard extends StatefulWidget {
  final Post post;
  final String userId;
  final NewsRemoteDataSource dataSource;

  const PostCard({
    super.key,
    required this.post,
    required this.userId,
    required this.dataSource,
  });

  @override
  State<PostCard> createState() => _PostCardState();
}

class _PostCardState extends State<PostCard> {
  bool _isLoading = false;
  OverlayEntry? _overlayEntry;
  final GlobalKey _reactionKey = GlobalKey();
  Timer? _closeTimer;
  bool _showComments = false;

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
          userId: widget.userId,
          reaction: selectedReaction,
        );
        _incrementReaction(selectedReaction);
      } else if (currentReaction == selectedReaction) {
        await widget.dataSource.deleteReaction(
          newsId: widget.post.id,
          userId: widget.userId,
        );
        _decrementReaction(selectedReaction);
        selectedReaction = '';
      } else {
        await widget.dataSource.updateReaction(
          newsId: widget.post.id,
          userId: widget.userId,
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
      case 'SAD':
        return Icons.sentiment_dissatisfied;
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
            PostCardHeader(
              authorName: widget.post.authorName,
              date: widget.post.createdAt,
              categoryIcon: CategoryIcon(category: widget.post.category),
            ),
            const SizedBox(height: 10),
            Text(widget.post.title, style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
            const SizedBox(height: 6),
            Text(widget.post.content),
            const SizedBox(height: 10),
            PostImageCarousel(imageUrls: images),
            const SizedBox(height: 10),
            PostReactionsBar(
              likeCount: likeCount,
              sadCount: sadCount,
              hopeCount: hopeCount,
            ),
            const Divider(),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: [
                MouseRegion(
                  onEnter: (_) {
                    _cancelCloseTimer();
                    _showReactionMenu(context);
                  },
                  onExit: (_) => _startCloseTimer(),
                  child: Icon(
                    key: _reactionKey,
                    _getIconForReaction(userReaction),
                    color: userReaction != null ? Colors.blue : Colors.grey,
                  ),
                ),
                TextButton.icon(
                  onPressed: () {
                    setState(() {
                      _showComments = !_showComments;
                    });
                  },
                  icon: const Icon(Icons.comment),
                  label: Text("(${widget.post.comments.length}) Comentar"),
                ),
              ],
            ),
            if (_showComments)
              CommentSection(
                postId: widget.post.id,
                authorId: widget.userId,
                authorName: widget.post.authorName,
                initialComments: widget.post.comments,
                dataSource: widget.dataSource,
              ),
          ],
        ),
      ),
    );
  }
}