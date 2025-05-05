package br.com.express_frete.fretesexpress.repository;

import br.com.express_frete.fretesexpress.model.Review;
import br.com.express_frete.fretesexpress.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByDriver(User driver);

    List<Review> findByClient(User client);

    List<Review> findByClientAndType(User client, String type);

    List<Review> findByDriverAndType(User driver, String type);
}