package codes.karlo.api.service.impl;

import codes.karlo.api.entity.ApiKey;
import codes.karlo.api.entity.Url;
import codes.karlo.api.entity.User;
import codes.karlo.api.exception.UrlNotFoundException;
import codes.karlo.api.exception.UserDoesntHaveApiKey;
import codes.karlo.api.repository.UrlRepository;
import codes.karlo.api.service.ApiKeyService;
import codes.karlo.api.service.UrlService;
import codes.karlo.api.service.UserService;
import codes.karlo.api.validator.UrlValidator;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CommonsLog
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;
    private final ApiKeyService apiKeyService;
    private final UserService userService;
    private final UrlValidator urlValidator;

    @Value("${url.short-length}")
    private int SHORT_URL_LENGTH;

    public UrlServiceImpl(UrlRepository urlRepository, ApiKeyService apiKeyService, UserService userService, UrlValidator urlValidator) {
        this.urlRepository = urlRepository;
        this.apiKeyService = apiKeyService;
        this.userService = userService;
        this.urlValidator = urlValidator;
    }

    @Override
    public Url saveUrl(Url url, String api_key) {

        urlValidator.longUrlInUrl(url);

        if (urlRepository.existsUrlByLongUrl(url.getLongUrl())) {

            log.warn("Long url already exists in DB, will return URL from long URL");
            return fetchUrlByLongUrl(url.getLongUrl());
        }

        setShortUrl(url, api_key);

        log.info("Saving URL");
        return urlRepository.save(url);

    }

    @Override
    public List<Url> fetchUrls(String apiKey) {

        User user = apiKeyService.fetchApiKeyByKey(apiKey).getOwner();

        return urlRepository.findAllByOwner(user)
                .orElse(null);
    }

    @Override
    public Url fetchUrlByShortUrl(String shortUrl) {
        return urlRepository.findByShortUrl(shortUrl)
                .map(url -> urlRepository.save(url.onVisit()))
                .orElseThrow(() -> new UrlNotFoundException("URL doesn't exist"));
    }

    @Override
    public Url fetchUrlByLongUrl(String longUrl) {

        return urlRepository.findByLongUrl(longUrl)
                .orElseThrow(() -> new UrlNotFoundException("URL doesn't exist"));
    }

    @Override
    public String generateShortUrl(int length) {

        return RandomStringUtils.random(length, true, false);
    }

    private void setShortUrl(Url url, String api_key) {

        log.info("Setting short URL");

        ApiKey apiKey = api_key != null ? apiKeyService.fetchApiKeyByKey(api_key) : null;

        if (apiKey == null && userService.getUserFromToken() != null) {
            apiKey = getFirstApiKeyForLoggedInUser();
        }

        if (url.getShortUrl() == null || apiKey == null) {
            url.setShortUrl(generateShortUrl(SHORT_URL_LENGTH));

            log.info("URL got generated short URL " + url.getShortUrl());

        } else {
            url.setApiKey(apiKey);
            url.setOwner(apiKey.getOwner());
            log.info(apiKey);
            apiKeyService.apiKeyUseAction(apiKey);

            log.info("URL got api key attached and is keeping custom short url: " + url.getShortUrl());

        }
    }

    private ApiKey getFirstApiKeyForLoggedInUser() throws UserDoesntHaveApiKey {
        log.info("User is authenticated but didn't pass API key");

        return userService.getUserFromToken().
                getApiKeys()
                .stream()
                .findFirst()
                .orElseThrow(() -> new UserDoesntHaveApiKey("You need to create API key first"));

    }
}
