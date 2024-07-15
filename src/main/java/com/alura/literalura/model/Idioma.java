package com.alura.literalura.model;

public enum Idioma {
    en("[en]", "Ingles"),
    es("[es]", "Español"),
    fr("[fr]", "Frances"),
    pt("[pt]", "Portugues");

    private String idiomaWeb;
    private String idiomaEspanol;

    Idioma(String idiomaWeb, String idiomaEspanol){
        this.idiomaWeb = idiomaWeb;
        this.idiomaEspanol = idiomaEspanol;

    }

    public static Idioma fromString(String text){
        for (Idioma idioma : Idioma.values()){
            if (idioma.idiomaWeb.equalsIgnoreCase(text)){
                return idioma;
            }
        }
        throw new IllegalArgumentException("Idioma no encontrado: " + text);
    }

    public static Idioma fromEspanol(String text){
        for (Idioma idioma : Idioma.values()){
            if (idioma.idiomaEspanol.equalsIgnoreCase(text)){
                return idioma;
            }
        }
        throw new IllegalArgumentException("Idioma no encontrado: " + text);
    }

    public String getIdiomaWeb() {
        return idiomaWeb;
    }

    public String getIdiomaEspanol() {
        return idiomaEspanol;
    }

}
