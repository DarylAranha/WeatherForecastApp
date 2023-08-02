data class WeatherResponse(
    val coord: Coord,
    val main: Main,
    val sys: Sys,
    val name: String
)

data class Coord(
    val lat: Double,
    val lon: Double
)

data class Main(
    val temp: Double,
    val humidity: Int
)

data class Sys(
    val country: String
)