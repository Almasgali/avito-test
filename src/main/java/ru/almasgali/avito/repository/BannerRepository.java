package ru.almasgali.avito.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.almasgali.avito.model.Banner;

import java.util.List;


public interface BannerRepository extends JpaRepository<Banner, Long> {

    @Query(value = "SELECT * FROM banners " +
            "WHERE (?1 = ANY(tag_ids)) and (feature_id = ?2) and (is_active = true or is_active = ?3);",
            nativeQuery = true)
    List<Banner> findByTagIdAndFeatureId(Long tagId, Long featureId, boolean isUser);

    @Query(value = "SELECT * FROM banners " +
            "WHERE (?1 = ANY(tag_ids)) and (feature_id = ?2) " +
            "ORDER BY updated_at " +
            "LIMIT ?3 " +
            "OFFSET ?4",
            nativeQuery = true)
    List<Banner> findByTagIdAndFeatureIdWithLimitAndOffset(
            Long tagId,
            Long featureId,
            int limit,
            int offset);

    @Query(value = "SELECT * FROM banners " +
            "WHERE (?1 = ANY(tag_ids)) " +
            "ORDER BY updated_at " +
            "LIMIT ?2 " +
            "OFFSET ?3",
            nativeQuery = true)
    List<Banner> findByTagIdWithLimitAndOffset(
            Long tagId,
            int limit,
            int offset);

    @Query(value = "SELECT * FROM banners " +
            "WHERE (feature_id = ?1) " +
            "ORDER BY updated_at " +
            "LIMIT ?2 " +
            "OFFSET ?3",
            nativeQuery = true)
    List<Banner> findByFeatureIdWithLimitAndOffset(
            Long featureId,
            int limit,
            int offset);

    @Query(value = "SELECT * FROM banners " +
            "ORDER BY updated_at " +
            "LIMIT ?1 " +
            "OFFSET ?2",
            nativeQuery = true)
    List<Banner> findWithLimitAndOffset(
            int limit,
            int offset);
}
