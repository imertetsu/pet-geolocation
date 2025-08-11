import 'package:flutter/material.dart';
import 'package:intl/intl.dart'; // para formatear fechas
import 'package:pets_location_app/data/datasources/news_remote_datasource.dart';
import 'package:pets_location_app/data/models/news_category.dart';
import 'package:pets_location_app/data/models/post.dart';
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
  List<Post> _posts = [];
  String? _userId;

  int _currentPage = 0;
  final int _pageSize = 10;
  bool _isLoading = false;
  bool _hasMore = true;

  final ScrollController _scrollController = ScrollController();

  // Filtros
  NewsCategory? _selectedCategory;
  String? _countryFilter;
  String? _cityFilter;
  DateTime? _fromDateFilter;

  @override
  void initState() {
    super.initState();
    _newsService = NewsRemoteDataSource(ApiClient.dio);

    _loadUserId();

    _scrollController.addListener(() {
      if (_scrollController.position.pixels >=
          _scrollController.position.maxScrollExtent - 200) {
        _loadMorePosts();
      }
    });
  }

  Future<void> _loadUserId() async {
    final storedUserId = await _storage.read(key: 'userId');
    setState(() {
      _userId = storedUserId;
    });
    _refreshPosts();
  }

  Future<void> _refreshPosts() async {
    _currentPage = 0;
    _posts.clear();
    _hasMore = true;
    await _loadMorePosts();
  }

  Future<void> _loadMorePosts() async {
    if (_isLoading || !_hasMore) return;

    setState(() {
      _isLoading = true;
    });

    try {
      final pageResult = await _newsService.fetchNews(
        userId: _userId,
        page: _currentPage,
        size: _pageSize,
        category: _selectedCategory?.name,
        country: _countryFilter,
        city: _cityFilter,
        fromDate: _fromDateFilter,
      );

      setState(() {
        _posts.addAll(pageResult.content);
        _hasMore = _currentPage < pageResult.totalPages - 1;
        if (_hasMore) _currentPage++;
      });
    } catch (e) {
      print('Error loading posts: $e');
    } finally {
      setState(() {
        _isLoading = false;
      });
    }
  }

  void _openFilterDialog() async {
    final result = await showDialog<_FiltersResult>(
      context: context,
      builder: (context) {
        return _FiltersDialog(
          initialCategory: _selectedCategory,
          initialCountry: _countryFilter,
          initialCity: _cityFilter,
          initialFromDate: _fromDateFilter,
        );
      },
    );

    if (result != null) {
      setState(() {
        _selectedCategory = result.category;
        _countryFilter = result.country;
        _cityFilter = result.city;
        _fromDateFilter = result.fromDate;
      });
      _refreshPosts();
    }
  }

  @override
  void dispose() {
    _scrollController.dispose();
    super.dispose();
  }

  String _formatDate(DateTime? date) {
    if (date == null) return '';
    return DateFormat('yyyy-MM-dd').format(date);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Noticias'),
        actions: [
          IconButton(
            icon: const Icon(Icons.filter_list),
            tooltip: 'Filtrar',
            onPressed: _openFilterDialog,
          )
        ],
      ),
      body: RefreshIndicator(
        onRefresh: _refreshPosts,
        child: ListView.builder(
          controller: _scrollController,
          physics: const AlwaysScrollableScrollPhysics(),
          itemCount: _posts.length + (_hasMore ? 1 : 0),
          itemBuilder: (context, index) {
            if (index == _posts.length) {
              return const Padding(
                padding: EdgeInsets.symmetric(vertical: 16),
                child: Center(child: CircularProgressIndicator()),
              );
            }
            return PostCard(
              post: _posts[index],
              dataSource: _newsService,
              showActions: false,
            );
          },
        ),
      ),
      floatingActionButton: _userId != null
          ? FloatingActionButton(
              onPressed: () async {
                await Navigator.push(
                  context,
                  MaterialPageRoute(builder: (_) => CreatePostPage()),
                );
                _refreshPosts();
              },
              child: const Icon(Icons.add),
              tooltip: 'Crear publicación',
            )
          : null,
    );
  }
}

// Clase para encapsular resultado de filtros
class _FiltersResult {
  final NewsCategory? category;
  final String? country;
  final String? city;
  final DateTime? fromDate;

  _FiltersResult({
    required this.category,
    required this.country,
    required this.city,
    required this.fromDate,
  });
}

// Diálogo de filtros
class _FiltersDialog extends StatefulWidget {
  final NewsCategory? initialCategory;
  final String? initialCountry;
  final String? initialCity;
  final DateTime? initialFromDate;

  const _FiltersDialog({
    Key? key,
    this.initialCategory,
    this.initialCountry,
    this.initialCity,
    this.initialFromDate,
  }) : super(key: key);

  @override
  State<_FiltersDialog> createState() => _FiltersDialogState();
}

class _FiltersDialogState extends State<_FiltersDialog> {
  NewsCategory? _selectedCategory;
  late TextEditingController _countryController;
  late TextEditingController _cityController;
  DateTime? _selectedDate;

  @override
  void initState() {
    super.initState();
    _selectedCategory = widget.initialCategory;
    _countryController = TextEditingController(text: widget.initialCountry ?? '');
    _cityController = TextEditingController(text: widget.initialCity ?? '');
    _selectedDate = widget.initialFromDate;
  }

  Future<void> _pickDate() async {
    final now = DateTime.now();
    final picked = await showDatePicker(
      context: context,
      initialDate: _selectedDate ?? now,
      firstDate: DateTime(2000),
      lastDate: now,
    );
    if (picked != null) {
      setState(() {
        _selectedDate = picked;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: const Text('Filtrar noticias'),
      content: SingleChildScrollView(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            DropdownButtonFormField<NewsCategory>(
              value: _selectedCategory,
              decoration: const InputDecoration(labelText: "Categoría"),
              items: NewsCategory.values.map((cat) {
                return DropdownMenuItem(
                  value: cat,
                  child: Text(cat.name), // O tu mapa a español si quieres
                );
              }).toList(),
              onChanged: (value) {
                setState(() {
                  _selectedCategory = value;
                });
              },
            ),
            TextFormField(
              controller: _countryController,
              decoration: const InputDecoration(labelText: "País"),
            ),
            TextFormField(
              controller: _cityController,
              decoration: const InputDecoration(labelText: "Ciudad"),
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                Expanded(
                  child: Text(_selectedDate == null
                      ? 'Fecha desde'
                      : 'Desde: ${DateFormat('yyyy-MM-dd').format(_selectedDate!)}'),
                ),
                TextButton(
                  onPressed: _pickDate,
                  child: const Text('Seleccionar fecha'),
                ),
                if (_selectedDate != null)
                  IconButton(
                    icon: const Icon(Icons.clear),
                    onPressed: () {
                      setState(() {
                        _selectedDate = null;
                      });
                    },
                  ),
              ],
            )
          ],
        ),
      ),
      actions: [
        TextButton(
          onPressed: () => Navigator.pop(context, null), // Cancelar sin cambios
          child: const Text('Cancelar'),
        ),
        ElevatedButton(
          onPressed: () {
            Navigator.pop(
              context,
              _FiltersResult(
                category: _selectedCategory,
                country: _countryController.text.isEmpty ? null : _countryController.text,
                city: _cityController.text.isEmpty ? null : _cityController.text,
                fromDate: _selectedDate,
              ),
            );
          },
          child: const Text('Aplicar'),
        ),
      ],
    );
  }
}
