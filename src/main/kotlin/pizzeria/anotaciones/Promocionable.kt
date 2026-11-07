package pizzeria.anotaciones

/**
 * Anotación personalizada que marca las clases de producto (pizzas o combos)
 * que califican para promociones especiales.
 *
 * - [descuento]: porcentaje de descuento que recibe el producto durante el periodo promocional.
 * - [descripcion]: texto libre que explica por qué es promocionable.
 *
 * RUNTIME es obligatorio: sin él la anotación no existiría al ejecutar el programa
 * y reflection no podría encontrarla.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Promocionable(
    val descuento: Int,
    val descripcion: String = ""
)
