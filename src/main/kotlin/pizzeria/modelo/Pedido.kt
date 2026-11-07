package pizzeria.modelo

import pizzeria.servicios.Promociones
import java.time.LocalDate

/** Desglose de los descuentos aplicados a un pedido (valores en pesos). */
data class Descuentos(val volumen: Int, val promocion: Int) {
    val total: Int get() = volumen + promocion
}

/**
 * Pedido de un [cliente] con sus [productos] (producto -> cantidad) en una [fecha].
 *
 * Como las claves del mapa son [Producto], Kotlin usa equals()/hashCode() de Producto para
 * saber si dos productos "son el mismo": por eso al combinar pedidos las cantidades de un
 * mismo producto se suman en lugar de duplicarse.
 */
class Pedido(
    val id: String,
    val cliente: Cliente,
    val productos: Map<Producto, Int>,
    val fecha: LocalDate
) {
    init {
        require(productos.isNotEmpty()) { "El pedido $id debe tener al menos un producto." }
        productos.forEach { (producto, cantidad) ->
            require(cantidad in Promociones.RANGO_CANTIDAD) {
                "Cantidad inválida para '${producto.nombre}' en el pedido $id: $cantidad " +
                    "(permitido: ${Promociones.RANGO_CANTIDAD})."
            }
        }
    }

    /** Cantidad total de pizzas (los combos cuentan como pizza). */
    val cantidadPizzas: Int
        get() = productos.filter { (producto, _) -> producto.categoria == Categoria.PIZZA }.values.sum()

    /** Suma de precio x cantidad, sin descuentos. */
    val subtotal: Int
        get() = productos.entries.sumOf { (producto, cantidad) -> producto.precio * cantidad }

    /**
     * Descuentos del pedido:
     *  - volumen: 10% del subtotal si hay más de 3 pizzas.
     *  - promoción: si la fecha cae dentro del periodo promocional, cada producto marcado con
     *    @Promocionable recibe el porcentaje indicado en su anotación.
     */
    val descuentos: Descuentos
        get() {
            val volumen = if (cantidadPizzas > Promociones.PIZZAS_PARA_DESCUENTO) {
                subtotal * Promociones.DESCUENTO_VOLUMEN / 100
            } else 0

            val promocion = if (Promociones.fechaEnPromocion(fecha)) {
                productos.entries.sumOf { (producto, cantidad) ->
                    if (Promociones.esPromocionable(producto)) {
                        producto.precio * cantidad * Promociones.descuentoDe(producto) / 100
                    } else 0
                }
            } else 0

            return Descuentos(volumen, promocion)
        }

    /** Total final = subtotal - descuentos. */
    val total: Int
        get() = subtotal - descuentos.total

    /**
     * Sobrecarga del operador `+`: `pedidoA + pedidoB` crea un pedido NUEVO que une ambos.
     * Reglas: mismo cliente; cantidades del mismo producto se suman; la fecha es la más reciente.
     * El total se recalcula solo (es una propiedad calculada), incluyendo descuentos.
     */
    operator fun plus(otro: Pedido): Pedido {
        require(cliente == otro.cliente) {
            "Solo se pueden combinar pedidos del mismo cliente " +
                "(${cliente.nombre} vs ${otro.cliente.nombre})."
        }
        val combinados = productos.toMutableMap()
        otro.productos.forEach { (producto, cantidad) ->
            combinados.merge(producto, cantidad, Int::plus)
        }
        return Pedido("$id+${otro.id}", cliente, combinados, maxOf(fecha, otro.fecha))
    }

    // Desestructuración: val (cliente, productos, total) = pedido
    operator fun component1(): Cliente = cliente
    operator fun component2(): Map<Producto, Int> = productos
    operator fun component3(): Int = total
}
