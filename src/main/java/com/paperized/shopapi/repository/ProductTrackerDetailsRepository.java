package com.paperized.shopapi.repository;

import com.paperized.shopapi.model.ProductTrackerDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductTrackerDetailsRepository extends JpaRepository<ProductTrackerDetails, String> {

}
