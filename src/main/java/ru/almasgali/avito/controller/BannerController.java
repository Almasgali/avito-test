package ru.almasgali.avito.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.almasgali.avito.dto.BannerRequest;
import ru.almasgali.avito.model.Banner;
import ru.almasgali.avito.service.BannerService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    @GetMapping("/user_banner")
    @ResponseStatus(HttpStatus.OK)
    public String getBannerForUser(
            @RequestParam long tagId,
            @RequestParam long featureId,
            @RequestParam(required = false, defaultValue = "false") boolean useLastRevision) {
        return bannerService.getBanner(tagId, featureId, useLastRevision).getBody();
    }

    @GetMapping("/banner")
    @ResponseStatus(HttpStatus.OK)
    public List<Banner> getBanners(
            @RequestParam(required = false) long tagId,
            @RequestParam(required = false) long featureId,
            @RequestParam(required = false, defaultValue = "ALL") String limit,
            @RequestParam(required = false, defaultValue = "0") int offset) {
        return bannerService.getBannersWithLimitAndOffset(tagId, featureId, limit, offset);
    }

    @PostMapping("/banner")
    @ResponseStatus(HttpStatus.CREATED)
    public void createBanner(@RequestBody BannerRequest bannerRequest) {
        bannerService.createBanner(bannerRequest);
    }

    @PostMapping("/banner/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateBanner(@PathVariable long id, @RequestBody BannerRequest bannerRequest) {
        bannerService.updateBanner(id, bannerRequest);
    }

    @PostMapping("/banner/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBanner(@PathVariable long id) {
        bannerService.deleteBanner(id);
    }
}
