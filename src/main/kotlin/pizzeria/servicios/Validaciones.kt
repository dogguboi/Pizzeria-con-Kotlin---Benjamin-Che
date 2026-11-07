package pizzeria.servicios

/**
 * Validaciones de datos de entrada con expresiones regulares (Regex) y rangos (Ranges).
 */
object Validaciones {

    // nombre@dominio.ext  (letras, números, . _ + - antes de la @; dominio con al menos un punto)
    private val REGEX_CORREO =
        Regex("""^[\w.+-]+@[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*\.[A-Za-z]{2,}$""")

    // Caracteres permitidos en un teléfono escrito por el usuario: dígitos, espacios, + ( ) . -
    private val REGEX_CARACTERES_TELEFONO = Regex("""^\+?[\d\s().-]+$""")

    /** `matches` exige que TODO el texto cumpla el patrón (no solo una parte). */
    fun esCorreoValido(correo: String): Boolean = REGEX_CORREO.matches(correo.trim())

    /**
     * Formatea un teléfono móvil chileno al estilo uniforme +56 9 XXXX XXXX.
     * Acepta 9 dígitos (9XXXXXXXX, se asume [codigoPais]) u 11 dígitos (56 + 9 dígitos).
     * El número móvil debe empezar con 9. Devuelve null si el texto no es válido.
     */
    fun formatearTelefono(entrada: String, codigoPais: String = "56"): String? {
        val texto = entrada.trim()
        if (!REGEX_CARACTERES_TELEFONO.matches(texto)) return null

        val digitos = texto.filter { it.isDigit() }
        val nacional = when (digitos.length) {
            9 -> digitos
            11 -> if (digitos.startsWith(codigoPais)) digitos.removePrefix(codigoPais) else return null
            else -> return null
        }
        if (!nacional.startsWith("9")) return null

        return "+$codigoPais ${nacional[0]} ${nacional.substring(1, 5)} ${nacional.substring(5)}"
    }

    /** Convierte el texto a Int y comprueba que esté dentro del rango 1..100. */
    fun parsearCantidad(texto: String): Int? =
        texto.trim().toIntOrNull()?.takeIf { Promociones.cantidadValida(it) }
}
