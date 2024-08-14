package com.paperized.easynotifier.repository;

import com.paperized.easynotifier.model.ProductTracker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductTrackerRepository extends JpaRepository<ProductTracker, String> {
    @Query("SELECT COUNT(*) > 0 FROM ProductTracker pt JOIN pt.productTrackerDetails pd WHERE pt.id = :id AND pd.wsEnabled = TRUE")
    boolean existsTrackerIdWithWsEnabled(@Param("id") String id);
}
