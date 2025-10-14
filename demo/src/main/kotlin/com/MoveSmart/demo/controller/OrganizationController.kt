package com.movesmart.demo.controller

import com.movesmart.demo.model.Organization
import com.movesmart.demo.service.OrganizationService
import org.springframework.web.bind.annotation.RequestBody


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

}
