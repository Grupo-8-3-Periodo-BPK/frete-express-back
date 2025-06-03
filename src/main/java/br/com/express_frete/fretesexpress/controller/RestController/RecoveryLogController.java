package br.com.express_frete.fretesexpress.controller.RestController;

import br.com.express_frete.fretesexpress.model.RecoveryLog;
import br.com.express_frete.fretesexpress.repository.RecoveryLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recovery/logs")
public class RecoveryLogController {

    @Autowired
    private RecoveryLogRepository logRepository;

    // GET /api/recovery/logs -> retorna todos os logs
    @GetMapping
    public List<RecoveryLog> getAllLogs() {
        return logRepository.findAll();
    }

    // GET /api/recovery/logs/{email} -> retorna logs de um email específico
    @GetMapping("/{email}")
    public List<RecoveryLog> getLogsByEmail(@PathVariable String email) {
        return logRepository.findByEmail(email);
    }
}
