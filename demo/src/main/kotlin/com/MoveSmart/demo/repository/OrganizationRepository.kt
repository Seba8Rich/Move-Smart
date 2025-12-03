package com.movesmart.demo.repository

import com.movesmart.demo.model.Organization
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrganizationRepository : JpaRepository<Organization, Long> {
    fun findFirstByOrderByIdAsc(): Organization?
}
