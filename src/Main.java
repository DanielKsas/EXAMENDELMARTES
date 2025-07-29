//Sistema de Inventario Completo con HashMap y ArrayList
//Requisitos cumplidos:
//1. Almacenar información completa de productos
//2. Base de datos simulada con HashMap y ArrayList
//3. Modificar precios de productos
//4. Eliminar productos de la bodega
//5. Verificación automática de productos vencidos (resta stock después de 3 días)

import java.util.ArrayList; //la lista
import java.util.HashMap; //y como el inventario
import java.util.Scanner;
import java.time.LocalDate; //obviamente calcula la hora de la ejecución del programa
import java.time.format.DateTimeFormatter; //por defecto cuando usamos una libreria de tiempo, este da un formato raro, y esta libreía lo que hace es darle un formato entendible
import java.time.format.DateTimeParseException; //Esta es para que el programa no tire erroes cuando se le da un valor incorrecto
import java.time.temporal.ChronoUnit; // Se usa principalmente para medir o sumar/restar tiempo entre fechas u horas

public class Main {
    // Scanner global para entrada de datos
    private static Scanner entrada = new Scanner(System.in);

    // HashMap: Almacena productos con código como clave única (base de datos principal)
    private static HashMap<String, Articulo> bodega = new HashMap<>();

    // ArrayList: Lista numerada para mostrar productos al usuario
    private static ArrayList<Articulo> inventario = new ArrayList<>();

    public static void main(String[] args) {
        // Sistema de gestión de inventario para supermercado
        // Funcionalidades: registrar, eliminar, mostrar, actualizar precios
        // Verificación automática de productos vencidos cada vez que se ejecuta el menú

        String seleccion;

        do {
            verificarProductosVencidos();

            mostrarMenuPrincipal();
            seleccion = entrada.nextLine();
            procesarOpcion(seleccion);
        } while (!seleccion.equals("6"));

        System.out.println("¡Gracias por usar el sistema de inventario!");
        entrada.close();
    }

    /**
     * Muestra el menú principal del sistema
     */
    private static void mostrarMenuPrincipal() {
        System.out.println("\n\u001B[=== Sistema de Inventario ===");
        System.out.println("Seleccione una opción:");
        System.out.println("  1. Registrar nuevo artículo en bodega ");
        System.out.println("  2. Guardar los productos del inventario en lista");
        System.out.println("  3. Mostrar los productos disponibles");
        System.out.println("  4. Eliminar artículo");
        System.out.println("  5. Actualizar precio");
        System.out.println("  6. Cerrar sistema");
        System.out.print("Opción: ");
    }

    /**
     * Procesa la opción seleccionada por el usuario
     */
    private static void procesarOpcion(String opcion) {
        switch (opcion) {
            case "1":
                registrarNuevoArticulo();
                break;
            case "2":
                pasarDelHashMapAlArrayList();
                break;
            case "3":
                mostrarInventario();
                break;
            case "4":
                eliminarArticulo();
                break;
            case "5":
                actualizarPrecio();
                break;
            case "6":
                break;
            default:
                System.out.println("⚠️ Opción no válida. Intente de nuevo.");
        }
    }

