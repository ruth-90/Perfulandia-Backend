
package cl.duoc.perfulandia.producto.config;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import cl.duoc.perfulandia.producto.model.Producto;
import cl.duoc.perfulandia.producto.repository.ProductoRepository;


// Configuración para cargar los perfumes iniciales de Perfulandia.
@Configuration
@Profile("dev")
public class DatosInicialesConfig {


    // Se ejecuta automáticamente cuando inicia Spring Boot.
    @Bean
    CommandLineRunner cargarProductosIniciales(
            ProductoRepository productoRepository,
            @Value("${app.seed.enabled:true}") boolean habilitado) {

        return args -> {

            // Permite desactivar la carga inicial desde las propiedades.
            if (!habilitado) {
                return;
            }

            // Lista de los 12 perfumes originales del frontend.
            List<Producto> productosIniciales = List.of(

                crear(
                    "ASAD de Lattafa EDP 100 ML",
                    "Hombre",
                    39990,
                    12,
                    "/img/Perfume1.webp",
                    "Fragancia intensa, elegante y especiada, ideal para ocasiones especiales."
                ),

                crear(
                    "Club de Nuit Woman EDP 105 ML Mujer de Armaf",
                    "Mujer",
                    44990,
                    8,
                    "/img/Perfume3.webp",
                    "Fragancia femenina con notas florales, frutales y sofisticadas."
                ),

                crear(
                    "Armaf Club de Nuit Intense EDT 105 ML",
                    "Hombre",
                    49990,
                    10,
                    "/img/Perfume2.webp",
                    "Una fragancia intensa con notas cítricas, amaderadas y elegantes."
                ),

                crear(
                    "Azzaro The Most Wanted Intense EDT 100 ML",
                    "Hombre",
                    64990,
                    6,
                    "/img/Perfume11.webp",
                    "Fragancia masculina intensa, cálida y sofisticada."
                ),

                crear(
                    "Ô OUI EDT 100 ML Lancôme",
                    "Mujer",
                    35990,
                    15,
                    "/img/Perfume4.webp",
                    "Fragancia fresca y femenina, perfecta para el uso diario."
                ),

                crear(
                    "Paco Rabanne Olympea",
                    "Mujer",
                    39990,
                    9,
                    "/img/Perfume5.webp",
                    "Fragancia femenina elegante, intensa y sofisticada."
                ),

                crear(
                    "Liquid Brun French Avenue",
                    "Unisex",
                    42990,
                    11,
                    "/img/Perfume6.webp",
                    "Fragancia cálida y envolvente con un aroma moderno y sofisticado."
                ),

                crear(
                    "Yara Tous EDP 100 ML Mujer",
                    "Mujer",
                    45990,
                    7,
                    "/img/Perfume7.webp",
                    "Fragancia femenina dulce, tropical y elegante."
                ),

                crear(
                    "Power Of You EDP 50 ML",
                    "Mujer",
                    49990,
                    5,
                    "/img/Perfume8.webp",
                    "Fragancia moderna y femenina con un aroma intenso y atractivo."
                ),

                crear(
                    "Sapphire Gemstone EDP",
                    "Unisex",
                    54990,
                    4,
                    "/img/Perfume9.webp",
                    "Fragancia elegante con notas sofisticadas y una esencia duradera."
                ),

                crear(
                    "Taj Mahal Wow EDP 100 ML",
                    "Unisex",
                    59990,
                    3,
                    "/img/Perfume10.webp",
                    "Fragancia intensa y sofisticada con un aroma elegante y distintivo."
                ),

                crear(
                    "Armaf Odyssey Mandarin Sky Limited Edit. EDP 100 ML",
                    "Unisex",
                    59990,
                    0,
                    "/img/Perfume12.webp",
                    "Fragancia intensa y sofisticada con un aroma elegante y distintivo."
                )
            );


            // Recorremos los perfumes iniciales.
            for (Producto producto : productosIniciales) {

                // Verificamos si ya existe un perfume con ese nombre.
                boolean existe = productoRepository.existsByNombre(
                        producto.getNombre()
                );

                // Solo guardamos los que todavía no existen.
                if (!existe) {
                    productoRepository.save(producto);
                }
            }
        };
    }


    // Método auxiliar para construir un perfume.
    private Producto crear(
            String nombre,
            String categoria,
            int precio,
            int stock,
            String imagen,
            String descripcion) {

        Producto producto = new Producto();

        producto.setNombre(nombre);
        producto.setCategoria(categoria);
        producto.setPrecio(BigDecimal.valueOf(precio));
        producto.setStock(stock);
        producto.setImagen(imagen);
        producto.setDescripcion(descripcion);

        return producto;
    }
}
