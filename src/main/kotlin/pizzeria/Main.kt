package pizzeria

import pizzeria.modelo.Categoria
import pizzeria.modelo.Cliente
import pizzeria.modelo.ComboFamiliar
import pizzeria.modelo.Pedido
import pizzeria.modelo.PizzaGourmet
import pizzeria.modelo.Producto
import pizzeria.servicios.Promociones
import pizzeria.servicios.Reflexion
import pizzeria.servicios.Registro
import pizzeria.servicios.Validaciones
import pizzeria.util.clp
import pizzeria.util.pedirDato
import pizzeria.util.subtitulo
import pizzeria.util.titulo
import java.time.LocalDate

/** Formatea un teléfono de demostración; si estuviera mal escrito, el programa avisa de inmediato. */
private fun telefonoDemo(texto: String): String =
    Validaciones.formatearTelefono(texto) ?: error("Teléfono de demostración inválido: $texto")

/** Imprime un pedido con su desglose de descuentos. */
private fun imprimirPedido(pedido: Pedido) {
    val d = pedido.descuentos
    println("Pedido ${pedido.id} | Cliente: ${pedido.cliente.nombre} | Fecha: ${pedido.fecha}")
    pedido.productos.forEach { (producto, cantidad) ->
        println("  - ${producto.nombre} (${producto.categoria.etiqueta}) x $cantidad = ${(producto.precio * cantidad).clp()}")
    }
    println("  ${"Subtotal".padEnd(40)} ${pedido.subtotal.clp()}")
    println("  ${"Descuento por volumen (>${Promociones.PIZZAS_PARA_DESCUENTO} pizzas)".padEnd(40)} -${d.volumen.clp()}")
    println("  ${"Descuento promocional (@Promocionable)".padEnd(40)} -${d.promocion.clp()}")
    println("  ${"TOTAL".padEnd(40)} ${pedido.total.clp()}")
}

/** Interpreta una respuesta s/n; devuelve "s", "n" o null si no se entiende. */
private fun respuestaSiNo(texto: String): String? =
    when (texto.lowercase()) {
        "s", "si", "sí" -> "s"
        "n", "no" -> "n"
        else -> null
    }

/**
 * Muestra el menú y deja elegir productos y cantidades (se puede repetir hasta que el cliente termine).
 * Devuelve producto -> cantidad. Si un producto se repite, las cantidades se suman (máximo 100).
 */
private fun tomarProductos(menu: List<Producto>): Map<Producto, Int> {
    println()
    println("Menú de productos:")
    menu.forEachIndexed { indice, producto ->
        println("  ${indice + 1}) ${producto.nombre.padEnd(20)} ${producto.precio.clp().padStart(8)}  [${producto.categoria.etiqueta}]")
    }

    val seleccion = linkedMapOf<Producto, Int>()
    do {
        val opcion = pedirDato(
            "Número de producto (1-${menu.size}): ", "Opción inválida. Ingrese un número entre 1 y ${menu.size}.", 1
        ) { texto -> texto.toIntOrNull()?.takeIf { it in 1..menu.size } }
        val producto = menu[opcion - 1]

        val cantidad = pedirDato(
            "Cantidad de '${producto.nombre}' (1-100): ", "Cantidad inválida. Ingrese un entero entre 1 y 100.", 2
        ) { Validaciones.parsearCantidad(it) }

        val acumulado = (seleccion[producto] ?: 0) + cantidad
        if (Promociones.cantidadValida(acumulado)) {
            seleccion[producto] = acumulado
        } else {
            println("  [ERROR] Con esa cantidad '${producto.nombre}' superaría el máximo de ${Promociones.RANGO_CANTIDAD.last} unidades. No se agregó.")
        }

        val otroProducto = pedirDato("¿Agregar otro producto? (s/n): ", "Responda s o n.", "n", ::respuestaSiNo)
    } while (otroProducto == "s")

    return seleccion
}

