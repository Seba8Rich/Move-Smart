package com.movesmart.demo.service

import com.movesmart.demo.model.Organization
import com.movesmart.demo.repository.OrganizationRepository
import org.springframework.stereotype.Service

@Service
class OrganizationService(
    private val organizationRepository: OrganizationRepository
) {
    fun createOrganization(org: Organization): Organization {
        return organizationRepository.save(org)
    }

}
