package ru.almasgali.avito.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Entity
@Table(name = "banners")
@AllArgsConstructor
@NoArgsConstructor
public class Banner {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @SequenceGenerator(name = "banners_gen", sequenceName = "banners_seq", allocationSize = 1)
    @Id
    Long id;
    List<Long> tagIds;
    Long featureId;
    @JdbcTypeCode(SqlTypes.JSON)
    String body;
    boolean isActive;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
