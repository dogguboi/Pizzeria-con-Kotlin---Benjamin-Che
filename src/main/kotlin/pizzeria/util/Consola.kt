package pizzeria.util

import java.util.Locale

private val LOCALE_CL: Locale = Locale.forLanguageTag("es-CL")

fun titulo(texto: String) {
    println()
    println("=".repeat(72))
    println("  $texto")
    println("=".repeat(72))
}

fun subtitulo(texto: String) {
    println()
    println("--- $texto ---")
}

/** Formatea un entero como pesos chilenos: 12990 -> $12.990 */
fun Int.clp(): String = "\$" + String.format(LOCALE_CL, "%,d", this)

/**
 * Pide un dato por consola y lo repite hasta que sea válido.
 *
 * - [convertir] devuelve el valor ya convertido, o null si el texto es inválido.
 * - Si no hay entrada disponible (por ejemplo, el programa se ejecuta sin teclado),
 *   usa [valorDemo] para que el programa no se quede bloqueado ni falle.
 */
fun <T : Any> pedirDato(
    mensaje: String,
    mensajeError: String,
    valorDemo: T,
    convertir: (String) -> T?
): T {
    while (true) {
        print(mensaje)
        val linea = readlnOrNull()
        if (linea == null) {
            println()
            println("  [Sin entrada por consola: se usa el valor de demostración \"$valorDemo\"]")
            return valorDemo
        }
        val valor = convertir(linea.trim())
        if (valor != null) return valor
        println("  [ERROR] $mensajeError")
    }
}
