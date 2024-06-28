package com.alura.literalura.model;

import jakarta.persistence.*;

@Entity
@Table(name = "libros")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String titulo;

    @ManyToOne
    private Autor autor;

    @Enumerated(EnumType.STRING)
    private Idioma lenguaje;
    private Integer numero_descargas;

    public Libro(){}

    public Libro(DatosLibros datosLibro){
        this.titulo = datosLibro.titulo();
        this.lenguaje = Idioma.fromString(datosLibro.idiomas().toString().split(",")[0].trim());
        this.numero_descargas = datosLibro.numero_descargas();
    }

    @Override
    public String toString() {
        //String nombreAutor = (autor != null) ? autor.getNombre(): "El autor es desconocido.";
        String nombreAutor = (autor == null) ? "El autor es desconocido." : autor.getNombre();
        return String.format("\n_-_-_-_Libro _-_-_-_%n\n1. Titulo: " +
                " %s%n2. Autor: %s%n3. Idioma: %s%n4. Número de Descargas realizadas: " +
                " %d%n\n_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_%n\n",titulo,nombreAutor,lenguaje, numero_descargas);
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }


    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }


    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }


    public Idioma getLenguaje() {
        return lenguaje;
    }

    public void setLenguaje(Idioma lenguaje) {
        this.lenguaje = lenguaje;
    }

    public Integer getNumero_descargas() {
        return numero_descargas;
    }

    public void setNumero_descargas(Integer numero_descargas) {
        this.numero_descargas = numero_descargas;
    }
}