    /**
     * Registra un nuevo artículo en el HashMap (base de datos principal)
     * Incluye todas las variables requeridas: ID, nombre, precio, cantidad, etc.
     */
    private static void registrarNuevoArticulo() {
        System.out.println("\n--- Registro de Nuevo Artículo en bodega ---");

        // ID del producto (código único)
        System.out.print("Código del artículo (ID): ");
        String codigo = entrada.nextLine();

        // Verificar si ya existe el código (evitar duplicados)
        if (bodega.containsKey(codigo)) {
            System.out.println("❌ Ya existe un artículo con ese código");
            return;
        }

        // Nombre del producto
        System.out.print("Nombre del artículo: ");
        String nombreArticulo = entrada.nextLine();

        // Descripción del producto
        System.out.print("Descripción: ");
        String detalles = entrada.nextLine();

        // Categoría del producto
        System.out.print("Categoría: ");
        String tipoCategoria = entrada.nextLine();

        // Nombre del proveedor
        System.out.print("Proveedor: ");
        String empresaProveedora = entrada.nextLine();

        // Fotografía 1
        System.out.print("URL imagen principal (Fotografía 1): ");
        String imagenPrincipal = entrada.nextLine();

        // Fotografía 2
        System.out.print("URL imagen secundaria (Fotografía 2): ");
        String imagenSecundaria = entrada.nextLine();

        // Fecha de vencimiento
        LocalDate fechaCaducidad = solicitarFecha();

        // Precio del producto
        System.out.print("Precio por unidad: ");
        int valorUnitario = entrada.nextInt();

        // Cantidad en bodega
        System.out.print("Cantidad en bodega (stock): ");
        int stockActual = entrada.nextInt();
        entrada.nextLine(); // Limpiar buffer

        // Caducidad: por defecto todos los productos pueden vencerse
        boolean tieneVencimiento = true;

        // Crear nuevo artículo con todos los datos
        Articulo nuevoArticulo = new Articulo(codigo, nombreArticulo, detalles,
                tipoCategoria, empresaProveedora,
                imagenPrincipal, imagenSecundaria,
                fechaCaducidad, valorUnitario,
                stockActual, tieneVencimiento);

        // Guardar en el HashMap usando el código como clave única
        bodega.put(codigo, nuevoArticulo);

        System.out.println("✅ Artículo registrado en inventario exitosamente");
        System.out.println("📦 Total productos en Bodega: " + bodega.size());
    }

    /**
     * FUNCIÓN NUEVA: Transfiere todos los productos del HashMap al ArrayList
     * Esto permite trabajar con listas numeradas para mostrar al usuario
     */
    private static void pasarDelHashMapAlArrayList() {
        if (bodega.isEmpty()) {
            System.out.println("⚠️ No hay artículos en el inventario para pasar a la lista");
            return;
        }

        System.out.println("\n--- Pasando productos del inventario a la lista---");

        // Limpiar el ArrayList antes de llenarlo (evitar duplicados)
        inventario.clear();

        // Recorrer el HashMap y agregar cada producto al ArrayList
        // keySet() devuelve todos los códigos (claves) del HashMap
        for (String codigo : bodega.keySet()) {
            Articulo producto = bodega.get(codigo); // Obtener producto por código
            inventario.add(producto);               // Agregarlo al ArrayList
        }

        System.out.println("✅ Se pasaron " + inventario.size() + " productos a la lista");
        System.out.println("📋 el inventaruo tiene: " + bodega.size() + " productos");
        System.out.println("📝 La lista tiene: " + inventario.size() + " productos");
    }

    /**
     * FUNCIÓN NUEVA: Verifica productos vencidos y reduce stock automáticamente
     * Si han pasado 3 días desde el vencimiento, resta 1 unidad del stock
     * Esta función se ejecuta automáticamente cada vez que se muestra el menú
     */
    private static void verificarProductosVencidos() {
        // Solo verificar si hay productos en el ArrayList
        if (inventario.isEmpty()) {
            return;
        }

        LocalDate hoy = LocalDate.now(); // Fecha actual del sistema

        // Recorrer todos los productos del inventario
        for (Articulo articulo : inventario) {
            // Solo verificar productos que pueden vencerse
            if (articulo.tieneVencimiento) {
                // Calcular cuántos días han pasado desde la fecha de vencimiento
                long diasVencido = ChronoUnit.DAYS.between(articulo.fechaCaducidad, hoy);

                // Si han pasado 3 o más días Y hay stock disponible
                if (diasVencido >= 3 && articulo.stockActual > 0) {
                    // Restar 1 unidad del stock (producto vencido)
                    articulo.stockActual -= 1;

                    // Mostrar mensaje informativo
                    System.out.println("⚠️ Producto vencido eliminado: " + articulo.nombreArticulo +
                            " - Stock actual: " + articulo.stockActual);

                    // También actualizar en el HashMap para mantener sincronización
                    bodega.get(articulo.codigo).stockActual = articulo.stockActual;
                }
            }
        }
    }

