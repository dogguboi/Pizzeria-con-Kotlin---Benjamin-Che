package pizzeria.servicios

import pizzeria.anotaciones.Promocionable
import pizzeria.modelo.Producto
import java.time.LocalDate
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

/**
 * Reglas de negocio de promociones y rangos permitidos.
 * Aquí viven los Ranges y la lectura de la anotación @Promocionable mediante reflection.
 */
object Promociones {

    /** Rango permitido de unidades por producto (Range de enteros: 1..100, ambos incluidos). */
    val RANGO_CANTIDAD: IntRange = 1..100

    /** Periodo promocional (Range de fechas, ambos extremos incluidos). */
    val PERIODO_PROMOCION = LocalDate.of(2026, 11, 1)..LocalDate.of(2026, 11, 15)

    /** Se aplica descuento por volumen cuando hay MÁS de esta cantidad de pizzas. */
    const val PIZZAS_PARA_DESCUENTO = 3

    /** Porcentaje de descuento por volumen. */
    const val DESCUENTO_VOLUMEN = 10

    fun fechaEnPromocion(fecha: LocalDate): Boolean = fecha in PERIODO_PROMOCION

    fun cantidadValida(cantidad: Int): Boolean = cantidad in RANGO_CANTIDAD

    /** Reflection: ¿la clase del producto está marcada con @Promocionable? */
    fun esPromocionable(producto: Producto): Boolean = producto::class.hasAnnotation<Promocionable>()

    /** Reflection: obtiene la anotación (o null si el producto no es promocionable). */
    fun anotacionDe(producto: Producto): Promocionable? = producto::class.findAnnotation<Promocionable>()

    /** Porcentaje de descuento que declara la anotación (0 si no es promocionable). */
    fun descuentoDe(producto: Producto): Int = anotacionDe(producto)?.descuento ?: 0

    /** Filtra dinámicamente los productos elegibles para promociones. */
    fun obtenerPromocionables(productos: Collection<Producto>): List<Producto> =
        productos.filter { esPromocionable(it) }
}
