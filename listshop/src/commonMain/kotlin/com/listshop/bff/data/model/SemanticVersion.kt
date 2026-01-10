package com.listshop.bff.data.model

data class SemanticVersion(
    val major: Int,
    val minor: Int,
    val patch: Int,
) {
    companion object Factory {
        fun create(versionString: String): SemanticVersion {
            var minor = 0
            var patch = 0

            val versionParts = versionString.split(".").map { it.toInt() }
            val major = versionParts[0]
            if (versionParts.size > 1) {
                minor = versionParts[1]
            }
            if (versionParts.size > 2) {
                patch = versionParts[2]
            }

            return SemanticVersion(major, minor, patch)
        }

        fun isGreaterThanOrEquals(first: SemanticVersion, second: SemanticVersion): Boolean {
            if (first.major != second.major) {
                return first.major > second.major
            }
            if (first.minor != second.minor) {
                return first.minor > second.minor
            }
            return first.patch >= second.patch
        }

    }

}

