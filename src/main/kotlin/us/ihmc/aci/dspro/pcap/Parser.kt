package us.ihmc.aci.dspro.pcap

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import us.ihmc.aci.dspro.sqlite.SqliteReader
import java.io.File

internal fun getParser(filename: String): ObservableOnSubscribe<Message> {
    val extension = File(filename).extension.toLowerCase()
    return when (extension) {
        "sqlite" -> SqliteReader(filename)
        "pcap" -> PcapReader(filename)
        else -> throw NotImplementedError("Parser for $extension file not supported")
    }
}

internal fun getObserver(filename: String): Observable<Message> = Observable.create(getParser(filename))

internal fun midpoint(a: String, b: String) = midpoint(a.toFloat(), b.toFloat())
internal fun midpoint(a: Float, b: Float) = Math.min(a, b) + (Math.abs(a - b)/2)

internal fun JsonObject.toCSV(): String {

    data class Info (val lat: Any?, val lon: Any?, val dtg: Any?)

    val info = if (this["Description"] == "x-dspro/x-soi-track-info") {
        val appMetadata = this["Application_Metadata"] as JsonObject
        val events = appMetadata["events"] as JsonArray<JsonObject>
        val location = events[0]["location"] as JsonObject
        val position = location["position"] as JsonObject
        Info(position["latitude"], position["longitude"], events[0]["dtg"])
    }
    else {
        val lat = midpoint(this["Left_Upper_Latitude"].toString(), this["Right_Lower_Latitude"].toString())
        val lon = midpoint(this["Right_Lower_Longitude"].toString(), this["Left_Upper_Longitude"].toString())
        Info(lat, lon, this["Source_Time_Stamp"])
    }
    val milstd2525 = this["Node_Type"].toString()
    return "${this["Referred_Data_Object_Id"]}, ${this["Referred_Data_Instance_Id"]}, ${this["Data_Content"]}, ${info.lat}, ${info.lon}, ${info.dtg}, $milstd2525"
}

internal fun Observable<Message>.tracks() = filter { it.getType() == Protocol.DSPro }
        .map { it as DSProMessage }
        .filter { it.isMetadata }
        .map { it.body as us.ihmc.aci.dspro.pcap.dspro.Metadata }
        .filter{ it.metadata["Description"] == "x-dspro/x-soi-track-info" }

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println(args)
        println("Usage: java -jar <capture.pcap> | <db.sqlite>")
    }
    var i = 0
    for (filename in args) {
        println("Reading, $filename")
        getObserver(filename)
                .map { it.getMessage(Protocol.DisService).getMessage(Protocol.DSPro) }
                .tracks()
                .map { it.metadata.toCSV() }
                .subscribe {
                    println("$it")
                }
    }
}

