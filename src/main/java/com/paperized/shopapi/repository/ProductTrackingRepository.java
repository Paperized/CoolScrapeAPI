package com.paperized.shopapi.repository;

import com.paperized.shopapi.model.ProductTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductTrackingRepository extends JpaRepository<ProductTracking, String> {

}
