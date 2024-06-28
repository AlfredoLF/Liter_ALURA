package com.alura.literalura.principal;

import com.alura.literalura.model.*;
import com.alura.literalura.repository.AutorRepository;
import com.alura.literalura.repository.LibroRepository;
import com.alura.literalura.service.ConsumoAPI;
import com.alura.literalura.service.ConvierteDatos;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://gutendex.com/books/?search=";
    private ConvierteDatos conversor = new ConvierteDatos();
    private LibroRepository repositoryLibro;
    private AutorRepository repositoryAutor;
    private List<Autor> autores;
    private List<Libro> libros;

    public Principal(LibroRepository repositoryLibro, AutorRepository repositoryAutor) {
        this.repositoryLibro = repositoryLibro;
        this.repositoryAutor = repositoryAutor;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    
                    =====_+_+_+_+_+ ¡Bienvenido a Liter-ALURA! +_+_+_+_+_=====!
                    
                    Escoje la opción de su preferencia:
                    
                    Seleccione una opción del 1 al 7, o 0 para salir de la Aplicación.
                    
                    1. "Buscar libros por título"
                    2. "Mostrar libros registrados"
                    3. "Mostrar autores registrados"
                    4. "Autores vivos en determinado año"
                    5. "Buscar libros por idioma"
                    6. "Top 10 libros más descargados"
                    7. "Libro más descargado y menos descargado" 
                    
                    0. "Salir"
                    """;

            System.out.println(menu);
            while (!teclado.hasNextInt()) {
                System.out.println("Su opción es inválida :( , Por favor, ingrese un número del 1 al 7, o 0 para salir de la aplicación.");
                teclado.nextLine();
            }
            opcion = teclado.nextInt();
            teclado.nextLine();
            switch (opcion) {
                case 1:
                    buscaUnLibro();
                    break;
                case 2:
                    mostrarLibros();
                    break;
                case 3:
                    mostrarAutores();
                    break;
                case 4:
                    autoresVivosPorAnio();
                    break;
                case 5:
                    buscarLibroPorIdioma();
                    break;
                case 6:
                    top10LibrosMasDescargados();
                    break;
                case 7:
                    rankingLibro();
                    break;
                case 0:
                    System.out.println("              Saliendo de la aplicación...");
                    System.out.println("_-_-_-_ ¡Muchas Gracias por visitar Liter-ALURA! _-_-_-_\n");
                    break;
                default:
                    System.out.printf("Opción inválida :( , Por favor, ingrese un número del 1 al 7, o 0 para salir de la aplicación.\n");
            }
        }
    }

    private DatosBusqueda getBusqueda() {
        System.out.println("Escribe el nombre del libro que deseas buscar: ");
        var nombreLibro = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + nombreLibro.replace(" ", "%20"));
        DatosBusqueda datos = conversor.obtenerDatos(json, DatosBusqueda.class);
        return datos;

    }

    private void buscaUnLibro() {
        DatosBusqueda datosBusqueda = getBusqueda();
        if (datosBusqueda != null && !datosBusqueda.resultado().isEmpty()) {
            DatosLibros primerLibro = datosBusqueda.resultado().get(0);


            Libro libro = new Libro(primerLibro);
            System.out.println(libro);

                Optional<Libro> libroExiste = repositoryLibro.findByTitulo(libro.getTitulo());
                if (libroExiste.isPresent()){
                    System.out.println("\nEl libro que busca ya está registrado...Puede realizar otra búsqueda, por favor.");
                }else {

                    if (!primerLibro.autor().isEmpty()) {
                        DatosAutor autor = primerLibro.autor().get(0);
                        Autor autor1 = new Autor(autor);
                        Optional<Autor> autorOptional = repositoryAutor.findByNombre(autor1.getNombre());

                        if (autorOptional.isPresent()) {
                            Autor autorExiste = autorOptional.get();
                            libro.setAutor(autorExiste);
                            repositoryLibro.save(libro);
                        } else {
                            Autor autorNuevo = repositoryAutor.save(autor1);
                            libro.setAutor(autorNuevo);
                            repositoryLibro.save(libro);
                        }

                        Integer numeroDescargas = libro.getNumero_descargas() != null ? libro.getNumero_descargas() : 0;
                        System.out.println("\n_-_-_-_  Libro _-_-_-_\n");
                        System.out.printf("1. Titulo: %s%n2. Autor: %s%n3. Idioma: %s%n4. Número de Descargas: %s%n",
                                libro.getTitulo(), autor1.getNombre(), libro.getLenguaje(), libro.getNumero_descargas());
                        System.out.println("_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_");
                    } else {
                        System.out.println("Sin autor :( ");
                    }
                }
        } else {
            System.out.println("\nSu libro no ha sido encontrado...Intente con otro ejemplar.");
        }
    }

    private void mostrarLibros() {
        libros = repositoryLibro.findAll();
        libros.stream()
                .forEach(System.out::println);
    }

    private void mostrarAutores() {
        autores = repositoryAutor.findAll();
        autores.stream()
                .forEach(System.out::println);
    }

    private void autoresVivosPorAnio() {
        System.out.println("Ingresa el año para el que desea saber si el(los) autor(es) están vivos: ");
        var anio = teclado.nextInt();
        autores = repositoryAutor.listaAutoresVivosPorAnio(anio);
        autores.stream()
                .forEach(System.out::println);
    }

    private List<Libro> datosBusquedaLenguaje(String idioma){
        var dato = Idioma.fromString(idioma);
        System.out.println("Lenguaje buscado: " + dato);

        List<Libro> libroPorIdioma = repositoryLibro.findByLenguaje(dato);
        return libroPorIdioma;
    }

    private void buscarLibroPorIdioma(){
        System.out.println("Selecciona el idioma en el que deseas buscar libros: ");

        var opcion = -1;
        while (opcion != 0) {
            var opciones = """
                    1. "en" --> Ingles
                    2. "es" --> Español
                    3. "fr" --> Francés
                    4. "pt" --> Portugués
                    
                    0. Pulse 0 para volver al menú principal.
                    """;
            System.out.println(opciones);
            while (!teclado.hasNextInt()) {
                System.out.println("Opción no válida :( Por favor, ingrese una opción que esté disponible en el menú");
                teclado.nextLine();
            }
            opcion = teclado.nextInt();
            teclado.nextLine();
            switch (opcion) {
                case 1:
                    List<Libro> librosEnIngles = datosBusquedaLenguaje("[en]");
                    librosEnIngles.forEach(System.out::println);
                    break;
                case 2:
                    List<Libro> librosEnEspanol = datosBusquedaLenguaje("[es]");
                    librosEnEspanol.forEach(System.out::println);
                    break;
                case 3:
                    List<Libro> librosEnFrances = datosBusquedaLenguaje("[fr]");
                    librosEnFrances.forEach(System.out::println);
                    break;
                case 4:
                    List<Libro> librosEnPortugues = datosBusquedaLenguaje("[pt]");
                    librosEnPortugues.forEach(System.out::println);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("No ha seleccionado ningún idioma para hacer su búsqueda.");
            }
        }
    }

    private void top10LibrosMasDescargados() {
        List<Libro> topLibros = repositoryLibro.top5LibrosMasDescargados();
        topLibros.forEach(System.out::println);
    }

    private void rankingLibro() {
        libros = repositoryLibro.findAll();
        IntSummaryStatistics est = libros.stream()
                .filter(l -> l.getNumero_descargas() > 0)
                .collect(Collectors.summarizingInt(Libro::getNumero_descargas));

        Libro libroMasDescargado = libros.stream()
                .filter(l -> l.getNumero_descargas() == est.getMax())
                .findFirst()
                .orElse(null);

        Libro libroMenosDescargado = libros.stream()
                .filter(l -> l.getNumero_descargas() == est.getMin())
                .findFirst()
                .orElse(null);
        System.out.println("_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_");
        System.out.printf("%nEl libro más descargado (+) es: %s%nNúmero de descargas: " +
                "%d%n%nEl libro menos descargado (-) es: %s%nNúmero de descargas: " +
                "%d%n%n",libroMasDescargado.getTitulo(),est.getMax(),
                libroMenosDescargado.getTitulo(),est.getMin());
        System.out.println("_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_");
    }

}






