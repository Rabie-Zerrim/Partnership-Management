package com.partnershipmanagement.Services;

import com.partnershipmanagement.Entities.Entreprise;
import com.partnershipmanagement.Entities.Partnership;
import com.partnershipmanagement.Entities.PartnershipStatus;
import com.partnershipmanagement.Entities.Proposal;
import com.partnershipmanagement.Repositories.EntrepriseRepository;
import com.partnershipmanagement.Repositories.PartnershipRepository;
import com.partnershipmanagement.Repositories.ProposalRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class PartnershipService implements IPartnershipService {

    @Autowired
    private PartnershipRepository partnershipRepository;
    @Autowired
    private EntrepriseRepository entrepriseRepository;
    @Autowired
    private ProposalRepository proposalRepository;



    // Add these methods to your existing PartnershipService class

    // Create a new partnership
    public Partnership createPartnership(Partnership partnership) {
        return partnershipRepository.save(partnership);
    }

    // Get a partnership by ID
    public Partnership getPartnershipById(int id) {
        return partnershipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Partnership not found with ID: " + id));
    }

    // Get all partnerships
    public List<Partnership> getAllPartnerships() {
        return partnershipRepository.findAll();
    }

    // Update a partnership
    public Partnership updatePartnership(int id, Partnership partnership) {
        Partnership existingPartnership = partnershipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Partnership not found with ID: " + id));

        // Update fields
        existingPartnership.setPartnershipStatus(partnership.getPartnershipStatus());
        existingPartnership.setEntreprise(partnership.getEntreprise());
        existingPartnership.setProposals(partnership.getProposals());

        return partnershipRepository.save(existingPartnership);
    }

    // Delete a partnership
    public void deletePartnership(int id) {
        partnershipRepository.deleteById(id);
    }

    // Delete all partnerships
    public void deleteAllPartnerships() {
        partnershipRepository.deleteAll();
    }


    public Partnership applyForPartnership(int entrepriseId, int proposalId) {
        Entreprise entreprise = entrepriseRepository.findById(entrepriseId)
                .orElseThrow(() -> new EntityNotFoundException("Entreprise not found with id " + entrepriseId));

        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new EntityNotFoundException("Proposal not found with id " + proposalId));

        //  If the proposal is already "FULFILLED", block the application
        if (proposal.getProposalStatus().equals("FULFILLED")) {
            throw new IllegalStateException("This proposal has already been fulfilled. No more applications are allowed.");
        }

        //  Check if any existing partnership for this proposal is already ACCEPTED
        boolean isAccepted = partnershipRepository.existsByProposalIdProposalAndPartnershipStatus(proposalId, PartnershipStatus.Approved);

        if (isAccepted) {
            // Update proposal status to "FULFILLED" and prevent further applications
            proposal.setProposalStatus("FULFILLED");
            proposalRepository.save(proposal);
            throw new IllegalStateException("A partnership for this proposal has already been accepted. No further applications are allowed.");
        }

        //  Create a new Partnership with status PENDING
        Partnership partnership = new Partnership();
        partnership.setEntreprise(entreprise);
        partnership.setProposals(proposal);
        partnership.setPartnershipStatus(PartnershipStatus.pending);

        return partnershipRepository.save(partnership);
    }

    @Transactional
    public void deleteExpiredPartnerships() {
        Date currentDate = new Date();
        List<Partnership> partnershipsToDelete = partnershipRepository.findByEndDateBefore(currentDate);


        // Delete the expired partnerships
        partnershipRepository.deleteAll(partnershipsToDelete);

        // Log remaining partnerships
        List<Partnership> remainingPartnerships = partnershipRepository.findByEndDateAfter(currentDate);
        System.out.println("Remaining partnerships: " + remainingPartnerships);
    }

    @Transactional
    public void acceptPartnership(int partnershipId, int entrepriseId) {
        // Retrieve the partnership by ID
        Partnership partnership = partnershipRepository.findById(partnershipId)
                .orElseThrow(() -> new EntityNotFoundException("Partnership not found"));

        // Check if the enterprise ID matches
        if (partnership.getEntreprise().getIdEntreprise() != entrepriseId) {
            throw new IllegalArgumentException("This partnership does not belong to the given enterprise.");
        }

        // Retrieve the associated proposal
        Proposal proposal = partnership.getProposals();
        if (proposal == null) {
            throw new IllegalStateException("Proposal not found for this partnership.");
        }

        // Accept the selected partnership
        partnership.setPartnershipStatus(PartnershipStatus.Approved);

        // Reject all other partnerships for the same proposal
        List<Partnership> otherPartnerships = partnershipRepository.findByProposal(proposal);
        for (Partnership p : otherPartnerships) {
            if (p.getIdPartnership() != partnershipId) {
                p.setPartnershipStatus(PartnershipStatus.Rejected);
            }
        }

        // Update the proposal status
        proposal.setProposalStatus("fulfilled");

        // Save changes
        partnershipRepository.saveAll(otherPartnerships);
        partnershipRepository.save(partnership);
        proposalRepository.save(proposal);
    }


    //api pdf
    public byte[] generatePdfForPartnership(Partnership partnership) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Define fonts and sizes
                PDType1Font boldFont = PDType1Font.HELVETICA_BOLD;
                PDType1Font regularFont = PDType1Font.HELVETICA;
                int headerSize = 16;
                int bodySize = 12;
                int smallSize = 10;

                float margin = 50;
                float yStart = page.getMediaBox().getHeight() - margin;
                float yPosition = yStart;

                // Title Section
                contentStream.setFont(boldFont, headerSize);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("PARTNERSHIP CONTRACT");
                contentStream.endText();

                yPosition -= 10;
                contentStream.moveTo(margin, yPosition);
                contentStream.lineTo(page.getMediaBox().getWidth() - margin, yPosition);
                contentStream.stroke();
                yPosition -= 20;

                // Invoice To / Pay To
                contentStream.setFont(boldFont, bodySize);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Invoice To:");
                contentStream.newLineAtOffset(250, 0);
                contentStream.showText("Pay To:");
                contentStream.endText();
                yPosition -= 15;

                contentStream.setFont(regularFont, smallSize);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(partnership.getEntreprise().getNameEntreprise());
                contentStream.newLineAtOffset(250, 0);
                contentStream.showText("ElevatED");
                contentStream.endText();
                yPosition -= 40;

                // start / end
                contentStream.setFont(boldFont, bodySize);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Start date:");
                contentStream.newLineAtOffset(250, 0);
                contentStream.showText("End date:");
                contentStream.endText();
                yPosition -= 15;

                contentStream.setFont(regularFont, smallSize);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(partnership.getProposals().getStartDate().toString());
                contentStream.newLineAtOffset(250, 0);
                contentStream.showText(partnership.getProposals().getEndDate().toString());
                contentStream.endText();
                yPosition -= 40;

                // Table Headers
                contentStream.setFont(boldFont, bodySize);
                float[] colWidths = { 500}; // Column widths
                String[] headers = { "Agreements"};

                float tableWidth = colWidths[0];
                float tableX = margin;
                drawTableRow(contentStream, headers, tableX, yPosition, colWidths, boldFont, bodySize);
                yPosition -= 20;

                // Table Data (Example Data - You can fetch actual partnership details)
                // Convert Set<String> to String[][]
                //Set<String> agreementsSet = partnership.getProposals().getAgreements();
                //List<String> agreementsList = new ArrayList<>(agreementsSet);

                // Define the structure for the table
                //String[][] items = new String[agreementsList.size()][1]; // One column per row

                // for (int i = 0; i < agreementsList.size(); i++) {
                //    items[i][0] = agreementsList.get(i); // Each agreement is placed in a separate row
                // }

                // contentStream.setFont(regularFont, bodySize);
                //for (String[] row : items) {
                //     drawTableRow(contentStream, row, tableX, yPosition, colWidths, regularFont, bodySize);
                // yPosition -= 20;
                //  }

                // Payment Section
                yPosition -= 30;
                contentStream.setFont(boldFont, bodySize);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Amount to pay : ");
                contentStream.endText();

                String plannedAmount = String.valueOf(partnership.getProposals().getPlannedAmount());

                yPosition -= 15;
                contentStream.setFont(regularFont, bodySize);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(plannedAmount);
                contentStream.endText();

                yPosition -= 30;

                // Terms & Conditions
                contentStream.setFont(boldFont, bodySize);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Terms & Conditions:");
                contentStream.endText();

                yPosition -= 15;
                contentStream.setFont(regularFont, smallSize);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("- Any disputes must be resolved within 30 days.");
                contentStream.newLineAtOffset(0, -12);
                contentStream.showText("- Payment must be made within 15 days of invoice.");
                contentStream.endText();
            }

            // Save and return PDF
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            document.save(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage(), e);
        }
    }

    private void drawTableRow(PDPageContentStream contentStream, String[] row, float x, float y, float[] colWidths, PDType1Font font, int fontSize) throws IOException {
        float cellHeight = 15;
        contentStream.setFont(font, fontSize);

        for (int i = 0; i < row.length; i++) {
            contentStream.beginText();
            contentStream.newLineAtOffset(x, y);
            contentStream.showText(row[i]);
            contentStream.endText();
            x += colWidths[i];
        }

        // Draw table row border
        contentStream.moveTo(x - sum(colWidths), y - 2);
        contentStream.lineTo(x, y - 2);
        contentStream.stroke();
    }

    private float sum(float[] arr) {
        float sum = 0;
        for (float v : arr) sum += v;
        return sum;
    }
}
