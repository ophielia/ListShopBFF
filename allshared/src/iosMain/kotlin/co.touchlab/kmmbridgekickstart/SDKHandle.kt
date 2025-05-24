package co.touchlab.kmmbridgekickstart

import co.touchlab.kmmbridgekickstart.repository.BreedRepository

data class LegacySDKHandle(
    val breedRepository: BreedRepository,
    val appAnalytics: AppAnalytics,
    val breedAnalytics: BreedAnalytics
)
