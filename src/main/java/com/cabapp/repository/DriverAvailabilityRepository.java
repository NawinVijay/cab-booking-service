package com.cabapp.repository;

import com.cabapp.model.DriverAvailability;
import com.cabapp.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface DriverAvailabilityRepository extends JpaRepository<DriverAvailability, Long> {
    
    List<DriverAvailability> findByDriver(Driver driver);

    DriverAvailability findByDriverId(Long aLong);

    List<DriverAvailability> findByAvailableFromBeforeAndAvailableToAfter(OffsetDateTime scheduledTime1, OffsetDateTime scheduledTime2);

    // This also can be used
    @Query("SELECT da FROM DriverAvailability da WHERE :scheduledTime BETWEEN da.availableFrom AND da.availableTo AND da.driver.isOnline = true")
    List<DriverAvailability> findDriverAvailabilityForTheScheduledTime(@Param("scheduledTime") OffsetDateTime localDateTime);


}
