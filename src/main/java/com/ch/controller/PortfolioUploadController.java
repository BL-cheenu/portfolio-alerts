package com.ch.controller;

import com.ch.dto.CommonDto;
import com.ch.dto.UploadConfirmDto;
import com.ch.dto.UploadPreviewDto;
import com.ch.dto.UploadResultDto;
import com.ch.service.PortfolioUploadService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST Controller for US5 – Portfolio Upload
 *
 * POST /api/v1/portfolio/upload/preview  → Step 1: parse + preview
 * POST /api/v1/portfolio/upload/confirm  → Step 2: confirm + save
 *
 * Both endpoints protected — JWT token required
 */
@RestController
@RequestMapping("/api/v1/portfolio/upload")
public class PortfolioUploadController {

    private static final Logger log = LoggerFactory.getLogger(PortfolioUploadController.class);

    @Autowired
    private PortfolioUploadService portfolioUploadService;

    // ── STEP 1: Upload file → get preview ───────────────────────────────
    @PostMapping(value = "/preview", consumes = "multipart/form-data")
    public ResponseEntity<CommonDto<UploadPreviewDto>> previewUpload(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest httpRequest) {

        String username = (String) httpRequest.getAttribute("username");
        log.info("POST /api/v1/portfolio/upload/preview - user: {}, file: {}",
                username, file.getOriginalFilename());

        CommonDto<UploadPreviewDto> response = portfolioUploadService.previewUpload(username, file);
        HttpStatus status = "SUCCESS".equals(response.getStatus()) ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(response, status);
    }

    // ── STEP 2: Confirm with user consent → save ─────────────────────────
    @PostMapping("/confirm")
    public ResponseEntity<CommonDto<UploadResultDto>> confirmUpload(
            @RequestBody UploadConfirmDto confirmDto,
            HttpServletRequest httpRequest) {

        String username = (String) httpRequest.getAttribute("username");
        log.info("POST /api/v1/portfolio/upload/confirm - user: {}", username);

        CommonDto<UploadResultDto> response = portfolioUploadService.confirmUpload(username, confirmDto);
        HttpStatus status = "SUCCESS".equals(response.getStatus()) ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(response, status);
    }
}
