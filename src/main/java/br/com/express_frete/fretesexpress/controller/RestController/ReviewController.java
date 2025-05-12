package br.com.express_frete.fretesexpress.controller.RestController;

import br.com.express_frete.fretesexpress.dto.ReviewDTO;
import br.com.express_frete.fretesexpress.model.Review;
import br.com.express_frete.fretesexpress.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewDTO> createReview(@Valid @RequestBody ReviewDTO reviewDTO) {
        Review newReview = reviewService.createReview(reviewDTO);

        ReviewDTO review = new ReviewDTO();
        review.setRating(newReview.getRating());
        review.setType(newReview.getType());
        review.setComment(newReview.getComment());
        review.setDriverId(newReview.getDriver().getId());
        review.setClientId(newReview.getClient().getId());

        return new ResponseEntity<>(review, HttpStatus.CREATED);
    }

    @GetMapping("/driver/{id}")
    public ResponseEntity<List<Review>> getReviewsByDriver(@PathVariable Long id) {
        List<Review> reviews = reviewService.findReviewsByDriver(id);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/client/{id}")
    public ResponseEntity<List<Review>> getReviewsByClient(@PathVariable Long id) {
        List<Review> reviews = reviewService.findReviewsByClient(id);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/made/{userId}")
    public ResponseEntity<List<Review>> getReviewsMade(@PathVariable Long userId) {
        List<Review> reviews = reviewService.findReviewsMade(userId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/received/{userId}")
    public ResponseEntity<List<Review>> getReviewsReceived(@PathVariable Long userId) {
        List<Review> reviews = reviewService.findReviewsReceived(userId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/summary/{userId}")
    public ResponseEntity<Map<String, Integer>> getReviewsSummary(@PathVariable Long userId) {
        Integer totalMade = reviewService.getTotalReviewsMade(userId);
        Integer totalReceived = reviewService.getTotalReviewsReceived(userId);

        Map<String, Integer> response = new HashMap<>();
        response.put("totalReviewsMade", totalMade);
        response.put("totalReviewsReceived", totalReceived);

        return ResponseEntity.ok(response);
    }
}