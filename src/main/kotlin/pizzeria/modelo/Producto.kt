package pizzeria.modelo

import pizzeria.anotaciones.Promocionable

/**
 * Producto que se vende en la pizzería (pizza, bebida o ingrediente adicional).
 *
 * Dos productos son iguales si tienen el mismo [nombre] (sin importar mayúsculas)
 * y la misma [categoria]. El precio NO participa de la igualdad.
 *
 * En Kotlin el operador `==` llama internamente a `equals()`, por lo que sobrescribir
 * `equals()` (junto con `hashCode()`) es la forma de "sobrecargar" `==`.
 */
open class Producto(
    val nombre: String,
    val categoria: Categoria,
    val precio: Int,
    val ingredientes: List<String> = emptyList()
) {
    init {
        require(nombre.isNotBlank()) { "El nombre del producto no puede estar vacío." }
        require(precio > 0) { "El precio de '$nombre' debe ser mayor que cero." }
    }

    /** Descripción completa del producto (se arrastra de la semana 2). */
    fun detallarProducto(): String {
        val detalle = if (ingredientes.isEmpty()) "sin ingredientes detallados" else ingredientes.joinToString(", ")
        return "$nombre [${categoria.etiqueta}] - $precio CLP - $detalle"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Producto) return false
        return nombre.trim().equals(other.nombre.trim(), ignoreCase = true) &&
            categoria == other.categoria
    }

    // Regla de oro: si dos objetos son equals(), DEBEN tener el mismo hashCode().
    override fun hashCode(): Int =
        31 * nombre.trim().lowercase().hashCode() + categoria.hashCode()

    override fun toString(): String = "Producto(nombre=$nombre, categoria=$categoria, precio=$precio)"
}

/** Pizza gourmet: elegible para promociones (15% de descuento en periodo promocional). */
@Promocionable(descuento = 15, descripcion = "Pizza gourmet con descuento en periodo promocional")
class PizzaGourmet(
    nombre: String,
    precio: Int,
    ingredientes: List<String> = emptyList()
) : Producto(nombre, Categoria.PIZZA, precio, ingredientes)

/** Combo familiar: elegible para promociones (20% de descuento en periodo promocional). */
@Promocionable(descuento = 20, descripcion = "Combo familiar con descuento en periodo promocional")
class ComboFamiliar(
    nombre: String,
    precio: Int,
    ingredientes: List<String> = emptyList()
) : Producto(nombre, Categoria.PIZZA, precio, ingredientes)
