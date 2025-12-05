package com.movesmart.demo.repository

import com.movesmart.demo.model.Bus
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface BusRepository : JpaRepository<Bus, Long> {
    @Query("SELECT b FROM Bus b LEFT JOIN FETCH b.driver LEFT JOIN FETCH b.organization WHERE b.plateNumber = :plateNumber")
    fun findByPlateNumber(@Param("plateNumber") plateNumber: String): Bus?
    
    @Query("SELECT b FROM Bus b LEFT JOIN FETCH b.driver LEFT JOIN FETCH b.organization")
    override fun findAll(): List<Bus>
    
    @Query("SELECT b FROM Bus b LEFT JOIN FETCH b.driver LEFT JOIN FETCH b.organization WHERE b.id = :id")
    fun findByIdWithDriver(@Param("id") id: Long): java.util.Optional<Bus>
    
    @EntityGraph(attributePaths = ["driver", "organization"])
    override fun findById(id: Long): java.util.Optional<Bus>
    
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE bus SET driver_id = :driverId WHERE id = :busId", nativeQuery = true)
    fun assignDriverToBus(@Param("busId") busId: Long, @Param("driverId") driverId: Long?): Int
    
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE bus SET driver_id = :driverId WHERE plate_number = :plateNumber", nativeQuery = true)
    fun assignDriverToBusByPlateNumber(@Param("plateNumber") plateNumber: String, @Param("driverId") driverId: Long?): Int
}