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

@Service
@Slf4j
@RequiredArgsConstructor
public class BannerService {

    private final BannerRepository bannerRepository;

    public void createBanner(BannerRequest request) {

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
    public void updateBanner(Long id, BannerRequest request) {
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
    public void deleteBanner(Long id) {
        bannerRepository.deleteById(id);
    }

    public Banner getBanner(Long tagId, Long featureId, boolean useLastRevision) {
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
            Long tagId,
            Long featureId,
            String limit,
            int offset) {
        return bannerRepository.findByTagIdAndFeatureIdWithLimitAndOffset(tagId, featureId, limit, offset);
    }

    @CacheEvict(value = "banners", allEntries = true)
    @Scheduled(fixedRateString = "${caching.spring.bannersTTL}")
    public void emptyBannersCache() {
        log.info("emptying banners cache");
    }
}
