package pizzeria.servicios

import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KType
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.valueParameters

/**
 * Uso de reflection: analiza una clase EN TIEMPO DE EJECUCIÓN y lista sus propiedades y métodos.
 * Requiere la librería kotlin-reflect.
 */
object Reflexion {

    // Quita los nombres de paquete ("kotlin.collections.Map" -> "Map") para una salida legible.
    private val PAQUETES = Regex("""\b(?:[a-z_][a-z0-9_]*\.)+""")

    private fun simplificar(tipo: KType): String = tipo.toString().replace(PAQUETES, "")

    fun listarMiembros(clase: KClass<*>) {
        println("Clase ${clase.simpleName}")

        println("  Propiedades:")
        clase.declaredMemberProperties.sortedBy { it.name }.forEach { propiedad ->
            val tipoDeclaracion = if (propiedad is KMutableProperty<*>) "var" else "val"
            println("    - $tipoDeclaracion ${propiedad.name}: ${simplificar(propiedad.returnType)}")
        }

        println("  Métodos:")
        clase.declaredMemberFunctions.sortedBy { it.name }.forEach { funcion ->
            val parametros = funcion.valueParameters.joinToString { "${it.name}: ${simplificar(it.type)}" }
            val operador = if (funcion.isOperator) "operator " else ""
            println("    - $operador${funcion.name}($parametros): ${simplificar(funcion.returnType)}")
        }
    }
}
