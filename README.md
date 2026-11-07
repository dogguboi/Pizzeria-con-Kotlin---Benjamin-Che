# Pizzería - Creando soluciones con Kotlin (Semana 3)

Aplicación de consola en Kotlin que continúa el caso de la pizzería (clientes, productos y pedidos)
e incorpora Regex, Ranges, anotaciones, reflection, sobrecarga de operadores, desestructuración
e igualdad de objetos.

## Estructura
```
src/main/kotlin/pizzeria/
├── Main.kt                      Programa principal: ejecuta y resume todo (Pasos 6 y 7)
├── anotaciones/Promocionable.kt Anotación personalizada @Promocionable (Paso 2)
├── modelo/
│   ├── Categoria.kt             Enum: PIZZA, BEBIDA, INGREDIENTE
│   ├── Producto.kt              Producto + PizzaGourmet y ComboFamiliar (@Promocionable); equals/hashCode (Pasos 3 y 5)
│   ├── Cliente.kt               data class: desestructuración (Paso 4) + equals/hashCode (Paso 5)
│   └── Pedido.kt                Operador + (Paso 3), desestructuración (Paso 4), descuentos
├── servicios/
│   ├── Validaciones.kt          Regex de correo y teléfono, validación de cantidad (Paso 1)
│   ├── Promociones.kt           Ranges de fechas y cantidades; lectura de @Promocionable (Pasos 1 y 2)
│   ├── Reflexion.kt             Lista propiedades y métodos de una clase con reflection (Paso 2)
│   └── Registro.kt              Registro sin duplicados (Paso 5)
└── util/Consola.kt              Lectura con reingreso, títulos y formato de pesos
```

## Reglas de negocio
- **Correo:** `nombre@dominio.ext`.
- **Teléfono:** 9 dígitos que empieza con 9 o 11 dígitos con el 56, y se formatea como `+56 9 XXXX XXXX`.
- **Cantidad:** de 1 a 100 unidades por producto.
- **Periodo promocional:** del 2026-11-01 al 2026-11-15 (ambos días incluidos).
- **Descuento por volumen:** 10% del subtotal si el pedido tiene más de 3 pizzas.
- **Descuento promocional:** dentro del periodo, los productos de clases marcadas con `@Promocionable`
  reciben el porcentaje que indica su anotación (PizzaGourmet 15%, ComboFamiliar 20%).
- **Igualdad:** Cliente = mismo nombre y correo; Producto = mismo nombre y categoría
  (sin distinguir mayúsculas).
- **Combinar pedidos (`+`):** solo del mismo cliente; las cantidades del mismo producto se suman
  y el total se recalcula con los descuentos.
