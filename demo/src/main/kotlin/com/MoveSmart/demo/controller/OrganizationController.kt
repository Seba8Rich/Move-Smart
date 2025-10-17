package com.movesmart.demo.controller

import com.movesmart.demo.model.Organization
import com.movesmart.demo.service.OrganizationService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/organization")
class OrganizationController(
    private val organizationService: OrganizationService
) {
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun createOrganization(@RequestBody org: Organization): ResponseEntity<Organization> {
        val createdOrg = organizationService.createOrganization(org)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrg)
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun getOrganization(): ResponseEntity<List<Organization>> {
        val organizations = organizationService.getOrganization()
        return ResponseEntity.ok(organizations)
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun getOrganizationById(@PathVariable id: Long): ResponseEntity<Organization> {
        val organization = organizationService.getOrganizationById(id)
        return ResponseEntity.ok(organization)
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateOrganization(@PathVariable id: Long, @RequestBody org: Organization): ResponseEntity<Organization> {
        val updatedOrg = organizationService.updateOrganization(id, org)
        return ResponseEntity.ok(updatedOrg)
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteOrganization(@PathVariable id: Long): ResponseEntity<String> {
        return if (organizationService.deleteOrganization(id)) {
            ResponseEntity.ok("Organization deleted successfully")
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
