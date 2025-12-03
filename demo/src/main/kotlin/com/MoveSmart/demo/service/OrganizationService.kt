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
    fun getOrganization(): List<Organization> = organizationRepository.findAll()
    
    fun getOrganizationById(id: Long): Organization {
        return organizationRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Organization not found with ID: $id") }
    }
    
    fun updateOrganization(id: Long, organization: Organization): Organization {
        val existingOrg = organizationRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Organization not found with ID: $id") }
        
        val updatedOrg = existingOrg.copy(
            name = organization.name,
            address = organization.address,
            contactNumber = organization.contactNumber,
            email = organization.email
        )
        
        return organizationRepository.save(updatedOrg)
    }
    
    fun deleteOrganization(id: Long): Boolean {
        return if (organizationRepository.existsById(id)) {
            organizationRepository.deleteById(id)
            true
        } else {
            false
        }
    }

}
