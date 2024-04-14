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
            @RequestHeader("token") String token,
            @RequestParam long tagId,
            @RequestParam long featureId,
            @RequestParam(required = false, defaultValue = "false") boolean useLastRevision) {
        return bannerService.getUserBanner(token, tagId, featureId, useLastRevision).getBody();
    }

    @GetMapping("/banner")
    @ResponseStatus(HttpStatus.OK)
    public List<Banner> getBanners(
            @RequestHeader("token") String token,
            @RequestParam(required = false) long tagId,
            @RequestParam(required = false) long featureId,
            @RequestParam(required = false, defaultValue = "ALL") String limit,
            @RequestParam(required = false, defaultValue = "0") int offset) {
        return bannerService.getBannersWithLimitAndOffset(token, tagId, featureId, limit, offset);
    }

    @PostMapping("/banner")
    @ResponseStatus(HttpStatus.CREATED)
    public void createBanner(
            @RequestHeader("token") String token,
            @RequestBody BannerRequest bannerRequest) {
        bannerService.createBanner(token, bannerRequest);
    }

    @PostMapping("/banner/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateBanner(
            @RequestHeader("token") String token,
            @PathVariable long id,
            @RequestBody BannerRequest bannerRequest) {
        bannerService.updateBanner(token, id, bannerRequest);
    }

    @PostMapping("/banner/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBanner(
            @RequestHeader("token") String token,
            @PathVariable long id) {
        bannerService.deleteBanner(token, id);
    }
}