    /**
     * Solicita al usuario que ingrese una fecha de vencimiento
     * retornar el LocalDate con la fecha ingresada
     */
    private static LocalDate solicitarFecha() {
        System.out.println("--- Fecha de Vencimiento ---");
        System.out.print("Año de vencimiento: ");
        int year = entrada.nextInt();
        System.out.print("Mes de vencimiento (1-12): ");
        int mesVencimiento = entrada.nextInt();
        System.out.print("Día de vencimiento: ");
        int diaVencimiento = entrada.nextInt();

        return LocalDate.of(year, mesVencimiento, diaVencimiento);
    }

    /**
     * Permite eliminar un artículo tanto del ArrayList como del HashMap
     * Cumple con el requisito: "permitir eliminar un producto de la bodega"
     */
    private static void eliminarArticulo() {
        if (inventario.isEmpty()) {
            System.out.println("⚠️ No hay artículos en el ArrayList");
            System.out.println("💡 Primero pase productos del HashMap al ArrayList (opción 2)");
            return;
        }

        System.out.println("\n--- Eliminar Artículo ---");
        listarArticulosSimple();

        System.out.print("Número del artículo a eliminar: ");
        int indiceEliminar = entrada.nextInt();
        entrada.nextLine();

        // Verificar que el índice sea válido
        if (indiceEliminar > 0 && indiceEliminar <= inventario.size()) {
            Articulo articuloEliminado = inventario.get(indiceEliminar - 1);

            // Eliminar del ArrayList (lista numerada)
            inventario.remove(indiceEliminar - 1);

            // También eliminar del HashMap (base de datos principal)
            bodega.remove(articuloEliminado.codigo);

            System.out.println("✅ Artículo eliminado de ambas estructuras");
            System.out.println("🗑️ Producto eliminado: " + articuloEliminado.nombreArticulo);
        } else {
            System.out.println("❌ Número de artículo inválido");
        }
    }

    /**
     * Muestra todos los productos del ArrayList (inventario numerado)
     */
    private static void mostrarInventario() {
        if (inventario.isEmpty()) {
            System.out.println("⚠️ El ArrayList está vacío");
            System.out.println("💡 Primero pase productos del HashMap al ArrayList (opción 2)");
            return;
        }

        System.out.println("\n--- Inventario del ArrayList ---");
        int contador = 1;
        for (Articulo articulo : inventario) {
            System.out.println("ARTÍCULO #" + contador);
            System.out.println(articulo);
            contador++;
        }
    }

    /**
     * Permite actualizar el precio de cualquier producto
     * Cumple con el requisito: "permitir modificar el precio unitario"
     */
    private static void actualizarPrecio() {
        if (inventario.isEmpty()) {
            System.out.println("⚠️ No hay artículos para actualizar en el ArrayList");
            System.out.println("💡 Primero pase productos del HashMap al ArrayList (opción 2)");
            return;
        }

        System.out.println("\n--- Actualizar Precio ---");
        listarArticulosConPrecio();

        System.out.print("Número del artículo: ");
        int indiceActualizar = entrada.nextInt();

        if (indiceActualizar > 0 && indiceActualizar <= inventario.size()) {
            System.out.print("Nuevo precio: ");
            int precioNuevo = entrada.nextInt();

            Articulo articuloActualizar = inventario.get(indiceActualizar - 1);

            // Actualizar precio en el ArrayList
            articuloActualizar.valorUnitario = precioNuevo;

            // También actualizar en el HashMap para mantener sincronización
            bodega.get(articuloActualizar.codigo).valorUnitario = precioNuevo;

            System.out.println("✅ Precio actualizado en ambas estructuras");
            System.out.println("💰 Nuevo precio: $" + precioNuevo);
        } else {
            System.out.println("❌ Número de artículo inválido");
        }
    }

