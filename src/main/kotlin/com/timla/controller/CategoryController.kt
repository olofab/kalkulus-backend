package com.timla.controller

import com.timla.model.Category
import com.timla.repository.CategoryRepository
import com.timla.security.JwtUtil
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/categories")
class CategoryController(
    private val categoryRepository: CategoryRepository,
    private val jwtUtil: JwtUtil
) {

    @GetMapping
    fun getCategories(request: HttpServletRequest): ResponseEntity<List<Category>> {
        val token = request.getHeader("Authorization")?.removePrefix("Bearer ")
            ?: return ResponseEntity.status(401).build()

        val companyId = jwtUtil.getCompanyId(token)
        val categories = categoryRepository.findByCompanyId(companyId)
        return ResponseEntity.ok(categories)
    }

    @PostMapping
    fun createCategory(
        @RequestBody category: Category,
        request: HttpServletRequest
    ): ResponseEntity<Category> {
        val token = request.getHeader("Authorization")?.removePrefix("Bearer ")
            ?: return ResponseEntity.status(401).build()

        val companyId = jwtUtil.getCompanyId(token)
        val saved = categoryRepository.save(
            Category(name = category.name, companyId = companyId)
        )
        return ResponseEntity.ok(saved)
    }

  @PutMapping("/{id}")
  fun updateCategory(
        @PathVariable id: Long,
        @RequestBody updatedData: Category,
        request: HttpServletRequest
    ): ResponseEntity<Category> {
        val token = request.getHeader("Authorization")?.removePrefix("Bearer ")
            ?: return ResponseEntity.status(401).build()

        return try {
            val companyId = jwtUtil.getCompanyId(token)
            val category = categoryRepository.findByIdAndCompanyId(id, companyId)
                ?: return ResponseEntity.notFound().build()

            category.name = updatedData.name
            val saved = categoryRepository.save(category)

            ResponseEntity.ok(saved)
        } catch (e: Exception) {
            ResponseEntity.status(400).build()
        }
    }


    @DeleteMapping("/{id}")
    fun deleteCategory(@PathVariable id: Long): ResponseEntity<Void> {
        if (!categoryRepository.existsById(id)) {
            return ResponseEntity.notFound().build()
        }
        categoryRepository.deleteById(id)
        return ResponseEntity.noContent().build()
    }
}
