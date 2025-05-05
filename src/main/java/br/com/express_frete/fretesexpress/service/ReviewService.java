package br.com.express_frete.fretesexpress.service;

import br.com.express_frete.fretesexpress.dto.ReviewDTO;
import br.com.express_frete.fretesexpress.model.Review;
import br.com.express_frete.fretesexpress.model.User;
import br.com.express_frete.fretesexpress.model.enums.Role;
import br.com.express_frete.fretesexpress.repository.ReviewRepository;
import br.com.express_frete.fretesexpress.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Review createReview(ReviewDTO reviewDTO) {
        // Validate if users exist
        User driver = userRepository.findById(reviewDTO.getDriverId())
                .orElseThrow(() -> new EntityNotFoundException("Driver not found"));

        User client = userRepository.findById(reviewDTO.getClientId())
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));

        // Validate user roles
        if (driver.getRole() != Role.DRIVER) {
            throw new IllegalArgumentException("User specified as driver does not have that role");
        }

        if (client.getRole() != Role.CLIENT) {
            throw new IllegalArgumentException("User specified as client does not have that role");
        }

        // Validate review type (who is reviewing whom)
        if (!reviewDTO.getType().equals("CLIENT") && !reviewDTO.getType().equals("DRIVER")) {
            throw new IllegalArgumentException("Review type must be 'CLIENT' or 'DRIVER'");
        }

        // Create the review
        Review review = new Review();
        review.setRating(reviewDTO.getRating());
        review.setType(reviewDTO.getType());
        review.setComment(reviewDTO.getComment());
        review.setDriver(driver);
        review.setClient(client);

        // Increment review counters
        if (reviewDTO.getType().equals("CLIENT")) {
            // Client is reviewing driver
            client.incrementReviewsMade();
            driver.incrementReviewsReceived();
        } else {
            // Driver is reviewing client
            driver.incrementReviewsMade();
            client.incrementReviewsReceived();
        }

        // Save updated users
        userRepository.save(client);
        userRepository.save(driver);

        // Save the review
        return reviewRepository.save(review);
    }

    public List<Review> findReviewsByDriver(Long driverId) {
        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new EntityNotFoundException("Driver not found"));

        return reviewRepository.findByDriver(driver);
    }

    public List<Review> findReviewsByClient(Long clientId) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));

        return reviewRepository.findByClient(client);
    }

    public List<Review> findReviewsMade(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (user.getRole() == Role.CLIENT) {
            return reviewRepository.findByClientAndType(user, "client");
        } else if (user.getRole() == Role.DRIVER) {
            return reviewRepository.findByDriverAndType(user, "driver");
        }

        throw new IllegalArgumentException("User does not have permission to make reviews");
    }

    public List<Review> findReviewsReceived(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (user.getRole() == Role.CLIENT) {
            return reviewRepository.findByClientAndType(user, "driver");
        } else if (user.getRole() == Role.DRIVER) {
            return reviewRepository.findByDriverAndType(user, "client");
        }

        throw new IllegalArgumentException("User does not have permission to receive reviews");
    }

    // Additional method to get only the total of reviews made
    public Integer getTotalReviewsMade(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return user.getTotalReviewsMade();
    }

    // Additional method to get only the total of reviews received
    public Integer getTotalReviewsReceived(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return user.getTotalReviewsReceived();
    }
}