package com.listshop.bff.repositories.impl

import com.listshop.bff.data.remote.ApiLayout
import com.listshop.bff.data.remote.ApiLayoutCategory
import com.listshop.bff.db.LayoutCategoryMappingEntity
import com.listshop.bff.db.ListshopDb
import com.listshop.bff.repositories.LayoutRepository
import com.listshop.bff.repositories.ListShopDatabase

class LayoutRepositoryImpl(
    private val listShopDatabase: ListShopDatabase
) : LayoutRepository {
    private val dbRef: ListshopDb = listShopDatabase.db

    override fun saveLayoutLocally(layout: ApiLayout) {

        // try the tag mappings first
        val layoutId = layout.externalId ?: 0
        for (cat in layout.categories) {
            val catId = cat.externalId.toString()
            // insert category mappings into table
            val mappings = cat.tags.map { tag -> tag.externalId }
                .map { tid -> LayoutCategoryMappingEntity(catId, tid) }
            insertLayoutMappings(catId, mappings)
            // insert category
            insertLayoutCategory(layoutId, cat)
        }
        // insert layout into table
        insertLayout(layout)

    }

    private fun insertLayoutMappings(
        catId: String,
        mappings: List<LayoutCategoryMappingEntity>
    ) {
        dbRef.layoutDefinitionQueries.transaction {
            mappings.forEach { mappingEntity ->
                dbRef.layoutDefinitionQueries
                    .insertIntoLayoutCategoryMappingEntity(
                        catId,
                        mappingEntity.tagExternalId
                    )

            }
        }
    }

    private fun insertLayoutCategory(layoutId: Int, cat: ApiLayoutCategory) {
        //MM add error or dont save if no external id
        dbRef.layoutDefinitionQueries
            .insertIntoLayoutCategoryEntity(
                name = cat.name ?: "",
                externalId = cat.externalId.toString(),
                layoutExternalId = layoutId.toString(),
                displayOrder = cat.displayOrder,
                isDefault = cat.isDefault
            )
    }

    private fun insertLayout(layout: ApiLayout) {
        dbRef.layoutDefinitionQueries
            .insertIntoLayoutEntity(
                name = layout.name,
                externalId = layout.externalId.toString(),
                isDefault = layout.isDefault,
                userId = layout.userId
            )
    }


    override fun clearLayoutDataLocally() {
        dbRef.layoutDefinitionQueries.removeAllLayoutCategoryMappingEntity()
        dbRef.layoutDefinitionQueries.removeAllLayoutCategoryEntities()
        dbRef.layoutDefinitionQueries.removeAllLayoutEntities()
    }

}
