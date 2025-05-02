package br.com.express_frete.fretesexpress.dto;

import java.time.LocalDateTime;
import br.com.express_frete.fretesexpress.model.enums.FreteStatus;

public class FreteDTO {

    private FreteStatus status;
    private LocalDateTime dataHoraAprovacaoCliente;
    private LocalDateTime dataHoraAprovacaoMotorista;
    private String tipoCaminhao;
    private String tipoCarga;
}
