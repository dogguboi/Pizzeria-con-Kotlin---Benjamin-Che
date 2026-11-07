package pizzeria.servicios

/**
 * Registro genérico que NO permite duplicados.
 *
 * Internamente usa un Set, que decide si un elemento "ya existe" llamando a hashCode() y equals().
 * Por eso funciona tanto para clientes como para productos: la lógica de igualdad
 * vive en cada clase (Cliente y Producto), no aquí.
 */
class Registro<T : Any> {
    private val elementos = mutableSetOf<T>()

    /** Devuelve true si se registró; false si ya existía un elemento igual (duplicado). */
    fun registrar(elemento: T): Boolean = elementos.add(elemento)

    val todos: Set<T> get() = elementos

    val cantidad: Int get() = elementos.size
}
