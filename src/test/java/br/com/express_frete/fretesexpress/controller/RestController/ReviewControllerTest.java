package br.com.express_frete.fretesexpress.controller.RestController;

import br.com.express_frete.fretesexpress.dto.ReviewDTO;
import br.com.express_frete.fretesexpress.model.Review;
import br.com.express_frete.fretesexpress.model.User;
import br.com.express_frete.fretesexpress.model.enums.RatingType;
import br.com.express_frete.fretesexpress.model.enums.Role;
import br.com.express_frete.fretesexpress.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private ReviewDTO reviewDTO;
    private Review review;
    private User driver;
    private User client;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController).build();
        objectMapper = new ObjectMapper();

        // Setup test data
        setupTestData();
    }

    private void setupTestData() {
        // Create driver user
        driver = new User();
        driver.setId(1L);
        driver.setName("Driver Test");
        driver.setEmail("driver@test.com");
        driver.setRole(Role.DRIVER);

        // Create client user
        client = new User();
        client.setId(2L);
        client.setName("Client Test");
        client.setEmail("client@test.com");
        client.setRole(Role.CLIENT);

        // Create ReviewDTO
        reviewDTO = new ReviewDTO();
        reviewDTO.setRating(RatingType.GOOD);
        reviewDTO.setType("CLIENT");
        reviewDTO.setComment("Excellent service!");
        reviewDTO.setDriverId(1L);
        reviewDTO.setClientId(2L);

        // Create Review
        review = new Review();
        review.setId(1L);
        review.setRating(RatingType.GOOD);
        review.setType("CLIENT");
        review.setComment("Excellent service!");
        review.setDriver(driver);
        review.setClient(client);
    }

    @Test
    void createReview_ShouldReturnCreatedReview_WhenValidInput() throws Exception {
        // Arrange
        when(reviewService.createReview(any(ReviewDTO.class))).thenReturn(review);

        // Act & Assert
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rating").value("GOOD"))
                .andExpect(jsonPath("$.type").value("CLIENT"))
                .andExpect(jsonPath("$.comment").value("Excellent service!"))
                .andExpect(jsonPath("$.driver_id").value(1))
                .andExpect(jsonPath("$.client_id").value(2));

        verify(reviewService).createReview(any(ReviewDTO.class));
    }

    @Test
    void createReview_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        // Arrange - ReviewDTO with null rating
        ReviewDTO invalidReviewDTO = new ReviewDTO();
        invalidReviewDTO.setType("CLIENT");
        invalidReviewDTO.setDriverId(1L);
        invalidReviewDTO.setClientId(2L);

        // Act & Assert
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReviewDTO)))
                .andExpect(status().isBadRequest());

        verify(reviewService, never()).createReview(any(ReviewDTO.class));
    }

    @Test
    void createReview_ShouldReturnBadRequest_WhenMissingRequiredFields() throws Exception {
        // Arrange - ReviewDTO without required fields
        ReviewDTO invalidReviewDTO = new ReviewDTO();
        invalidReviewDTO.setComment("Test comment");

        // Act & Assert
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReviewDTO)))
                .andExpect(status().isBadRequest());

        verify(reviewService, never()).createReview(any(ReviewDTO.class));
    }

    @Test
    void getReviewsByDriver_ShouldReturnDriverReviews_WhenDriverExists() throws Exception {
        // Arrange
        List<Review> reviews = Arrays.asList(review);
        when(reviewService.findReviewsByDriver(1L)).thenReturn(reviews);

        // Act & Assert
        mockMvc.perform(get("/api/reviews/driver/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].rating").value("GOOD"))
                .andExpect(jsonPath("$[0].type").value("CLIENT"))
                .andExpect(jsonPath("$[0].comment").value("Excellent service!"));

        verify(reviewService).findReviewsByDriver(1L);
    }

    @Test
    void getReviewsByDriver_ShouldReturnEmptyList_WhenNoReviewsFound() throws Exception {
        // Arrange
        when(reviewService.findReviewsByDriver(1L)).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/reviews/driver/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(reviewService).findReviewsByDriver(1L);
    }

    @Test
    void getReviewsByClient_ShouldReturnClientReviews_WhenClientExists() throws Exception {
        // Arrange
        List<Review> reviews = Arrays.asList(review);
        when(reviewService.findReviewsByClient(2L)).thenReturn(reviews);

        // Act & Assert
        mockMvc.perform(get("/api/reviews/client/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].rating").value("GOOD"))
                .andExpect(jsonPath("$[0].type").value("CLIENT"));

        verify(reviewService).findReviewsByClient(2L);
    }

    @Test
    void getReviewsByClient_ShouldReturnEmptyList_WhenNoReviewsFound() throws Exception {
        // Arrange
        when(reviewService.findReviewsByClient(2L)).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/reviews/client/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(reviewService).findReviewsByClient(2L);
    }

    @Test
    void getReviewsMade_ShouldReturnMadeReviews_WhenUserExists() throws Exception {
        // Arrange
        List<Review> reviews = Arrays.asList(review);
        when(reviewService.findReviewsMade(2L)).thenReturn(reviews);

        // Act & Assert
        mockMvc.perform(get("/api/reviews/made/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].rating").value("GOOD"));

        verify(reviewService).findReviewsMade(2L);
    }

    @Test
    void getReviewsMade_ShouldReturnEmptyList_WhenNoReviewsMade() throws Exception {
        // Arrange
        when(reviewService.findReviewsMade(2L)).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/reviews/made/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(reviewService).findReviewsMade(2L);
    }

    @Test
    void getReviewsReceived_ShouldReturnReceivedReviews_WhenUserExists() throws Exception {
        // Arrange
        List<Review> reviews = Arrays.asList(review);
        when(reviewService.findReviewsReceived(1L)).thenReturn(reviews);

        // Act & Assert
        mockMvc.perform(get("/api/reviews/received/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].rating").value("GOOD"));

        verify(reviewService).findReviewsReceived(1L);
    }

    @Test
    void getReviewsReceived_ShouldReturnEmptyList_WhenNoReviewsReceived() throws Exception {
        // Arrange
        when(reviewService.findReviewsReceived(1L)).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/reviews/received/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(reviewService).findReviewsReceived(1L);
    }

    @Test
    void getReviewsSummary_ShouldReturnSummary_WhenUserExists() throws Exception {
        // Arrange
        when(reviewService.getTotalReviewsMade(1L)).thenReturn(5);
        when(reviewService.getTotalReviewsReceived(1L)).thenReturn(3);

        // Act & Assert
        mockMvc.perform(get("/api/reviews/summary/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalReviewsMade").value(5))
                .andExpect(jsonPath("$.totalReviewsReceived").value(3));

        verify(reviewService).getTotalReviewsMade(1L);
        verify(reviewService).getTotalReviewsReceived(1L);
    }

    @Test
    void getReviewsSummary_ShouldReturnZeros_WhenUserHasNoReviews() throws Exception {
        // Arrange
        when(reviewService.getTotalReviewsMade(1L)).thenReturn(0);
        when(reviewService.getTotalReviewsReceived(1L)).thenReturn(0);

        // Act & Assert
        mockMvc.perform(get("/api/reviews/summary/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalReviewsMade").value(0))
                .andExpect(jsonPath("$.totalReviewsReceived").value(0));

        verify(reviewService).getTotalReviewsMade(1L);
        verify(reviewService).getTotalReviewsReceived(1L);
    }

    @Test
    void getReviewsByDriver_ShouldHandleInvalidId() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/reviews/driver/invalid"))
                .andExpect(status().isBadRequest());

        verify(reviewService, never()).findReviewsByDriver(anyLong());
    }

    @Test
    void getReviewsByClient_ShouldHandleInvalidId() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/reviews/client/invalid"))
                .andExpect(status().isBadRequest());

        verify(reviewService, never()).findReviewsByClient(anyLong());
    }

    @Test
    void createReview_ShouldValidateRatingType() throws Exception {
        // Arrange - Test with all valid rating types
        ReviewDTO goodReview = new ReviewDTO();
        goodReview.setRating(RatingType.GOOD);
        goodReview.setType("CLIENT");
        goodReview.setDriverId(1L);
        goodReview.setClientId(2L);

        ReviewDTO neutralReview = new ReviewDTO();
        neutralReview.setRating(RatingType.NEUTRAL);
        neutralReview.setType("DRIVER");
        neutralReview.setDriverId(1L);
        neutralReview.setClientId(2L);

        ReviewDTO badReview = new ReviewDTO();
        badReview.setRating(RatingType.BAD);
        badReview.setType("CLIENT");
        badReview.setDriverId(1L);
        badReview.setClientId(2L);

        when(reviewService.createReview(any(ReviewDTO.class))).thenReturn(review);

        // Act & Assert - Test GOOD rating
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(goodReview)))
                .andExpect(status().isCreated());

        // Act & Assert - Test NEUTRAL rating
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(neutralReview)))
                .andExpect(status().isCreated());

        // Act & Assert - Test BAD rating
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badReview)))
                .andExpect(status().isCreated());

        verify(reviewService, times(3)).createReview(any(ReviewDTO.class));
    }

    @Test
    void createReview_ShouldValidateTypeField() throws Exception {
        // Arrange - Test with empty type
        ReviewDTO emptyTypeReview = new ReviewDTO();
        emptyTypeReview.setRating(RatingType.GOOD);
        emptyTypeReview.setType("");
        emptyTypeReview.setDriverId(1L);
        emptyTypeReview.setClientId(2L);

        // Act & Assert
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyTypeReview)))
                .andExpect(status().isBadRequest());

        verify(reviewService, never()).createReview(any(ReviewDTO.class));
    }
}