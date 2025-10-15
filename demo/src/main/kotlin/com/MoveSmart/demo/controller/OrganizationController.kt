package com.movesmart.demo.controller

import com.movesmart.demo.model.Organization
import com.movesmart.demo.service.OrganizationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/organization")
class OrganizationController(
    private val organizationService: OrganizationService
) {
    @PostMapping
    fun createOrganization(@RequestBody org: Organization): Organization {
        return organizationService.createOrganization(org)
    }
    @GetMapping
    fun getOrganization(): List<Organization> = organizationService.getOrganization()
    
    @GetMapping("/{id}")
    fun getOrganizationById(@PathVariable id: Long): Organization = organizationService.getOrganizationById(id)
    
    @PutMapping("/{id}")
    fun updateOrganization(@PathVariable id: Long, @RequestBody org: Organization): Organization = 
        organizationService.updateOrganization(id, org)
    
    @DeleteMapping("/{id}")
    fun deleteOrganization(@PathVariable id: Long): ResponseEntity<String> {
        return if (organizationService.deleteOrganization(id)) {
            ResponseEntity.ok("Organization deleted successfully")
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