    /**
     * Muestra una lista simple de artículos (solo nombre y código)
     */
    private static void listarArticulosSimple() {
        int index = 1;
        for (Articulo a : inventario) {
            System.out.println("Artículo " + index + ": " + a.nombreArticulo + " (Código: " + a.codigo + ")");
            index++;
        }
    }

    /**
     * Muestra una lista de artículos con sus precios
     */
    private static void listarArticulosConPrecio() {
        int index = 1;
        for (Articulo a : inventario) {
            System.out.println("Artículo " + index + ": " + a.nombreArticulo +
                    " - Precio: $" + a.valorUnitario + " (Código: " + a.codigo + ")");
            index++;
        }
    }
}

/**
 * Clase para validar datos (funcionalidad adicional)
 */
class ValidadorDatos {
    /**
     * Solicita una fecha con formato específico
     * @param scanner Scanner para entrada de datos
     * @return LocalDate con la fecha válida
     */
    public static LocalDate solicitarFechaConFormato(Scanner scanner) {
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        while (true) {
            System.out.print("📅 Ingrese fecha (YYYY-MM-DD): ");
            String fechaTexto = scanner.nextLine();

            try {
                return LocalDate.parse(fechaTexto, formatoFecha);
            } catch (DateTimeParseException error) {
                System.out.println("❌ Formato incorrecto. Use YYYY-MM-DD");
            }
        }
    }
}

/**
 * Clase Articulo: Contiene toda la información requerida de un producto
 * Cumple con TODAS las variables solicitadas en los requisitos
 */
class Articulo {
    // Variables requeridas por los requisitos:
    String codigo;              // ID del producto
    String nombreArticulo;      // Nombre del producto
    String detalles;           // Descripción
    String tipoCategoria;      // Categoría
    String empresaProveedora;  // Nombre del proveedor
    String imagenPrincipal;    // Fotografía 1
    String imagenSecundaria;   // Fotografía 2
    LocalDate fechaCaducidad;  // Fecha de vencimiento
    int valorUnitario;         // Precio
    int stockActual;           // Cantidad en bodega
    boolean tieneVencimiento;  // Caducidad (si el producto puede vencerse)

    /**
     * Constructor que inicializa todas las variables del producto
     */
    public Articulo(String codigo, String nombreArticulo, String detalles,
                    String tipoCategoria, String empresaProveedora,
                    String imagenPrincipal, String imagenSecundaria,
                    LocalDate fechaCaducidad, int valorUnitario,
                    int stockActual, boolean tieneVencimiento) {
        this.codigo = codigo;
        this.nombreArticulo = nombreArticulo;
        this.detalles = detalles;
        this.tipoCategoria = tipoCategoria;
        this.empresaProveedora = empresaProveedora;
        this.imagenPrincipal = imagenPrincipal;
        this.imagenSecundaria = imagenSecundaria;
        this.fechaCaducidad = fechaCaducidad;
        this.valorUnitario = valorUnitario;
        this.stockActual = stockActual;
        this.tieneVencimiento = tieneVencimiento;
    }

    /**
     * Método toString para mostrar toda la información del producto de forma organizada
     */
    @Override
    public String toString() {
        return "🛍️ Artículo: " + nombreArticulo +
                "\n🏷️ Código (ID): " + codigo +
                "\n📋 Descripción: " + detalles +
                "\n🗂️ Categoría: " + tipoCategoria +
                "\n🏪 Proveedor: " + empresaProveedora +
                "\n🖼️ Fotografía 1: " + imagenPrincipal +
                "\n🖼️ Fotografía 2: " + imagenSecundaria +
                "\n📆 Vence: " + fechaCaducidad +
                "\n💰 Precio: $" + valorUnitario +
                "\n📊 Cantidad en bodega: " + stockActual + " unidades" +
                "\n⏰ Tiene vencimiento: " + (tieneVencimiento ? "✅ Sí" : "❌ No") +
                "\n=============================================";
    }
}