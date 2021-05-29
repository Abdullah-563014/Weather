package weather.app.live.update.forecast.models

class LatAndLonModel {

    var status: Boolean=false
    var lat: Double=.0
    var lon: Double=.0


    constructor()

    constructor(status: Boolean, lat: Double, lon: Double) {
        this.status = status
        this.lat = lat
        this.lon = lon
    }


}