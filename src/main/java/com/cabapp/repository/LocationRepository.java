package com.cabapp.repository;

import com.cabapp.model.Driver;
import com.cabapp.model.LocationUpdate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<LocationUpdate, Long> {
}
