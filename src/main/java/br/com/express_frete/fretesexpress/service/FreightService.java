package br.com.express_frete.fretesexpress.service;

import br.com.express_frete.fretesexpress.model.Freight;
import br.com.express_frete.fretesexpress.model.enums.FreightStatus;
import br.com.express_frete.fretesexpress.repository.FreightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FreightService {

    @Autowired
    private FreightRepository freightRepository;

    public List<Freight> findAll() {
        return freightRepository.findAll();
    }

    public List<Freight> findByStatus(FreightStatus status) {
        return freightRepository.findByStatus(status);
    }

    // Adicionar outros métodos conforme necessário (findById, save, delete, etc.)
}