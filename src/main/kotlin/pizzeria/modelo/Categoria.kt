package pizzeria.modelo

/** Categorías posibles de un producto de la pizzería. */
enum class Categoria(val etiqueta: String) {
    PIZZA("Pizza"),
    BEBIDA("Bebida"),
    INGREDIENTE("Ingrediente")
}
