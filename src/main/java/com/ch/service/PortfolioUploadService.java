package com.ch.service;

import com.ch.dto.CommonDto;
import com.ch.dto.UploadConfirmDto;
import com.ch.dto.UploadPreviewDto;
import com.ch.dto.UploadResultDto;
import org.springframework.web.multipart.MultipartFile;

public interface PortfolioUploadService {

    // Step 1: Parse + preview (show conflicts to user)
    CommonDto<UploadPreviewDto> previewUpload(String username, MultipartFile file);

    // Step 2: Confirm (user gives consent for conflicts)
    CommonDto<UploadResultDto> confirmUpload(String username, UploadConfirmDto confirmDto);
}
