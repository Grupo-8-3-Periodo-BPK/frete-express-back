package br.com.express_frete.fretesexpress.controller;

import br.com.express_frete.fretesexpress.dto.TrackingDTO;
import br.com.express_frete.fretesexpress.model.Tracking;
import br.com.express_frete.fretesexpress.service.TrackingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tracking")
public class TrackingController {

    @Autowired
    private TrackingService trackingService;

    @GetMapping
    public ResponseEntity<List<Tracking>> getAllTrackings() {
        return ResponseEntity.ok(trackingService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tracking> getTrackingById(@PathVariable Long id) {
        return ResponseEntity.ok(trackingService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Tracking> createTracking(@Valid @RequestBody TrackingDTO trackingDTO) {
        Tracking createdTracking = trackingService.create(trackingDTO);
        return new ResponseEntity<>(createdTracking, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tracking> updateTracking(@PathVariable Long id, @Valid @RequestBody TrackingDTO trackingDTO) {
        Tracking updatedTracking = trackingService.update(id, trackingDTO);
        return ResponseEntity.ok(updatedTracking);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTracking(@PathVariable Long id) {
        trackingService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/freight/{freightId}")
    public ResponseEntity<List<Tracking>> getTrackingsByFreight(@PathVariable Long freightId) {
        return ResponseEntity.ok(trackingService.findByFreight(freightId));
    }

    @GetMapping("/contract/{contractId}")
    public ResponseEntity<List<Tracking>> getTrackingsByContract(@PathVariable Long contractId) {
        return ResponseEntity.ok(trackingService.findByContract(contractId));
    }

    @GetMapping("/contract/{contractId}/latest")
    public ResponseEntity<Tracking> getLatestTrackingForContract(@PathVariable Long contractId) {
        return ResponseEntity.ok(trackingService.getLatestTrackingForContract(contractId));
    }
} 