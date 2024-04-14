package ru.almasgali.avito.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.almasgali.avito.dto.BannerRequest;
import ru.almasgali.avito.model.Banner;
import ru.almasgali.avito.repository.BannerRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class BannerService {

    private final BannerRepository bannerRepository;

    public void createBanner(String token, BannerRequest request) {
        if (!checkToken(token)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        LocalDateTime time = LocalDateTime.now();

        Banner banner = Banner.builder()
                .tagIds(request.getTagIds())
                .featureId(request.getFeatureId())
                .body(request.getBody())
                .isActive(request.isActive())
                .createdAt(time)
                .updatedAt(time)
                .build();

        bannerRepository.save(banner);
        log.info("Banner with id {} was created", banner.getId());
    }

    @CachePut(value = "banners", key = "#id")
    public void updateBanner(String token, Long id, BannerRequest request) {
        if (!checkToken(token)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        Banner banner = bannerRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Banner not found."));

        banner.setTagIds(request.getTagIds());
        banner.setFeatureId(request.getFeatureId());
        banner.setBody(request.getBody());
        banner.setActive(request.isActive());
        banner.setUpdatedAt(LocalDateTime.now());
        bannerRepository.save(banner);
    }

    @CacheEvict(value = "banners", key = "#id")
    public void deleteBanner(String token, Long id) {
        if (!checkToken(token)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        try {
            bannerRepository.deleteById(id);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public Banner getUserBanner(String token, Long tagId, Long featureId, boolean useLastRevision) {
        boolean isAdmin = checkToken(token);
        try {
            if (useLastRevision) {
                return bannerRepository.findByTagIdAndFeatureId(tagId, featureId, !isAdmin).getFirst();
            } else {
                return getBannerCacheable(tagId, featureId, isAdmin);
            }
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Cacheable(value = "banners", key = "#result.id")
    public Banner getBannerCacheable(Long tagId, Long featureId, boolean isAdmin) {
        return bannerRepository.findByTagIdAndFeatureId(tagId, featureId, !isAdmin).getFirst();
    }

    @Cacheable(value = "banners")
    public List<Banner> getBannersWithLimitAndOffset(
            String token,
            Long tagId,
            Long featureId,
            int limit,
            int offset) {
        if (!checkToken(token)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        if (tagId == null) {
            if (featureId == null) {
                log.info("LIMIT : {}, OFFSET : {}", limit, offset);
                return bannerRepository.findWithLimitAndOffset(limit, offset);
            } else {
                return bannerRepository.findByFeatureIdWithLimitAndOffset(featureId, limit, offset);
            }
        } else {
            if (featureId == null) {
                return bannerRepository.findByTagIdWithLimitAndOffset(tagId, limit, offset);
            } else {
                return bannerRepository.findByTagIdAndFeatureIdWithLimitAndOffset(tagId, featureId, limit, offset);
            }
        }

    }

    private boolean checkToken(String token) {
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authorized");
        }
        if (token.equals("admin_token")) {
            return true;
        }
        return false;
    }

    @CacheEvict(value = "banners", allEntries = true)
    @Scheduled(fixedRateString = "${caching.spring.bannersTTL}")
    public void emptyBannersCache() {
        log.info("emptying banners cache");
    }
}
