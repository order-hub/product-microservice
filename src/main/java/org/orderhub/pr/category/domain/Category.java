package org.orderhub.pr.category.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.orderhub.pr.category.dto.request.CategoryUpdateRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.orderhub.pr.category.exception.ExceptionMessage.CANNOT_BE_YOUR_OWN_PARENT;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> children;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType type;

    private CategoryStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public Category(Long id, String name, Category parent, CategoryType type) {
        this.id = id;
        this.name = name;
        this.parent = parent;
        this.type = type;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = CategoryStatus.ACTIVE;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.status = CategoryStatus.DELETED;
    }

    public void restore() {
        this.status = CategoryStatus.ACTIVE;
    }

    public boolean isActive() {
        return this.status == CategoryStatus.ACTIVE;
    }

    public void applyUpdate(String newName, CategoryType newType, Category newParent) {
        if (newParent != null && newParent.equals(this)) {
            throw new IllegalArgumentException(CANNOT_BE_YOUR_OWN_PARENT);
        }
        this.name = newName;
        this.parent = newParent;
        this.type = newType;
    }

}
