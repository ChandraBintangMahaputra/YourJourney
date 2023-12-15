package com.example.yourjourney.map

import com.example.yourjourney.identity.Journey
import com.example.yourjourney.database.JourneyDb
import com.example.yourjourney.database.Widget


fun JourneyToEntity(journey: Journey): JourneyDb {
    return JourneyDb (
        id = journey.id,
        name = journey.name,
        description = journey.description,
        createdAt = journey.createdAt,
        photoUrl = journey.photoUrl,
        lat = journey.lat,
        lon = journey.lon
    )
}

fun journeyToWidget(story: Journey) : Widget {
    return Widget(
        id = story.id,
        photoUrl = story.photoUrl
    )
}