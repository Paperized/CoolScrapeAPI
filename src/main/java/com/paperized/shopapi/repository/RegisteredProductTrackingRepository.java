package com.paperized.shopapi.repository;

import com.paperized.shopapi.model.RegisteredProductTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegisteredProductTrackingRepository extends JpaRepository<RegisteredProductTracking, String> {

}
