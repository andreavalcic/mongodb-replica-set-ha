package com.szubp.mongodb_replica_set_ha.db.model

import com.szubp.mongodb_replica_set_ha.util.DateUtil
import java.io.Serializable
import java.net.URI
import java.util.Date
import java.util.Properties
import java.util.regex.Pattern
import org.apache.commons.lang3.time.FastDateFormat

sealed class EntryPoint(val uri: URI) {
	var version: String? = null
	var year: Int? = null
	var releasedDate: Date? = null
	var releasedVersion: String? = null
	var rgsUrl: URI? = null
	var unitsUrl: URI? = null

	companion object {
		val ENTRY_POINT_PATTERN = Pattern.compile("((.*www\\.nltaxonomie\\.nl)|(.*skarpliance/taxonomie)|(.*custom_taxo))/(nt\\d{2})/\\w*/(((\\d{4})(\\d{2})(\\d{2}))[^/]*)/.*(2\\d{3}).*", Pattern.CASE_INSENSITIVE or Pattern.DOTALL)
		val DF = FastDateFormat.getInstance("yyyyMMdd")
		private val entryPointsProp = Properties()
		private const val ENTRY_POINTS_PROPERTIES = "/entryPoints.properties"

		@JvmStatic
		fun create(uri: URI): EntryPoint {
			val href = uri.toString()
			val entryPoint = OtherEntryPoint(uri)
			val regimeType = RegimeType.values().find { href.endsWith("${it.value}.xsd") }
			val xbrlType = XbrlDocumentType.values().find { href.contains(it.partName) } ?: regimeType ?.documentType
			entryPoint.tryLoadUnits()
			if(null == regimeType || null == xbrlType || null == entryPoint.version) {
				entryPoint.tryLoadRgs()
				return entryPoint
			}

			val version = entryPoint.version!!
			return DviEntryPoint.getEntryPoint(uri, regimeType, version, xbrlType)
		}

		protected fun loadProps() {
			if(entryPointsProp.isEmpty) {
				synchronized(entryPointsProp) {
					if(entryPointsProp.isEmpty) {
						println("loading entryPoints urls from ${ENTRY_POINTS_PROPERTIES}")
						javaClass.getResourceAsStream(ENTRY_POINTS_PROPERTIES).use { entryPointsProp.load(it) }
					}
				}
			}
		}

		/**
		 * Creates entry point for preconfigured set of (supported) keys in entryPoint.properties
		 */
		fun createForKey(propKey: String): EntryPoint {
			loadProps()
			val urlVal = entryPointsProp.getProperty(propKey) ?: error("Not found key at entryPoint.properties? ($propKey)")
			val entryPoint = create(URI(urlVal))
			return entryPoint
		}

		fun propsKeyForUrl(uri: URI): String? {
			loadProps()
			return entryPointsProp.filter {
				it.value as String == uri.toString()
						|| (it.value as String).replace("https", "http") == uri.toString()
						|| (it.value as String).replace("http", "https") == uri.toString()
			}.keys.firstOrNull() as String?
		}

		fun uriToString(uri: URI): String = uri.toString().replace("https:", "http:")
	}

	private fun init() { //can't use kotlin init { ... } that use open/abstract fields
		val matcher = ENTRY_POINT_PATTERN.matcher(uri.toString())
		if(matcher.find() && matcher.groupCount() == 11) {
			version = matcher.group(5)
			releasedVersion = matcher.group(6)
			val releaseYear = matcher.group(8).toInt()
			val releaseMonth = matcher.group(9).toInt()
			val releaseDay = matcher.group(10).toInt()
			year = matcher.group(11).toInt()
			releasedDate = DateUtil.dateFor(releaseYear!!, releaseMonth - 1, releaseDay)
		}
	}

	open fun key(): String {
		if(null != version) {
			return "${version}_${year}_$releasedVersion"
		}
		return ""
	}

	override fun equals(other: Any?): Boolean {
		//URI string is only genuine property, everything else is derived from it
		if(this === other) return true
		if(javaClass != other?.javaClass) return false
		other as EntryPoint
		return uriToString(uri) == uriToString(other.uri)
	}

	override fun hashCode(): Int {
		//URI string is only genuine property, everything else is derived from it
		return uriToString(uri).hashCode()
	}

	class OtherEntryPoint(aUrl: URI): EntryPoint(aUrl), Serializable {

		override fun key(): String {
			val lastPart = uri.toString().substringAfterLast("/").substringBeforeLast(".")
			return "${super.key()}_$lastPart"
		}

		fun tryLoadRgs() {
			val key = propsKeyForUrl(uri)
			if(null != key && null != version) {
				val rgsUrlKey = "rgs_$key"
				entryPointsProp.getProperty(rgsUrlKey) ?.let {
					rgsUrl = URI(it)
				}
			}
		}

