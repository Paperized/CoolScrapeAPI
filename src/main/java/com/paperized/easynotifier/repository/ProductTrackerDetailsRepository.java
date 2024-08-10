package com.paperized.easynotifier.repository;

import com.paperized.easynotifier.model.ProductTrackerDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductTrackerDetailsRepository extends JpaRepository<ProductTrackerDetails, String> {

}
