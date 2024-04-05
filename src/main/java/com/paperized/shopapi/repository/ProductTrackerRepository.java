package com.paperized.shopapi.repository;

import com.paperized.shopapi.model.ProductTracker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductTrackerRepository extends JpaRepository<ProductTracker, String> {

}
