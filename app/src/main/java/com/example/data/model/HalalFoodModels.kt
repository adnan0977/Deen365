package com.example.data.model

/**
 * Halal Food & E-Numbers Guide Models & Database.
 * Comprehensive classifications based on international Islamic dietary standards.
 */

enum class HalalStatus {
    HALAL,      // Permissible (Green)
    HARAM,      // Forbidden (Red)
    MUSHBOOH    // Doubtful / Source-dependent (Amber)
}

enum class FoodOrigin {
    VEGETABLE,
    ANIMAL,
    SYNTHETIC,
    MICROBIAL,
    MINERAL
}

data class ENumberItem(
    val code: String,              // e.g., "E120"
    val name: String,              // e.g., "Carmine / Cochineal"
    val category: String,          // e.g., "Food Coloring"
    val status: HalalStatus,
    val origin: FoodOrigin,
    val description: String,
    val fiqhVerdict: String,
    val commonFoods: String,
    val isVegetarian: Boolean
)

data class HalalFoodState(
    val searchQuery: String = "",
    val selectedStatusFilter: HalalStatus? = null, // null = All
    val selectedCategoryFilter: String? = null,
    val selectedENumber: ENumberItem? = null,
    val isDetailDialogOpen: Boolean = false
)

object HalalFoodData {

