package com.nfs.PremierNotes.nfs.dto;

import com.nfs.PremierNotes.nfs.models.TomadorModel;
import java.util.List;

public class TomadoresFormWrapper {
    private List<TomadorModel> tomadores;

    // Getters e Setters
    public List<TomadorModel> getTomadores() { return tomadores; }
    public void setTomadores(List<TomadorModel> tomadores) { this.tomadores = tomadores; }
}