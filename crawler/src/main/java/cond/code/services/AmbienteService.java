package cond.code.services;


import cond.code.entities.Ambiente;

public interface AmbienteService {
    void create (Ambiente ambiente);
    void update (Ambiente ambiente);
    void delete (Integer id);
    Ambiente getAmbienteByName (String name);

}
