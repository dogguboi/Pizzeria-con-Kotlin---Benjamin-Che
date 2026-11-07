package pizzeria.modelo

/**
 * Cliente de la pizzería.
 *
 * Al ser una `data class`, Kotlin genera automáticamente component1(), component2() y component3()
 * (en el orden del constructor), lo que permite la desestructuración:
 * `val (nombre, correo, telefono) = cliente`.
 *
 * Dos clientes son iguales si coinciden su [nombre] y su [correo] (sin distinguir mayúsculas).
 * El teléfono puede cambiar sin que el cliente deje de ser el mismo.
 * Se sobrescriben equals() y hashCode() a propósito: así reemplazamos la versión
 * que generaría la data class (que compararía los 3 campos).
 */
data class Cliente(
    val nombre: String,
    val correo: String,
    val telefono: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Cliente) return false
        return nombre.trim().equals(other.nombre.trim(), ignoreCase = true) &&
            correo.trim().equals(other.correo.trim(), ignoreCase = true)
    }

    override fun hashCode(): Int =
        31 * nombre.trim().lowercase().hashCode() + correo.trim().lowercase().hashCode()
}