    val ALL_E_NUMBERS = listOf(
        ENumberItem(
            code = "E100",
            name = "Curcumin / Turmeric",
            category = "Coloring",
            status = HalalStatus.HALAL,
            origin = FoodOrigin.VEGETABLE,
            description = "Natural yellow pigment extracted from the rhizomes of the Curcuma longa plant.",
            fiqhVerdict = "100% Halal. Derived exclusively from plants with zero animal components.",
            commonFoods = "Mustard, curry powder, cheeses, margarine, pastries.",
            isVegetarian = true
        ),
        ENumberItem(
            code = "E120",
            name = "Carmine / Cochineal",
            category = "Coloring",
            status = HalalStatus.HARAM,
            origin = FoodOrigin.ANIMAL,
            description = "Vibrant crimson-red color extracted from crushed dried bodies of female scale insects (Dactylopius coccus).",
            fiqhVerdict = "Haram according to Hanafi, Shafi'i, and Hanbali schools due to insects being Maytah/Khabith. (Maliki school permits insect consumption when processed, but mainstream halal certification bodies designate it Haram).",
            commonFoods = "Yogurts, red candies, fruit drinks, baked goods, cosmetics.",
            isVegetarian = false
        ),
        ENumberItem(
            code = "E150a",
            name = "Plain Caramel",
            category = "Coloring",
            status = HalalStatus.HALAL,
            origin = FoodOrigin.VEGETABLE,
            description = "Natural brown coloring made by controlled heat treatment of carbohydrates (sugars).",
            fiqhVerdict = "Halal. Plant-based caramelization with no animal additives.",
            commonFoods = "Colas, gravies, breads, chocolate products.",
            isVegetarian = true
        ),
        ENumberItem(
            code = "E160a",
            name = "Carotenes",
            category = "Coloring",
            status = HalalStatus.HALAL,
            origin = FoodOrigin.VEGETABLE,
            description = "Orange-yellow pigment extracted from carrots or produced synthetically.",
            fiqhVerdict = "Halal if plant/synthetic based. Verify if gelatin was used as a carrier in dry mixes.",
            commonFoods = "Butter, margarine, fruit juices, cakes.",
            isVegetarian = true
        ),
        ENumberItem(
            code = "E211",
            name = "Sodium Benzoate",
            category = "Preservative",
            status = HalalStatus.HALAL,
            origin = FoodOrigin.SYNTHETIC,
            description = "Widely used chemical food preservative that inhibits mold, yeast, and bacterial growth.",
            fiqhVerdict = "Halal. Fully synthetic chemical compound without animal derivatives.",
            commonFoods = "Carbonated drinks, fruit salads, jams, pickles.",
            isVegetarian = true
        ),
        ENumberItem(
            code = "E422",
            name = "Glycerol / Glycerin",
            category = "Humectant & Sweetener",
            status = HalalStatus.MUSHBOOH,
            origin = FoodOrigin.ANIMAL,
            description = "Clear, viscous liquid used to retain moisture and sweeten confections.",
            fiqhVerdict = "Mushbooh (Doubtful). Halal if derived from plant oils (palm/soy) or synthetic sources. Haram if derived from non-dhabiha animal fats (pork or unslaughtered beef). Look for 'Vegetable Glycerin'.",
            commonFoods = "Baked goods, energy bars, fondants, marshmallows, pharmaceuticals.",
            isVegetarian = false
        ),
        ENumberItem(
            code = "E441",
            name = "Gelatin",
            category = "Gelling Agent",
            status = HalalStatus.MUSHBOOH,
            origin = FoodOrigin.ANIMAL,
            description = "Protein substance derived from boiling animal skin, tendons, ligaments, and bones.",
            fiqhVerdict = "Strictly Haram if derived from pork (swine) or non-halal slaughtered cattle. Halal ONLY if certified from Halal-slaughtered cattle (Bovine Gelatin) or Fish Gelatin.",
            commonFoods = "Gummy bears, jelly desserts, marshmallows, mousses, capsule pills.",
            isVegetarian = false
        ),
        ENumberItem(
            code = "E471",
            name = "Mono- and Diglycerides of Fatty Acids",
            category = "Emulsifier",
            status = HalalStatus.MUSHBOOH,
            origin = FoodOrigin.ANIMAL,
            description = "Semisynthetic emulsifier used to blend oils and water together and extend shelf life.",
            fiqhVerdict = "Mushbooh. Can be manufactured from plant fats (soy/palm) or animal lard/tallow. Permissible only if labeled 'Plant-based' or 'Suitable for Vegetarians'.",
            commonFoods = "Bread, ice cream, margarine, cakes, potato chips, peanut butter.",
            isVegetarian = false
        ),
        ENumberItem(
            code = "E472e",
            name = "Diacetyltartaric Acid Esters of Mono- and Diglycerides",
            category = "Emulsifier (DATEM)",
            status = HalalStatus.MUSHBOOH,
            origin = FoodOrigin.ANIMAL,
            description = "Dough conditioner that strengthens bread gluten network.",
            fiqhVerdict = "Mushbooh. Requires verification of the fatty acid source (vegetable vs animal).",
            commonFoods = "Commercial bakery bread, hamburger buns, pizza dough.",
            isVegetarian = false
        ),
        ENumberItem(
            code = "E542",
            name = "Bone Phosphate",
            category = "Anti-caking Agent",
            status = HalalStatus.HARAM,
            origin = FoodOrigin.ANIMAL,
            description = "Phosphate extracted directly from steamed and crushed animal bones.",
            fiqhVerdict = "Haram unless bones originate strictly from Islamic dhabiha-slaughtered cattle.",
            commonFoods = "Dry food powders, dry beverage bases, table salt.",
            isVegetarian = false
        ),
        ENumberItem(
            code = "E901",
            name = "Beeswax (White & Yellow)",
            category = "Glazing Agent",
            status = HalalStatus.HALAL,
            origin = FoodOrigin.ANIMAL,
            description = "Natural wax produced by honey bees (Apis mellifera).",
            fiqhVerdict = "Halal. Pure excretion of bees comparable to honey, permissible by consensus.",
            commonFoods = "Confectionery coatings, chewing gum, coffee bean coating.",
            isVegetarian = true
        ),
        ENumberItem(
            code = "E904",
            name = "Shellac",
            category = "Glazing Agent",
            status = HalalStatus.MUSHBOOH,
            origin = FoodOrigin.ANIMAL,
            description = "Resinous secretion from the female lac bug (Kerria lacca) found in forests of India and Thailand.",
            fiqhVerdict = "Mushbooh / Disputed. Many halal authorities consider it Halal as it is an insect secretion rather than bug body; others avoid it due to filtration residues. Preferred to avoid when possible.",
            commonFoods = "Shiny chocolate dragees, sprinkles, pill coatings, fruit shine.",
            isVegetarian = false
        ),
        ENumberItem(
            code = "E920",
            name = "L-Cysteine",
            category = "Flour Treatment Agent",
            status = HalalStatus.MUSHBOOH,
            origin = FoodOrigin.ANIMAL,
            description = "Amino acid used to make bread dough softer and more elastic.",
            fiqhVerdict = "Strictly Haram if extracted from human hair (prohibited by sacred human dignity in Islam). Permissible if synthesized chemically, or derived from poultry feathers through halal processing.",
            commonFoods = "Commercial sliced bread, croissants, pizza crusts.",
            isVegetarian = false
        ),
        ENumberItem(
            code = "E951",
            name = "Aspartame",
            category = "Artificial Sweetener",
            status = HalalStatus.HALAL,
            origin = FoodOrigin.SYNTHETIC,
            description = "Intense low-calorie artificial sweetener around 200 times sweeter than sucrose.",
            fiqhVerdict = "Halal. Synthesized from amino acids aspartic acid and phenylalanine; free from animal fats.",
            commonFoods = "Diet sodas, sugar-free gum, low-calorie yogurts.",
            isVegetarian = true
        ),
        ENumberItem(
            code = "E1105",
            name = "Lysozyme",
            category = "Preservative",
            status = HalalStatus.HALAL,
            origin = FoodOrigin.ANIMAL,
            description = "Natural antibacterial enzyme isolated from egg whites.",
            fiqhVerdict = "Halal. Sourced from poultry egg whites which are universally halal.",
            commonFoods = "Aged cheeses, wines, beer brewing.",
            isVegetarian = true
        )
    )

    fun search(query: String, statusFilter: HalalStatus? = null): List<ENumberItem> {
        return ALL_E_NUMBERS.filter { item ->
            val matchesQuery = query.isBlank() ||
                    item.code.contains(query, ignoreCase = true) ||
                    item.name.contains(query, ignoreCase = true) ||
                    item.category.contains(query, ignoreCase = true)

            val matchesStatus = statusFilter == null || item.status == statusFilter

            matchesQuery && matchesStatus
        }
    }
}
