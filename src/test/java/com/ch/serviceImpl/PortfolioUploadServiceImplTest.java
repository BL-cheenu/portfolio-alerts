package com.ch.serviceImpl;

import com.ch.dto.*;
import com.ch.entity.PortfolioEntity;
import com.ch.entity.UserEntity;
import com.ch.repository.PortfolioRepository;
import com.ch.repository.StockRepository;
import com.ch.repository.UserRepository;
import com.ch.utils.ExcelParserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PortfolioUploadServiceImpl - Unit Tests")
class PortfolioUploadServiceImplTest {

    @Mock private ExcelParserUtil excelParserUtil;
    @Mock private StockRepository stockRepository;
    @Mock private PortfolioRepository portfolioRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private PortfolioUploadServiceImpl uploadService;

    private UserEntity mockUser;

    @BeforeEach
    void setUp() {
        mockUser = UserEntity.builder()
                .id(1L).name("John").username("johndoe").email("john@example.com")
                .password("hashed").build();
    }

    // ── PREVIEW TESTS ────────────────────────────────────────────────────

    @Test
    @DisplayName("Preview - empty file should return FAILED")
    void testPreview_EmptyFile() {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file", "portfolio.xls", "application/vnd.ms-excel", new byte[0]);

        CommonDto<UploadPreviewDto> response = uploadService.previewUpload("johndoe", emptyFile);

        assertEquals("FAILED", response.getStatus());
        assertEquals(400, response.getStatusCode());
    }

    @Test
    @DisplayName("Preview - user not found should return FAILED")
    void testPreview_UserNotFound() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "portfolio.xls", "application/vnd.ms-excel", "data".getBytes());

        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.empty());

        CommonDto<UploadPreviewDto> response = uploadService.previewUpload("johndoe", file);

        assertEquals("FAILED", response.getStatus());
        assertEquals("User not found.", response.getMsg());
    }

    @Test
    @DisplayName("Preview - valid new stock should classify as new")
    void testPreview_NewStock() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "portfolio.xls", "application/vnd.ms-excel", "data".getBytes());

        UploadRowDto row = UploadRowDto.builder()
                .stockSymbol("RELIANCE").companyName("Reliance").quantity(10).buyPrice(2800).build();

        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));
        when(excelParserUtil.parseExcel(any())).thenReturn(List.of(row));
        when(stockRepository.existsByTickerSymbolIgnoreCase("RELIANCE")).thenReturn(true);
        when(portfolioRepository.existsByUserAndStockSymbolIgnoreCase(mockUser, "RELIANCE")).thenReturn(false);

        CommonDto<UploadPreviewDto> response = uploadService.previewUpload("johndoe", file);

        assertEquals("SUCCESS", response.getStatus());
        assertEquals(1, response.getData().getNewCount());
        assertEquals(0, response.getData().getConflictCount());
        assertEquals(0, response.getData().getInvalidCount());
    }

    @Test
    @DisplayName("Preview - existing stock should classify as conflict")
    void testPreview_ConflictStock() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "portfolio.xls", "application/vnd.ms-excel", "data".getBytes());

        UploadRowDto row = UploadRowDto.builder()
                .stockSymbol("TCS").companyName("TCS").quantity(5).buyPrice(3900).build();

        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));
        when(excelParserUtil.parseExcel(any())).thenReturn(List.of(row));
        when(stockRepository.existsByTickerSymbolIgnoreCase("TCS")).thenReturn(true);
        when(portfolioRepository.existsByUserAndStockSymbolIgnoreCase(mockUser, "TCS")).thenReturn(true);

        CommonDto<UploadPreviewDto> response = uploadService.previewUpload("johndoe", file);

        assertEquals("SUCCESS", response.getStatus());
        assertEquals(0, response.getData().getNewCount());
        assertEquals(1, response.getData().getConflictCount());
    }

    @Test
    @DisplayName("Preview - invalid ticker should classify as invalid")
    void testPreview_InvalidStock() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "portfolio.xls", "application/vnd.ms-excel", "data".getBytes());

        UploadRowDto row = UploadRowDto.builder()
                .stockSymbol("FAKESTOCK").companyName("Fake").quantity(5).buyPrice(100).build();

        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));
        when(excelParserUtil.parseExcel(any())).thenReturn(List.of(row));
        when(stockRepository.existsByTickerSymbolIgnoreCase("FAKESTOCK")).thenReturn(false);

        CommonDto<UploadPreviewDto> response = uploadService.previewUpload("johndoe", file);

        assertEquals("SUCCESS", response.getStatus());
        assertEquals(0, response.getData().getNewCount());
        assertEquals(1, response.getData().getInvalidCount());
    }

    // ── CONFIRM TESTS ────────────────────────────────────────────────────

    @Test
    @DisplayName("Confirm - new stocks should be added")
    void testConfirm_AddNewStocks() {
        UploadRowDto row = UploadRowDto.builder()
                .stockSymbol("WIPRO").companyName("Wipro").quantity(20).buyPrice(450).build();

        UploadConfirmDto confirmDto = UploadConfirmDto.builder()
                .newStocks(List.of(row))
                .updateStocks(List.of())
                .build();

        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));
        when(portfolioRepository.save(any())).thenReturn(new PortfolioEntity());

        CommonDto<UploadResultDto> response = uploadService.confirmUpload("johndoe", confirmDto);

        assertEquals("SUCCESS", response.getStatus());
        assertEquals(1, response.getData().getAddedCount());
        assertEquals(0, response.getData().getUpdatedCount());
        verify(portfolioRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Confirm - conflict stocks should be updated")
    void testConfirm_UpdateConflictStocks() {
        UploadRowDto row = UploadRowDto.builder()
                .stockSymbol("INFY").companyName("Infosys").quantity(15).buyPrice(1500).build();

        UploadConfirmDto confirmDto = UploadConfirmDto.builder()
                .newStocks(List.of())
                .updateStocks(List.of(row))
                .build();

        PortfolioEntity existing = PortfolioEntity.builder()
                .id(1L).stockSymbol("INFY").quantity(10).buyPrice(1400).user(mockUser).build();

        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));
        when(portfolioRepository.findByUserAndStockSymbolIgnoreCase(mockUser, "INFY"))
                .thenReturn(Optional.of(existing));
        when(portfolioRepository.save(any())).thenReturn(existing);

        CommonDto<UploadResultDto> response = uploadService.confirmUpload("johndoe", confirmDto);

        assertEquals("SUCCESS", response.getStatus());
        assertEquals(0, response.getData().getAddedCount());
        assertEquals(1, response.getData().getUpdatedCount());
    }

    @Test
    @DisplayName("Confirm - user not found should return FAILED")
    void testConfirm_UserNotFound() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.empty());

        CommonDto<UploadResultDto> response = uploadService.confirmUpload("johndoe",
                new UploadConfirmDto());

        assertEquals("FAILED", response.getStatus());
        assertEquals("User not found.", response.getMsg());
    }
}