		fun tryLoadUnits() {
			val key = propsKeyForUrl(uri)
			lateinit var unitsUrlKey: String
			var unitsProp: String? = null
			if (null != key && null != version) {
				unitsUrlKey = "units_$key"
				unitsProp = entryPointsProp.getProperty(unitsUrlKey)
			}
			if (null == unitsProp) {
				unitsUrlKey = "units"
				unitsProp = entryPointsProp.getProperty(unitsUrlKey)
			}
			if (null != unitsProp) {
				unitsUrl = URI(unitsProp)
			}
		}
	}

	class DviEntryPoint(aUri: URI, val regimeType: RegimeType, val xbrlType: XbrlDocumentType = regimeType.documentType): EntryPoint(aUri),
		Serializable {

		companion object {

			private val entryPoints: MutableMap<String, MutableSet<DviEntryPoint>> = mutableMapOf()
			private val DVI_ENTRY_POINT_CONFIG_KEY_PATTERN = Pattern.compile("dVi_(nt\\d{2}).*", Pattern.CASE_INSENSITIVE or Pattern.DOTALL)
			private val KVK_ENTRY_POINT_CONFIG_KEY_PATTERN = Pattern.compile("KvK_(nt\\d{2}).*", Pattern.CASE_INSENSITIVE or Pattern.DOTALL)
			private val DPI_ENTRY_POINT_CONFIG_KEY_PATTERN = Pattern.compile("dPi_(nt\\d{2}).*", Pattern.CASE_INSENSITIVE or Pattern.DOTALL)

			fun getEntryPoint(regimeType: RegimeType, version: String, aXbrlType: XbrlDocumentType? = null): DviEntryPoint {
				val key = getXbrlPropKey(regimeType, version)
				loadProps()
				val url = entryPointsProp.getProperty(key) ?: error("Unable to locate entry point for $key")
				synchronized(entryPoints) {
					entryPoints[key] ?.let {
						it.firstOrNull { entryPointWithExactUrl -> entryPointWithExactUrl.uri.toString() == url } ?.let { match ->
							return@getEntryPoint match
						}
					}
				}
				return getEntryPoint(URI(url), regimeType, version, aXbrlType)
			}

			fun getEntryPoint(uri: URI, regimeType: RegimeType, version: String, aXbrlType: XbrlDocumentType? = null): DviEntryPoint {
				val key = getXbrlPropKey(regimeType, version, aXbrlType)
				loadProps()
				synchronized(entryPoints) {
					entryPoints[key]?.let {
						it.firstOrNull { entryPointWithExactUrl -> entryPointWithExactUrl.uri.toString() == uri.toString() }?.let { match ->
							return@getEntryPoint match
						}
					}

					val xbrlType = aXbrlType ?: regimeType.documentType
					val entryPoint = DviEntryPoint(uri, regimeType, xbrlType)

					var rgsUrlKey = getRgsPropKey(key) //first try specific rgs per entrypoint key
					var rgs = entryPointsProp.getProperty(rgsUrlKey)
					if(null == rgs) {
						rgsUrlKey = getRgsPropKey(xbrlType, version) //secondly try xbrlType and version specific key
						rgs = entryPointsProp.getProperty(rgsUrlKey)
					}
					if(null != rgs) {
						entryPoint.rgsUrl = URI(rgs)
					}
					var unitsUrlKey = getUnitsPropKey(xbrlType, version)
					var units = entryPointsProp.getProperty(unitsUrlKey)
					if(null == units) {
						unitsUrlKey = getUnitsPropKey()
						units = entryPointsProp.getProperty(unitsUrlKey)
					}
					if(null != unitsUrlKey) {
						entryPoint.unitsUrl = URI(units)
					}
					val set = entryPoints.computeIfAbsent(key) {
						mutableSetOf()
					}
					set += entryPoint
					return entryPoint
				}
			}

			private fun getRgsPropKey(entypointKey: String): String {
				return "rgs_$entypointKey"
			}

			private fun getUnitsPropKey(): String {
				return "units"
			}

			private fun getUnitsPropKey(xbrlType: XbrlDocumentType, version: String): String {
				return "units_${xbrlType.key}_$version"
			}

			private fun getXbrlPropKey(regimeType: RegimeType, version: String, aXbrlType: XbrlDocumentType? = null): String {
				val xbrlType = aXbrlType ?: regimeType.documentType
				return "${xbrlType.key}_${version}_${regimeType.code}"
			}

			private fun getRgsPropKey(xbrlType: XbrlDocumentType, version: String): String {
				return "rgs_${xbrlType.key}_$version"
			}
		}

		override fun key(): String {
			return "${xbrlType}_${super.key()}_${regimeType.code}"
		}
	}

	init {
		init()
	}
}

