package br.com.express_frete.fretesexpress.service;

import br.com.express_frete.fretesexpress.dto.ReviewDTO;
import br.com.express_frete.fretesexpress.model.Review;
import br.com.express_frete.fretesexpress.model.User;
import br.com.express_frete.fretesexpress.model.enums.RatingType;
import br.com.express_frete.fretesexpress.model.enums.Role;
import br.com.express_frete.fretesexpress.repository.ReviewRepository;
import br.com.express_frete.fretesexpress.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewService reviewService;

    private User driver;
    private User client;
    private ReviewDTO reviewDTO;
    private Review review;

    @BeforeEach
    void setUp() {
        driver = new User();
        driver.setId(1L);
        driver.setRole(Role.DRIVER);
        driver.setTotalReviewsMade(0);
        driver.setTotalReviewsReceived(0);

        client = new User();
        client.setId(2L);
        client.setRole(Role.CLIENT);
        client.setTotalReviewsMade(0);
        client.setTotalReviewsReceived(0);

        reviewDTO = new ReviewDTO();
        reviewDTO.setDriverId(1L);
        reviewDTO.setClientId(2L);
        reviewDTO.setRating(RatingType.GOOD); // Alterado para GOOD
        reviewDTO.setComment("Great service!");
        reviewDTO.setType("CLIENT");

        review = new Review();
        review.setId(1L);
        review.setRating(RatingType.GOOD); // Alterado para GOOD
        review.setComment("Great service!");
        review.setType("CLIENT");
        review.setDriver(driver);
        review.setClient(client);
    }

    @Test
    void createReview_ClientReviewingDriver_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(driver));
        when(userRepository.findById(2L)).thenReturn(Optional.of(client));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        Review result = reviewService.createReview(reviewDTO);

        assertNotNull(result);
        assertEquals(RatingType.GOOD, result.getRating()); // Alterado para GOOD
        assertEquals("Great service!", result.getComment());
        assertEquals(1, driver.getTotalReviewsReceived());
        assertEquals(1, client.getTotalReviewsMade());

        verify(userRepository, times(2)).save(any(User.class));
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void createReview_DriverNotFound_ThrowsException() {
        reviewDTO.setDriverId(99L);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> reviewService.createReview(reviewDTO));
    }

    @Test
    void createReview_InvalidRole_ThrowsException() {
        driver.setRole(Role.CLIENT);
        when(userRepository.findById(1L)).thenReturn(Optional.of(driver));
        when(userRepository.findById(2L)).thenReturn(Optional.of(client));

        assertThrows(IllegalArgumentException.class, () -> reviewService.createReview(reviewDTO));
    }

    @Test
    void createReview_InvalidType_ThrowsException() {
        reviewDTO.setType("INVALID");
        when(userRepository.findById(1L)).thenReturn(Optional.of(driver));
        when(userRepository.findById(2L)).thenReturn(Optional.of(client));

        assertThrows(IllegalArgumentException.class, () -> reviewService.createReview(reviewDTO));
    }

    @Test
    void findReviewsByDriver_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(driver));
        when(reviewRepository.findByDriver(driver)).thenReturn(Arrays.asList(review));

        List<Review> results = reviewService.findReviewsByDriver(1L);

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(review, results.get(0));
    }

    @Test
    void findReviewsByClient_Success() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(client));
        when(reviewRepository.findByClient(client)).thenReturn(Arrays.asList(review));

        List<Review> results = reviewService.findReviewsByClient(2L);

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(review, results.get(0));
    }

    @Test
    void findReviewsMade_Client_Success() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(client));
        when(reviewRepository.findByClientAndType(client, "client")).thenReturn(Arrays.asList(review));

        List<Review> results = reviewService.findReviewsMade(2L);

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
    }

    @Test
    void findReviewsReceived_Driver_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(driver));
        when(reviewRepository.findByDriverAndType(driver, "client")).thenReturn(Arrays.asList(review));

        List<Review> results = reviewService.findReviewsReceived(1L);

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
    }

    @Test
    void getTotalReviewsMade_Success() {
        client.setTotalReviewsMade(5);
        when(userRepository.findById(2L)).thenReturn(Optional.of(client));

        int result = reviewService.getTotalReviewsMade(2L);

        assertEquals(5, result);
    }

    @Test
    void getTotalReviewsReceived_Success() {
        driver.setTotalReviewsReceived(3);
        when(userRepository.findById(1L)).thenReturn(Optional.of(driver));

        int result = reviewService.getTotalReviewsReceived(1L);

        assertEquals(3, result);
    }
}