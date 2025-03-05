package com.partnershipmanagement.Controllers;

import com.partnershipmanagement.Entities.Partnership;
import com.partnershipmanagement.Services.PartnershipService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/partnerships")
@CrossOrigin(origins = "http://localhost:4200")
public class PartnershipController {

    @Autowired
    private PartnershipService partnershipService;

    // Create a new partnership
    @PostMapping("/add")
    public ResponseEntity<Partnership> createPartnership(@RequestBody Partnership partnership) {
        Partnership newPartnership = partnershipService.createPartnership(partnership);
        return ResponseEntity.ok(newPartnership);
    }

    // Get a partnership by ID
    @GetMapping("/{id}")
    public ResponseEntity<Partnership> getPartnershipById(@PathVariable int id) {
        Partnership partnership = partnershipService.getPartnershipById(id);
        return ResponseEntity.ok(partnership);
    }

    // Get all partnershipspackage com.partnershipmanagement.Controllers;
    //
    //import com.partnershipmanagement.Entities.Partnership;
    //import com.partnershipmanagement.Services.PartnershipService;
    //import jakarta.persistence.EntityNotFoundException;
    //import org.springframework.beans.factory.annotation.Autowired;
    //import org.springframework.core.io.ByteArrayResource;
    //import org.springframework.http.HttpHeaders;
    //import org.springframework.http.HttpStatus;
    //import org.springframework.http.MediaType;
    //import org.springframework.http.ResponseEntity;
    //import org.springframework.web.bind.annotation.*;
    //
    //import java.util.List;
    //
    //@RestController
    //@RequestMapping("/partnerships")
    //@CrossOrigin(origins = "http://localhost:4200")
    //public class PartnershipController {
    //
    //    @Autowired
    //    private PartnershipService partnershipService;
    //
    //    // Create a new partnership
    //    @PostMapping("/add")
    //    public ResponseEntity<Partnership> createPartnership(@RequestBody Partnership partnership) {
    //        Partnership newPartnership = partnershipService.createPartnership(partnership);
    //        return ResponseEntity.ok(newPartnership);
    //    }
    //
    //    // Get a partnership by ID
    //    @GetMapping("/{id}")
    //    public ResponseEntity<Partnership> getPartnershipById(@PathVariable int id) {
    //        Partnership partnership = partnershipService.getPartnershipById(id);
    //        return ResponseEntity.ok(partnership);
    //    }
    //
    //    // Get all partnerships
    //    @GetMapping("/all")
    //    public ResponseEntity<List<Partnership>> getAllPartnerships() {
    //        List<Partnership> partnerships = partnershipService.getAllPartnerships();
    //        return ResponseEntity.ok(partnerships);
    //    }
    //
    //    // Update a partnership
    //    @PutMapping("/update/{id}")
    //    public ResponseEntity<Partnership> updatePartnership(@PathVariable int id, @RequestBody Partnership partnership) {
    //        Partnership updatedPartnership = partnershipService.updatePartnership(id, partnership);
    //        return ResponseEntity.ok(updatedPartnership);
    //    }
    //
    //    // Delete a partnership
    //    @DeleteMapping("/delete/{id}")
    //    public ResponseEntity<Void> deletePartnership(@PathVariable int id) {
    //        partnershipService.deletePartnership(id);
    //        return ResponseEntity.noContent().build();
    //    }
    //
    //    // Delete all partnerships
    //    @DeleteMapping("/deleteAll")
    //    public ResponseEntity<Void> deleteAllPartnerships() {
    //        partnershipService.deleteAllPartnerships();
    //        return ResponseEntity.noContent().build();
    //    }
    //
    //    @PostMapping("/applyForPartnership")
    //    public Partnership applyForPartnership(@RequestParam int entrepriseId, @RequestParam int proposalId) {
    //        return partnershipService.applyForPartnership(entrepriseId, proposalId);
    //    }
    //
    //    @PostMapping("/accept/{partnershipId}/{entrepriseId}")
    //    public String acceptPartnership(@PathVariable int partnershipId, @PathVariable int entrepriseId) {
    //        partnershipService.acceptPartnership(partnershipId, entrepriseId);
    //        return "Partnership accepted successfully.";
    //    }
    //
    //    @GetMapping("/partnerships/{id}/pdf")
    //    public ResponseEntity<?> generatePartnershipPdf(@PathVariable int id) {
    //        try {
    //            Partnership partnership = partnershipService.getPartnershipById(id);
    //            byte[] pdfBytes = partnershipService.generatePdfForPartnership(partnership);
    //
    //            ByteArrayResource resource = new ByteArrayResource(pdfBytes);
    //            return ResponseEntity.ok()
    //                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=partnership_" + id + ".pdf")
    //                    .contentType(MediaType.APPLICATION_PDF)
    //                    .contentLength(pdfBytes.length)
    //                    .body(resource);
    //
    //        } catch (EntityNotFoundException e) {
    //            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Partnership not found");
    //        } catch (RuntimeException e) {
    //            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                    .body("Failed to generate PDF: " + e.getMessage());
    //        }
    //    }
    //}
    @GetMapping("/all")
    public ResponseEntity<List<Partnership>> getAllPartnerships() {
        List<Partnership> partnerships = partnershipService.getAllPartnerships();
        return ResponseEntity.ok(partnerships);
    }

    // Update a partnership
    @PutMapping("/update/{id}")
    public ResponseEntity<Partnership> updatePartnership(@PathVariable int id, @RequestBody Partnership partnership) {
        Partnership updatedPartnership = partnershipService.updatePartnership(id, partnership);
        return ResponseEntity.ok(updatedPartnership);
    }

    // Delete a partnership
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePartnership(@PathVariable int id) {
        partnershipService.deletePartnership(id);
        return ResponseEntity.noContent().build();
    }

    // Delete all partnerships
    @DeleteMapping("/deleteAll")
    public ResponseEntity<Void> deleteAllPartnerships() {
        partnershipService.deleteAllPartnerships();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/applyForPartnership")
    public Partnership applyForPartnership(@RequestParam int entrepriseId, @RequestParam int proposalId) {
        return partnershipService.applyForPartnership(entrepriseId, proposalId);
    }

    @PostMapping("/accept/{partnershipId}/{entrepriseId}")
    public String acceptPartnership(@PathVariable int partnershipId, @PathVariable int entrepriseId) {
        partnershipService.acceptPartnership(partnershipId, entrepriseId);
        return "Partnership accepted successfully.";
    }

    @GetMapping("/partnerships/{id}/pdf")
    public ResponseEntity<?> generatePartnershipPdf(@PathVariable int id) {
        try {
            Partnership partnership = partnershipService.getPartnershipById(id);
            byte[] pdfBytes = partnershipService.generatePdfForPartnership(partnership);

            ByteArrayResource resource = new ByteArrayResource(pdfBytes);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=partnership_" + id + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(resource);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Partnership not found");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to generate PDF: " + e.getMessage());
        }
    }
}
