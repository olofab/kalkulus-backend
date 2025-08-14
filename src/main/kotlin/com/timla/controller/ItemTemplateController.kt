package com.timla.controller

import com.timla.model.ItemTemplate
import com.timla.dto.CreateItemTemplateRequest
import com.timla.dto.ItemTemplateResponse
import com.timla.dto.CategoryDto
import com.timla.repository.ItemTemplateRepository
import com.timla.repository.CategoryRepository
import com.timla.security.JwtUtil
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/items/templates")
class ItemTemplateController(
    private val itemTemplateRepository: ItemTemplateRepository,
    private val categoryRepository: CategoryRepository,
    private val jwtUtil: JwtUtil
) {

    @GetMapping
    fun getAllTemplates(request: HttpServletRequest): ResponseEntity<List<ItemTemplateResponse>> {
        val token = request.getHeader("Authorization")?.removePrefix("Bearer ") ?: return ResponseEntity.status(401).build()
        return try {
            val companyId = jwtUtil.getCompanyId(token)
            val templates = itemTemplateRepository.findByCompanyId(companyId)
            
            val response = templates.map { template ->
                ItemTemplateResponse(
                    id = template.id,
                    name = template.name,
                    unitPrice = template.unitPrice,
                    categories = template.categories.map { category ->
                        CategoryDto(
                            id = category.id,
                            name = category.name
                        )
                    }
                )
            }
            
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity.status(400).build()
        }
    }

  @PostMapping
    fun createTemplate(
        @RequestBody request: CreateItemTemplateRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ItemTemplateResponse> {
        val token = httpRequest.getHeader("Authorization")?.removePrefix("Bearer ")
            ?: return ResponseEntity.status(401).build()

        return try {
            val companyId = jwtUtil.getCompanyId(token)
            val categories = categoryRepository.findByIdInAndCompanyId(request.categoryIds, companyId)

            val newItem = ItemTemplate(
                name = request.name,
                unitPrice = request.unitPrice,
                categories = categories.toMutableList(),
                companyId = companyId
            )

            val saved = itemTemplateRepository.save(newItem)
            
            val response = ItemTemplateResponse(
                id = saved.id,
                name = saved.name,
                unitPrice = saved.unitPrice,
                categories = saved.categories.map { category ->
                    CategoryDto(
                        id = category.id,
                        name = category.name
                    )
                }
            )
            
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity.status(400).build()
        }
    }

    @PutMapping("/{id}")
    fun updateItemTemplate(
        @PathVariable id: Long,
        @RequestBody updatedData: ItemTemplate,
        request: HttpServletRequest
    ): ResponseEntity<ItemTemplate> {
        val token = request.getHeader("Authorization")?.removePrefix("Bearer ")
            ?: return ResponseEntity.status(401).build()

        return try {
            val companyId = jwtUtil.getCompanyId(token)
            val existing = itemTemplateRepository.findByIdAndCompanyId(id, companyId)
                ?: return ResponseEntity.notFound().build()

            existing.name = updatedData.name
            existing.unitPrice = updatedData.unitPrice
            existing.categories = updatedData.categories

            val saved = itemTemplateRepository.save(existing)
            ResponseEntity.ok(saved)
        } catch (e: Exception) {
            ResponseEntity.status(400).build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteTemplate(@PathVariable id: Long, request: HttpServletRequest): ResponseEntity<Void> {
        val token = request.getHeader("Authorization")?.removePrefix("Bearer ") ?: return ResponseEntity.status(401).build()
        return try {
            val companyId = jwtUtil.getCompanyId(token)
            val template = itemTemplateRepository.findByIdAndCompanyId(id, companyId)
                ?: return ResponseEntity.notFound().build()
            itemTemplateRepository.delete(template)
            ResponseEntity.noContent().build()
        } catch (e: Exception) {
            ResponseEntity.status(400).build()
        }
    }
}
