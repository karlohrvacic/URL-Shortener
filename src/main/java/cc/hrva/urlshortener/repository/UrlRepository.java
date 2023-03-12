package cc.hrva.urlshortener.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import cc.hrva.urlshortener.model.Url;
import cc.hrva.urlshortener.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    Optional<Url> findByLongUrlAndActiveTrue(String longUrl);

    Optional<Url> findByShortUrlAndActiveTrue(String shortUrl);

    Optional<List<Url>> findAllByOwner(User owner);

    boolean existsUrlByLongUrlAndActiveTrue(String longUrl);

    boolean existsUrlByLongUrlAndActiveTrueAndOwnerIsNull(String longUrl);

    boolean existsUrlByShortUrlAndActiveTrue(String shortUrl);

    List<Url> findByExpirationDateLessThanEqualAndActiveTrue(LocalDateTime expirationDate);

}
