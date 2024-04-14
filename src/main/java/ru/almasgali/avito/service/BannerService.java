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
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class BannerService {

    private final BannerRepository bannerRepository;

    public void createBanner(String token, BannerRequest request) {
        checkAdminToken(token);
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
        checkAdminToken(token);
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
        checkAdminToken(token);
        bannerRepository.deleteById(id);
    }

    public Banner getUserBanner(String token, Long tagId, Long featureId, boolean useLastRevision) {
        if (useLastRevision) {
            return bannerRepository.findByTagIdAndFeatureId(tagId, featureId).getFirst();
        } else {
            return getBannerCacheable(tagId, featureId);
        }
    }

    @Cacheable(value = "banners", key = "#result.id")
    public Banner getBannerCacheable(Long tagId, Long featureId) {
        return bannerRepository.findByTagIdAndFeatureId(tagId, featureId).getFirst();
    }

    @Cacheable(value = "banners")
    public List<Banner> getBannersWithLimitAndOffset(
            String token, Long tagId,
            Long featureId,
            String limit,
            int offset) {
        checkAdminToken(token);
        return bannerRepository.findByTagIdAndFeatureIdWithLimitAndOffset(tagId, featureId, limit, offset);
    }

    private void checkAdminToken(String token) {
        if (!Objects.equals(token, "admin_token")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "user is not admin");
        }
    }

    @CacheEvict(value = "banners", allEntries = true)
    @Scheduled(fixedRateString = "${caching.spring.bannersTTL}")
    public void emptyBannersCache() {
        log.info("emptying banners cache");
    }
}
