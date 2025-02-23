package com.alura.literalura.principal;

import com.alura.literalura.model.*;
import com.alura.literalura.repository.RepositorioAutor;
import com.alura.literalura.repository.RepositorioLibros;
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
    private RepositorioLibros repositorioLibros;
    private RepositorioAutor repositorioAutor;
    private List<Autor> autores;
    private List<Libro> libros;

    public Principal(RepositorioLibros repositorioLibros, RepositorioAutor repositorioAutor) {
        this.repositorioLibros = repositorioLibros;
        this.repositorioAutor = repositorioAutor;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            System.out.println("*************************************\n");
            var menu = """
                          Gestione sus libros
                    1 - Buscar libros por título
                    2 - Mostrar libros registrados
                    3 - Mostrar autores registrados
                    4 - Autores vivos en determinado año
                    5 - Buscar libros por idioma
                    6 - Top 10 libros más descargados
                    
                    0 - Salir
                    
                    """;


            System.out.println(menu);
            while (!teclado.hasNextInt()) {
                System.out.println("Opcion no valida");
                teclado.nextLine();
            }
            opcion = teclado.nextInt();
            teclado.nextLine();
            switch (opcion) {
                case 1:
                    buscarLibro();
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
                case 0:
                    System.out.println("Finalizando la aplicación");
                    break;
                default:
                    System.out.printf("Opción inválida\n");
            }
        }
    }

    private DatosBusqueda getBusqueda() {
        System.out.println("Escribe el nombre del libro: ");
        var nombreLibro = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + nombreLibro.replace(" ", "%20"));
        DatosBusqueda datos = conversor.obtenerDatos(json, DatosBusqueda.class);
        return datos;

    }

    private void buscarLibro() {
        DatosBusqueda datosBusqueda = getBusqueda();
        if (datosBusqueda != null && !datosBusqueda.resultado().isEmpty()) {
            DatosLibros primerLibro = datosBusqueda.resultado().get(0);


            Libro libro = new Libro(primerLibro);
            Optional<Libro> libroExiste = repositorioLibros.findByTitulo(libro.getTitulo());
                if (libroExiste.isPresent()){
                    System.out.println("\nEl libro ya esta registrado\n");
                }else {
                    if (!primerLibro.autor().isEmpty()) {
                        DatosAutor autor = primerLibro.autor().get(0);
                        Autor autor1 = new Autor(autor);
                        Optional<Autor> autorOptional = repositorioAutor.findByNombre(autor1.getNombre());

                        if (autorOptional.isPresent()) {
                            Autor autorExiste = autorOptional.get();
                            libro.setAutor(autorExiste);
                            repositorioLibros.save(libro);
                        } else {
                            Autor autorNuevo = repositorioAutor.save(autor1);
                            libro.setAutor(autorNuevo);
                            repositorioLibros.save(libro);
                        }

                        Integer numeroDescargas = libro.getNumero_descargas() != null ? libro.getNumero_descargas() : 0;
                        System.out.println("********** Libro **********");
                        System.out.printf("Titulo: %s%nAutor: %s%nIdioma: %s%nNumero de Descargas: %s%n",
                                libro.getTitulo(), autor1.getNombre(), libro.getLenguaje(), libro.getNumero_descargas());
                        System.out.println("***************************\n");
                    } else {
                        System.out.println("Sin autor");
                    }
                }
        } else {
            System.out.println("libro no encontrado");
        }
    }
    private void mostrarLibros() {
        libros = repositorioLibros.findAll();
        libros.stream()
                .forEach(System.out::println);
    }

    private void mostrarAutores() {
        autores = repositorioAutor.findAll();
        autores.stream()
                .forEach(System.out::println);
    }

    private void autoresVivosPorAnio() {
        System.out.println("Ingresa el año vivo de autor(es) que desea buscar: ");
        var anio = teclado.nextInt();
        autores = repositorioAutor.listaAutoresVivosPorAnio(anio);
        autores.stream()
                .forEach(System.out::println);
    }

    private List<Libro> datosBusquedaLenguaje(String idioma){
        var dato = Idioma.fromString(idioma);
        System.out.println("Lenguaje buscado: " + dato);

        List<Libro> libroPorIdioma = repositorioLibros.findByLenguaje(dato);
        return libroPorIdioma;
    }

    private void buscarLibroPorIdioma(){
        System.out.println("Selecciona el lenguaje/idioma que deseas buscar: ");

        var opcion = -1;
        while (opcion != 0) {
            var opciones = """
                    1. en - Ingles
                    2. es - Español
                    3. fr - Francés
                    4. pt - Portugués
                    
                    0. Volver a Las opciones anteriores
                    """;
            System.out.println(opciones);
            while (!teclado.hasNextInt()) {
                System.out.println("Opcion invalida");
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
                    System.out.println("Ningún idioma seleccionado");
            }
        }
    }

    private void top10LibrosMasDescargados() {
        List<Libro> topLibros = repositorioLibros.top10LibrosMasDescargados();
        topLibros.forEach(System.out::println);
    }


}






