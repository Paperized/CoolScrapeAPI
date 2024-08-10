package com.paperized.easynotifier.repository;

import com.paperized.easynotifier.model.ProductTracker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductTrackerRepository extends JpaRepository<ProductTracker, String> {

}
