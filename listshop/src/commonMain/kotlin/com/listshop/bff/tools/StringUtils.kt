package com.listshop.bff.tools

import com.listshop.bff.remote.ListShopUrl

class StringUtils {
    companion object {
        fun buildUrl(urlString: String): ListShopUrl {
            val shemeAndUrl = urlString.split("://")
            if (shemeAndUrl.size > 1) {
                val buildUrl = ListShopUrl(scheme = shemeAndUrl.get(0))
                return splitIntoSegmentsAndPath(buildUrl, shemeAndUrl.get(1))
            } else {
                val buildUrl = ListShopUrl(scheme = "https")
                return splitIntoSegmentsAndPath(buildUrl, shemeAndUrl.get(1))
            }


            //return MyUrl("https","www.the-list-shop.com","")
        }

        private fun splitIntoSegmentsAndPath(url: ListShopUrl, hostAddress: String): ListShopUrl {
            val addressParts = hostAddress.split("/")
            val buildUrl = url.copy(host = addressParts.get(0))
            if (addressParts.size > 1) {
                val segments: List<String> = addressParts.subList(1, addressParts.size)
                val joinedSegments = segments.joinToString(prefix = "/",separator = "/")
                return buildUrl.copy(pathSegments = joinedSegments)
            }
            return buildUrl
        }
    }


}