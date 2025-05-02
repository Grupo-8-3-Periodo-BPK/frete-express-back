package br.com.express_frete.fretesexpress.repository;

import br.com.express_frete.fretesexpress.model.Frete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FreteRepository extends JpaRepository<Frete, Long> {

    @Query("SELECT f FROM Frete f " +
            "WHERE (:cidadeOrigem IS NULL OR f.cidadeOrigem = :cidadeOrigem) " +
            "AND (:cidadeDestino IS NULL OR f.cidadeDestino = :cidadeDestino) " +
            "AND (:tipoCaminhao IS NULL OR f.tipoCaminhao = :tipoCaminhao) " +
            "AND (:tipoCarga IS NULL OR f.tipoCarga = :tipoCarga)")
    List<Frete> buscarPorFiltros(
            @Param("cidadeOrigem") String cidadeOrigem,
            @Param("cidadeDestino") String cidadeDestino,
            @Param("tipoCaminhao") String tipoCaminhao,
            @Param("tipoCarga") String tipoCarga
    );
}