fun main() {
    val registroProductos = Registro<Producto>()
    val registroClientes = Registro<Cliente>()

    // ------------------------------------------------------------------
    titulo("PASO 5 (parte 1): CATALOGO SIN DUPLICADOS")
    // ------------------------------------------------------------------
    val cuatroQuesos = PizzaGourmet("Cuatro Quesos", 12990, listOf("mozzarella", "gorgonzola", "parmesano", "provolone"))
    val pepperoni = PizzaGourmet("Pepperoni Especial", 11990, listOf("mozzarella", "pepperoni"))
    val margarita = Producto("Pizza Margarita", Categoria.PIZZA, 8990, listOf("mozzarella", "tomate", "albahaca"))
    val comboFamiliar = ComboFamiliar("Combo Familiar", 24990, listOf("2 pizzas medianas", "bebida 1.5 L"))
    val bebida = Producto("Bebida 1.5 L", Categoria.BEBIDA, 2500)
    val quesoExtra = Producto("Queso Extra", Categoria.INGREDIENTE, 1500)
    val margaritaRepetida = Producto("pizza margarita", Categoria.PIZZA, 9500) // mismo nombre y categoría

    listOf(cuatroQuesos, pepperoni, margarita, comboFamiliar, bebida, quesoExtra, margaritaRepetida).forEach { producto ->
        if (registroProductos.registrar(producto)) {
            println("  [OK]        Registrado: ${producto.detallarProducto()}")
        } else {
            println("  [DUPLICADO] No se registra '${producto.nombre}': ya existe un producto igual (mismo nombre y categoría).")
        }
    }
    println("Productos en el catálogo: ${registroProductos.cantidad}")

    // ------------------------------------------------------------------
    titulo("PASO 1: REGISTRO INTERACTIVO (reingreso si el dato es inválido)")
    // ------------------------------------------------------------------
    println("Ingrese los datos del nuevo cliente. Si un dato es inválido, se vuelve a pedir.")
    val nombreIngresado = pedirDato("Nombre: ", "El nombre no puede estar vacío.", "Cliente Consola") { it.ifBlank { null } }
    val correoIngresado = pedirDato(
        "Correo (nombre@dominio.com): ", "Formato de correo inválido. Ejemplo: nombre@dominio.com.", "cliente@pizzeria.com"
    ) { if (Validaciones.esCorreoValido(it)) it else null }
    val telefonoIngresado = pedirDato(
        "Teléfono móvil (ej: 9 1234 5678): ", "Teléfono inválido. Use un móvil chileno: 9 dígitos que empiecen con 9, por ejemplo 9 1234 5678.", telefonoDemo("9 1200 0123")
    ) { Validaciones.formatearTelefono(it) }
    val clienteConsola = Cliente(nombreIngresado, correoIngresado, telefonoIngresado)
    registroClientes.registrar(clienteConsola)

    val menu = registroProductos.todos.toList()
    val fechaPedido = LocalDate.now()
    val pedidoConsola = Pedido("P-CONSOLA", clienteConsola, tomarProductos(menu), fechaPedido)

    println()
    println("Cliente registrado: $clienteConsola")
    imprimirPedido(pedidoConsola)

    // Opción de pedido combinado: un pedido aparte que se une al anterior con el operador +
    var pedidoActual = pedidoConsola
    var numeroExtra = 2
    while (pedirDato(
            "\n¿Desea agregar más cosas en un pedido aparte para combinarlo con el anterior? (s/n): ",
            "Responda s o n.", "n", ::respuestaSiNo
        ) == "s"
    ) {
        val pedidoAparte = Pedido("P-CONSOLA-$numeroExtra", clienteConsola, tomarProductos(menu), fechaPedido)
        println()
        imprimirPedido(pedidoAparte)
        try {
            pedidoActual = pedidoActual + pedidoAparte
            println()
            println(">>> Pedido combinado (${pedidoActual.id}): se suman los productos y se recalculan los descuentos")
            imprimirPedido(pedidoActual)
        } catch (e: IllegalArgumentException) {
            println("  [ERROR] No se pudo combinar los pedidos: ${e.message}")
        }
        numeroExtra++
    }

    // ------------------------------------------------------------------
    titulo("PASO 5 (parte 2): CLIENTES SIN DUPLICADOS")
    // ------------------------------------------------------------------
    val ana = Cliente("Ana Pérez", "ana.perez@correo.com", telefonoDemo("+56 9 1234 5678"))
    val luis = Cliente("Luis Gómez", "luis_g@dominio.cl", telefonoDemo("9 8765 4321"))
    // Misma persona escrita distinto: otro uso de mayúsculas y otro teléfono
    val anaRepetida = Cliente("ana pérez", "ANA.PEREZ@correo.com", telefonoDemo("9 9988 7766"))

    listOf(ana, luis, anaRepetida).forEach { cliente ->
        if (registroClientes.registrar(cliente)) {
            println("  [OK]        Cliente registrado: ${cliente.nombre} <${cliente.correo}>")
        } else {
            println("  [DUPLICADO] No se registra a ${cliente.nombre} <${cliente.correo}>: ya existe un cliente igual (mismo nombre y correo).")
        }
    }
    println("Clientes registrados: ${registroClientes.cantidad}")

    // ------------------------------------------------------------------
    titulo("PASO 3: OPERADOR + (pedidos combinados de demostración)")
    // ------------------------------------------------------------------
    // Se usa OTRA instancia de "Cuatro quesos" (distinto objeto y distinto uso de mayúsculas):
    // como Producto.equals() los considera iguales, las cantidades se suman en una sola línea.
    val cuatroQuesosOtraInstancia = PizzaGourmet("Cuatro quesos", 12990)

    val pedido1 = Pedido("P-001", ana, mapOf(cuatroQuesos to 2, margarita to 1), LocalDate.of(2026, 11, 3))
    val pedido2 = Pedido(
        "P-002", ana,
        mapOf(cuatroQuesosOtraInstancia to 1, comboFamiliar to 1, bebida to 2),
        LocalDate.of(2026, 11, 5)
    )
    val pedidoCombinado = pedido1 + pedido2
    println("${pedido1.id} (${pedido1.total.clp()}) + ${pedido2.id} (${pedido2.total.clp()}) = ${pedidoCombinado.id} (${pedidoCombinado.total.clp()})")
    println("'Cuatro Quesos' estaba en ambos pedidos y se sumó en una sola línea: x ${pedidoCombinado.productos[cuatroQuesos]}")

    // ------------------------------------------------------------------
    titulo("PASO 4: DESESTRUCTURACIÓN (con tus datos)")
    // ------------------------------------------------------------------
    val (nombreCliente, correoCliente, telefonoCliente) = clienteConsola
    println("Cliente desestructurado -> nombre: $nombreCliente | correo: $correoCliente | teléfono: $telefonoCliente")

    val (clientePedido, productosPedido, totalPedido) = pedidoActual
    println("Pedido desestructurado  -> cliente: ${clientePedido.nombre} | total: ${totalPedido.clp()}")
    println("Productos del pedido:")
    productosPedido.forEach { (producto, cantidad) -> println("  - ${producto.nombre} x $cantidad") }

    // Identificación dinámica de productos promocionables (se muestra en el resumen final)
    val promocionables = Promociones.obtenerPromocionables(registroProductos.todos)

    // ------------------------------------------------------------------
    titulo("PASO 2: REFLECTION (propiedades y métodos de las clases)")
    // ------------------------------------------------------------------
    Reflexion.listarMiembros(Pedido::class)
    println()
    Reflexion.listarMiembros(Producto::class)
    println()
    Reflexion.listarMiembros(Cliente::class)

    // ------------------------------------------------------------------
    titulo("PASO 5: EVALUACIONES DE IGUALDAD")
    // ------------------------------------------------------------------
    val clientesIguales = ana == anaRepetida
    val clientesMismaReferencia = ana === anaRepetida
    val hashClientesIguales = ana.hashCode() == anaRepetida.hashCode()
    val clientesDistintos = ana == luis

    val productosIguales = margarita == margaritaRepetida
    val hashProductosIguales = margarita.hashCode() == margaritaRepetida.hashCode()
    val otraCategoria = Producto("Pizza Margarita", Categoria.BEBIDA, 1000)
    val productosDistintaCategoria = margarita == otraCategoria

    println("Clientes")
    println("  ana == anaRepetida (mismo nombre y correo)      : $clientesIguales")
    println("  ana === anaRepetida (misma referencia en memoria): $clientesMismaReferencia")
    println("  hashCode iguales                                 : $hashClientesIguales")
    println("  ana == luis                                      : $clientesDistintos")
    println("Productos")
    println("  Margarita == margarita (mismo nombre y categoría): $productosIguales")
    println("  hashCode iguales                                 : $hashProductosIguales")
    println("  Margarita (PIZZA) == Margarita (BEBIDA)          : $productosDistintaCategoria")

    // ------------------------------------------------------------------
    titulo("PASO 6: RESUMEN FINAL INTEGRADO")
    // ------------------------------------------------------------------
    println("1) Pedidos combinados con el operador +")
    println("   ${pedido1.id} (${pedido1.total.clp()}) + ${pedido2.id} (${pedido2.total.clp()}) = ${pedidoCombinado.id} (${pedidoCombinado.total.clp()})")
    println("   Pizzas del pedido combinado: ${pedidoCombinado.cantidadPizzas} -> descuento por volumen: ${pedidoCombinado.descuentos.volumen.clp()}")
    println("   Descuento promocional aplicado: ${pedidoCombinado.descuentos.promocion.clp()}")
    if (pedidoActual !== pedidoConsola) {
        println("   Tu pedido combinado: ${pedidoActual.id} (${pedidoActual.total.clp()})")
    }
    println()
    println("2) Cliente desestructurado")
    println("   Nombre: $nombreCliente | Correo: $correoCliente | Teléfono: $telefonoCliente")
    println()
    println("3) Productos promocionables detectados con @Promocionable")
    promocionables.forEach { println("   - ${it.nombre} (${Promociones.descuentoDe(it)}% en periodo promocional)") }
    println()
    // Se intenta registrar de nuevo los duplicados: el Set los rechaza gracias a equals()/hashCode().
    val clienteDuplicadoRechazado = clientesIguales && !registroClientes.registrar(anaRepetida)
    val productoDuplicadoRechazado = productosIguales && !registroProductos.registrar(margaritaRepetida)
    println("4) Validación de igualdad de objetos")
    println("   Clientes duplicados rechazados : ${if (clienteDuplicadoRechazado) "sí" else "no"} (registrados: ${registroClientes.cantidad})")
    println("   Productos duplicados rechazados: ${if (productoDuplicadoRechazado) "sí" else "no"} (registrados: ${registroProductos.cantidad})")
    println()
    println("Gracias por preferir nuestra pizzería.")
}
