package ru.almasgali.avito.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.almasgali.avito.model.Banner;

import java.util.List;

public interface BannerRepository extends JpaRepository<Banner, Long> {

    @Query(value = "SELECT b FROM banners " +
            "WHERE (?1 = ANY(b.tag_ids)) and (b.feature_id = ?2) and (b.is_active = true);",
            nativeQuery = true)
    List<Banner> findByTagIdAndFeatureId(long tagId, long featureId);

    @Query(value = "SELECT b FROM banners " +
            "WHERE (?1 = ANY(b.tag_ids)) and (b.feature_id = ?2)" +
            "ORDER BY b.updated_at" +
            "LIMIT ?3" +
            "OFFSET ?4;",
            nativeQuery = true)
    List<Banner> findByTagIdAndFeatureIdWithLimitAndOffset(
            long tagId,
            long featureId,
            String limit,
            int offset);
}
