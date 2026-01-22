package com.example.ecodonnees.domain.model

data class InternetPackage(
    val id: Int,
    val name: String,
    val dataGB: Int,
    val validityDays: Int,
    val ussdCode: String,
    val description: String
) {
    companion object {
        fun getAvailablePackages(): List<InternetPackage> {
            return listOf(
                InternetPackage(
                    id = 1,
                    name = "Express",
                    dataGB = 1,
                    validityDays = 1,
                    ussdCode = "*164*2*1*1#",
                    description = "1 Go valable 24 heures"
                ),
                InternetPackage(
                    id = 2,
                    name = "Découverte",
                    dataGB = 5,
                    validityDays = 3,
                    ussdCode = "*164*2*2*1#",
                    description = "5 Go valables 3 jours"
                ),
                InternetPackage(
                    id = 3,
                    name = "Évasion",
                    dataGB = 12,
                    validityDays = 7,
                    ussdCode = "*164*2*3*1#",
                    description = "12 Go valables 7 jours"
                ),
                InternetPackage(
                    id = 4,
                    name = "Confort",
                    dataGB = 20,
                    validityDays = 30,
                    ussdCode = "*164*2*4*1#",
                    description = "20 Go valables 30 jours"
                )
            )
        }
    }
}