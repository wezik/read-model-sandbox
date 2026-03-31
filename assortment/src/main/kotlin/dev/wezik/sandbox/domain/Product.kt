data class Product(
  val id: UUID,
  val styles: List<Style>,
)

data class Style(
  val id: UUID,
  val articles: List<Article>,
)

data class Article(
  val id: UUID,
)
